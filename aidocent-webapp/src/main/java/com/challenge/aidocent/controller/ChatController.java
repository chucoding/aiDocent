package com.challenge.aidocent.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.challenge.aidocent.service.ChatService;
import com.challenge.aidocent.service.EtriService;
import com.challenge.aidocent.service.GoogleService;

@RestController
public class ChatController {

	private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

	@Autowired
	ChatService chatService;
	EtriService etriService;

	@Autowired
	GoogleService googleService;

	@CrossOrigin("*")
	@PostMapping(value = "/chat/open")
	public Map open(HttpServletRequest req, @RequestBody Map<String, Object> data) {
		return chatService.chatopen(data);
	}

	@CrossOrigin("*")
	@PostMapping(value = "/chat/message")
	public Map message(HttpServletRequest req, @RequestBody Map<String, Object> data) {
		logger.info("메시지");
		Map map = MapUtils.getMap(data, "data");
		if (map.get("text").toString().toLowerCase().contentEquals("quiz") || map.get("text").toString().contentEquals("퀴즈")) {
			return etriService.quiz(data);
		} else {
			return etriService.chatmessage(data);
		}
	}

	@CrossOrigin("*")
	@PostMapping(value = "/chat/question")
	public Map stt(HttpServletRequest req, MultipartFile file) throws Exception {
		logger.info("STT API로 추출된 질문 불러오기", file);
		return etriService.stt(req, file);
	}

	@CrossOrigin("*")
	@PostMapping(value = "/chat/read")
	public Map stt(HttpServletRequest req, @RequestBody Map<String, Object> data) {
		logger.info("TTS API");
		System.out.println(data);
		Map<String, Object> map = new HashedMap<String, Object>();
		map.put("file_name", googleService.TTS(req, data.get("data").toString()));
		return map;
	}

	@CrossOrigin("*")
	@PostMapping(value = "/chat/QAnal")
	public Map QAnal(HttpServletRequest req, @RequestBody Map<String, Object> data) {
		logger.info("WiseQAnal API");
		System.out.println(data);
		Map<String, Object> map = new HashedMap<String, Object>();
		return map;
	}

}
