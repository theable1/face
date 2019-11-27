package com.ffcs.face.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ffcs.face.service.FaissService;
import com.ffcs.face.service.FrsService;
import com.ffcs.face.vo.ImageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search/")
public class SearchController {
    @Autowired
    FrsService frsService;
    FaissService faissService;
    @RequestMapping("visit")
    public ModelAndView visit(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("search");
        return modelAndView;
    }

    @RequestMapping("process")
    public ModelAndView process(@RequestBody ImageVo imageVo){
        double similarity=0.6;
        String url = null;
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("imageUrl",url );
        modelAndView.setViewName("search");
        //获取特征值
        String featureByPost = frsService.getFeatureByPost(imageVo.getImageId(), imageVo.getImageB64());
        JSONObject jsonObject = JSON.parseObject(featureByPost);
        String feature_b64 = jsonObject.getString("feature_b64");
        List<String> features= new ArrayList<>();
        features.add(feature_b64);
        //搜索相似图片
        String featuresByPost = faissService.searchFeaturesByPost(imageVo.getGroup(), features, 3);
        JSONObject jsonObject1 = JSON.parseObject(featuresByPost);
        JSONArray data = jsonObject1.getJSONArray("data");
        //distance最大值小于0.6,把图片增加到group中
        int size = data.size();
        double maxDistance = Double.parseDouble(data.getJSONObject(size-1).getString("distance"));
        if(maxDistance<similarity){
            Map<String,String> featuresMap=new HashMap<>();
            featuresMap.put("image_id",imageVo.getImageId());
            featuresMap.put("feature",feature_b64);
            List<Map<String,String>> featuresMapList=new ArrayList<>();
            featuresMapList.add(featuresMap);
            faissService.addFeaturesByPost(imageVo.getGroup(),featuresMapList);
        }
        //最大值>0.6，把数组中distance所以大于0.6的图片返回
        for(int i=size-1;i<=0;i--){
            double distance = Double.parseDouble(data.getJSONObject(i).getString("distance"));
            if(distance >similarity){
                String id = data.getJSONObject(i).getString("id");
            }else {
                break;
            }

        }

        return modelAndView;
    }



}
