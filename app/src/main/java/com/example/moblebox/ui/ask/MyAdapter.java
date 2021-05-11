package com.example.moblebox.ui.ask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.moblebox.R;

import java.util.ArrayList;

public class MyAdapter extends ArrayAdapter<Question> {

    private ArrayList<Question> al = new ArrayList<>();
    private LayoutInflater inflater;

    public MyAdapter(@NonNull Context context, int resource, ArrayList<Question> al) {
        super(context, resource, al);
        inflater = LayoutInflater.from(context);
        this.al = al;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null) {
            view = inflater.inflate(R.layout.listview_layout, null);
        }

        Question question = al.get(position);
        ((TextView)view.findViewById(R.id.tv_askNum)).setText("NO." + position);
        ((TextView)view.findViewById(R.id.tv_askID)).setText("ID : " + question.id);
        ((TextView)view.findViewById(R.id.tv_askTitle)).setText("제목 : " + question.title);

        return view;
    }
}
