package com.example.moblebox.tag;

public interface ApiMovieTag {
    String BOXOFFICERESULT = "boxOfficeResult";
    String DAILYBOXOFFICELIST = "dailyBoxOfficeList";
    String BOXOFFICETYPE = "boxofficeType";
    String SHOWRANGE = "showRange";
    String RNUM = "rnum";
    String RANK = "rank";
    String RANKINTEN = "rankInten";
    String RANKOLDANDNEW = "rankOldAndNew";
    String MOVIECD = "movieCd";
    String MOVIENM = "movieNm";
    String OPENDT = "openDt";
    String SALESAMT = "salesAmt";
    String SALESSHARE = "salesShare";
    String SALESINTEN = "salesInten";
    String SALESCHANGE = "salesChange";
    String SALESACC = "salesAcc";
    String AUDICNT = "audiCnt";
    String AUDIINTEN = "audiInten";
    String AUDICHANGE = "audiChange";
    String AUDIACC = "audiAcc";
    String SCRNCNT = "scrnCnt";
    String SHOWCNT = "showCnt";

    String[] UseMovieTags = {MOVIENM, RANK, MOVIECD, RANKINTEN, RANKOLDANDNEW,
            OPENDT, AUDIACC, SALESACC};
}