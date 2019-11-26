package com.ffcs.face;

import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class FaceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void testHash() {
        //访问路径
        String url="http://localhost:10004/bvs/api/v1/faceinfo/feature";
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
