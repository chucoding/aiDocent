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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.challenge.aidocent.service.EntriService;
import com.challenge.aidocent.service.GoogleService;

@RestController
public class FileController {

	private static final Logger logger = LoggerFactory.getLogger(FileController.class);

	@CrossOrigin("*")
	@PostMapping(value = "/files")
	public Map<String, Object> upload(HttpServletRequest req, MultipartFile file) throws Exception {
		EntriService entriservice = new EntriService();
		GoogleService googleservice = new GoogleService();
		Map<String, Object> map = new HashMap<String, Object>();

		UUID uuid = UUID.randomUUID();
		String folder_name = req.getSession().getServletContext().getRealPath("/") + "resources" + File.separator
				+ "img";
		String file_name = uuid.toString().replaceAll("-", "")
				+ file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		String path = folder_name + File.separator + file_name;

		File Folder = new File(folder_name);
		if (Folder.exists() == false) {
			Folder.mkdirs();
		}
		file.transferTo(new File(path));

		String result = entriservice.ObjectDetect(req, path);
		JSONObject json = new JSONObject(result);
		
		JSONObject return_object = json.getJSONObject("return_object");
		if(!return_object.isEmpty()) {
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
				result = result.replaceAll(token.split(",")[count++], str);
			}
		}

		map.put("path", "resources/img/" + file_name);
		map.put("translate", result);

		return map;
	}
}
