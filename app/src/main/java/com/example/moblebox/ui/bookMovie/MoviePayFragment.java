package com.example.moblebox.ui.bookMovie;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.moblebox.MainActivity;
import com.example.moblebox.R;
import com.example.moblebox.mobleboxdb.MobleBoxDB;
import com.example.moblebox.tag.DBPathTag;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MoviePayFragment extends Fragment implements DBPathTag {

    private View view;

    MainActivity activity;

    TextView pay_info;
    TextView pay_money;
    TextView tv_pay;
    TextView movie_name;
    ImageView[] iv;
    Button btn_pay;
    int[] arrIvid = {R.id.iv_pay1, R.id.iv_pay2, R.id.iv_pay3};
    int[] arrIv = {R.drawable.pay1,R.drawable.pay2,R.drawable.pay3};
    int[] arrIv_ok = {R.drawable.pay1_ok,R.drawable.pay2_ok,R.drawable.pay3_ok};
    int user;
    String place;
    String movie;
    String day;
    String time;
    ArrayList<String> seat_num;
    char[] setseat = new char[120];
    HashMap<String, String> map_seat = new HashMap<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_moviepay, container, false);

        btn_pay = view.findViewById(R.id.btn_pay);

        if(getArguments() != null){
            place = getArguments().getString("place");
            movie = getArguments().getString("movie");
            day = getArguments().getString("day");
            time = getArguments().getString("time");
            seat_num = getArguments().getStringArrayList("seat_num");
            setseat = getArguments().getCharArray("seat");
            user = getArguments().getInt("user", -1);


        }

        activity = (MainActivity)getActivity();

        movie_name = view.findViewById(R.id.tv_movie_name);
        movie_name.setText(movie);
        pay_info = view.findViewById(R.id.tv_movie_info);
        pay_info.setText(day+" "+time+"\n"+place+ seat_num);
        pay_money = view.findViewById(R.id.tv_movie_info_money);
        pay_money.setText(user*10000+"원");
        tv_pay = view.findViewById(R.id.tv_pay);
        tv_pay.setText("최종결제금액:"+user*10000+"원");

        iv = new ImageView[3];
        for(int i = 0; i<arrIvid.length ; i++){
            iv[i] = view.findViewById(arrIvid[i]);
            iv[i].setTag(i);
            iv[i].setOnClickListener(lis);
        }

        btn_pay.setOnClickListener(onClick_pay);

        return view;
    }

    View.OnClickListener lis = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            for(int i = 0; i<arrIvid.length ; i++){
                if((int) v.getTag() == i) {
                    iv[(int) v.getTag()].setImageResource(arrIv_ok[(int) v.getTag()]);
                }
                if((int) v.getTag() != i){
                    iv[i].setImageResource(arrIv[i]);
                }
            }
        }
    };

    View.OnClickListener onClick_pay = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SimpleDateFormat reserveFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle("결제 하시겠습니까?");

            dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //ex) hashmap 저장
                    for (int i = 0; i < PATH_SEATS_ARR.length; i++) {
                        char[] hash_seat = new char[12];
                        for (int j = 0; j < 12; j++) {
                            hash_seat[j] = setseat[(i * 12) + j];
                        }
                        String hashSeat = new String(hash_seat);
                        map_seat.put(PATH_SEATS_ARR[i], hashSeat);
                    }

                    //DB에 저장 >> 형식 변경
                    Date date = null;
                    try { date = reserveFormat.parse(day); }
                    catch (ParseException e) { e.printStackTrace(); }
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    String dbformatDay = dbFormat.format(calendar.getTime());

                    AlertDialog.Builder dial = new AlertDialog.Builder(getContext());
                    dial.setTitle("결제 되었습니다.");

                    dial.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dial, int which) {

                            MobleBoxDB.reservationUpdate(place, movie, dbformatDay, time, user, map_seat);
                            Navigation.findNavController(view).navigate(R.id.action_nav_payMovie_to_nav_home);
                        }
                    });
                    dial.show();
                }
            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            dialog.show();
        }
    };
}
