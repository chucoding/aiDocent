package com.challenge.aidocent.service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.MapUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import com.challenge.aidocent.dao.EtriDao;
import com.challenge.aidocent.util.CacheUtils;
import com.challenge.aidocent.util.Dictionary;

@Service
public class EtriService {

	@Autowired
	Dictionary dictionary;

	@Autowired
	CacheUtils cache;

	public Map chatopen() {
		EtriDao chatDao = new EtriDao();
		Map resp = chatDao.chatopen();
		Map return_object = MapUtils.getMap(resp, "return_object");
		String uuid = MapUtils.getString(return_object, "uuid");

		cache.put("uuid", uuid);
		return makeTemplate(resp);
	}

	public Map chatmessage(Map<String, Object> data) {
		EtriDao chatDao = new EtriDao();
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
	public Map<String, Object> ObjectDetect(HttpServletRequest req, MultipartFile file) throws IllegalStateException, IOException {

		EtriDao chatDao = new EtriDao();

		Map<String, Object> result = new HashMap<String, Object>();

		UUID uuid = UUID.randomUUID();
		String folder_name = req.getSession().getServletContext().getRealPath("/") + "resources" + File.separator + "img";
		String file_name = uuid.toString().replaceAll("-", "") + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
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
	public Map<String, Object> wiki(String text) {
		EtriDao chatDao = new EtriDao();
		Map<String, Object> result = new HashMap<String, Object>();
		String body = chatDao.wiki(text);

		JSONObject json = new JSONObject(body);
		JSONObject WiKiInfo = json.getJSONObject("return_object").getJSONObject("WiKiInfo");
		JSONArray jArray = WiKiInfo.getJSONArray("AnswerInfo");

		for (int i = 0; i < jArray.length(); i++) {
			result.put(Integer.toString(i + 1), jArray.getJSONObject(i).getString("answer"));
		}

		return result;
	}

	// STT
	public Map<String, Object> stt(HttpServletRequest req, MultipartFile file) throws IllegalStateException, IOException, InterruptedException {
		EtriDao chatDao = new EtriDao();
		Map<String, Object> map = new HashMap<String, Object>();
		UUID uuid = UUID.randomUUID();
		String folder_name = req.getSession().getServletContext().getRealPath("/") + "resources" + File.separator + "stt";
		String file_name = uuid.toString().replaceAll("-", "") + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
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

	// WiseQAnal
	public String WiseQAnal(String text) {
		EtriDao chatDao = new EtriDao();
		return chatDao.WiseQAnal(text);
	}

	public Map<String, Object> quiz(Map<String, Object> data) {
		String[] quiz_type = { /*"search", "number",*/ "word", "wiki" };
		Random rand = new Random();
		Map<?, ?> is_obj = MapUtils.getMap(data, "data");
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> chatbotInfo = new HashMap<String, Object>();
		chatbotInfo.put("id", "user");

		// 검출된 객채 없으면 리턴
		if (is_obj.get("translate").toString().isEmpty()) {
			map.put("id", "chatbot");
			map.put("text", "검출된 객체가 없어 퀴즈를 진행할 수 없습니다.");
			map.put("createdAt", new Date());
			map.put("user", chatbotInfo);
			map.put("quiz_type", "Null");
			return map;
		}
		JSONObject body = new JSONObject(is_obj.get("translate").toString());
		JSONArray arr = body.getJSONArray("data");
		int select = rand.nextInt(arr.length());
		String answer = "";
		System.out.println(arr.getJSONObject(select).get("class").toString());
		// 검출된 객채 있으면 문제 만들기
		switch (quiz_type[rand.nextInt(2)]) {
		/*case "search":
			// 답변(좌표 리스트)과 질문(총 개수)
			map.put("id", "chatbot");
			map.put("text", "을(를) 이미지에서 찾아 클릭해주세요.");
			map.put("createdAt", new Date());
			map.put("user", chatbotInfo);
			map.put("quiz_type", "search");
			break;
		case "number":
			// 답변과 질문(총 개수)
			map.put("id", "chatbot");
			map.put("text", "인가요?");
			map.put("createdAt", new Date());
			map.put("user", chatbotInfo);
			map.put("quiz_type", "number");
			break;*/
		case "word":
			// 답변과 질문
			for (int i = 0; i < dictionary.getEc_noun().length; i++) {
				if (dictionary.getEc_noun()[i].equals(arr.getJSONObject(select).get("class").toString())) {
					answer = dictionary.getKo_noun()[i];
					break;
				}
			}
			map.put("id", "chatbot");
			map.put("text", arr.getJSONObject(select).get("class").toString() + "의 한글 뜻이 어떻게 되나요?");
			map.put("answer", answer);
			map.put("createdAt", new Date());
			map.put("user", chatbotInfo);
			map.put("quiz_type", "word");
			break;
		case "wiki":
			// 답변과 위키 내용
			System.out.println("wiki");
			for (int i = 0; i < dictionary.getEc_noun().length; i++) {
				System.out.println(dictionary.getEc_noun()[i]);
				if (dictionary.getEc_noun()[i].equals(arr.getJSONObject(select).get("class").toString())) {
					answer = dictionary.getKo_noun()[i];

					break;
				}
			}

			String context = "";
			Map<String, Object> result = wiki(answer);
			for (int i = 0; i < result.size(); i++) {
				context += (i + 1 + "번째 설명 : " + result.get(Integer.toString(i + 1)) + "<br/>");
			}
			map.put("id", "chatbot");
			map.put("text", "다음 설명으로 알맞은 단어를 입력하세요. <br/>" + context);
			map.put("answer", answer);
			map.put("createdAt", new Date());
			map.put("user", chatbotInfo);
			map.put("quiz_type", "wiki");
			break;
		}

		return map;
	}
}
