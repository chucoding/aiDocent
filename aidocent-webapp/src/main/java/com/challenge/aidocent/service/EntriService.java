package com.challenge.aidocent.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.challenge.aidocent.dao.EntriDao;
import com.challenge.aidocent.util.CacheUtils;


@Service
public class EntriService {
	
	@Autowired CacheUtils cache;
	
	public Map chatopen() {
		EntriDao chatDao = new EntriDao();
		Map resp = chatDao.chatopen();
		Map return_object = MapUtils.getMap(resp, "return_object");
		String uuid = MapUtils.getString(return_object,"uuid");
		
		cache.put("uuid", uuid);
		return makeTemplate(resp);
	}
	
	public Map chatmessage(Map<String, Object> data) {
		EntriDao chatDao = new EntriDao();
		Map map = MapUtils.getMap(data, "data");
		
		String uuid = (String) cache.get("uuid");
		if(StringUtils.isEmpty(uuid)) {
			
			Map expireMap = new HashMap();
			Map chatbotInfo = new HashMap();
			Map return_object = MapUtils.getMap(map, "return_object");
			Map result = MapUtils.getMap(return_object, "result");
			
			chatbotInfo.put("id","user");
			
			expireMap.put("id","chatbot");
			expireMap.put("text","������ ����Ǿ����ϴ�.");
			expireMap.put("createdAt",new Date());
			expireMap.put("user",chatbotInfo);
			
			return expireMap;
		}
			
		map.put("uuid", uuid);
		return makeTemplate(chatDao.chatmessage(map));
	}
	
	private Map makeTemplate(Map resp) {
		
		Map map = new HashMap();
		Map chatbotInfo = new HashMap();
		Map return_object = MapUtils.getMap(resp, "return_object");
		Map result = MapUtils.getMap(return_object, "result");
		chatbotInfo.put("id","user");
		
		map.put("id","chatbot");
		map.put("text",MapUtils.getString(result,"system_text"));
		map.put("createdAt",new Date());
		map.put("user",chatbotInfo);
		
		return map;
	}
}
