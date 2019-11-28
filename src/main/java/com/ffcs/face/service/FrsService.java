package com.ffcs.face.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Demo class
 *
 * @author liuxin
 * @date 2019/11/26
 */
@Service
public class FrsService {
    @Value("${frsBaseUri}")
    private String frsBaseUri;

    @Autowired
    private RestTemplate restTemplate;

    public String getFeatureByPost(String imageId,String imageB64){
        try {
            JSONObject json = new JSONObject();
            json.put("image_id", imageId);
            json.put("image_b64", imageB64);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(json.toString(), headers);
            ResponseEntity<String> response = this.restTemplate
                    .postForEntity(frsBaseUri+"faceinfo/feature", request, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public String viewGroupByGet(Integer gId){
        try {
            Map<String, Integer> params = new HashMap<>();
            params.put("gid", gId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            ResponseEntity<String> response = this.restTemplate
                    .getForEntity(frsBaseUri+"group", String.class,params);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //public String viewFaceinfoByGet()



}
