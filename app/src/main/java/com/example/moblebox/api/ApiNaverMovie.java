package com.example.moblebox.api;

import android.util.AttributeSet;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.moblebox.MainActivity;
import com.example.moblebox.tag.NaverApiTag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import static com.example.moblebox.api.ApiDailyMovie.mobleBoxDB;

public class ApiNaverMovie extends Thread implements NaverApiTag{

    public ApiNaverMovie(HashSet<String> hashSet) {
        movieNmHashSet = hashSet;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        Calendar calendar = Calendar.getInstance();
        thisYear = simpleDateFormat.format(calendar.getTime());
        calendar.add(Calendar.YEAR,-1);
        lastYear = simpleDateFormat.format(calendar.getTime());
    }

    static Map<String, Object> mainNaverMap = new HashMap<>();
    static HashSet<String> movieNmHashSet;
    static private final String clientId = "7RWGlboxrKhHABGmC2QW"; //애플리케이션 클라이언트 아이디값"
    static private final String clientSecret = "gG1J0Dt3iB"; //애플리케이션 클라이언트 시크릿값"
    static String thisYear;
    static String lastYear;

    @Override
    public void run() {

        for(String movieNm : movieNmHashSet){
            main(movieNm);
        }
    }

    public static void main(String movieName) {
        String duration = "&yearto" + thisYear + "&yearfrom=" + thisYear + "&display=1&start=1";
        String text;
        try {
            text = URLEncoder.encode(movieName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패",e);
        }

        String apiURL = "https://openapi.naver.com/v1/search/movie.json?query=" + text
                + duration;    // json 결과
        //String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+ text; // xml 결과

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        String responseBody = get(apiURL,requestHeaders);

        parseData(responseBody);

    }
    private static String get(String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }

        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }

    private static void parseData(String responseBody) {
        Map<String, String> movieInfo;
        String moviename;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(responseBody);
            JSONArray jsonArray = jsonObject.getJSONArray(ITEMS);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                movieInfo = new HashMap<>();
                moviename = item.getString(TITLE).trim();
                moviename = moviename.replaceAll("[.]", "");
                moviename = moviename.replaceAll("<b>", "");
                moviename = moviename.replaceAll("</b>", "");
                movieInfo.put(LINK, item.getString(LINK).trim());
                movieInfo.put(IMAGEURL, item.getString(IMAGEURL).trim());
                movieInfo.put(USERRATING_INTEGER, item.getString(USERRATING_INTEGER).trim());

                for(String mNm : movieNmHashSet){
                    if(mNm.trim().equals(moviename)){
                        mainNaverMap.put(moviename, movieInfo);
                    }
                }
            }

            Log.i("chiffon95", "mainNaverMap : " + mainNaverMap.toString());
            mobleBoxDB.putNaverApiToDB(mainNaverMap);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
