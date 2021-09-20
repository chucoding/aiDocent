package com.challenge.aidocent;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {
	
	private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
	
	@CrossOrigin("*")
	@PostMapping(value = "/chat/message")
	public Map message(Locale locale, Model model) {
		logger.info("메시지 불러오기 ");
		
		Map map = new HashMap();
		Map chatbotInfo = new HashMap();
		chatbotInfo.put("id","user");
		
		map.put("id","chatbot");
		map.put("text","메시지가 전송되었습니다.");
		map.put("createdAt",new Date());
		map.put("user",chatbotInfo);
		
		System.out.println(map);
		
		return map;
	}
	
}
