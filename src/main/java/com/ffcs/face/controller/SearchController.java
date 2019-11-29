package com.ffcs.face.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ffcs.face.service.IFaissService;
import com.ffcs.face.service.IFrsService;
import com.ffcs.face.util.JsonUtils;
import com.ffcs.face.vo.ImageVO;
import com.ffcs.image.Simple;
import com.ffcs.visionbigdata.mysql.bean.UploadImageInfo;
import com.ffcs.visionbigdata.mysql.service.UploadImageInfoService;
import com.ffcs.visionbigdata.rabbitmq.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import sun.java2d.pipe.SpanShapeRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search/")
public class SearchController {
    @Autowired
    IFrsService frsService;
    @Autowired
    IFaissService faissService;
    @Autowired
    private UploadImageInfoService uploadImageInfoService;
    @Autowired
    private Sender sender;
    @RequestMapping("list")
    public ModelAndView visit() {
        ModelAndView modelAndView = new ModelAndView();
        List<Map<String,Object>> groupList = new ArrayList();
        //查询所有人脸库
        String groups = faissService.viewGroupByGet(null);
        JSONArray  groupArrary= JsonUtils.getJsonValueArray(groups, "data");
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
    public Object process(@RequestBody ImageVO imageVo) throws Exception{
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
            JSONArray data= JsonUtils.getJsonValueArray(searchFeaturesResult, "data");
            //distance最大值小于0.6,把图片增加到group中
            int size = data.size();
            boolean saveImage = false;
            boolean maxFlag =false;
            List<String> imageShowPathList = new ArrayList<>();
            if (size != 0) {
                double maxDistance = Double.parseDouble(data.getJSONObject(0).getString("distance"));
                saveImage = maxDistance==1.0? false : true;
                maxFlag = maxDistance>0.6?true:false;
                System.out.println("是否保存"+saveImage);
                List<Long> featureIdLong = new ArrayList<Long>();
                for(int i=0;i<size;i++){
                    if( Double.parseDouble(data.getJSONObject(i).getString("distance"))<similarity){
                        break;
                    }else{
                        featureIdLong.add(data.getJSONObject(i).getLongValue("id"));
                    }
                }
                if(featureIdLong.size()!=0){
                    Long[] a1 = new Long[featureIdLong.size()];
                    List<UploadImageInfo> images = this.uploadImageInfoService.getImages(null,null,featureIdLong.toArray(a1));
                    if(images!=null && images.size()>0) {

                        for(int j=0;j<images.size();j++){
                            imageShowPathList.add(images.get(j).getImageShowPath());
                        }
                        System.out.println(imageShowPathList);
                    }
                }

//                if (maxDistance < similarity) {
//                    Map<String,String> featureMap = new HashMap<>();
//                    featureMap.put(imageVo.getImageId(),imageVo.getImageB64());
//                    List<Map<String, String>> featuresMapList = JsonUtils.getMapsList(featureMap);
//                    faissService.addFeaturesByPost(imageVo.getGroupName(), featuresMapList);
//                    JSONObject resultJson1 = new JSONObject();
//                    resultJson1.put("message", "未找到相似图片，此图片已保存到当前库中！");
//                    return resultJson1;
//
//                } else {
//                    //最大值>0.6，把数组中distance所以大于0.6的图片返回
//                    JSONObject resultJson1 = new JSONObject();
//                    Long[] intArray0 = new Long[size];
//                    for (int i = 0; i < size; i++) {
//                        double distance = Double.parseDouble(data.getJSONObject(i).getString("distance"));
//                        if (distance > similarity) {
//                            long id = data.getJSONObject(i).getLongValue("id");
//                            intArray0[i] = id;
//                            //根据特征码ID获取图片展示路径
//                        } else {
//                            break;
//                        }
//                    }
//                    List<UploadImageInfo> images = this.uploadImageInfoService.getImages(null,null,intArray0);
//                    if(images!=null && images.size()>0) {
//                        List<String> imageShowPathList = new ArrayList<>();
//                        for(int i=0;i<images.size();i++){
//                            imageShowPathList.add(images.get(i).getImageShowPath());
//                        }
//                        System.out.println(imageShowPathList);
//                        return imageShowPathList;
//                    }
//                }
            } else {
                saveImage = true;
//                Map<String,String> featureMap = new HashMap<>();
//                featureMap.put(imageVo.getImageId(),imageVo.getImageB64());
//                List<Map<String, String>> featuresMapList = JsonUtils.getMapsList(featureMap);
//                faissService.addFeaturesByPost(imageVo.getGroupName(), featuresMapList);
//                JSONObject resultJson2 = new JSONObject();
//                resultJson2.put("message", "当前库中没有图片，此图片已保存到当前库中！");
//                return resultJson2;
            }
            if(saveImage == true){
//                Map<String,String> featureMap = new HashMap<>();
//                featureMap.put(imageVo.getImageId(),imageVo.getImageB64());
//                List<Map<String, String>> featuresMapList = JsonUtils.getMapsList(featureMap);
//                String s= faissService.addFeaturesByPost(imageVo.getGroupName(), featuresMapList);
                Simple simple = new Simple();
                simple.setBase64(imageVo.getImageB64());
                simple.setHashCode(imageVo.getImageId());
                sender.apply(simple);
                if(maxFlag == false){
                    JSONObject resultJson2 = new JSONObject();
                    resultJson2.put("message", "当前库中没有图片，此图片已保存到当前库中！");
                    return resultJson2;
                }
                System.out.println(imageShowPathList);
                return imageShowPathList;
            }

        } else {

            return getFeatureResult;
        }
        return null;
    }
}
