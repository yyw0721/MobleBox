package com.example.moblebox.ui.snackBar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.moblebox.R;

import java.util.ArrayList;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.example.moblebox.ui.snackBar.BasketFragment.aa;
import static com.example.moblebox.ui.snackBar.BasketFragment.all_cb;
import static com.example.moblebox.ui.snackBar.BasketFragment.cb_click;
import static com.example.moblebox.ui.snackBar.BasketFragment.checkCount;
import static com.example.moblebox.ui.snackBar.BasketFragment.check_remove;
import static com.example.moblebox.ui.snackBar.BasketFragment.discount_money;
import static com.example.moblebox.ui.snackBar.BasketFragment.final_money;
import static com.example.moblebox.ui.snackBar.BasketFragment.pay_money;
import static com.example.moblebox.ui.snackBar.PopcornFragment.al;
import static com.example.moblebox.ui.snackBar.PopcornFragment.price;
import static com.example.moblebox.ui.snackBar.PopcornFragment.tv_pay;

public class SnackBarAdapter extends ArrayAdapter<Popcorn_menu>{

    LayoutInflater inflater;
    String[] menuName = {"MGV 콤보", "더블콤보", "라지콤보", "스몰 세트", "팝콘(L)", "팝콘(M)", "콜라", "스프라이트", "환타", "아이스티", "에이드",
            "핫도그", "즉석구이오징어", "칠리치즈나초", "치즈볼"};
    Context context;
    CheckBox arr_cb;

    int[] popcornImgId ={R.drawable.popcorn_mgv_combo, R.drawable.popcorn_double_combo, R.drawable.popcorn_large_combo, R.drawable.popcorn_small_set,
            R.drawable.popcorn_large, R.drawable.popcorn_m,
            R.drawable.popcorn_cola, R.drawable.popcorn_sprite, R.drawable.popcorn_fanta, R.drawable.popcorn_icetea, R.drawable.popcorn_ade,
            R.drawable.popcorn_hatdog, R.drawable.popcorn_ojingo, R.drawable.popcorn_naco, R.drawable.popcorn_cheeseball};

    public SnackBarAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Popcorn_menu> al) {
        super(context, resource, al);
        this.context = context;
        inflater = LayoutInflater.from(context);
        PopcornFragment.al = al;
    }
    //SharedPreferences 체크박스 쓰기
    public void writeCheckBox(){
        SharedPreferences pref = context.getSharedPreferences("popcornList", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        for(int i = 0; i<al.size(); i++){
            String RankKey = String.valueOf(al.get(i).menu);
            String arrValue = al.get(i).menu+ "@" + al.get(i).price + "@" + al.get(i).num+ "@" + al.get(i).cb;
            editor.putString(RankKey, arrValue);
            editor.commit();
        }
    }
    //SharedPreferences 체크박스 지우기
    void removeCheckBox(int position){
        SharedPreferences pref = context.getSharedPreferences("popcornList", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(al.get(position).menu);
        editor.commit();
    }
    // 가격 저장
    static void setPrice(){
        price = 0;
        for(int i = 0; i<al.size(); i++){
            if(al.get(i).cb == true){
                price += (al.get(i).price*al.get(i).num);}
        }
        tv_pay.setText("결제 예정 금액:" + price + "원");
        pay_money.setText(price + "원");
        discount_money.setText(0 + "원");
        final_money.setText(price - 0 + "원");
    }

    // 리스트뷰 드레그 시 체크초기화 방지
    public boolean alChecked (int position){
        return al.get(position).cb;
    }

    private static class ViewHolder {
        private Spinner num_spinner;
        private EditText num_edit;
        private Button num_btn;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if(view == null){
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.snackbar_layout,null);

            // getView 1번만 실행
            holder.num_spinner = (Spinner)view.findViewById(R.id.num_spinner);
            holder.num_edit = (EditText)view.findViewById(R.id.num_edit);
            holder.num_btn = (Button)view.findViewById(R.id.num_btn);

            if(al.get(position).num > 9){
                holder.num_spinner.setVisibility(View.INVISIBLE);
                holder.num_edit.setVisibility(View.VISIBLE);
                holder.num_btn.setVisibility(View.VISIBLE);
                holder.num_edit.setText(al.get(position).num+"");
                holder.num_spinner.setSelection(9);
            }else {
                holder.num_spinner.setVisibility(View.VISIBLE);
                holder.num_edit.setVisibility(View.INVISIBLE);
                holder.num_btn.setVisibility(View.INVISIBLE);
                holder.num_spinner.setSelection(al.get(position).num-1);
            }
        }

        // 팝콘 이미지 가져오기
        int pic = 0;
        for(int i = 0; i<popcornImgId.length; i++){
            if(al.get(position).menu.equals(menuName[i])){
                pic = popcornImgId[i];
            }
        }

        TextView tv_price = (TextView)view.findViewById(R.id.price);
        tv_price.setText(al.get(position).price*al.get(position).num+"원");
        ImageView iv = (ImageView)view.findViewById(R.id.iv);
        iv.setImageResource(pic);
        arr_cb = (CheckBox)view.findViewById(R.id.cb);
        TextView delete = (TextView)view.findViewById(R.id.delete);
        Spinner num_spinner = (Spinner)view.findViewById(R.id.num_spinner);
        EditText num_edit = (EditText)view.findViewById(R.id.num_edit);
        Button num_btn = (Button)view.findViewById(R.id.num_btn);

//         수량 선택시 spinner or editText 선택
//        if(al.get(position).num >= 10){
//            num_spinner.setVisibility(View.INVISIBLE);
//            num_edit.setVisibility(View.VISIBLE);
//            num_btn.setVisibility(View.VISIBLE);
//            num_edit.setText(al.get(position).num+"");
//            num_edit.setSelection(num_edit.length());
//        }else{
//            num_spinner.setVisibility(View.VISIBLE);
//            num_edit.setVisibility(View.INVISIBLE);
//            num_btn.setVisibility(View.INVISIBLE);
//            num_spinner.setSelection(al.get(position).num-1);
//        }

        //CheckBox 세팅
        arr_cb.setChecked(alChecked(position));

        if(num_spinner.getVisibility() == View.VISIBLE) {
            num_spinner.setSelection(al.get(position).num - 1);
        }else{
//            num_edit.setText(al.get(position).num+"");
        }
        // CheckBox 선택
        arr_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_click = 0;
                if(((CheckBox)v).isChecked() == true){
                    al.get(position).cb = true;

                }
                else if(((CheckBox)v).isChecked() == false){
                    al.get(position).cb = false;
                }

                //전체 체크 설정
                for(int i = 0; i<al.size(); i++){
                    if(al.get(i).cb == true){
                        cb_click++;
                        if(cb_click == al.size()){all_cb.setChecked(true);}
                    }
                    else{all_cb.setChecked(false);}
                }
                check_remove.setText("선택삭제("+cb_click+")");
                setPrice();
                writeCheckBox();
            }
        });
        writeCheckBox();

        if(al.get(position).num < 10){

            //스피너 개수 선택
            num_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
                    if(pos != 9){
                        num_spinner.setVisibility(View.VISIBLE);
                        num_edit.setVisibility(View.INVISIBLE);
                        num_btn.setVisibility(View.INVISIBLE);
                        al.get(position).num = Integer.parseInt(parent.getItemAtPosition(pos)+"");
                        num_edit.setText(al.get(position).num+"");
                        num_edit.setSelection(num_edit.length());
                    }
                    else if(pos == 9){
                        num_spinner.setVisibility(View.INVISIBLE);
                        num_edit.setVisibility(View.VISIBLE);
                        num_btn.setVisibility(View.VISIBLE);
//                        num_edit.requestFocus();
//                        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
//                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        num_edit.setText("");
                        num_edit.setSelection(num_edit.length());
                    }
                    setPrice();
                    tv_price.setText(al.get(position).price*al.get(position).num+"원");
                    writeCheckBox();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }else{
            num_edit.setText(al.get(position).num+"");
            num_edit.setSelection(num_edit.length());
            tv_price.setText(al.get(position).price*al.get(position).num+"원");
        }
        // 수량 입력
        num_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] edit_num = {0};

                try {
                    edit_num[0] = Integer.parseInt(num_edit.getText().toString()+"");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                al.get(position).num = edit_num[0];
                // 0 또는 null일때 출력
                if(edit_num[0] == 0 || edit_num.toString().equals("")){
                    Toast.makeText(v.getContext(),"다시 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                // 31 이상일때 출력
                else if(al.get(position).num > 30){
                    Toast.makeText(v.getContext(),
                            "             31개이상은 구매할 수 없습니다.\n구매를 원하시면 매장에 문의해주시기 바랍니다.",
                            Toast.LENGTH_SHORT).show();
                }
                // 10미만일때
                else if(al.get(position).num < 10){
                    num_spinner.setVisibility(View.VISIBLE);
                    num_edit.setVisibility(View.INVISIBLE);
                    num_btn.setVisibility(View.INVISIBLE);
                    num_spinner.setSelection(al.get(position).num-1);

                    setPrice();
                    tv_price.setText(al.get(position).price*al.get(position).num+"원");
                    writeCheckBox();
                    Toast.makeText(v.getContext(),"변경되었습니다.", Toast.LENGTH_SHORT).show();
//                    al.get(position).num = Integer.parseInt(num_edit.getText().toString());
                    InputMethodManager imm = (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);
                }else {
                    setPrice();
                    tv_price.setText(al.get(position).price * al.get(position).num + "원");
                    writeCheckBox();
                    Toast.makeText(v.getContext(), "변경되었습니다.", Toast.LENGTH_SHORT).show();
//                    al.get(position).num = Integer.parseInt(num_edit.getText().toString());
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
            }
        });

        // 선택 삭제
        check_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
                dialog.setTitle("선택한 상품을 삭제 하시겠습니까?");

                dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        for(int i = al.size()-1; i >= 0; i--){
                            if(al.get(i).cb == true){
                                removeCheckBox(i);
                                al.remove(i);
                            }
                        }
                        aa.notifyDataSetChanged();
                        checkCount();
                        setPrice();

                        AlertDialog.Builder dial = new AlertDialog.Builder(v.getContext());
                        dial.setTitle("삭제 되었습니다.");

                        dial.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dial, int which) {

                            }
                        });
                        dial.show();
                    }
                });
                dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
                writeCheckBox();
            }
        });
        // 개별 삭제
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
                dialog.setTitle("상품을 삭제 하시겠습니까?");

                dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        
                        removeCheckBox(position);
                        al.remove(position);
                        checkCount();
                        setPrice();
                        for(int i = 0;i<al.size(); i++){
                            if(al.get(i).num >= 10){
                                num_spinner.setVisibility(View.INVISIBLE);
                                num_edit.setVisibility(View.VISIBLE);
                                num_btn.setVisibility(View.VISIBLE);
//                                num_spinner.setSelection(9);
                                num_edit.setText(al.get(i).num+"");
                                num_edit.setSelection(num_edit.length());
                            }else{
                                num_spinner.setVisibility(View.VISIBLE);
                                num_edit.setVisibility(View.INVISIBLE);
                                num_btn.setVisibility(View.INVISIBLE);
                                num_spinner.setSelection(al.get(i).num-1);
                            }
                        }
                        aa.notifyDataSetChanged();
                        AlertDialog.Builder dial = new AlertDialog.Builder(v.getContext());
                        dial.setTitle("삭제 되었습니다.");

                        dial.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dial, int which) {

                            }
                        });
                        dial.show();
                    }
                });
                dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
                writeCheckBox();
            }
        });
        ((TextView)view.findViewById(R.id.menu)).setText(al.get(position).menu);

        return view;
    }
}