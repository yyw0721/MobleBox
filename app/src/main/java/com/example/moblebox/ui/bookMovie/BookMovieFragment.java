package com.example.moblebox.ui.bookMovie;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.moblebox.MainActivity;
import com.example.moblebox.R;
import com.example.moblebox.api.ApiDailyMovie;
import com.example.moblebox.mobleboxdb.MobleBoxDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static android.content.Context.MODE_PRIVATE;
import static com.example.moblebox.tag.DBPathTag.PATH_IMAGE;
import static com.example.moblebox.tag.DBPathTag.PATH_MOVIE;
import static com.example.moblebox.tag.DBPathTag.PATH_STORE;
import static com.example.moblebox.tag.NaverApiTag.LINK;
import static com.example.moblebox.tag.SharedPrefTags.DEFAULTVAL;
import static com.example.moblebox.tag.SharedPrefTags.MOVIEINFOAPI;
import static com.example.moblebox.tag.SharedPrefTags.PREFNAME;

public class BookMovieFragment extends Fragment {

    // 날짜 설정
    Calendar calendar = Calendar.getInstance();
    Calendar minDate = Calendar.getInstance();
    Calendar maxDate = Calendar.getInstance();
    int tYear = calendar.get(Calendar.YEAR);
    int tMonth = calendar.get(Calendar.MONTH);
    int tDay = calendar.get(Calendar.DAY_OF_MONTH);
    //지점 설정
    TextView tv_place;
    TextView arr_tv_place;
    int[] arrPlaceIds = {R.id.tv_place1, R.id.tv_place2, R.id.tv_place3};
    //시간 설정
    TextView tv_day;
    TextView tv_time;
    TextView arr_tv_time;
    SimpleDateFormat mFormat = new SimpleDateFormat("HH", Locale.KOREA);
    int nowtime = Integer.parseInt(mFormat.format(new Date()));
    int[] arrTimeIds = {R.id.tv_time1, R.id.tv_time2, R.id.tv_time3};
    int[] arrTime = {9, 12, 19};
    //영화 스크롤 설정
    static LinearLayout linearLayout;
    TextView tv_movie;
    static ImageView iv_movie;
    //DB설정
    Map<String, Object> intentMap = new HashMap<>();
    int[] textViewIds = {R.id.tv_movieImage1, R.id.tv_movieImage2, R.id.tv_movieImage3,
            R.id.tv_movieImage4, R.id.tv_movieImage5, R.id.tv_movieImage6, R.id.tv_movieImage7,
            R.id.tv_movieImage8, R.id.tv_movieImage9, R.id.tv_movieImage10};
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    static Map<String, Object> hashMap = new HashMap<>();

    View view;

    Button btn_day;
    Button btn_next;

    FloatingActionButton fab;
    ScrollView myScrollView;

    private String loginID;

    MainActivity activity;



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_bookmovie, container, false);


        activity = (MainActivity) getActivity();
        loginID = activity.getPreference();

        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        SharedPreferences pref = this.getActivity().getSharedPreferences(PREFNAME, MODE_PRIVATE);
        Calendar calendar = Calendar.getInstance();
        String today = sdFormat.format(calendar.getTime());
        String prefMovieInfoApi = pref.getString(MOVIEINFOAPI, DEFAULTVAL);

        if(prefMovieInfoApi.equals(DEFAULTVAL)){
            ApiDailyMovie dailyMovie = new ApiDailyMovie(DEFAULTVAL);
            dailyMovie.start();

            SharedPreferences.Editor editor = pref.edit();
            editor.putString(MOVIEINFOAPI, today);
            editor.apply();

        } else if (!"prefMovieInfoApi".equals(today)) {
            ApiDailyMovie dailyMovie = new ApiDailyMovie(today);
            dailyMovie.start();

            SharedPreferences.Editor editor = pref.edit();
            editor.putString(MOVIEINFOAPI, today);
            editor.apply();
        }

        tv_place = view.findViewById(R.id.tv_place);
        tv_day = view.findViewById(R.id.tv_day);
        tv_time = view.findViewById(R.id.tv_time);
        tv_movie = view.findViewById(R.id.tv_movie);
        linearLayout = view.findViewById(R.id.linearLayout);

        //지점 id불러오기
        for (int arrPlaceId : arrPlaceIds) {
            arr_tv_place = view.findViewById(arrPlaceId);
            arr_tv_place.setOnClickListener(onClick_place);
        }

        firestore.collection(PATH_IMAGE)
                .get()
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
                        ImageLoadTask imageTask = new ImageLoadTask();
                        imageTask.execute(indexImageMap);
                    } else {
                        Log.w("chiffon95", "Error getting documents.", task.getException());
                    }
                });

        btn_day = view.findViewById(R.id.btn_day);
        btn_day.setOnClickListener(onClick_day);

        btn_next = view.findViewById(R.id.btn_reservation);
        btn_next.setOnClickListener(onClick_next);




        fab = view.findViewById(R.id.fab_movie);

        myScrollView = view.findViewById(R.id.scrollView_movie);
        fab.setOnClickListener(fab_click);

        return view;

    }

    View.OnClickListener fab_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Thread scroll_up = new Thread("scroll thread"){
                @Override
                public void run() {
                    myScrollView.fullScroll(ScrollView.FOCUS_UP);
                }
            };
            scroll_up.start();
        }
    };

    View.OnClickListener onClick_place = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            tv_place.setText(((TextView) v).getText());
        }
    };

    // 영화 선택하기
    final View.OnClickListener onClick_movie = v -> {
        String movieNm = ((TextView)v).getText().toString();
        TextView tv = view.findViewById(R.id.tv_movie);
        tv.setText(movieNm);
    };

    //날짜 선택하기
    View.OnClickListener onClick_day = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
            Calendar calendarDate = Calendar.getInstance();
            HashSet<String> dateHashSet = new HashSet<>();
            TextView textView = view.findViewById(R.id.tv_movie);
            String movieTv = textView.getText().toString().trim();

            if (tv_place.getText().equals("극장을 선택 하세요")){
                Toast.makeText(getContext(), "극장을 먼저 선택해주세요", Toast.LENGTH_SHORT).show();
                return;
            }else if (textView.getText().equals("영화를 선택 하세요")){
                Toast.makeText(getContext(), "영화를 먼저 선택해주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            firestore.collection(PATH_STORE).document(tv_place.getText().toString()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Map<String, Object> hashMap = (Map) task.getResult().getData().get(PATH_MOVIE);

                            Map<String, Object> dateMap = (Map)hashMap.get(movieTv);
                            if(dateMap == null || dateMap.size() == 0){
                                Toast.makeText(getContext(), "미개봉 영화입니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            intentMap.putAll(dateMap);
                            for(String date : dateMap.keySet()){
                                dateHashSet.add(date);
                            }


                            if(dateHashSet.size() != 0){
                                String tempMin = ""; String tempMax = ""; String temp;
                                for(String dateMovieInfo : dateHashSet){

                                    if (tempMin.equals("")) tempMin = dateMovieInfo;
                                    if (tempMax.equals("")) tempMax = dateMovieInfo;

                                    temp = dateMovieInfo;
                                    if (MobleBoxDB.calDateBetweenAandB(dateMovieInfo, tempMin) < 0){
                                        if(MobleBoxDB.calDateBetweenAandB(dateFormat.format(calendarDate.getTime()), tempMin) < 0){
                                            tempMin = dateFormat.format(calendarDate.getTime());
                                        }else{
                                            tempMin = temp;
                                        }
                                    }
                                    if (MobleBoxDB.calDateBetweenAandB(dateMovieInfo,tempMax) > 0){
                                        tempMax = temp;
                                    }
                                }
                                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                                        (DatePicker view1, int year, int month, int dayOfMonth) -> {
                                            tv_day.setText(year + "." + (month + 1) + "." + dayOfMonth);

                                            //시간 id불러오기
                                            tv_time.setText("시간을 선택 하세요");
                                            if (tDay == dayOfMonth) {
                                                for (int i = 0; i < arrTimeIds.length; i++) {
                                                    arr_tv_time = view.findViewById(arrTimeIds[i]);
                                                    if (nowtime >= arrTime[i]) {
                                                        arr_tv_time.setVisibility(View.INVISIBLE);
                                                    }
                                                    arr_tv_time.setOnClickListener(onClick_time);
                                                }
                                            } else {
                                                for (int arrTimeId : arrTimeIds) {
                                                    arr_tv_time = view.findViewById(arrTimeId);
                                                    arr_tv_time.setVisibility(View.VISIBLE);
                                                    arr_tv_time.setOnClickListener(onClick_time);
                                                }
                                            }
                                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
//                            tempMin.get(Calendar.YEAR), tempMin.get(Calendar.MONTH),
//                            tempMin.get(Calendar.DAY_OF_MONTH)
                                );
                                Date date = null;
                                try { date = dateFormat.parse(tempMin); }
                                catch (ParseException e) { e.printStackTrace(); }
                                calendarDate.setTime(date);
                                minDate.set(calendarDate.get(Calendar.YEAR), calendarDate.get(Calendar.MONTH),
                                        calendarDate.get(Calendar.DAY_OF_MONTH));
                                datePickerDialog.getDatePicker().setMinDate(minDate.getTime().getTime());

                                try { date = dateFormat.parse(tempMax); }
                                catch (ParseException e) { e.printStackTrace(); }
                                calendarDate.setTime(date);
                                maxDate.set(calendarDate.get(Calendar.YEAR), calendarDate.get(Calendar.MONTH),
                                        calendarDate.get(Calendar.DAY_OF_MONTH));
                                datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

                                datePickerDialog.show();
                            }
                        }
                    });
        }
    };

    //시간 선택하기
    View.OnClickListener onClick_time = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            tv_time.setText(((TextView) v).getText());
        }
    };

    //다음버튼튼 클릭
   View.OnClickListener onClick_next = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String place = tv_place.getText().toString();
            String movie = tv_movie.getText().toString();
            String day = tv_day.getText().toString();
            String time = tv_time.getText().toString();


            if (place.equals("극장을 선택 하세요")) {
                Toast.makeText(getContext(), "극장을 선택 하세요", Toast.LENGTH_SHORT).show();
            } else if (movie.equals("영화를 선택 하세요")) {
                Toast.makeText(getContext(), "영화를 선택 하세요", Toast.LENGTH_SHORT).show();
            } else if (day.equals("날짜를 선택 하세요")) {
                Toast.makeText(getContext(), "날짜를 선택 하세요", Toast.LENGTH_SHORT).show();
            } else if (time.equals("시간을 선택 하세요")) {
                Toast.makeText(getContext(), "시간을 선택 하세요", Toast.LENGTH_SHORT).show();
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("place", place);
                bundle.putString("movie", movie);
                bundle.putString("day", day);
                bundle.putString("time", time);
                bundle.putSerializable("movieMap", (Serializable) intentMap);
                Navigation.findNavController(view).navigate(R.id.action_nav_bookMovie_to_nav_reservationMovie, bundle);
            }
        }
    };

    public class ImageLoadTask extends AsyncTask<HashMap<Integer, HashMap<String, String>>,
            ArrayList<String>, String> {

        int index = 0;
        TextView textView;
        ArrayList<String> arrayList;

        @Override
        protected void onPreExecute() {
            arrayList = new ArrayList<String>();
            index = 0;
        }

        @SafeVarargs
        @Override
        protected final String doInBackground(HashMap<Integer, HashMap<String, String>>... hashMaps) {
            try {
                for (int i = 0; i < hashMaps[0].size(); i++) {
                    for (String mN : hashMaps[0].get(i).keySet()) {
                        arrayList = new ArrayList<String>();
                        String imageUrl = hashMaps[0].get(i).get(mN);
                        Document doc = Jsoup.connect(imageUrl).get();
                        Elements elements = doc.select(".poster a img[src *= ?type=m203_290_2]");
                        Element element = elements.first();
                        Log.i("chiffon95", element.toString());
                        String url = element.attr("src");
                        arrayList.add(String.valueOf(i));
                        arrayList.add(mN);
                        arrayList.add(url);
                        publishProgress(arrayList);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }

        @SafeVarargs
        @Override
        protected final void onProgressUpdate(ArrayList<String>... values) {
            Glide.with(BookMovieFragment.this).load(values[0].get(2))
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Drawable> transition) {
                            textView= view.findViewById(textViewIds[Integer.parseInt(values[0].get(0))]);
                            textView.setText(values[0].get(1));
                            textView.setTextColor(Color.TRANSPARENT);
                            textView.setBackground(resource);

                            textView.setOnClickListener(onClick_movie);
                        }
                    });
        }
    }
}