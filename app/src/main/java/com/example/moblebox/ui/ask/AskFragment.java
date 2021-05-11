package com.example.moblebox.ui.ask;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.moblebox.MainActivity;
import com.example.moblebox.R;

import java.util.ArrayList;

public class AskFragment extends Fragment {

    private View view;

    private Button btn_write;

    ArrayList<Question> al = new ArrayList<>();
    ListView listView;
    MyAdapter myAdapter;

    private String loginID;
    private String title, contents;
    private Question quest;

    MainActivity activity;

    public static AskFragment newInstance(){
        return new AskFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_ask, container, false);

        activity = (MainActivity) getActivity();
        loginID = activity.getPreference();

        myAdapter = new MyAdapter(requireContext(), R.layout.listview_layout, al);

        listView = view.findViewById(R.id.listView);
        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "Clicked : " + position, Toast.LENGTH_SHORT).show();
            }
        });

        btn_write = view.findViewById(R.id.btn_write);
        btn_write.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_QA));


        setArrayList();

        return view;
    }

    public void setArrayList(){
        if(getArguments() != null){
            title = getArguments().getString("title");
            contents = getArguments().getString("contents");

            quest = new Question(loginID, title, contents);
            al.add(quest);
            myAdapter.notifyDataSetChanged();
        }
    }
}