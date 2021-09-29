package com.challenge.aidocent.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.challenge.aidocent.service.EntriService;
import com.challenge.aidocent.service.GoogleService;

@RestController
public class FileController {

	private static final Logger logger = LoggerFactory.getLogger(FileController.class);
	@Autowired
	EntriService entriservice;
	@Autowired
	GoogleService googleservice;

	@CrossOrigin("*")
	@PostMapping(value = "/files")
	public Map<String, Object> upload(HttpServletRequest req, MultipartFile file) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> result = entriservice.ObjectDetect(req, file);
		String body = (String) result.get("body");
		JSONObject json = new JSONObject(body);

		JSONObject return_object = json.getJSONObject("return_object");
		if (!return_object.isEmpty()) {
			String token = "";
			JSONArray jArray = return_object.getJSONArray("data");
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject str = jArray.getJSONObject(i);
				if (i == 0) {
					token += str.getString("class");
				} else {
					token += "," + str.getString("class");
				}
			}
			String tokens[] = googleservice.translate(token).split(",");
			int count = 0;
			for (String str : tokens) {
				System.out.print(token.split(",")[count] + ":" + str);
				body = body.replaceAll(token.split(",")[count++], str);
			}
			json = new JSONObject(body);
			body = json.getJSONObject("return_object").getJSONArray("data").toString();
		} else {
			body = "";
		}

		String vision_text = googleservice.vision(req, result.get("file_name").toString());
		map.put("path", "resources/img/" + result.get("file_name").toString());
		map.put("translate", body);
		map.put("vision_text", vision_text);

		return map;
	}
}
