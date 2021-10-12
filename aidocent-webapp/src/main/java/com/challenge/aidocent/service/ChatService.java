package com.challenge.aidocent.service;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import com.challenge.aidocent.dao.EtriDao;
import com.challenge.aidocent.dao.GoogleDao;

@Service
public class ChatService {
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> chatopen(Map<String, Object> data) {
		
		Map<String, Object> map = (Map<String, Object>) MapUtils.getMap(data, "data");
		
		String text = "";
		String menu = "";
		if(map.get("text").equals("1")) {
			text = "사진 또는 그림에 대해 궁금한 것을 물어보세요.";
			menu = "dialog";
		} else if(map.get("text").equals("2")) {
			text = "지금부터 퀴즈를 시작하겠습니다.";
			menu = "quiz";
		} else {
			text = "숫자 1 또는 2를 클릭해주세요";
		}
		
		Map<String, Object> answer = new HashMap<String, Object>();
		answer.put("position", "left");
		answer.put("type", "text");
		answer.put("text", text);
		answer.put("date", new Date());
		answer.put("menu", menu);
		return answer;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> chatmessage(Map<String, Object> data, HttpServletRequest servletReq) {
		
		GoogleDao googleDao = new GoogleDao();
		EtriDao chatDao = new EtriDao();
		
		Map<String, Object> datamap = (Map<String, Object>) MapUtils.getMap(data, "data");
		Map<String, Object> answer = new HashMap<String, Object>();
		String folderName = servletReq.getSession().getServletContext().getRealPath("/") + "resources" + File.separator + "tts";

		Map<String, Object> map = chatDao.wiseNLU_spoken((String)datamap.get("text"));
		
		String text = nlp(map);

		answer.put("position", "left");
		answer.put("type", "text");
		answer.put("text", text);
		answer.put("date", new Date());
		answer.put("menu", "dialog");
		answer.put("ttsUrl", googleDao.synthesizeText(folderName, text));
		
		return answer;
	}
	
	@SuppressWarnings("unchecked")
	private String nlp(Map<String, Object> data) {
		
		Map<String, Object> return_object = (Map<String, Object>) MapUtils.getMap(data, "return_object");
		System.out.println(return_object);
		List<Map<String, Object>> morp = (List<Map<String, Object>>) MapUtils.getObject(return_object, "morp");
		System.out.println(morp);
		for(Map<String, Object> map : morp) {
			System.out.println(map);
		}
		
		
		
		return "테스트";
	}
	
}
