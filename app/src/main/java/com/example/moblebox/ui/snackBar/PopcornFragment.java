package com.example.moblebox.ui.snackBar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.example.moblebox.MainActivity;
import com.example.moblebox.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.example.moblebox.ui.snackBar.SnackBarAdapter.setPrice;

public class PopcornFragment extends Fragment {

    private View view;

    Button btn_popcorn_pay;

    static int price = 0;
    static TextView tv_pay;
    private final int menu_combo = 1;
    private final int menu_popcorn = 2;
    private final int menu_drink = 3;
    private final int menu_snack = 4;
    static ArrayList<Popcorn_menu> al = new ArrayList<>();

    private String loginID;

    MainActivity activity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_popcorn, container, false);

        activity = (MainActivity) getActivity();
        loginID = activity.getPreference();

        if(loginID.length() == 0){
            Toast.makeText(getContext(), "로그인 후 이용하실수 있는 메뉴입니다.", Toast.LENGTH_LONG).show();
            activity.onBackPressed();
        }

        tv_pay = (TextView)view.findViewById(R.id.tv_popcorn_pay);
        tv_pay.setText("결제 예정 금액:"+price+"원");

        btn_popcorn_pay = view.findViewById(R.id.btn_popcorn_pay);
        btn_popcorn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_nav_snackBar_to_nav_basket);
            }
        });

        readCheckBox();

        view.findViewById(R.id.menu_combo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentView(menu_combo);
            }
        });
        view.findViewById(R.id.menu_popcorn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentView(menu_popcorn);
            }
        });
        FragmentView(menu_popcorn);
        view.findViewById(R.id.menu_drink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentView(menu_drink);
            }
        });
        view.findViewById(R.id.menu_snack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentView(menu_snack);
            }
        });
        FragmentView(menu_combo);

        return view;
    }

    public void readCheckBox() {
        al.clear();
        SharedPreferences pref = getContext().getSharedPreferences("popcornList", MODE_PRIVATE);

        HashMap<String, ?> values = (HashMap<String, ?>) pref.getAll();
        Iterator<String> iterator = sortByValue(values).iterator();

        for(int i = 0; i<values.size(); i++) {
            String key = iterator.next();
            if(key != null );{
                try{
                    String arrInfoData = (String)values.get(key);
                    String[] arrinfo = arrInfoData.split("@");

                    Popcorn_menu popcorn_menu = new Popcorn_menu();

                    popcorn_menu.menu = String.valueOf(arrinfo[0]);
                    popcorn_menu.price = Integer.parseInt(arrinfo[1]);
                    popcorn_menu.num = Integer.parseInt(arrinfo[2]);
                    popcorn_menu.cb = Boolean.parseBoolean(arrinfo[3]);

                    al.add(popcorn_menu);
                    setPrice();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //정렬
    public static List sortByValue(final Map values){
        List<String> list = new ArrayList();
        list.addAll(values.keySet());

        Collections.sort(list, new Comparator() {

            public int compare(Object o1, Object o2) {
                Object v1 = values.get(o1);
                Object v2 = values.get(o2);

                return ((Comparable) v1).compareTo(v2);
            }
        });
        return list;
    }

    private void FragmentView(int fragment) {

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        switch (fragment) {
            case 1:
                // 첫번 째 프래그먼트 호출
                Fragment_popcorn_1 fragment1 = new Fragment_popcorn_1();
                transaction.replace(R.id.pager_pop, fragment1);
                transaction.commit();
                break;
            case 2:
                // 두번 째 프래그먼트 호출
                Fragment_popcorn_2 fragment2 = new Fragment_popcorn_2();
                transaction.replace(R.id.pager_pop, fragment2);
                transaction.commit();
                break;
            case 3:
                // 첫번 째 프래그먼트 호출
                Fragment_popcorn_3 fragment3 = new Fragment_popcorn_3();
                transaction.replace(R.id.pager_pop, fragment3);
                transaction.commit();
                break;
            case 4:
                // 두번 째 프래그먼트 호출
                Fragment_popcorn_4 fragment4 = new Fragment_popcorn_4();
                transaction.replace(R.id.pager_pop, fragment4);
                transaction.commit();
                break;
        }
    }//FragmentView

}//Popcorn