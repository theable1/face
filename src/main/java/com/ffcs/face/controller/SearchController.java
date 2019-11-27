package com.ffcs.face.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ffcs.face.service.FaissService;
import com.ffcs.face.service.FrsService;
import com.ffcs.face.vo.ImageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/search/")
public class SearchController {
    @Autowired
    FrsService frsService;

    @Autowired
    FaissService faissService;

    @RequestMapping("visit")
    public ModelAndView visit(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("search");
        return modelAndView;
    }

    @RequestMapping(value = "process",method = RequestMethod.POST)
    public ModelAndView process(@RequestBody ImageVo imageVo){
        System.out.println(imageVo.toString());
        ModelAndView modelAndView = new ModelAndView();
        //1、获取图片特征值
        String featureByPost = frsService.getFeatureByPost(imageVo.getImageId(), imageVo.getImageB64());
        JSONObject jsonObject = JSON.parseObject(featureByPost);
        String featureB64 = jsonObject.getString("feature_b64");
        String imageId = jsonObject.getString("image_id");
        System.out.println("feature_b64；"+featureB64);
        System.out.println("image_id；"+imageId);
        List<String> features= new ArrayList<>();
        features.add(featureB64);
        String featuresByPost = faissService.searchFeaturesByPost(imageVo.getGroup(), features, 3);


        modelAndView.addObject("imageUrl","url");
        modelAndView.setViewName("search");
        return modelAndView;
    }



}
