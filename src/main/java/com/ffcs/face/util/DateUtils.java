package com.ffcs.face.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static Date strToDate(String str) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = simpleDateFormat.parse(str);
        return date;
    }
    public static String dateToString(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String str = simpleDateFormat.format(date);
        return str;
    }

    public static void main(String[] args) throws ParseException {
//        String str = "2019-12-12";
//        Date date = strToDate(str);
//        System.out.println(date);
        System.out.println(dateToString(new Date()));
    }
}
