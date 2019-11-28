package com.ffcs.face.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

/**
 * Demo class
 *
 * @author liuxin
 * @date 2019/11/28
 */
public class JsonUtils {
    public static JSONArray getJsonValueArray(String jsonStr,String key){
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        JSONArray jsonArray= jsonObject.getJSONArray(key);
        return jsonArray;
    }
    public static List<Map<String,String>> getMapsList(Map<String,String> featureMap){
        if(featureMap.isEmpty()){
            System.out.println("参数map为空");
            return null;
        }else {
            List<Map<String, String>> featuresMapList = new ArrayList<>();
            Iterator<Map.Entry<String, String>> it = featureMap.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<String, String> entry = it.next();
                System.out.println("image_id:"+entry.getKey()+" feature:"+entry.getValue());
                Map<String, String> featuresMap = new HashMap<>();
                featuresMap.put("image_id", entry.getKey());
                featuresMap.put("feature", entry.getValue());
                featuresMapList.add(featuresMap);
            }
            return featuresMapList;
        }
    }
}
