package com.challenge.aidocent.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.challenge.aidocent.service.EntriService;
import com.challenge.aidocent.service.GoogleService;

@RestController
public class ChatController {

	private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
	@Autowired
	EntriService chatService;

	@Autowired
	GoogleService googleService;

	@CrossOrigin("*")
	@PostMapping(value = "/chat/open")
	public Map open() {
		return chatService.chatopen();
	}

	@CrossOrigin("*")
	@PostMapping(value = "/chat/message")
	public Map message(@RequestBody Map<String, Object> data) {
		logger.info("메시지");
		return chatService.chatmessage(data);
	}

	@CrossOrigin("*")
	@PostMapping(value = "/chat/question")
	public Map stt(MultipartFile file) {
		logger.info("STT API로 추출된 질문 불러오기", file);
		return null;
	}

	@CrossOrigin("*")
	@PostMapping(value = "/chat/read")
	public String stt(String text) {
		logger.info("TTS API 불러오기", text);
		
		
		return null;
	}
}
