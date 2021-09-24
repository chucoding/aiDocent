package com.challenge.aidocent.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {
	
	private static final Logger logger = LoggerFactory.getLogger(FileController.class);
	
	@CrossOrigin("*")
	@PostMapping(value = "/files")
	public void upload(MultipartFile file) throws Exception {
		logger.info("���� ���ε�");
		// 서버에 파일 저장
		// 객체 검출
		// 번역
		
	}
}
