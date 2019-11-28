package com.ffcs.face;


import com.alibaba.fastjson.JSONObject;
import com.ffcs.face.service.FaissService;
import com.ffcs.face.service.FrsService;
import com.ffcs.face.util.JsonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;


@SpringBootTest
class FaceApplicationTests {
    @Autowired
    FaissService faissService;
    @Autowired
    FrsService frsService;
    @Test
    void testJsonUtils(){
        Map<String,String> featureMap = new HashMap<>();
        featureMap.put("a","1");
        featureMap.put("b","22");
        featureMap.put("c","333");
        featureMap.put("d","4444");

        System.out.println(JsonUtils.getMapsList(featureMap));
    }
    @Test
    void contextLoads() {
        String groupByGet = frsService.viewGroupByGet(null);
        System.out.println(groupByGet);
    }


    @Test
    void testHash() {
        //访问路径
        String url="http://localhost:5001/frs/api/v1/faceinfo/feature";
        //设置请求头,可根据接口文档要求设置
        HttpHeaders header = new HttpHeaders();
        header.set("Accept-Charset", "UTF-8");
        header.set("Content-Type", "application/json; charset=utf-8");
        //设置请求参数，此处设置为json
        JSONObject object = new JSONObject();
        object.put("image_id","1");
        object.put("image_b64","/9j/4Q/cRXhpZgAATU");
        //entity为请求体，包含请求参数

        HttpEntity entity = new HttpEntity(object.toJSONString(), header);
        System.out.println(entity);
        RestTemplate restTemplate = new RestTemplate();
        //发送post请求
        String res = restTemplate.postForObject(url, entity, String.class);
        System.out.println(res);

    }

}
