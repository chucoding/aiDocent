package com.challenge.aidocent.dao;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

public class EntriDao {

	private static final String OPEN_API_URL = "http://aiopen.etri.re.kr:8000/Dialog";
	private static final String ACCESS_KEY = "417ac904-4b08-4ba6-9f5e-ea214b0994ad";
	
	public Map chatopen() {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, String> argument = new HashMap<String, String>();
		
		argument.put("name", "Genie_Pizza");
		argument.put("access_method", "internal_data");
		argument.put("method", "open_dialog");

		params.put("access_key",ACCESS_KEY);
		params.put("argument", argument);
		
		HttpHeaders headers = new HttpHeaders();
		
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Map> entity = new HttpEntity<Map>(params, headers);
		RestTemplate rt = new RestTemplate();
		Map resp = rt.postForObject(OPEN_API_URL, entity, Map.class);
		
		return resp;
		//throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public Map chatmessage(Map map) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, String> argument = new HashMap<String, String>();
		
		System.out.println(map);
		argument.put("uuid", (String) map.get("uuid"));
		argument.put("text", (String) map.get("text"));
		argument.put("method", "dialog");
	
		params.put("access_key",ACCESS_KEY);
		params.put("argument", argument);
		
		HttpHeaders headers = new HttpHeaders();
		
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Map> entity = new HttpEntity<Map>(params, headers);
		RestTemplate rt = new RestTemplate();
		Map resp = rt.postForObject(OPEN_API_URL, entity, Map.class);
		System.out.println(resp);
		
		return resp;
	}
}
