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

public class Fragment_popcorn_3 extends Fragment {

    ImageView[] iv;
    int[] popcornId = {R.id.popcorn_cola, R.id.popcorn_sprite, R.id.popcorn_fanta, R.id.popcorn_icetea, R.id.popcorn_ade};
    int[] popcornTvId = {R.id.tv_popcorn_cola, R.id.tv_popcorn_sprite, R.id.tv_popcorn_fanta, R.id.tv_popcorn_icetea , R.id.tv_popcorn_ade};
    String[] popcornName;
    int[] popcornPrice = {2500, 2500, 2500, 4000, 4000};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.activity_fragment_popcorn_3, container, false);

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
            dial_spinner.setTitle("수량을 선택해주세요");
            dial_spinner.setItems(R.array.개수,new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dial_spinner, int select) {

                    Popcorn_menu popcorn_menu = new Popcorn_menu();
                    EditText editText = new EditText(getActivity());
                    editText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                    int[] edit_num = new int[1];

                    if(select == 9){

                        dial_edit.setTitle("수량을 선택해주세요");
                        dial_edit.setView(editText);
                        dial_edit.setNegativeButton("수량 선택", new DialogInterface.OnClickListener() {
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
                                    Toast.makeText(v.getContext(),"다시 입력하세요.", Toast.LENGTH_SHORT).show();
                                }
                                // 31 이상일때 출력
                                else if(edit_num[0] > 30){
                                    Toast.makeText(v.getContext(),
                                            "             31개이상은 구매할 수 없습니다.\n구매를 원하시면 매장에 문의해주시기 바랍니다.",
                                            Toast.LENGTH_SHORT).show();
                                }else{
                                    dialog.setTitle("장바구니에 담으시겠습니까?");
                                    dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which){

                                            for(int i = 0; i <= al.size(); i++){
                                                if(al.size() == 0){
                                                    if(select==9){
                                                        popcorn_menu.num = edit_num[0]; //메뉴 갯수 지정
                                                    }else{
                                                        popcorn_menu.num = (select+1); //메뉴 갯수 지정
                                                    }
                                                    al.add(popcorn_menu);
                                                    break;
                                                }
                                                else if(al.size() != 0 && i != al.size()){
                                                    if(al.get(i).menu.equals(menu)){
                                                        if(select==9){
                                                            al.get(i).num += edit_num[0]; //메뉴 갯수 지정
                                                        }else{
                                                            al.get(i).num += (select+1);
                                                        }
                                                        break;
                                                    }
                                                    else if(i == al.size()-1) {
                                                        if(select==9){
                                                            popcorn_menu.num = edit_num[0]; //메뉴 갯수 지정
                                                        }else{
                                                            popcorn_menu.num = (select+1); //메뉴 갯수 지정
                                                        }
                                                        al.add(popcorn_menu);
                                                        break;
                                                    }
                                                }
                                            }
                                            writeCheckBox();
                                            if(select==9){
                                                PopcornFragment.price += price*edit_num[0];  //메뉴 갯수 지정
                                            }else{
                                                PopcornFragment.price += price*(select+1);
                                            }
                                            PopcornFragment.tv_pay.setText("결제 예정 금액:" + PopcornFragment.price + "원");
                                        }
                                    });

                                    dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
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

                        dialog.setTitle("장바구니에 담으시겠습니까?");

                        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which){

                                for(int i = 0; i <= al.size(); i++){
                                    if(al.size() == 0){
                                        popcorn_menu.num = (select+1); //메뉴 갯수 지정
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
                                PopcornFragment.tv_pay.setText("결제 예정 금액:" + PopcornFragment.price + "원");
                            }
                        });

                        dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
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