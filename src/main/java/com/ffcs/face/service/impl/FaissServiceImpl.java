package com.ffcs.face.service.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ffcs.face.service.IFaissService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
/**
 * 
 * @author chenxiping
 *
 */
@Service
public class FaissServiceImpl implements IFaissService {
	@Value("${baseUri}")
	private String baseUri;
	@Autowired
	private RestTemplate restTemplate;
	/**
	 * 
	 * @param name
	 * @param dimension
	 * @return
	 */
	@Override
	public String addGroupByPost(String name, Integer dimension) {
		try {
			JSONObject json = new JSONObject();
			json.put("name", name);
			json.put("dimension", (dimension==null||dimension==0)?512:dimension);
			HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> request = new HttpEntity<>(json.toString(), headers);
			ResponseEntity<String> response = this.restTemplate
					.postForEntity(baseUri+"group", request, String.class);
			return response.getBody();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 
	 * @param name
	 * @return
	 */
	@Override
	public String viewGroupByGet(String name) {
		try {
			Map<String, String> params = new HashMap<>();
			params.put("name", name);
			HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_JSON);
			ResponseEntity<String> response = this.restTemplate
					.getForEntity(baseUri+"group", String.class,params);
			return response.getBody();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	@Override
	public String deleteGroupByDelete(String name) {
		try {
			Map<String, String> params = new HashMap<>();
			params.put("name", name);
			HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_JSON);
			ResponseEntity<String> response = this.restTemplate
					.exchange(baseUri+"group", HttpMethod.DELETE, null, String.class, params);
			return response.getBody();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 
	 * @param group
	 * @param features
	 * @return
	 */
	@Override
	public String addFeaturesByPost(String group, List<Map<String,String>> features) {
		try {
			JSONObject json = new JSONObject();
			json.put("group", group);
			json.put("features",features);
			HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> request = new HttpEntity<>(json.toString(), headers);
			ResponseEntity<String> response = this.restTemplate
					.postForEntity(baseUri+"features", request, String.class);
			return response.getBody();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 
	 * @param group
	 * @param features
	 * @param backNum
	 * @return
	 */
	@Override
	public String searchFeaturesByPost(String group, List<String> features, Integer backNum) {
		try {
			JSONObject json = new JSONObject();
			json.put("group", group);
			json.put("features",features);
			if(backNum!=null && backNum>0) {
				json.put("topK", backNum);
			}
			HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> request = new HttpEntity<>(json.toString(), headers);
			ResponseEntity<String> response = this.restTemplate
					.postForEntity(baseUri+"features/search", request, String.class);
			return response.getBody();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
