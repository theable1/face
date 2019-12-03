package com.ffcs.face;


import com.alibaba.fastjson.JSONObject;
import com.ffcs.face.service.IFaissService;
import com.ffcs.face.service.IFrsService;
import com.ffcs.face.util.JsonUtils;
import com.ffcs.face.util.StringUtils;
import com.ffcs.visionbigdata.fastdfs.FastdfsDownload;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@SpringBootTest
class FaceApplicationTests {
    @Autowired
    IFaissService faissService;
    @Autowired
    IFrsService frsService;
    @Autowired
    FastdfsDownload fastdfsDownload;
    @Autowired
    TrackerClient trackerClient;
    @Autowired
    StorageClient storageClient;
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

    @Test
    void downloadTest() throws Exception {
        String url = "http://192.168.50.10:8888/group1/M00/00/17/wKgyCl3kwSuAN58sAACF8jZSvTo187.jpg";
        String[] strings = url.split("/");
        String group = "group1";
        String dir = "M00/00/17/wKgyCl3kwSuAN58sAACF8jZSvTo187.jpg";
        byte[] result = storageClient.download_file(group, dir);
        System.out.println(new String(result));
    }

    @Test
    void stringUtilsTest(){
        String url = "http://192.168.50.10:8888/group1/M00/00/17/wKgyCl3kwSuAN58sAACF8jZSvTo187.jpg";
        String group = StringUtils.getGroup(url);
        System.out.println("group:"+group);
        String dir = StringUtils.getDir(url);
        System.out.println("dir:"+dir);
    }

}
