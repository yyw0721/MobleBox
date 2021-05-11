package com.example.moblebox.ui.mainHome;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.moblebox.tag.DBPathTag;
import com.example.moblebox.ui.event.EventFragment1;
import com.example.moblebox.ui.event.EventFragment2;
import com.example.moblebox.MainActivity;
import com.example.moblebox.R;
import com.example.moblebox.ui.moviePoster.Movie1PosterFragment;
import com.example.moblebox.ui.moviePoster.Movie2PosterFragment;
import com.example.moblebox.ui.moviePoster.Movie3PosterFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

import static com.example.moblebox.tag.NaverApiTag.LINK;

public class HomeFragment extends Fragment implements DBPathTag {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private View view;
    private SwipeRefreshLayout swipe;

    MainActivity mainActivity;

    ViewPager pager_event, pager_movie;
    CircleIndicator indicator_event, indicator_movie;

    NestedScrollView myScrollView;
    FloatingActionButton fab;

    int currentPage = 0;
    Timer timer1;

    final long DELAY_MS = 500;
    final long PERIOD_MS = 3000;

    class EventPagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<Fragment> items = new ArrayList<>();

        public EventPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return items.get(position);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(Fragment item){
            items.add(item);
        }
    }

    class MoviePagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<Fragment> items = new ArrayList<>();

        public MoviePagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return items.get(position);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(Fragment item){
            items.add(item);
        }
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);

        mainActivity = (MainActivity)getActivity();

        swipe = view.findViewById(R.id.swipeLayout);

        pager_event = (ViewPager)view.findViewById(R.id.pager_event);
        pager_event.setOffscreenPageLimit(3);

        pager_movie = (ViewPager)view.findViewById(R.id.pager_movie);
        pager_movie.setOffscreenPageLimit(3);



        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(){
                    @Override
                    public void run() {

                        swipe.setRefreshing(false);
                    }
                }.start();
            }
        });


        /*---------------------------------EventFragmentPager----------------------------------------*/
        EventPagerAdapter eventPagerAdapter = new EventPagerAdapter(getChildFragmentManager());
        EventFragment1 eventFragment1 = new EventFragment1();
        EventFragment2 eventFragment2 = new EventFragment2();

        eventPagerAdapter.addItem(eventFragment1);
        eventPagerAdapter.addItem(eventFragment2);
        pager_event.setAdapter(eventPagerAdapter);

        indicator_event = view.findViewById(R.id.indicator);
        indicator_event.setViewPager(pager_event);

        Handler handler1 = new Handler();
        Runnable Update1 = new Runnable() {
            @Override
            public void run() {
                if(currentPage == 2){
                    currentPage = 0;
                }
                pager_event.setCurrentItem(currentPage++, true);
            }
        };
        timer1 = new Timer();
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                handler1.post(Update1);
            }
        }, DELAY_MS, PERIOD_MS);
        /*---------------------------------EventFragmentPager----------------------------------------*/

        /*---------------------------------MovieFragmentPager----------------------------------------*/
        MoviePagerAdapter moviePagerAdapter = new MoviePagerAdapter(getChildFragmentManager());
        final int movieRank = 3;
        firestore.collection(PATH_IMAGE).get()
                .addOnCompleteListener(task -> {
                    HashMap<String, String> hashMap;
                    Map<Integer, HashMap<String, String>> indexImageMap = new HashMap<>();
                    if (task.isSuccessful()) {
                        int index = 0; int[] indexArr = new int[movieRank];
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            for(String key : document.getData().keySet()){
                                hashMap = new HashMap<>();
                                Log.i("chiffon95", "image & link : " + document.getData().get(key));
                                hashMap.put(key, (String) ((Map)document.getData().get(key)).get(LINK));
                                indexImageMap.put(index++, hashMap);
                            }
                        }

                        for(int i = 0; i < movieRank; i++){
                            int temp = (int) (Math.random() * indexImageMap.size());
                            indexArr[i] = temp;
                            for(int j = 0; j < i; j++){
                                if(indexArr[i] == indexArr[j] && i != 0){
                                    i--;
                                }
                            }
                        }

                        Log.i("chiffon95", "intArr Test : " + Arrays.toString(indexArr));
                        Log.i("chiffon95", "indexImageMap Test : " + indexImageMap);

                        Movie1PosterFragment movie1PosterFragment
                                = new Movie1PosterFragment(indexImageMap.get(indexArr[0]));
                        Movie2PosterFragment movie2PosterFragment
                                = new Movie2PosterFragment(indexImageMap.get(indexArr[1]));
                        Movie3PosterFragment movie3PosterFragment
                                = new Movie3PosterFragment(indexImageMap.get(indexArr[2]));

                        moviePagerAdapter.addItem(movie1PosterFragment);
                        moviePagerAdapter.addItem(movie2PosterFragment);
                        moviePagerAdapter.addItem(movie3PosterFragment);

                        pager_movie.setAdapter(moviePagerAdapter);

                        indicator_movie = view.findViewById(R.id.indicator_movie);
                        indicator_movie.setViewPager(pager_movie);
                    } else {
                        Log.w("chiffon95", "Error getting documents.", task.getException());
                    }
                });

        /*---------------------------------MovieFragmentPager----------------------------------------*/

        fab = view.findViewById(R.id.fab_home);
        myScrollView = view.findViewById(R.id.scrollView_home);

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
                    Log.i("yongu", "scroll");
                }
            };
            scroll_up.start();
        }
    };

}




