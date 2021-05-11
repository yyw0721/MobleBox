package com.example.moblebox.ui.snackBar;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.moblebox.MainActivity;
import com.example.moblebox.R;
import com.example.moblebox.tag.DBPathTag;

import static android.content.Context.MODE_PRIVATE;
import static com.example.moblebox.ui.snackBar.SnackBarAdapter.setPrice;
import static com.example.moblebox.ui.snackBar.PopcornFragment.al;
import static com.example.moblebox.ui.snackBar.PopcornFragment.price;

public class BasketFragment extends Fragment implements DBPathTag {

    private View view;

    MainActivity activity;

    Button btn_pay;

    static ArrayAdapter<Popcorn_menu> aa;
    static int cb_click = 0;
    static CheckBox all_cb;
    static TextView pay_money;
    static TextView discount_money;
    static TextView final_money;
    static TextView check_remove;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_basket, container, false);

        activity = (MainActivity)getActivity();

        btn_pay = view.findViewById(R.id.btn_pay);
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle("결제 하시겠습니까?");

                dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder dial = new AlertDialog.Builder(getContext());
                        dial.setTitle("결제 되었습니다.");
                        // 결제 된 품목 삭제
                        for(int i = al.size()-1; i >= 0; i--){
                            if(al.get(i).cb == true){
                                removeCheckBox(i);
                                al.remove(i);
                            }
                        }
                        setPrice();

                        dial.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dial, int which) {

                                Navigation.findNavController(view).navigate(R.id.action_nav_basket_to_nav_home);
                            }
                        });
                        dial.show();
                    }
                });
                dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                dialog.show();
            }
        });

        aa = new SnackBarAdapter(getContext(), R.layout.snackbar_layout, al);
        aa.notifyDataSetChanged();

        pay_money = (TextView)view.findViewById(R.id.basket_money);
        discount_money = (TextView)view.findViewById(R.id.basket_discount_money);
        final_money = (TextView)view.findViewById(R.id.basket_final_money);
        ListView lv = (ListView)view.findViewById(R.id.list);
        all_cb = (CheckBox)view.findViewById(R.id.cb_basket);
        check_remove = (TextView)view.findViewById(R.id.check_remove);
        pay_money.setText(price + "원");
        discount_money.setText(0 + "원");
        final_money.setText(price - 0 + "원");
        lv.setAdapter(aa);
        readCheckBox();
        all_cb.setOnClickListener(click_lis);
        all_cb.setOnCheckedChangeListener(lis);


        return view;
    }
    static void checkCount(){
        cb_click = 0;
        for(int i = 0; i<al.size(); i++){
            if(al.get(i).cb == true){
                cb_click++;
            }
        }
        check_remove.setText("선택삭제("+cb_click+")");
    }

    void readCheckBox(){
        SharedPreferences pref = getContext().getSharedPreferences("CheckBox", MODE_PRIVATE);
        boolean cb = pref.getBoolean("check",true);
        checkCount();
        all_cb.setChecked(cb);
    }
    void writeCheckBox(boolean isChecked){
        SharedPreferences pref = getContext().getSharedPreferences("CheckBox", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("check",isChecked);
        editor.commit();
    }
    void removeCheckBox(int position){
        SharedPreferences pref = getContext().getSharedPreferences("popcornList", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(al.get(position).menu);
        editor.commit();
    }
    // 전체선택 클릭 리스너
    CheckBox.OnClickListener click_lis = new CheckBox.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(((CheckBox)v).isChecked() == true){
                for(int i = 0; i< al.size(); i++){
                    al.get(i).cb = true;
                }
            }else if (((CheckBox)v).isChecked() == false){
                for(int i = 0; i< al.size(); i++){
                    al.get(i).cb = false;
                }
            }
            checkCount();
            setPrice();
            aa.notifyDataSetChanged();
        }
    };
    // 전체선택 체인지 리스너
    CompoundButton.OnCheckedChangeListener lis = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            writeCheckBox(isChecked);
        }
    };
}
