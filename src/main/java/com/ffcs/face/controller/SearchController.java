package com.ffcs.face.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ffcs.face.service.FaissService;
import com.ffcs.face.service.FrsService;
import com.ffcs.face.vo.ImageVO;
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

    @Autowired
    FaissService faissService;

    @RequestMapping("list")
    public ModelAndView visit() {
        ModelAndView modelAndView = new ModelAndView();
        List groupList = new ArrayList();
        //查询所有人脸库
        String groups = faissService.viewGroupByGet(null);
        JSONObject jsonObject = JSON.parseObject(groups);
        JSONArray groupArrary = jsonObject.getJSONArray("data");

        for (int i = 0; i < groupArrary.size(); i++) {
            Map<String, Object> groupMap = new HashMap<>();
            String name = groupArrary.getJSONObject(i).getString("name");
            groupMap.put("name", name);
            if (name != null) {
                groupList.add(groupMap);
            }
        }
        modelAndView.addObject("groupList", groupList);
        modelAndView.setViewName("search");
        return modelAndView;
    }

    @RequestMapping("process")
    public String process(@RequestBody ImageVO imageVo) {
        double similarity = 0.6;

        //获取特征值
        String getFeatureResult = frsService.getFeatureByPost(imageVo.getImageId(), imageVo.getImageB64());
        System.out.println("获取特征值结果：:" + getFeatureResult);
        JSONObject jsonObject = JSON.parseObject(getFeatureResult);
        String featureB64 = jsonObject.getString("feature_b64");
        if (featureB64 != null) {
            List<String> features = new ArrayList<>();
            features.add(featureB64);
            //搜索相似图片
            String searchFeaturesResult = faissService.searchFeaturesByPost(imageVo.getGroupName(), features, null);
            System.out.println("搜索相似图片结果：" + searchFeaturesResult);
            JSONObject jsonObject1 = JSON.parseObject(searchFeaturesResult);
            JSONArray data = jsonObject1.getJSONArray("data");
            //distance最大值小于0.6,把图片增加到group中
            int size = data.size();
            if (size != 0) {
                double maxDistance = Double.parseDouble(data.getJSONObject(0).getString("distance"));
                System.out.println("最大相似度为："+maxDistance);
                if (maxDistance < similarity) {
                    Map<String, String> featuresMap = new HashMap<>();
                    featuresMap.put("image_id", imageVo.getImageId());
                    featuresMap.put("feature", featureB64);
                    List<Map<String, String>> featuresMapList = new ArrayList<>();
                    featuresMapList.add(featuresMap);
                    faissService.addFeaturesByPost(imageVo.getGroupName(), featuresMapList);
                    JSONObject resultJson1 = new JSONObject();
                    resultJson1.put("message", "未找到相似图片！");
                    return resultJson1.toString();
                } else {
                    //最大值>0.6，把数组中distance所以大于0.6的图片返回
                    for (int i = size - 1; i >= 0; i--) {
                        double distance = Double.parseDouble(data.getJSONObject(i).getString("distance"));
                        if (distance > similarity) {
                            String id = data.getJSONObject(i).getString("id");
                            //根据特征码ID获取
                        } else {
                            break;
                        }
                    }
                }
            } else {
                Map<String, String> featuresMap = new HashMap<>();
                featuresMap.put("image_id", imageVo.getImageId());
                featuresMap.put("feature", featureB64);
                List<Map<String, String>> featuresMapList = new ArrayList<>();
                featuresMapList.add(featuresMap);
                faissService.addFeaturesByPost(imageVo.getGroupName(), featuresMapList);
                JSONObject resultJson2 = new JSONObject();
                resultJson2.put("message", "未找到相似图片！");
                return resultJson2.toString();
            }
        } else {
            return getFeatureResult;
        }
        return null;
    }


}
