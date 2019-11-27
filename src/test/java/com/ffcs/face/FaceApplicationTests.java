package com.ffcs.face;

import com.ffcs.face.service.FaissService;
import com.ffcs.face.service.FrsService;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class FaceApplicationTests {
    @Autowired
    FaissService faissService;
    @Autowired
    FrsService frsService;
    @Test
    void contextLoads() {

    }


    @Test
    void testHash() {
        Map<String,String> featuresMap=new HashMap<>();
        featuresMap.put("image_id","1");
        featuresMap.put("feature","2");
        Map<String,String> featuresMap1=new HashMap<>();
        featuresMap1.put("image_id","2");
        featuresMap1.put("feature","3");
        List<Map<String,String>> featuresMapList=new ArrayList<>();
        featuresMapList.add(featuresMap);
        featuresMapList.add(featuresMap1);
        System.out.println(featuresMapList.get(0).get("image_id"));
    }

}
