package com.example.moblebox.ui.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.moblebox.R;

import java.util.ArrayList;

public class MapFragment extends Fragment {

    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_map, container, false);

        ViewPager pager = (ViewPager)view.findViewById(R.id.pager);
        pager.setOffscreenPageLimit(3);

        MoviePagerAdapter adapter = new MoviePagerAdapter(getChildFragmentManager());
        CheonanMapFragment f1 = new CheonanMapFragment();
        SeoulMapFragment f2 = new SeoulMapFragment();
        BusanMapFragment f3 = new BusanMapFragment();
        adapter.additem(f1);
        adapter.additem(f2);
        adapter.additem(f3);
        pager.setAdapter(adapter);

        return view;
    }

    class MoviePagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<Fragment> items = new ArrayList<Fragment>();

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

        public void additem(Fragment item){
            items.add(item);
        }

    }
}