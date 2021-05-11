package com.example.moblebox.ui.moviePoster;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.example.moblebox.ui.bookMovie.BookMovieFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Movie1PosterFragment extends Fragment {

    private View view;
    TextView img_movie1;
    MainActivity activity;

    HashMap<String, String> movieMap;
    public Movie1PosterFragment(HashMap<String, String> map){
        this.movieMap = map;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_movie1_poster, container, false);

        img_movie1 = (TextView)view.findViewById(R.id.tv_movie1_poster);
        activity = (MainActivity)getActivity();

        ImageLoadTask imageLoadTask = new ImageLoadTask();
        imageLoadTask.execute(movieMap);

        img_movie1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i("yongu", "click abo poster");

                Bundle bundle = new Bundle();
                bundle.putSerializable("movieLink",(Serializable) movieMap);
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_nav_abo, bundle);
            }
        });
        return view;
    }

    public class ImageLoadTask extends AsyncTask<HashMap<String, String>,
            ArrayList<String>, String> {

        int index = 0;
        ArrayList<String> arrayList;

        @Override
        protected void onPreExecute() {
            arrayList = new ArrayList<String>();
            index = 0;
        }

        @SafeVarargs
        @Override
        protected final String doInBackground(HashMap<String, String>... hashMaps) {
                for (int i = 0; i < hashMaps[0].size(); i++) {
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
                        onProgressUpdate(arrayList);
                    }
                }
            return null;
        }

        @SafeVarargs
        @Override
        protected final void onProgressUpdate(ArrayList<String>... values) {
            Log.i("chiffon95", "arraylist : " + values[0] + "length : " + values[0].size());
            Log.i("chiffon95", "values[0].indexof(0) : " + values[0].get(0)
                    + ", values[0].indexof(1) : " + values[0].get(1));
            Glide.with(Movie1PosterFragment.this).load(values[0].get(1))
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Drawable> transition) {
                            img_movie1.setText(String.valueOf(values[0].get(0)));
                            img_movie1.setTextColor(Color.TRANSPARENT);
                            img_movie1.setBackground(resource);
                        }
                    });
        }
    }
}