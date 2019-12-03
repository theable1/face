package com.ffcs.face.util;

public class StringUtils {
    public static String getGroup(String url){
        String[] strings = url.split("/");
        return strings[3];
    }

    public static String getDir(String url){
        String[] strings = url.split(getGroup(url) + "/");
        return strings[1];
    }
}
