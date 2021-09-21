package com.challenge.aidocent.controller;

import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.aidocent.service.ChatService;

@RestController
public class ChatController {
	
	private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
	@Autowired ChatService chatService;
	
	@CrossOrigin("*")
	@PostMapping(value = "/chat/open")
	public Map open(Locale locale, Model model) {
		return chatService.open();
	}
	
	@CrossOrigin("*")
	@PostMapping(value = "/chat/message")
	public Map message(Locale locale, Model model) {
		logger.info("메시지 불러오기 ");
		//Map map = chatService.open();
		//System.out.println(map);
		return null;
	}
	
}
