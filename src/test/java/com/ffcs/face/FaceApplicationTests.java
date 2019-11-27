package com.ffcs.face;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ffcs.face.service.FaissService;
import com.ffcs.face.service.FrsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;


@SpringBootTest
class FaceApplicationTests {
    @Autowired
    FaissService faissService;
    @Autowired
    FrsService frsService;

    @Test
    void contextLoads() {
        String groups = faissService.viewGroupByGet(null);
        JSONObject jsonObject1 = JSON.parseObject(groups);
        JSONArray groupArrary = jsonObject1.getJSONArray("data");
        for(int i=0;i<groupArrary.size();i++){
            String name = groupArrary.getJSONObject(i).getString("name");
            System.out.println(name);
        }
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
