package com.example.moblebox.api;

import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moblebox.mobleboxdb.MobleBoxDB;
import com.example.moblebox.tag.SharedPrefTags;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class SharedPrefCheck extends AppCompatActivity implements SharedPrefTags {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    SharedPreferences prefApi
            = getSharedPreferences("com.example.mgvtestprj.ApiCheck", MODE_PRIVATE);
    Calendar calendar = Calendar.getInstance();
    String today = dateFormat.format(calendar.getTime());

    //MovieApi & NaverApi Pref >> default Setting
    public void setApiPrefToday(){
        setPrefMovieApi();
        setPrefMaverApi();
    }
    //MovieApi Pref
    public String getPrefMovieApi(){
        return prefApi.getString(MOVIEINFOAPI, DEFAULTVAL);
    }
    public void setPrefMovieApi(){
        SharedPreferences.Editor editor = prefApi.edit();
        editor.putString(MOVIEINFOAPI, today);
        editor.apply();
    }
    public void setPrefMovieApi(String movieApiUpdateDay){
        SharedPreferences.Editor editor = prefApi.edit();
        editor.putString(MOVIEINFOAPI, movieApiUpdateDay);
        editor.apply();
    }

    public boolean movieApiCheck(){
        String movieApiRead = prefApi.getString(MOVIEINFOAPI, DEFAULTVAL);
        return MobleBoxDB.calDateBetweenAandB(today, movieApiRead) > 0;
    }


    //NaverApi Pref
    public String getPrefNaverApi(){
        return prefApi.getString(NAVERAPI, DEFAULTVAL);
    }
    public void setPrefMaverApi(){
        SharedPreferences.Editor editor = prefApi.edit();
        editor.putString(NAVERAPI, today);
        editor.apply();
    }
    public void setPrefMaverApi(String naverApiUpdateDay){
        SharedPreferences.Editor editor = prefApi.edit();
        editor.putString(NAVERAPI, naverApiUpdateDay);
        editor.apply();
    }

    public boolean naverApiCheck(String date){
        String naverApiRead = prefApi.getString(NAVERAPI, DEFAULTVAL);
        return MobleBoxDB.calDateBetweenAandB(today, naverApiRead) > 0;
    }
}
