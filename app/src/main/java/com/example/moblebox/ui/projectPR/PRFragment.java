package com.example.moblebox.ui.projectPR;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moblebox.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PRFragment extends Fragment {

    private View view;


    ScrollView myScrollView;
    FloatingActionButton fab;

    TextView tv4;
    TextView tv0_1;
    TextView tv_brand;

    ImageView img_2;
    ImageView img_3;
    ImageView img_4;

    TextView tv_about_1;
    TextView tv_about_2;
    TextView tv_about_3;
    TextView tv_about_4;
    Button btn_introduction, btn_about_1, btn_history_1;

    public static PRFragment newInstance(){
        return new PRFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        
        view = inflater.inflate(R.layout.fragment_pr, container, false);

        fab = view.findViewById(R.id.fab_pr);
        myScrollView = view.findViewById(R.id.scrollView_pr);

        fab.setOnClickListener(fab_click);



        btn_introduction = view.findViewById(R.id.btn_introduction);
        btn_about_1 = view.findViewById(R.id.btn_about_1);
        btn_history_1 = view.findViewById(R.id.btn_history_1);

        btn_introduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (img_2.getVisibility() == v.VISIBLE) {
                    img_2.setVisibility(v.GONE);
                } else {
                    img_2.setVisibility(View.VISIBLE);
                }
                if (img_3.getVisibility() == v.VISIBLE) {
                    img_3.setVisibility(v.GONE);
                } else {
                    img_3.setVisibility(View.VISIBLE);
                }
                if (img_4.getVisibility() == v.VISIBLE) {
                    img_4.setVisibility(v.GONE);
                } else {
                    img_4.setVisibility(View.VISIBLE);
                }
                if (tv4.getVisibility() == v.VISIBLE) {
                    tv4.setVisibility(v.GONE);
                } else {
                    tv4.setVisibility(View.VISIBLE);
                }
                if (tv0_1.getVisibility() == v.VISIBLE) {
                    tv0_1.setVisibility(v.GONE);
                } else {
                    tv0_1.setVisibility(View.VISIBLE);
                }
            }
        });

        btn_about_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_about_1.getVisibility() == v.VISIBLE) {
                    tv_about_1.setVisibility(v.GONE);
                } else {
                    tv_about_1.setVisibility(View.VISIBLE);
                }
                if (tv_about_2.getVisibility() == v.VISIBLE) {
                    tv_about_2.setVisibility(v.GONE);
                } else {
                    tv_about_2.setVisibility(View.VISIBLE);
                }
                if (tv_about_3.getVisibility() == v.VISIBLE) {
                    tv_about_3.setVisibility(v.GONE);
                } else {
                    tv_about_3.setVisibility(View.VISIBLE);
                }
                if (tv_about_4.getVisibility() == v.VISIBLE) {
                    tv_about_4.setVisibility(v.GONE);
                } else {
                    tv_about_4.setVisibility(View.VISIBLE);
                }
            }
        });

        btn_history_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_brand.getVisibility() == v.VISIBLE) {
                    tv_brand.setVisibility(v.GONE);
                } else {
                    tv_brand.setVisibility(View.VISIBLE);
                }
            }
        });


        img_2 = (ImageView)view.findViewById(R.id.img_2);
        img_3 = (ImageView)view.findViewById(R.id.img_3);
        img_4 = (ImageView)view.findViewById(R.id.img_4);
        tv4 = (TextView)view.findViewById(R.id.tv4);
        tv0_1 = (TextView)view.findViewById(R.id.tv0_1);

        img_2.setVisibility(View.GONE);
        img_3.setVisibility(View.GONE);
        img_4.setVisibility(View.GONE);
        tv4.setVisibility(View.GONE);
        tv0_1.setVisibility(View.GONE);



        tv_about_1 = (TextView)view.findViewById(R.id.tv_about_1);
        tv_about_2 = (TextView)view.findViewById(R.id.tv_about_2);
        tv_about_3 = (TextView)view.findViewById(R.id.tv_about_3);
        tv_about_4 = (TextView)view.findViewById(R.id.tv_about_4);


        tv_about_1.setVisibility(View.GONE);
        tv_about_2.setVisibility(View.GONE);
        tv_about_3.setVisibility(View.GONE);
        tv_about_4.setVisibility(View.GONE);


        tv_brand = (TextView)view.findViewById(R.id.tv_brand);
        tv_brand.setVisibility(View.GONE);


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
}




