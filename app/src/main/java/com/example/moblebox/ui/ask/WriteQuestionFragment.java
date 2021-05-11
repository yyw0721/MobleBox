package com.example.moblebox.ui.ask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.moblebox.MainActivity;
import com.example.moblebox.R;

public class WriteQuestionFragment extends Fragment {

    private View view;
    private EditText et_title;
    private EditText et_contents;
    private Button btn_ok;
    private Button btn_cancle;

    MainActivity activity;

    private String loginID;
    private Question quest;

    public static WriteQuestionFragment newInstance(){
        return new WriteQuestionFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_writequestion, container, false);
        activity = (MainActivity)getActivity();

        loginID = activity.getPreference();


        et_title = view.findViewById(R.id.et_QAtitle);
        et_contents = view.findViewById(R.id.et_QAcontents);

        btn_cancle = view.findViewById(R.id.btn_QAcancle);
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        /* 글 쓰기 버튼 클릭시 askfragment에 전달 */
        btn_ok = view.findViewById(R.id.btn_QAwrite);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginID.length() != 0) {
                    if (et_title.length() == 0) {
                        et_title.requestFocus();
                        Toast.makeText(getContext(), "제목을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    } else if (et_contents.length() == 0) {
                        et_contents.requestFocus();
                        Toast.makeText(getContext(), "내용을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    } else {
                        Fragment fragment = new AskFragment();
                        Bundle bundle = new Bundle(2);
                        bundle.putString("title", et_title.getText().toString());
                        bundle.putString("contents", et_contents.getText().toString());

                        Navigation.findNavController(view).navigate(R.id.action_nav_QA_to_nav_ask, bundle);
                    }
                } else {
                    Toast.makeText(getContext(), "로그인 후 작성이 가능합니다.", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    private void showDialog(){
        AlertDialog.Builder goBackDialog = new AlertDialog.Builder(getContext());

        goBackDialog.setMessage("종료하시겠습니까?\n확인을 누르면 종료되고 내용은 저장되지 않습니다.");

        goBackDialog.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        goBackDialog.setNegativeButton("종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.onBackPressed();
            }
        });

        AlertDialog dialog = goBackDialog.create();
        dialog.show();
    }
}


