package com.example.moblebox.mobleboxdb;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.moblebox.api.ApiNaverMovie;
//import com.example.moblebox.api.SharedPrefCheck;
import com.example.moblebox.tag.ApiMovieTag;
import com.example.moblebox.tag.DBPathTag;
import com.example.moblebox.tag.UserInfoTags;
import com.example.moblebox.ui.bookMovie.BookMovieFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Handler;

import static com.example.moblebox.tag.NaverApiTag.LINK;

public class MobleBoxDB implements DBPathTag, ApiMovieTag, UserInfoTags {
    ApiNaverMovie naverMovie;
    static FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//    SharedPrefCheck check = new SharedPrefCheck();

    public void putMovieApiToDB(Map<String, Object> movieInfo){
        DocumentReference coll = firestore.collection(PATH_MOVIEINFO).document(PATH_MOVIE);
            coll.set(movieInfo);
    }
    public void putNaverApiToDB(Map<String, Object> movieInfo){
        CollectionReference coll = firestore.collection(PATH_IMAGE);
            coll.document(PATH_MOVIE).set(movieInfo);
    }
    public void checkNaverApi(){
        HashSet<String> movieNm = new HashSet<>();
        firestore.collection(PATH_MOVIEINFO).document(PATH_MOVIE).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            movieNm.addAll(task.getResult().getData().keySet());

                            if(movieNm.size() != 0){
                                naverMovie = new ApiNaverMovie(movieNm);
                                naverMovie.start();
                            }
                        }
                    }
                });
    }
    public void putStoreInfoToDB(Map movieInfos, Map openDtMap){
        firestore.collection(PATH_STORE).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                        if (task1.getResult().isEmpty()){
                            if(movieInfos.size() != 0){
                                onStoreMovieTimeMap(movieInfos);
                            }
                            Log.i("chiffon95", "moviemovie >> " + movieInfos.toString());
                        }
                    }
                }
        );

        for(String st : PATH_STORE_ARR){
            firestore.collection(PATH_STORE).document(st).get().addOnCompleteListener(
                    new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                            if(task1.getResult().exists()){
                                if(openDtMap.size() != 0) {
                                    Log.i("chiffon95", "moviemovie >> " + openDtMap.toString());
                                    storeDateUpdate(openDtMap, st, task1);
                                }
//                                HashSet<String> movieSet = new HashSet<>();
//                                movieSet.addAll(task.getResult().getData().keySet());
//                                movieMap.putAll((Map)task.getResult().getData().get(PATH_MOVIE));
//                                Log.i("chiffon95", "task list : " + task.getResult().getData().get(PATH_MOVIE));
                            }
                        }
                    }
            );
        }
    }

    //task -> collection(PATH_STORE).document(storeName).get()
    public void storeDateUpdate(Map openDateMap, String store, Task<DocumentSnapshot> task){
        final int week = 7;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        Calendar calendar = Calendar.getInstance();
        String today = dateFormat.format(calendar.getTime());
        Map<String, Object> storeMovieMap = new HashMap<>();

        Calendar calendarWeek;
        Map<String, Map> map1 = (Map)task.getResult().getData().get(PATH_MOVIE);
        Iterator<Map.Entry<String, Map>> itrMap1 = map1.entrySet().iterator();

        Map<String, Map> map2 = new HashMap<>();
        for(String mName : map1.keySet()){
            String movieDay;
            if (openDateMap.containsKey(mName)){
                movieDay = (String) openDateMap.get(mName);
                map2.putAll(map1.get(mName));

                Iterator<Map.Entry<String, Map>> itrMap2 = map2.entrySet().iterator();
                while (itrMap2.hasNext()){
                    if(calDateBetweenAandB(itrMap2.next().getKey(), movieDay) < 0 ||
                            calDateBetweenAandB(itrMap2.next().getKey(), today) < 0){
                        itrMap2.remove();
                    }
                }

            }else {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.add(Calendar.DATE, -week);
                String BackWeekToday = dateFormat.format(calendar1.getTime());
                Iterator<Map.Entry<String, Map>> itrMap2 = map2.entrySet().iterator();
                while (itrMap2.hasNext()){
                    if(calDateBetweenAandB(itrMap2.next().getKey(), BackWeekToday) < 0){
                        itrMap2.remove();
                    }
                }
            }
        }
        //Store >> movie
        Map<String, String> seatsMap = new HashMap<>();
        for (String seats : PATH_SEATS_ARR) {
            seatsMap.put(seats, PATH_SEATS_INFO);
        }

        Map<String, Object> seats = new HashMap<>();
        seats.put(PATH_SEATS, seatsMap);
        seats.put(PATH_RESERVATION, 0);

        Map<String, Object> timeMap = new HashMap<>();
        for (String time : PATH_RUNNINGTIME) {
            timeMap.put(time, seats);
        }

        Map<String, Object> updateDateMap = new HashMap<>();
        if (map2.size() < week) {
            for (int dateAdd = 0; dateAdd <= week - map2.size(); dateAdd++) {
                calendarWeek = Calendar.getInstance();
                calendarWeek.add(Calendar.DATE, dateAdd);
                String date = dateFormat.format(calendarWeek.getTime());
                updateDateMap.put(date, timeMap);
            }
            Log.i("chiffon95", "map1 ; " + map1.toString());
        }
        storeMovieMap.put(PATH_CONVENIENCE_STORE,
                (Map)task.getResult().getData().get(PATH_CONVENIENCE_STORE));
        storeMovieMap.put(PATH_MOVIE, map1);
        Log.i("chiffon95", "storeDateUpdate1212");
        Log.i("chiffon95", "map1 1; " + map1.toString());
        Log.i("chiffon95", "storeMovieMap ; " + storeMovieMap.toString());

        firestore.collection(PATH_STORE).document(store).update(storeMovieMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("chiffon95", "Store date update Success :"
                                + storeMovieMap.toString());
                    }
                });
    }

    private void onStoreMovieTimeMap(Map<String, String> movieNmDate){

        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        HashSet<String> dateSet = new HashSet<>();
        final int week = 7;
        Calendar calendar;
        for(int dateAdd = 0; dateAdd <= week; dateAdd++){
            calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, dateAdd);
            String date = dbFormat.format(calendar.getTime());
            dateSet.add(date);
        }

        //Store >> movie
        Map<String, String> seatsMap = new HashMap<>();
        for(String seats : PATH_SEATS_ARR){
            seatsMap.put(seats, PATH_SEATS_INFO);
        }

        Map<String, Object> seats = new HashMap<>();
        seats.put(PATH_SEATS, seatsMap);
        seats.put(PATH_RESERVATION, 0);

        Map<String, Object> timeMap = new HashMap<>();
        for(String time : PATH_RUNNINGTIME){
            timeMap.put(time, seats);
        }

        Map<String, Object> runningTimeMap;
        Map<String, Object> movieMap = new HashMap<>();
        for(String movie : movieNmDate.keySet()){
            runningTimeMap  = new HashMap<>();
            for(String runningDate : dateSet){
                String date = movieNmDate.get(movie);
                if(calDateBetweenAandB(date, runningDate) <= 0){
                    runningTimeMap.put(runningDate, timeMap);
                }
            }
            movieMap.put(movie, runningTimeMap);
        }
//        movieMap.put(movie, timeMap);
        Map<String, Object> movie = new HashMap<>();
        movie.put(PATH_MOVIE, movieMap);

        //Store >> Convenience
        //Convenience >> parking
        Map<String, Object> parkingMap = new HashMap<>();
        for(String parking : PATH_PARKING_SPACE){
           parkingMap.put(parking, PATH_PARKING_INFO);
        }

        Map<String, Object> parkingFloorMap = new HashMap<>();
        for(String floor : PATH_PARKING_FLOOR){
            parkingFloorMap.put(floor, parkingMap);
        }

        //Convenience >> food >> foods
        Map<String, Object> foodsMap = new HashMap<>();
        for(String foods : PATH_FOODS_MENU){
            foodsMap.put(foods, 0);
        }

        //Convenience >> food >> drink
        Map<String, Object> drinkMap = new HashMap<>();
        for(String drink : PATH_DRINKS_MENU){
            drinkMap.put(drink, 0);
        }

        //Convenience >> food
        Map<String, Object> food = new HashMap<>();
        food.put(PATH_FOODS, foodsMap);
        food.put(PATH_DRINKS, drinkMap);

        //Convenience >> QnA
        Map<String, Object> qna = new HashMap<>();
        qna.put(PATH_Q_N_A_ID,PATH_Q_N_A_BOARD);

        Map<String, Object> convenienceMap = new HashMap<>();
        convenienceMap.put(PATH_PARKING, parkingFloorMap);
        convenienceMap.put(PATH_FOOD, food);
        convenienceMap.put(PATH_Q_N_A, qna);

        Map<String, Object> convenience = new HashMap<>();
        convenience.put(PATH_CONVENIENCE_STORE, convenienceMap);

        //Store >> Movie & Convenience
        for(String storeName : PATH_STORE_ARR){
            firestore.collection(PATH_STORE).document(storeName).set(movie);
            firestore.collection(PATH_STORE).document(storeName).update(convenience);
        }
    }

    //UserInfo
    public void newUserInfo(HashMap<String, Map> userInfoMap){

        HashMap<String, String> information;
        for(String userId : userInfoMap.keySet()){
            information = (HashMap)userInfoMap.get(userId);
            firestore.collection(PATH_USERINFO).document(userId).set(information);
        }
    }

    public static void reservationUpdate(String storeName, String movieName, String date,
                                         String ruuningTime, int reservation, Map<String, String> seats){

        firestore.collection(PATH_STORE).document(storeName).get().addOnCompleteListener(
                task -> {
                    if(task.isSuccessful()){
                        int reserveNum = 0;
                        Map <String, Object> inTimeMap = (Map<String, Object>)
                                (task.getResult().getData().get(PATH_MOVIE));
                        Map <String, Object> writeMap =
                                (Map) (inTimeMap.get(movieName));
                        Map <String, Object> writeMap1 =
                                (Map) (writeMap.get(date));
                        Map <String, Object> writeMap2 =
                                (Map) (writeMap1.get(ruuningTime));
                        reserveNum = Integer.parseInt(writeMap2.get(PATH_RESERVATION).toString());
                        reserveNum += reservation;

                        //덮어쓰기를 위해 빈칸 생성

                        writeMap2.put(PATH_RESERVATION, reserveNum);
                        writeMap2.put(PATH_SEATS, seats);

                        writeMap1.put(ruuningTime, writeMap2);

                        writeMap.put(date, writeMap1);

                        inTimeMap.put(movieName, writeMap);

                        Map<String, Object> map = new HashMap<>();
                        map.put(PATH_MOVIE, inTimeMap);

                        firestore.collection(PATH_STORE).document(storeName).update(map);
                    }
                }
        );
    }

    public static HashMap<String, String> getDBSeats(String storeName, String movieName,
                                                     String date, String runningTime){
        Map<String, String> readMap = null;
        firestore.collection(PATH_STORE).document(storeName).get().addOnCompleteListener(
                task -> {
                    if(task.isSuccessful()){
                        Map <String, String> inTimeMap;
                        inTimeMap = (Map<String, String>)
                                ((Map)((Map)((Map)((Map) ((Map) task.getResult()
                                        .getData().get(PATH_MOVIE))).get(movieName)).get(date))
                                        .get(runningTime)).get(PATH_SEATS);

                        readMap.putAll(inTimeMap);
                    }
                }
        );

        return (HashMap<String, String>) readMap;
    }

    public void getDBMovieLink(){
        Map<String, Object> hashMap = new HashMap<>();
        firestore.collection(PATH_IMAGE).get()
            .addOnCompleteListener(task -> {
                HashMap<Integer, HashMap<String, String>> indexImageMap = new HashMap<>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        for(String key : document.getData().keySet()){
                            hashMap.put(key, document.getData().get(key));
                        }
                    }
                    Log.i("chiffon95", "Result : " +  hashMap.toString());
                    int index = 0;
                    for(String mN : hashMap.keySet()) {
                        HashMap<String, String> imageMap = new HashMap<>();
                        Map moImage = (HashMap) hashMap.get(mN);
                        Log.i("chiffon95", "Link ; " + moImage.get(LINK).toString());

                        String imageUrl = moImage.get(LINK).toString();
                        imageMap.put(mN, imageUrl);
                        indexImageMap.put(index++, imageMap);
                    }
                } else {
                    Log.w("chiffon95", "Error getting documents.", task.getException());
                }
            });
    }

    public static long calDateBetweenAandB(String baseDate, String compareDate) {

        try{ // String Type을 Date Type으로 캐스팅하면서 생기는 예외로 인해 여기서 예외처리 해주지 않으면 컴파일러에서 에러가 발생해서 컴파일을 할 수 없다.
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
            // date1, date2 두 날짜를 parse()를 통해 Date형으로 변환.
            Date FirstDate = format.parse(baseDate);
            Date SecondDate = format.parse(compareDate);

            // Date로 변환된 두 날짜를 계산한 뒤 그 리턴값으로 long type 변수를 초기화 하고 있다.
            // 연산결과 -950400000. long type 으로 return 된다.
            long calDate = FirstDate.getTime() - SecondDate.getTime();

            // Date.getTime() 은 해당날짜를 기준으로1970년 00:00:00 부터 몇 초가 흘렀는지를 반환해준다.
            // 이제 24*60*60*1000(각 시간값에 따른 차이점) 을 나눠주면 일수가 나온다.
            long calDateDays = calDate / ( 24*60*60*1000);

            Log.i("chiffon95","두 날짜의 날짜 차이 >> " + baseDate + " & " + compareDate + " :: "+ calDateDays);
            return calDateDays;
        }
        catch(ParseException e)
        {
            return -1;
        }

    }
}
