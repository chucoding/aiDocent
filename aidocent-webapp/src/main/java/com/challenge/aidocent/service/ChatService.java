package com.challenge.aidocent.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.challenge.aidocent.dao.ChatDao;
import com.challenge.aidocent.util.CacheUtils;


@Service
public class ChatService {
	
	@Autowired CacheUtils cache;
	
	public Map open() {
		ChatDao chatDao = new ChatDao();
		Map resp = chatDao.open();
		
		Map return_object = MapUtils.getMap(resp, "return_object");
		String uuid = MapUtils.getString(return_object,"uuid");
		
		cache.put("uuid", uuid);
		return makeTemplate(resp);
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
