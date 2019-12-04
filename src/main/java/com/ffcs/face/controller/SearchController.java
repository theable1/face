package com.ffcs.face.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ffcs.common.tools.FileAccessUtil;
import com.ffcs.face.service.IFaissService;
import com.ffcs.face.service.IFrsService;
import com.ffcs.face.util.JsonUtils;
import com.ffcs.face.util.StringUtils;
import com.ffcs.face.vo.ImageVO;
import com.ffcs.image.Simple;
import com.ffcs.visionbigdata.fastdfs.FastdfsDownload;
import com.ffcs.visionbigdata.mysql.bean.UploadImageInfo;
import com.ffcs.visionbigdata.mysql.service.UploadImageInfoService;
import com.ffcs.visionbigdata.rabbitmq.Sender;
import org.csource.fastdfs.StorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import sun.misc.BASE64Encoder;

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
    public Object list() {
        List<Map<String, Object>> groupList = new ArrayList();
        //查询所有人脸库
        String groups = faissService.viewGroupByGet(null);
        JSONArray groupArrary = JsonUtils.getJsonValueArray(groups, "data");
        for (int i = 0; i < groupArrary.size(); i++) {
            Map<String, Object> groupMap = new HashMap<>();
            String name = groupArrary.getJSONObject(i).getString("name");
            groupMap.put("name", name);
            if (name != null) {
                groupList.add(groupMap);
            }
        }
        return groupList;
    }

    @RequestMapping("process")
    public Object process(@RequestBody List<ImageVO> imageVOList) throws Exception {
        String imageId;
        String imageB64;
        double similarity = 0.6;
        List<Object> imageMessageListMax = new ArrayList<>();
        for(int k = 0; k<imageVOList.size() ; k++)
        {
            //再次搜索前端只传groupName、url,本地上传图片搜索url为null
            if (imageVOList.get(k).getImageUrl() != null) {
//            System.out.println("group:" + StringUtils.getGroup(imageVo.getImageUrl()));
//            System.out.println("dir:" + StringUtils.getDir(imageVo.getImageUrl()));
//            byte[] imageB64Bytes = storageClient.download_file(StringUtils.getGroup(imageVo.getImageUrl()), StringUtils.getDir(imageVo.getImageUrl()));
//            BASE64Encoder encoder = new BASE64Encoder();
//            imageB64 =  encoder.encode(imageB64Bytes);
//            imageId = FileAccessUtil.getHashCode(imageB64Bytes);
                imageId = "";
                imageB64 = "";
            } else {
                imageId = imageVOList.get(k).getImageId();
                imageB64 = imageVOList.get(k).getImageB64();
            }
            //获取特征值
            String getFeatureResult = frsService.getFeatureByPost(imageId, imageB64);
            System.out.println("获取特征值结果：:" + getFeatureResult);
            JSONObject jsonObject = JSON.parseObject(getFeatureResult);
            String featureB64 = jsonObject.getString("feature_b64");
            System.out.println("featureB64"+featureB64);
            if (featureB64 != null) {
                List<String> features = new ArrayList<>();
                features.add(featureB64);
                //搜索相似图片
                String searchFeaturesResult = faissService.searchFeaturesByPost(imageVOList.get(k).getGroupName(), features, imageVOList.get(k).getImageNum());
                System.out.println("搜索相似图片结果：" + searchFeaturesResult);
                JSONArray data = JsonUtils.getJsonValueArray(searchFeaturesResult, "data");
                //distance最大值小于0.6,把图片增加到group中
                int size = data.size();
                boolean saveImage = false;
                boolean maxFlag = false;
                List<Map<String, Object>> imageMessageList = new ArrayList<>();
                if (size != 0) {
                    double maxDistance = data.getJSONObject(0).getDouble("distance");
                    saveImage = maxDistance == 1.0 ? false : true;
                    maxFlag = maxDistance > 0.6 ? true : false;
                    System.out.println("是否保存" + saveImage);
                    List<Long> featureIdLong = new ArrayList<>();
                    int[] flag = new int[10];
                    int num = 0;
                    for (int i = 0; i < size; i++) {
                        if (data.getJSONObject(i).getDouble("distance") < similarity) {
                            break;
                        } else {
                            flag[num] = i;
                            num++;
                            featureIdLong.add(data.getJSONObject(i).getLongValue("id"));
                        }
                    }
                    System.out.println("featureIdLong:" + featureIdLong);
                    if (featureIdLong.size() != 0) {
                        Long[] a1 = new Long[featureIdLong.size()];
                        List<UploadImageInfo> images = this.uploadImageInfoService.getImages(null, null, featureIdLong.toArray(a1));
                        System.out.println("images:" + JSON.toJSONString(images));
                        if (images != null && images.size() > 0) {
                            for (int i = 0; i < data.size(); i++) {
                                for (int j = 0; j < images.size(); j++) {
                                    if (data.getJSONObject(i).getLongValue("id") == Long.parseLong(images.get(j).getFaissFeatureId())) {
                                        Map<String, Object> imageMessageMap = new HashMap<>();
                                        imageMessageMap.put("distance", data.getJSONObject(i).getDouble("distance"));
                                        imageMessageMap.put("imageShowPath", images.get(j).getImageShowPath());
                                        imageMessageList.add(imageMessageMap);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    saveImage = true;
                }
                if (saveImage == true) {
                    Simple simple = new Simple();
                    simple.setBase64(imageB64);
                    simple.setHashCode(imageId);
                    sender.apply(simple);
                    if (maxFlag == false) {
                        JSONObject resultJson2 = new JSONObject();
                        resultJson2.put("message", "找不到相似图片，此图片已保存到当前库中！");
                        return resultJson2;
                    } else {
                        System.out.println(imageMessageList);
                        imageMessageListMax.add(imageMessageList);
                    }
                } else {
                    imageMessageListMax.add(imageMessageList);
                }

            } else {
                imageMessageListMax.add(getFeatureResult);
            }
        }
        return imageMessageListMax;
    }
}
