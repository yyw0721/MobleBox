package com.example.moblebox.ui.moviePoster;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.moblebox.MainActivity;
import com.example.moblebox.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Movie3PosterFragment extends Fragment {

    private View view;
    MainActivity activity;
    TextView img_movie3;

    Map<String, String> movieMap;
    public Movie3PosterFragment(Map<String, String> map){
        this.movieMap = map;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_movie3_poster, container, false);

        activity = (MainActivity)getActivity();
        img_movie3 = (TextView)view.findViewById(R.id.tv_movie3_poster);

        for(String movieNm : movieMap.keySet()){
            ArrayList<String> arrayList = new ArrayList<>();
            String crawlUrl = movieMap.get(movieNm);
            new Thread(){
                Handler handler = new Handler(Looper.getMainLooper()){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);

                        if (msg.what == 10){
                            Glide.with(Movie3PosterFragment.this).load(arrayList.get(1))
                                    .into(new SimpleTarget<Drawable>() {
                                        @Override
                                        public void onResourceReady(
                                                @NonNull Drawable resource,
                                                @Nullable com.bumptech.glide.request.transition.
                                                        Transition<? super Drawable> transition) {
                                            img_movie3.setBackground(resource);
                                        }
                                    });
                            img_movie3.setText(String.valueOf(arrayList.get(0)));
                            img_movie3.setTextColor(Color.TRANSPARENT);
                        }
                    }
                };
                @Override
                public void run() {

                    String url;
                    for(String movieName : movieMap.keySet()){
                        String imageUrl = movieMap.get(movieName);
                        Document doc = null;
                        try {
                            doc = Jsoup.connect(imageUrl).get();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Elements elements = doc.select(".poster a img[src *= ?type=m203_290_2]");
                        Element element = elements.first();
                        Log.i("chiffon95", "element.toString() : " + element.toString());
                        url = element.attr("src");
                        arrayList.add(movieName);
                        arrayList.add(url);

                        handler.sendEmptyMessage(10);
                    }
                }
            }.start();
        }

        img_movie3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putSerializable("movieLink",(Serializable) movieMap);
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_nav_abo, bundle);

            }
        });

        return view;
    }
}