package com.ffcs.face.service;

import java.util.List;
import java.util.Map;

public interface IFaissService {
    String addGroupByPost(String name,Integer dimension);
    String viewGroupByGet(String name);
    String deleteGroupByDelete(String name);
    String addFeaturesByPost(String group, List<Map<String,String>> features);
    String searchFeaturesByPost(String group,List<String> features,Integer backNum);
}
