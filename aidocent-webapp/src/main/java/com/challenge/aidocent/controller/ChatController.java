package com.challenge.aidocent.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.aidocent.service.EntriService;

@RestController
public class ChatController {
	
	private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
	@Autowired EntriService chatService;
	
	@CrossOrigin("*")
	@PostMapping(value = "/chat/open")
	public Map open() {
		return chatService.chatopen();
	}
	
	@CrossOrigin("*")
	@PostMapping(value = "/chat/message")
	public Map message(@RequestBody Map<String, Object> data) {
		logger.info("�޽��� �ҷ����� ");
		return chatService.chatmessage(data);
	}
}
