package com.ffcs.face;


import com.alibaba.fastjson.JSONObject;
import com.ffcs.face.service.IFaissService;
import com.ffcs.face.service.IFrsService;
import com.ffcs.face.service.impl.FaissServiceImpl;
import com.ffcs.face.util.JsonUtils;
import com.ffcs.face.util.StringUtils;
import com.ffcs.visionbigdata.fastdfs.FastdfsDownload;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import java.util.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {FaceApplication.class})
public class FaceApplicationTests {
    @Autowired
    private IFaissService faissService;

    @Autowired
    IFrsService frsService;
    @Test
    public void testHeads(){
        String a ="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";
        String b ="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";
        List<String> features = new ArrayList<>();
        features.add(a);
        features.add(b);
        //搜索相似图片
        System.out.println(features);
        String searchFeaturesResult = faissService.searchFeaturesByPost("fortest", features, 10);
        System.out.println("搜索相似图片结果：" + searchFeaturesResult);
        System.out.println(new Date());
    }
//    @Test
    void testJsonUtils(){
        Map<String,String> featureMap = new HashMap<>();
        featureMap.put("a","1");
        featureMap.put("b","22");
        featureMap.put("c","333");
        featureMap.put("d","4444");

        System.out.println(JsonUtils.getMapsList(featureMap));
    }
//    @Test
    void contextLoads() {
        String groupByGet = frsService.viewGroupByGet(null);
        System.out.println(groupByGet);
    }
//    @Test
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

    @Test
    void add(){

    }

}
