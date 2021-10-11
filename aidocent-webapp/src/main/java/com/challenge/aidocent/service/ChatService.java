package com.challenge.aidocent.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
	
	public Map<String, Object> chatopen(Map<String, Object> data) {
		
		Map map = MapUtils.getMap(data, "data");
		
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

}
