package com.ffcs.face.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ffcs.face.service.FaissService;
import com.ffcs.face.service.FrsService;
import com.ffcs.face.util.ImageUtils;
import com.ffcs.face.vo.ImageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
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
        String groups = frsService.viewGroupByGet(null);
        JSONObject jsonObject = JSON.parseObject(groups);
        JSONArray groupArrary = jsonObject.getJSONArray("data");

        for (int i = 0; i < groupArrary.size(); i++) {
            Map<String, Object> groupMap = new HashMap<>();
            int gid = groupArrary.getJSONObject(i).getInteger("gid");
            String name = groupArrary.getJSONObject(i).getString("name");
            groupMap.put("gid", gid);
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
            List<Integer> groupList = new ArrayList<>();
            groupList.add(imageVo.getGroupId());
            //搜索相似图片
            try {
                String searchFeaturesResult = frsService.searchFeaturesByPost(ImageUtils.base64ToFile(imageVo.getImageB64(), imageVo.getFileName()), groupList, null, null);
                System.out.println("搜索相似图片结果：" + searchFeaturesResult);
                JSONObject jsonObject1 = JSON.parseObject(searchFeaturesResult);
                JSONArray data = jsonObject1.getJSONArray("data");
                //distance最大值小于0.6,把图片增加到group中
                int size = data.size();
                if (size != 0) {
                    double maxDistance = Double.parseDouble(data.getJSONObject(0).getString("distance"));
                    if (maxDistance < similarity) {
                        Map<String, String> featuresMap = new HashMap<>();
                        featuresMap.put("image_id", imageVo.getImageId());
                        featuresMap.put("feature", featureB64);
                        List<Map<String, String>> featuresMapList = new ArrayList<>();
                        featuresMapList.add(featuresMap);
//                        faissService.addFeaturesByPost(imageVo.getGroupName(), featuresMapList);
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
//                faissService.addFeaturesByPost(imageVo.getGroupId(),featuresMapList);
                    JSONObject resultJson2 = new JSONObject();
                    resultJson2.put("message", "未找到相似图片！");
                    return resultJson2.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("图片Base64转File出现异常");
                JSONObject resultJson3 = new JSONObject();
                resultJson3.put("message", "图片Base64转File出现异常！");
                return resultJson3.toString();
            }
        } else {
            return getFeatureResult;
        }
        return null;
    }


}
