package com.example.moblebox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moblebox.mobleboxdb.MobleBoxDB;
import com.example.moblebox.tag.DBPathTag;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements UserInfoTags, DBPathTag {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private View header;
    private Button btn_login;
    private Button btn_join;
    private Button btn_logout;
    private TextView tv_myID;
    private TextView tv_myData;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    public final String PREFERENCE = "Login_User_data";


    String gender;


    private String login;

    private DrawerLayout drawer;
    static private AppBarConfiguration mAppBarConfiguration;

    Map<String, Map> userID= new HashMap<>();
    Map<String, String> userData = new HashMap<>();

    public String getUserID(){
        return this.login;
    }

    public void setUserID(String id){
        this.login = id;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deletePreference();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close);
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        mAppBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).setDrawerLayout(drawer).build();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        header = navigationView.getHeaderView(0);

        btn_login = header.findViewById(R.id.btn_login);
        btn_join = header.findViewById(R.id.btn_join);
        btn_logout = header.findViewById(R.id.btn_logout);
        tv_myID = header.findViewById(R.id.tv_userName);
        tv_myData = header.findViewById(R.id.tv_mydata);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginDialog();
            }
        });

        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showJoinDialog();
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserID(null);
                btn_logout.setVisibility(View.INVISIBLE);
                btn_logout.setClickable(false);

                btn_login.setVisibility(View.VISIBLE);
                btn_login.setClickable(true);

                btn_join.setVisibility(View.VISIBLE);
                btn_join.setClickable(true);

                tv_myData.setText("");
                tv_myID.setText("로그인 하세요.");

                /* 로그아웃 시 pref에 저장되어있는 ID값 삭제 */
                deletePreference();

                Toast.makeText(MainActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                drawer.closeDrawers();
            }
        });
    } // onCreate end

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

 
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    private void showLoginDialog(){
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout loginLayout = (RelativeLayout)vi.inflate(R.layout.dialog_login, null);

        final EditText id = loginLayout.findViewById(R.id.username);
        final EditText pw = loginLayout.findViewById(R.id.password);

        AlertDialog.Builder loginDialog = new AlertDialog.Builder(this);

        loginDialog.setTitle("로그인");
        loginDialog.setView(loginLayout);
        loginDialog.setNegativeButton("취소", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "로그인을 취소하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        loginDialog.setPositiveButton("로그인", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = loginDialog.create();
        dialog.setCancelable(false);


        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginCheck(dialog, id, pw, id.getText().toString(), pw.getText().toString());
            }
        });
    }

    //회원가입 파트
    private void showJoinDialog() {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout joinLayout = (RelativeLayout) vi.inflate(R.layout.dialog_join, null);

        final EditText id = joinLayout.findViewById(R.id.userID);
        final EditText pw = joinLayout.findViewById(R.id.password);
        final EditText name = joinLayout.findViewById(R.id.username);
        final EditText phoneNum = joinLayout.findViewById(R.id.phoneNum);
        final EditText birth = joinLayout.findViewById(R.id.birth);
        final EditText address = joinLayout.findViewById(R.id.address);
        final TextView tvIdCheck = joinLayout.findViewById(R.id.tv_idCheck);
        final Spinner spinner = joinLayout.findViewById(R.id.spinner);

        final Button idCheck = joinLayout.findViewById(R.id.btn_idCheck);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ///////////////////////////////////////////////////
        /* 중복 체크 버튼 */
        idCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = id.getText().toString().trim();
                if(userId.isEmpty()){
                    tvIdCheck.setText("ID를 입력해 주세요");
                    return;
                }

                firestore.collection(PATH_USERINFO).document(userId).get().addOnCompleteListener(
                        task -> {
                            if (task.getResult().exists()) {
                                tvIdCheck.setText("중복된 아이디입니다");
                            } else {
                                tvIdCheck.setText("회원가입이 가능한 아이디입니다");
                            }
                        }
                );
            }
        });

        AlertDialog.Builder loginDialog = new AlertDialog.Builder(this);

        loginDialog.setTitle("회원가입");
        loginDialog.setView(joinLayout);

        loginDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "회원가입을 취소하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        loginDialog.setPositiveButton("회원가입", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        AlertDialog dialog = loginDialog.create();
        dialog.setCancelable(false);
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean wantToCloseDialog = false;
                if(id.length() == 0 || pw.length() == 0 || name.length() == 0
                        || phoneNum.length() == 0 || birth.length() == 0
                        || birth.length() > 7 || address.length() == 0 || tvIdCheck.getText().equals("중복된 아이디입니다")) {

                    Toast.makeText(MainActivity.this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();

                } else if(tvIdCheck.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "아이디 중복체크를 해주세요.", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(MainActivity.this, "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                    wantToCloseDialog = true;

                    /* 이곳에 Map 작성*/
                    /*Map<String, Map> userID= new HashMap<>();*/
                    /*Map<String, String> userData = new HashMap<>();*/

                    userData.put(USER_ID, id.getText().toString());
                    userData.put(USER_PW, pw.getText().toString());
                    userData.put(USER_NAME, name.getText().toString());
                    userData.put(GENDER, gender);
                    userData.put(USER_PHONE, phoneNum.getText().toString());
                    userData.put(USER_DAY, birth.getText().toString());
                    userData.put(USER_ADDRESS, address.getText().toString());

                    userID.put(id.getText().toString(), userData);

                    MobleBoxDB db = new MobleBoxDB();
                    db.newUserInfo((HashMap<String, Map>) userID);
                    /* DB에 userID 전송 후 HashMap 삭제 */
                    userID.remove(id.getText().toString());

//                    Log.i("yongu", "ID :" + userID.get(id.getText().toString()));

                    drawer.closeDrawers();
                }
                if(wantToCloseDialog) dialog.dismiss();
            }
        });
    }

    private void loginCheck(AlertDialog dialog, EditText idEt, EditText pwEt, String userId, String userPw){
        /* 로그인 성공 true, 로그인 실패 false */
        if(userId.length() == 0 ){
            Toast.makeText(MainActivity.this, "ID를 입력 해주세요.", Toast.LENGTH_SHORT).show();
        }else if(userPw.length() == 0){
            Toast.makeText(MainActivity.this, "PW를 입력 해주세요.", Toast.LENGTH_SHORT).show();
        } else{
            firestore.collection(PATH_USERINFO).document(userId.trim()).get().addOnCompleteListener(
                    new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(!task.getResult().exists()){
                                // 로그인 실패 false
                                Toast.makeText(MainActivity.this, "ID또는 PW를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                                idEt.setText("");
                                pwEt.setText("");
                            }else {
                                if(task.getResult().getData().get(USER_PW).equals(userPw)) {
                                    Toast.makeText(MainActivity.this, userId + " 님 환영합니다.", Toast.LENGTH_SHORT).show();
                                    setUserID(userId);

                                    btn_join.setVisibility(View.INVISIBLE);
                                    btn_join.setClickable(false);

                                    btn_login.setVisibility(View.INVISIBLE);
                                    btn_login.setClickable(false);

                                    btn_logout.setVisibility(View.VISIBLE);
                                    btn_logout.setClickable(true);

                                    /* 공유 프레프런스에 'ID' key값에 value editText 'id'값 저장 */
                                    setPreference(userId);

                                    tv_myID.setText(userId + "님 안녕하세요");

                                    drawer.closeDrawers();
                                    dialog.dismiss();
                                }
                            }
                        }
                    }
            );
        }
    }
    public void setPreference(String value){
        pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        editor = pref.edit();
        editor.putString("ID", value);
        editor.commit();
    }

    public String getPreference(){
        pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        String userID = pref.getString("ID", "");
        return userID;
    }

    public void deletePreference(){
        pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        editor = pref.edit();
        editor.remove("ID");
        editor.commit();
    }
}