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
                tv_myID.setText("????????? ?????????.");

                /* ???????????? ??? pref??? ?????????????????? ID??? ?????? */
                deletePreference();

                Toast.makeText(MainActivity.this, "???????????? ???????????????.", Toast.LENGTH_SHORT).show();
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

        loginDialog.setTitle("?????????");
        loginDialog.setView(loginLayout);
        loginDialog.setNegativeButton("??????", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "???????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
            }
        });

        loginDialog.setPositiveButton("?????????", new DialogInterface.OnClickListener(){
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

    //???????????? ??????
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
        /* ?????? ?????? ?????? */
        idCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = id.getText().toString().trim();
                if(userId.isEmpty()){
                    tvIdCheck.setText("ID??? ????????? ?????????");
                    return;
                }

                firestore.collection(PATH_USERINFO).document(userId).get().addOnCompleteListener(
                        task -> {
                            if (task.getResult().exists()) {
                                tvIdCheck.setText("????????? ??????????????????");
                            } else {
                                tvIdCheck.setText("??????????????? ????????? ??????????????????");
                            }
                        }
                );
            }
        });

        AlertDialog.Builder loginDialog = new AlertDialog.Builder(this);

        loginDialog.setTitle("????????????");
        loginDialog.setView(joinLayout);

        loginDialog.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
            }
        });

        loginDialog.setPositiveButton("????????????", new DialogInterface.OnClickListener() {
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
                        || birth.length() > 7 || address.length() == 0 || tvIdCheck.getText().equals("????????? ??????????????????")) {

                    Toast.makeText(MainActivity.this, "??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();

                } else if(tvIdCheck.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "????????? ??????????????? ????????????.", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(MainActivity.this, "??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                    wantToCloseDialog = true;

                    /* ????????? Map ??????*/
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
                    /* DB??? userID ?????? ??? HashMap ?????? */
                    userID.remove(id.getText().toString());

//                    Log.i("yongu", "ID :" + userID.get(id.getText().toString()));

                    drawer.closeDrawers();
                }
                if(wantToCloseDialog) dialog.dismiss();
            }
        });
    }

    private void loginCheck(AlertDialog dialog, EditText idEt, EditText pwEt, String userId, String userPw){
        /* ????????? ?????? true, ????????? ?????? false */
        if(userId.length() == 0 ){
            Toast.makeText(MainActivity.this, "ID??? ?????? ????????????.", Toast.LENGTH_SHORT).show();
        }else if(userPw.length() == 0){
            Toast.makeText(MainActivity.this, "PW??? ?????? ????????????.", Toast.LENGTH_SHORT).show();
        } else{
            firestore.collection(PATH_USERINFO).document(userId.trim()).get().addOnCompleteListener(
                    new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(!task.getResult().exists()){
                                // ????????? ?????? false
                                Toast.makeText(MainActivity.this, "ID?????? PW??? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                                idEt.setText("");
                                pwEt.setText("");
                            }else {
                                if(task.getResult().getData().get(USER_PW).equals(userPw)) {
                                    Toast.makeText(MainActivity.this, userId + " ??? ???????????????.", Toast.LENGTH_SHORT).show();
                                    setUserID(userId);

                                    btn_join.setVisibility(View.INVISIBLE);
                                    btn_join.setClickable(false);

                                    btn_login.setVisibility(View.INVISIBLE);
                                    btn_login.setClickable(false);

                                    btn_logout.setVisibility(View.VISIBLE);
                                    btn_logout.setClickable(true);

                                    /* ?????? ?????????????????? 'ID' key?????? value editText 'id'??? ?????? */
                                    setPreference(userId);

                                    tv_myID.setText(userId + "??? ???????????????");

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