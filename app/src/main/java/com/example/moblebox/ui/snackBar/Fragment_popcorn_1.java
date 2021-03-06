package com.example.moblebox.ui.snackBar;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.moblebox.R;

import static android.content.Context.MODE_PRIVATE;
import static com.example.moblebox.ui.snackBar.PopcornFragment.al;

public class Fragment_popcorn_1 extends Fragment {

    ImageView[] iv;
    int[] popcornId = {R.id.popcorn_mgv_combo, R.id.popcorn_double_combo, R.id.popcorn_large_combo, R.id.popcorn_small_set};
    int[] popcornTvId = {R.id.tv_popcorn_mgv_combo, R.id.tv_popcorn_double_combo, R.id.tv_popcorn_large_combo, R.id.tv_popcorn_small_set};
    String[] popcornName;
    int[] popcornPrice = {9000, 12000, 14000, 6500};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.activity_fragment_popcorn_1, container, false);

        iv = new ImageView[popcornId.length];
        for(int i = 0; i<popcornId.length; i++){
            iv[i] = (ImageView)rootView.findViewById(popcornId[i]);
            iv[i].setTag(i);
            iv[i].setOnClickListener(lis);
        }
        popcornName = new String[popcornTvId.length];
        for(int i = 0; i<popcornTvId.length; i++){
            popcornName[i] = ((TextView)rootView.findViewById(popcornTvId[i])).getText().toString();
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    public void writeCheckBox(){
        SharedPreferences pref = getActivity().getSharedPreferences("popcornList", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        for(int i = 0; i<al.size(); i++){
            String RankKey = String.valueOf(al.get(i).menu);
            String arrValue = al.get(i).menu+ "@" + al.get(i).price + "@" + al.get(i).num+ "@" + al.get(i).cb;
            editor.putString(RankKey, arrValue);
            editor.commit();
        }
    }

    View.OnClickListener lis = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            AlertDialog.Builder dial_spinner = new AlertDialog.Builder(getActivity());
            AlertDialog.Builder dial_edit = new AlertDialog.Builder(getActivity());
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dial_spinner.setTitle("????????? ??????????????????");
            dial_spinner.setItems(R.array.??????,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dial_spinner, int select) {

                    Popcorn_menu popcorn_menu = new Popcorn_menu();
                    EditText editText = new EditText(getActivity());
                    editText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                    int[] edit_num = new int[1];

                    if(select == 9){

                        dial_edit.setTitle("????????? ??????????????????");
                        dial_edit.setView(editText);
                        dial_edit.setNegativeButton("?????? ??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dial_edit, int which) {
                                String menu = popcornName[(int) v.getTag()];
                                int price = popcornPrice[(int) v.getTag()];

                                popcorn_menu.menu = menu;
                                popcorn_menu.price = price;
                                popcorn_menu.cb = true;

                                try {
                                    edit_num[0] = Integer.parseInt(editText.getText().toString()+"");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if(edit_num[0] == 0 || edit_num.toString().equals("")){
                                    Toast.makeText(v.getContext(),"?????? ???????????????.", Toast.LENGTH_SHORT).show();
                                }
                                // 31 ???????????? ??????
                                else if(edit_num[0] > 30){
                                    Toast.makeText(v.getContext(),
                                            "             31???????????? ????????? ??? ????????????.\n????????? ???????????? ????????? ?????????????????? ????????????.",
                                            Toast.LENGTH_SHORT).show();
                                }else{
                                    dialog.setTitle("??????????????? ??????????????????????");
                                    dialog.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which){

                                            for(int i = 0; i <= al.size(); i++){
                                                if(al.size() == 0){
                                                    if(select==9){
                                                        popcorn_menu.num = edit_num[0]; //?????? ?????? ??????
                                                    }else{
                                                        popcorn_menu.num = (select+1); //?????? ?????? ??????
                                                    }
                                                    al.add(popcorn_menu);
                                                    break;
                                                }
                                                else if(al.size() != 0 && i != al.size()){
                                                    if(al.get(i).menu.equals(menu)){
                                                        if(select==9){
                                                            al.get(i).num += edit_num[0]; //?????? ?????? ??????
                                                        }else{
                                                            al.get(i).num += (select+1);
                                                        }
                                                        break;
                                                    }
                                                    else if(i == al.size()-1) {
                                                        if(select==9){
                                                            popcorn_menu.num = edit_num[0]; //?????? ?????? ??????
                                                        }else{
                                                            popcorn_menu.num = (select+1); //?????? ?????? ??????
                                                        }
                                                        al.add(popcorn_menu);
                                                        break;
                                                    }
                                                }
                                            }
                                            writeCheckBox();
                                            if(select==9){
                                                PopcornFragment.price += price*edit_num[0];  //?????? ?????? ??????
                                            }else{
                                                PopcornFragment.price += price*(select+1);
                                            }
                                            PopcornFragment.tv_pay.setText("?????? ?????? ??????:" + PopcornFragment.price + "???");
                                        }
                                    });

                                    dialog.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    dialog.show();
                                }
                            }
                        });
                        dial_edit.show();
                    }else{
                        String menu = popcornName[(int) v.getTag()];
                        int price = popcornPrice[(int) v.getTag()];

                        popcorn_menu.menu = menu;
                        popcorn_menu.price = price;
                        popcorn_menu.cb = true;

                        dialog.setTitle("??????????????? ??????????????????????");

                        dialog.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which){

                                for(int i = 0; i <= al.size(); i++){
                                    if(al.size() == 0){
                                        popcorn_menu.num = (select+1); //?????? ?????? ??????
                                        al.add(popcorn_menu);
                                        break;
                                    }
                                    else if(al.size() != 0 && i != al.size()){
                                        if(al.get(i).menu.equals(menu)){
                                            al.get(i).num += (select+1);
                                            break;
                                        }
                                        else if(i == al.size()-1) {
                                            popcorn_menu.num = (select+1);
                                            al.add(popcorn_menu);
                                            break;
                                        }
                                    }
                                }
                                writeCheckBox();
                                PopcornFragment.price += price*(select+1);
                                PopcornFragment.tv_pay.setText("?????? ?????? ??????:" + PopcornFragment.price + "???");
                            }
                        });

                        dialog.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        dialog.show();
                    }
                }
            });
            dial_spinner.show();
        }
    };
}
