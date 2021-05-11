package com.example.moblebox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.example.moblebox.api.ApiDailyMovie;
import com.example.moblebox.api.ApiNaverMovie;
import com.example.moblebox.tag.SharedPrefTags;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SplashActivity extends AppCompatActivity implements SharedPrefTags {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        CallAsyncTask();
    }

    private void CallAsyncTask(){
        SplashActivity.ApiAsyncTask apiAsyncTask = new SplashActivity.ApiAsyncTask();
        apiAsyncTask.execute();
    }

    private class ApiAsyncTask extends AsyncTask<Integer, Integer, String>{
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        SharedPreferences pref = getSharedPreferences(PREFNAME, MODE_PRIVATE);
        Calendar calendar = Calendar.getInstance();
        String today = sdFormat.format(calendar.getTime());
        String prefMovieInfoApi = pref.getString(MOVIEINFOAPI, DEFAULTVAL);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(Integer... integers) {
            if(prefMovieInfoApi.equals(DEFAULTVAL)){
            ApiDailyMovie dailyMovie = new ApiDailyMovie(DEFAULTVAL);
            dailyMovie.start();

                try { dailyMovie.join(); }
                catch (InterruptedException e) { e.printStackTrace(); }

            SharedPreferences.Editor editor = pref.edit();
            editor.putString(MOVIEINFOAPI, today);
            editor.apply();

        } else if (!"prefMovieInfoApi".equals(today)) {
            ApiDailyMovie dailyMovie = new ApiDailyMovie(prefMovieInfoApi);
            dailyMovie.start();

                try { dailyMovie.join(); }
                catch (InterruptedException e) { e.printStackTrace(); }

            SharedPreferences.Editor editor = pref.edit();
            editor.putString(MOVIEINFOAPI, today);
            editor.apply();
        }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            super.onPostExecute(s);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}