package com.example.moblebox.api;

import com.example.moblebox.mobleboxdb.MobleBoxDB;
import com.example.moblebox.tag.ApiMainTag;
import com.example.moblebox.tag.DBPathTag;
import com.example.moblebox.tag.SharedPrefTags;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.example.moblebox.tag.ApiMovieTag.*;


public class ApiDailyMovie extends Thread implements DBPathTag, ApiMainTag, SharedPrefTags {
    static MobleBoxDB mobleBoxDB = new MobleBoxDB();
    public final static String apiDailyURL = "https://www.kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json";
    private final static String KEY = "1a20b23b690802bc912c804b24922eeb";
    public static String today;
    public static String prefDate;

    public ApiDailyMovie(String day) {
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        ApiDailyMovie.prefDate = day;
        ApiDailyMovie.today = sdformat.format(calendar.getTime());
    }

    public void run(){
        main();
    }

    public static void main() {

        String url = apiDailyURL+"?key="+KEY+"&targetDt="+today;
        String responseBody = get(url);
        parseData(responseBody);

    }

    private static String get(String apiUrl) {
        String responseBody = null;
        try {
            URL url = new URL(apiUrl);
            InputStream in = url.openStream();
            responseBody = readBody(in);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseBody;
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        SimpleDateFormat dateFormatDB = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        JSONObject jsonObject = null;
        Map<String, Object> movieInfo = new HashMap<>();
        Map<String, Object> movieInfos;
        Map<String, Object> movieOpenDt = null;
        try {
            jsonObject = new JSONObject(responseBody.toString());
            JSONObject jsonObject1 = (JSONObject) jsonObject.get(BOXOFFICERESULT);
            JSONArray jsonArray = jsonObject1.getJSONArray(DAILYBOXOFFICELIST);

            for (int i = 0; i < jsonArray.length(); i++) {
                String mNm;
                JSONObject item = jsonArray.getJSONObject(i);
                movieInfos = new HashMap<>();
                movieOpenDt = new HashMap<>();
                movieInfos.put(UseMovieTags[1], item.getString(UseMovieTags[1]));
                movieInfos.put(UseMovieTags[2], item.getString(UseMovieTags[2]));
                movieInfos.put(UseMovieTags[3], item.getString(UseMovieTags[3]));
                movieInfos.put(UseMovieTags[4], item.getString(UseMovieTags[4]));
                Date date = dateFormatDB.parse(item.getString(UseMovieTags[5]));
                String openDt = dateFormat.format(date);
                movieInfos.put(UseMovieTags[5], openDt);
                movieInfos.put(UseMovieTags[6], item.getString(UseMovieTags[6]));
                movieInfos.put(UseMovieTags[7], item.getString(UseMovieTags[7]));
                mNm = item.getString(UseMovieTags[0]).replaceAll("[.]","");
                movieOpenDt.put(mNm, openDt);
                movieInfo.put(mNm, movieInfos);
            }

            //SharedPref add need
            if(prefDate.equals(DEFAULTVAL) || !"prefDate".equals(today)){
                mobleBoxDB.putMovieApiToDB(movieInfo);
                mobleBoxDB.checkNaverApi();
                mobleBoxDB.putStoreInfoToDB(movieInfo, movieOpenDt);
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }
}
