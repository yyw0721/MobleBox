package com.example.moblebox.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public interface DBPathTag {
    String PATH_STORE = "store";
    String PATH_STORE_SEOUL = "서울";
    String PATH_STORE_CHEONAN = "천안";
    String PATH_STORE_BUSAN = "부산";
    String[] PATH_STORE_ARR = {PATH_STORE_SEOUL, PATH_STORE_CHEONAN, PATH_STORE_BUSAN};
    String PATH_MOVIE = "movie";
    String[] PATH_RUNNINGTIME = {"09:00", "12:00", "19:00"};
    String PATH_RESERVATION = "reservation";
    String PATH_SEATS = "seats";
    String[] PATH_SEATS_ARR = {"A", "B", "C", "D", "E",
            "F", "G", "H", "I", "J"};
    String PATH_SEATS_INFO = "000000000000";
    String PATH_USERINFO = "userinfo";
    String PATH_CONVENIENCE_STORE ="convenience";
    String PATH_PARKING = "parking";
    String PATH_FOOD = "food";
    String PATH_Q_N_A = "QnA";
    String PATH_FOODS = "foods";
    String PATH_DRINKS = "drinks";
    String[] PATH_FOODS_MENU = {"나쵸", "팝콘", "핫도그"};
    String[] PATH_DRINKS_MENU = {"콜라", "사이다", "환타", "마운틴듀"};
    String[] PATH_CONVENIENCE_MENU = {PATH_PARKING, PATH_FOOD, PATH_Q_N_A};
    String[] PATH_PARKING_FLOOR = {"B1", "B2", "B3"};
    String[] PATH_PARKING_SPACE = {"A", "B", "C", "D", "E", "F", "G", "H"};
    String PATH_PARKING_INFO = "00000000";
    String PATH_Q_N_A_ID = "QnA_Id_default";
    String PATH_Q_N_A_BOARD = "QnA_Board_default";
    String PATH_MOVIEINFO = "movieinfo";
    String PATH_IMAGE = "movieImage";
}
