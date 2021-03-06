package com.challenge.aidocent.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.challenge.aidocent.service.ChatService;
import com.challenge.aidocent.service.EtriService;
import com.challenge.aidocent.service.GoogleService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestController
public class ChatController {

	private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

	@Autowired
	ChatService chatService;

	@Autowired
	EtriService etriService;

	@Autowired
	GoogleService googleService;

	@CrossOrigin("*")
	@PostMapping(value = "/chat/open")
	public Map open(HttpServletRequest req, @RequestBody Map<String, Object> data) {
		return chatService.chatopen(data, req);
	}

	@CrossOrigin("*")
	@PostMapping(value = "/chat/{menu}")
	public Map<String, Object> message(HttpServletRequest req, @RequestBody Map<String, Object> data, @PathVariable String menu) throws JsonParseException, JsonMappingException, IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		System.out.println(MapUtils.getMap(data, "data").get("quiz_type"));
		if ("quiz".equals(menu)) {
			if (MapUtils.getMap(data, "data").get("quiz_type") == null || MapUtils.getMap(data, "data").get("quiz_type").toString().equals("null")) {
				map = etriService.quiz(data, req);
			} else {
				map = etriService.quiz_answer(data, req);
			}
		} else if ("dialog".equals(menu))
			map = chatService.dialog(data, req);
		else {
			map = chatService.chatopen(data, req);
		}
		return map;
	}
	/*
	@CrossOrigin("*")
	@PostMapping(value = "/chat/quiz")
	public Map quiz(HttpServletRequest req, @RequestBody Map<String, Object> data) {
		logger.info("?????????");
		Map map = MapUtils.getMap(data, "data");
		if (map.get("text").toString().toLowerCase().contentEquals("quiz") || map.get("text").toString().contentEquals("??????")) {
			return etriService.quiz(data);
		} else {
			return etriService.chatmessage(data);
		}
	}*/

	@CrossOrigin("*")
	@PostMapping(value = "/chat/question")
	public Map stt(HttpServletRequest req, MultipartFile file) throws Exception {
		logger.info("STT API??? ????????? ?????? ????????????", file);
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

	/*
	 * @CrossOrigin("*")
	 * 
	 * @PostMapping(value = "/chat/QAnal") public Map QAnal(HttpServletRequest
	 * req, @RequestBody Map<String, Object> data) { logger.info("WiseQAnal API");
	 * System.out.println(data); Map<String, Object> map = new HashedMap<String,
	 * Object>(); return map; }
	 */

}
