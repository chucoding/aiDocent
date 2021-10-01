package com.challenge.aidocent.service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.challenge.aidocent.dao.EntriDao;
import com.challenge.aidocent.util.CacheUtils;

@Service
public class EntriService {

	@Autowired
	CacheUtils cache;

	public Map chatopen() {
		EntriDao chatDao = new EntriDao();
		Map resp = chatDao.chatopen();
		Map return_object = MapUtils.getMap(resp, "return_object");
		String uuid = MapUtils.getString(return_object, "uuid");

		cache.put("uuid", uuid);
		return makeTemplate(resp);
	}

	public Map chatmessage(Map<String, Object> data) {
		EntriDao chatDao = new EntriDao();
		Map map = MapUtils.getMap(data, "data");

		String uuid = (String) cache.get("uuid");
		if (StringUtils.isEmpty(uuid)) {

			Map expireMap = new HashMap();
			Map chatbotInfo = new HashMap();
			Map return_object = MapUtils.getMap(map, "return_object");
			Map result = MapUtils.getMap(return_object, "result");

			chatbotInfo.put("id", "user");

			expireMap.put("id", "chatbot");
			expireMap.put("text", "세션이 만료되었습니다.");
			expireMap.put("createdAt", new Date());
			expireMap.put("user", chatbotInfo);

			return expireMap;
		}

		map.put("uuid", uuid);
		return makeTemplate(chatDao.chatmessage(map));
	}

	private Map makeTemplate(Map resp) {

		Map map = new HashMap();
		Map chatbotInfo = new HashMap();
		Map return_object = MapUtils.getMap(resp, "return_object");
		Map result = MapUtils.getMap(return_object, "result");
		chatbotInfo.put("id", "user");

		map.put("id", "chatbot");
		map.put("text", MapUtils.getString(result, "system_text"));
		map.put("createdAt", new Date());
		map.put("user", chatbotInfo);

		return map;
	}

	// 객체 검출
	public Map<String, Object> ObjectDetect(HttpServletRequest req, MultipartFile file)
			throws IllegalStateException, IOException {

		EntriDao chatDao = new EntriDao();

		Map<String, Object> result = new HashMap<String, Object>();

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

		result.put("file_name", file_name);
		result.put("body", chatDao.ObjectDetect(path));
		return result;

	}

	// 위키사전
	public String wiki(String text) {
		EntriDao chatDao = new EntriDao();
		return chatDao.wiki(text);
	}

	// STT
	public Map<String, Object> stt(HttpServletRequest req, MultipartFile file)
			throws IllegalStateException, IOException, InterruptedException {
		EntriDao chatDao = new EntriDao();
		Map<String, Object> map = new HashMap<String, Object>();
		UUID uuid = UUID.randomUUID();
		String folder_name = req.getSession().getServletContext().getRealPath("/") + "resources" + File.separator
				+ "stt";
		String file_name = uuid.toString().replaceAll("-", "")
				+ file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		String path = folder_name + File.separator + file_name;

		File Folder = new File(folder_name);
		if (Folder.exists() == false) {
			Folder.mkdirs();
		}
		file.transferTo(new File(path));

		File is_file = new File(path);
		boolean isExists;
		while (true) {
			Thread.sleep(1000);
			isExists = is_file.exists();
			if (isExists) {
				map.put("stt", chatDao.stt(path));
				break;
			}
		}
		return map;
	}
}
