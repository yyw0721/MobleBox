package com.example.moblebox.ui.myDetail;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.moblebox.MainActivity;
import com.example.moblebox.R;
import com.example.moblebox.UserInfoTags;
import com.example.moblebox.tag.DBPathTag;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyDetailFragment extends Fragment implements UserInfoTags, DBPathTag {

    private View view;

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();


    private TextView gender, tv_pwCheck;
    private Button btn_changePW, btn_ok, btn_change;
    private EditText name, phoneNum, birth, address, newPw, newPwCheck;

    private String loginID, userName, userGender, userPhoneNum, userBirth, userAddress, userNewPw, userNewPwCheck;

    MainActivity activity;


    ScrollView myScrollView;

    public static MyDetailFragment newInstance(){
        return new MyDetailFragment();
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_mydetail, container, false);

        activity = (MainActivity) getActivity();

        loginID = activity.getPreference();
        
        if(loginID.length() == 0){
            Toast.makeText(getContext(), "로그인 후 이용하실수 있는 메뉴입니다.", Toast.LENGTH_LONG).show();
            activity.onBackPressed();
        }


        btn_change = view.findViewById(R.id.btn_change);
        btn_changePW = (Button)view.findViewById(R.id.btn_changePw);
        btn_ok = view.findViewById(R.id.btn_ok);

        name = view.findViewById(R.id.et_name);
        phoneNum = view.findViewById(R.id.et_phoneNum);
        birth = view.findViewById(R.id.et_birth);
        address = view.findViewById(R.id.et_address);
        newPw = view.findViewById(R.id.et_newPw);
        newPwCheck = view.findViewById(R.id.et_newPwCheck);
        gender = view.findViewById(R.id.gender);

        tv_pwCheck = view.findViewById(R.id.tv_PWcheck);

        if(loginID.length() == 0){                      // 로그인 x
            Log.i("yongu", "null");
            btn_changePW.setClickable(false);
            btn_change.setClickable(false);

        } else {                                        // 로그인 o
            Log.i("yongu", loginID);
            firestore.collection(PATH_USERINFO).document(loginID).get().addOnCompleteListener(
                    task -> {
                        userName = task.getResult().getData().get(USER_NAME).toString().trim();
                        userGender = task.getResult().getData().get(GENDER).toString().trim();
                        userPhoneNum = task.getResult().getData().get(USER_PHONE).toString().trim();
                        userBirth = task.getResult().getData().get(USER_DAY).toString().trim();
                        userAddress = task.getResult().getData().get(USER_ADDRESS).toString().trim();

                        name.setText(userName);
                        gender.setText(userGender);
                        phoneNum.setText(userPhoneNum);
                        birth.setText(userBirth);
                        address.setText(userAddress);
                    }
            );

            btn_change.setOnClickListener(new View.OnClickListener() {          // 내정보 변경
                @Override
                public void onClick(View v) {

                }
            });

            btn_changePW.setOnClickListener(new View.OnClickListener() {        // 비밀번호 변경
                @Override
                public void onClick(View v) {

                    userNewPw = newPw.getText().toString().trim();
                    userNewPwCheck = newPwCheck.getText().toString().trim();

                    Log.i("yongu", "" + userNewPw + ", " + userNewPwCheck);

                    if(userNewPw.length() != 0 && userNewPwCheck.length() != 0){
                        if(!userNewPw.equals(userNewPwCheck)){
                            tv_pwCheck.setText("비밀번호가 일치하지 않습니다.");
                            newPwCheck.setText("");
                        } else {
                            firestore.collection(PATH_USERINFO).document(loginID).get().addOnCompleteListener(
                                    task -> {

                                    }
                            );
                        }

                    } else {
                        Toast.makeText(getContext(), "변경할 비밀번호를 입력해 주세요", Toast.LENGTH_SHORT).show();
                        /* firebase의 비밀번호 수정*/
                    }
                }
            });
        }

        btn_ok.setOnClickListener(new View.OnClickListener() {                  // 확인버튼
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });

        myScrollView = view.findViewById(R.id.scrollView_my);



        return view;
    }
}