package com.example.moblebox.ui.bookMovie;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.moblebox.MainActivity;
import com.example.moblebox.tag.DBPathTag;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moblebox.R;
import com.example.moblebox.tag.DBPathTag;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import pl.polidea.view.ZoomView;


public class MovieReservationSeatFragment extends Fragment implements DBPathTag, Serializable {
    SimpleDateFormat sdformat = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
    SimpleDateFormat dbformat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    Calendar calendar = Calendar.getInstance();
    int user = 0;
    String place;
    String movie;
    String day;
    String time;
    Map<String, Object> movieMap;
    ArrayList<String> seat_num;
    TextView seat_temp_reset;
    TextView seat_used;
    TextView seat_click;
    TextView[] arr_seat_temp = new TextView[PATH_SEATS_ARR.length * 12];
    TextView userNum;
    TextView price;
    char[] setseat = new char[PATH_SEATS_ARR.length * 12];
    HashMap<String, char[]> map_seat = new HashMap<>();

    private Button btn_cancle;
    private Button btn_ok;
    private Button btn_reset;
    private MainActivity activity;

    View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_moviereservationseat, container, false);

        btn_cancle = view.findViewById(R.id.btn_cancel);
        btn_ok = view.findViewById(R.id.btn_ok);
        btn_reset = view.findViewById(R.id.btn_reset);

        activity = (MainActivity)getActivity();
        /*데이터를 받아오는 부분*/
        if(getArguments() != null){
            place = getArguments().getString("place");
            movie = getArguments().getString("movie");
            day = getArguments().getString("day");
            time = getArguments().getString("time");
            movieMap = (Map)getArguments().getSerializable("movieMap");

        }

        zoom();
        seat_num = new ArrayList<>();

        seat_click = view.findViewById(R.id.seat_click);
        seat_used = view.findViewById(R.id.seat_used);
        seat_temp_reset = view.findViewById(R.id.seat_temp_reset);
        userNum = view.findViewById(R.id.tv_userNum);
        price = view.findViewById(R.id.tv_userPrice);

        //좌석 id 호출
        for(int i = 0; i <setseat.length; i++) {
            String TextViewId = "seat_temp_" + PATH_SEATS_ARR[i/12]+(i%12+1);
            arr_seat_temp[i] = view.findViewById(getResources().getIdentifier(TextViewId, "id", getContext().getPackageName()));
            arr_seat_temp[i].setText(PATH_SEATS_ARR[i/12]+""+(i%12+1));
            arr_seat_temp[i].setTag(i);
            arr_seat_temp[i].setOnClickListener(onClick);
        }

        Date date = null;
        try {
            date = sdformat.parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        String dbformatDay = dbformat.format(calendar.getTime());

//        Log.i("chiffon95", "movieMap ; " + movieMap.toString());

        Map<String, String> seat1 = (Map)((Map)(((Map)movieMap.get(dbformatDay)).get(time))).get(PATH_SEATS);
        for(String key : seat1.keySet()){
            String value = seat1.get(key);
            map_seat.put(key, value.toCharArray());
        }

        //hashmap 불러오기
        for(int i = 0; i<PATH_SEATS_ARR.length;i++){
            char[] arr = map_seat.get(PATH_SEATS_ARR[i]);
            for(int j = 0; j<12;j++) {
                setseat[(i * 12)+j] = arr[j];
            }
        }
        Log.i("chiffon95", "seatSeat : " + String.valueOf(setseat));

        for(int i = 0; i <setseat.length; i++) {
            if (setseat[i] == '1') {
                arr_seat_temp[i].setBackground(seat_used.getBackground());
            }
        }

        btn_reset.setOnClickListener(onClick_reset);
        btn_cancle.setOnClickListener(onClick_cancle);
        btn_ok.setOnClickListener(onClick_ok);

        return view;
    }

    public void zoom(){
        //줌 인 아웃
        View v = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.zoom_item, null, false);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        ZoomView zoomView = new ZoomView(getContext());
        zoomView.addView(v);
        zoomView.setLayoutParams(layoutParams);
        zoomView.setMaxZoom(2f); // 줌 Max 배율 설정  1f 로 설정하면 줌 안됩니다.

        RelativeLayout container = view.findViewById(R.id.container);
        container.addView(zoomView);
    }


    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView t = (TextView) v;

            for(TextView tempText : arr_seat_temp)
            {
                // 클릭된 버튼을 찾았으면
                if(tempText == t)
                {
                    // 위에서 저장한 버튼의 포지션을 태그로 가져옴
                    int position = (int) v.getTag();

                    if(arr_seat_temp[position].getBackground() == seat_used.getBackground()){
                        //클릭한 좌석이 예약된 좌석이면 아무런 이벤트를 실행 하지 않음
                    }
                    else if(setseat[position] == '0') {
                        setseat[position] = '1';
                        arr_seat_temp[position].setBackground(seat_click.getBackground());
                        user++;
                        userNum.setText(user +"명");
                        price.setText(user*10000 +"원");
                        seat_num.add(arr_seat_temp[position].getText().toString());
                        Collections.sort(seat_num);

                    }else if(setseat[position] == '1') {
                        setseat[position] = '0';
                        arr_seat_temp[position].setBackground(seat_temp_reset.getBackground());
                        user--;
                        userNum.setText(user +"명");
                        price.setText(user*10000 +"원");
                        seat_num.remove(arr_seat_temp[position].getText().toString());
                    }
                }
            }
        }
    };

    View.OnClickListener onClick_ok = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (user == 0) {
                Toast.makeText(getContext(),"좌석을 선택 하세요", Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle("진행 하시겠습니까?");

                dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Bundle bundle = new Bundle();
                        bundle.putString("place", place);
                        bundle.putString("movie", movie);
                        bundle.putString("day", day);
                        bundle.putString("time", time);
                        bundle.putCharArray("seat", setseat);
                        bundle.putInt("user", user);
                        bundle.putStringArrayList("seat_num", seat_num);
                        Navigation.findNavController(view).navigate(R.id.action_nav_reservationMovie_to_nav_payMovie, bundle);

//                        Intent i = new Intent(MovieReservationSeat.this, MoviePay.class);
//                        i.putExtra("place", place + "")
//                                .putExtra("movie", movie + "")
//                                .putExtra("day", day + "")
//                                .putExtra("time", time + "")
//                                .putExtra("seat", setseat)
//                                .putExtra("user", user)
//                                .putExtra("seat_num", seat_num);
//
//                        startActivity(i);
                    }
                });
                dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }
        }
    };

    View.OnClickListener onClick_cancle = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle("취소 하시겠습니까?");

            dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    activity.onBackPressed();
                }
            });
            dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dialog.show();
        }
    };

    View.OnClickListener onClick_reset = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for(int i = 0; i<arr_seat_temp.length;i++){
                if(arr_seat_temp[i].getBackground() != seat_used.getBackground()){
                    arr_seat_temp[i].setBackground(seat_temp_reset.getBackground());
                }
                user = 0;
                setseat[i] = '0';
                userNum.setText(user +"명");
                price.setText(user*10000 +"원");
                seat_num.clear();
            }
        }
    };
}
