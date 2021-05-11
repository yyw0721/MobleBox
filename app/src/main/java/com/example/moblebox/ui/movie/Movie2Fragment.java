package com.example.moblebox.ui.movie;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.moblebox.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Movie2Fragment extends Fragment {

    HashMap<String, String> movieMap;

    ImageView iv_movie2;
    TextView tv_movieNm;
    TextView tv_subNm;
    TextView tv_year;
    TextView tv_plot;
    TextView tv_view1;

    Button btn_open;
    Button btn_close;

    private View view;

    FloatingActionButton fab;
    ScrollView myScrollView;

    public static Movie2Fragment newInstance(){
        return new Movie2Fragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_movie2, container, false);

        iv_movie2 = (ImageView)view.findViewById(R.id.movie2_iv_movie2);
        tv_movieNm = (TextView)view.findViewById(R.id.movie2_tv_0);
        tv_subNm = (TextView)view.findViewById(R.id.movie2_tv_1);
        tv_year = (TextView)view.findViewById(R.id.movie2_tv_1_sub);
        tv_plot = (TextView)view.findViewById(R.id.movie2_tv_3);
        tv_view1 = (TextView)view.findViewById(R.id.movie2_tv_view1);
        tv_view1.setVisibility(View.GONE);

        btn_close = view.findViewById(R.id.btn_close);
        btn_close.setVisibility(View.GONE);

        btn_open = (Button)view.findViewById(R.id.movie2_btn_add);

        fab = view.findViewById(R.id.fab_thefather);

        myScrollView = view.findViewById(R.id.scrollView_thefather);
        fab.setOnClickListener(fab_click);

        movieMap = (HashMap<String, String>) getArguments().getSerializable("movieLink");

        for(String movieNm : movieMap.keySet()){
            String crawlUrl = movieMap.get(movieNm);
            ArrayList<String> arrayList = new ArrayList<>();
            tv_movieNm.setText(movieNm);

            new Thread(){

                Handler handler = new Handler(Looper.getMainLooper()){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        Log.e("chiffon95", arrayList.toString());
                        if(msg.what == 10){
                            tv_subNm.setText(arrayList.get(1));
                            tv_year.setText(arrayList.get(2));
                            tv_plot.setText(arrayList.get(3));

                            Glide.with(Movie2Fragment.this).load(arrayList.get(0))
                                    .into(iv_movie2);
                        }
                    }
                };
                @Override
                public void run() {
                    Document doc = null;
                    try {
                        doc = Jsoup.connect(crawlUrl).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //이미지 크롤링
                    Elements elements1 = doc.select(".poster a img[src *= ?type=m203_290_2]");
                    Element element1 = elements1.first();
                    String url = element1.attr("src");
                    Log.i("chiffon95", "element.toString() : " + url);
                    arrayList.add(url);

                    //외국 이름 & 년도 크롤링
                    Elements elements2 = doc.select(".h_movie2");
                    Element element2 = elements2.first();
                    StringBuffer stringBuffer1 = new StringBuffer(element2.attr("title"));
                    StringBuffer stringBuffer2 = new StringBuffer(element2.attr("title"));
                    int index = stringBuffer1.indexOf(",");

                    String subNm; String year;
                    if(!String.valueOf(stringBuffer1).equals("") && stringBuffer1.length() != 4){
                        String movieNameString = new String(stringBuffer1.replace(index -1, stringBuffer1.length(), ""));
                        String movieYearString = new String(stringBuffer2.replace(0, index + 1, ""));
                        subNm = movieNameString.trim();
                        year = movieYearString.trim();
                        arrayList.add(subNm);
                        arrayList.add(year);
                    }else {
                        String movieYearString = new String(stringBuffer2.replace(0, index + 1, ""));
                        subNm = "";
                        year = movieYearString.trim();
                        arrayList.add(subNm);
                        arrayList.add(year);
                    }

                    //줄거리 크롤링
                    Elements elements3 = doc.select(".con_tx");
                    Element element3 = elements3.first();
                    String con_tx = element3.text();
                    con_tx = con_tx.replace(". ", ". \n");
                    arrayList.add(con_tx);
                    Message msg = handler.obtainMessage();
                    handler.sendEmptyMessage(10);
                }
            }.start();
//            MovieInfoTask movieInfoTask = new MovieInfoTask();
//            movieInfoTask.execute(strings);
        }

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_view1.getVisibility() == v.GONE) {
                    tv_view1.setVisibility(v.GONE);
                } else {
                    tv_view1.setVisibility(View.GONE);
                    btn_open.setVisibility(v.VISIBLE);

                }
                if (btn_close.getVisibility() == v.GONE) {
                    btn_close.setVisibility(v.GONE);
                }else{
                    btn_close.setVisibility(v.GONE);
                }
            }
        });

        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_view1.getVisibility() == v.VISIBLE) {
                    tv_view1.setVisibility(v.GONE);
                } else {
                    tv_view1.setVisibility(View.VISIBLE);
                    btn_close.setVisibility(v.VISIBLE);
                }
                if (btn_open.getVisibility() == v.VISIBLE) {
                    btn_open.setVisibility(v.GONE);
                } else {
                    btn_open.setVisibility(View.VISIBLE);
                }
            }
        });
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

