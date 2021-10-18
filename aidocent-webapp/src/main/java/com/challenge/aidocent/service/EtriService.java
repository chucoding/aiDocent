package com.challenge.aidocent.service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.MapUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.challenge.aidocent.dao.EtriDao;
import com.challenge.aidocent.dao.GoogleDao;
import com.challenge.aidocent.util.CacheUtils;
import com.challenge.aidocent.util.Dictionary;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EtriService {

	@Autowired
	Dictionary dictionary;

	@Autowired
	CacheUtils cache;
	@Autowired
	ChatService chatservice;

	private static final GoogleDao googleDao = new GoogleDao();

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

		String body = chatDao.ObjectDetect(path);
		saveObjectToCache(body);

		result.put("file_name", file_name);
		result.put("body", body);
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

	public Map<String, Object> quiz(Map<String, Object> data, HttpServletRequest req) {
		String folderName = req.getSession().getServletContext().getRealPath("/") + "resources" + File.separator + "tts";
		String[] quiz_type = { /*"search",*/ "number", "word"/*, "wiki"*/ };
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
			map.put("quiz_type", "null");
			return map;
		}
		JSONObject body = new JSONObject(is_obj.get("translate").toString());
		JSONArray arr = body.getJSONArray("data");
		int select = rand.nextInt(arr.length());
		String select_text = arr.getJSONObject(select).get("class").toString();
		String answer = "";

		// 검출된 객채 있으면 문제 만들기

		switch (quiz_type[rand.nextInt(2)]) {

		/*case "search":
			// 답변(좌표 리스트)과 질문(총 개수)
			JSONArray list = new JSONArray();
		
			for (int i = 0; i < arr.length(); i++) {
				if (select_text.equals(arr.getJSONObject(i).get("class").toString())) {
					list.put(arr.getJSONObject(i));
				}
			}
			for (int i = 0; i < dictionary.getEc_noun().length; i++) {
				if (dictionary.getEc_noun()[i].equals(select_text)) {
					answer = dictionary.getKo_noun()[i];
					break;
				}
			}
		
			map.put("id", "chatbot");
			map.put("text", answer + "을(를) 이미지에서 찾아 클릭해주세요.");
			map.put("answer", list);
			map.put("createdAt", new Date());
			map.put("user", chatbotInfo);
			map.put("quiz_type", "search");
			map.put("menu", "quiz");
			break;*/

		case "number":
			// 답변과 질문(총 개수)
			int total = 0;
			String str = "";

			for (int i = 0; i < arr.length(); i++) {
				if (select_text.equals(arr.getJSONObject(i).get("class").toString())) {
					total += 1;
				}
			}
			System.out.println(select_text.replaceAll(" ", "_") + ".ko");
			str = dictionary.getDic().get(select_text.replaceAll(" ", "_") + ".ko") + "는(은) 몇 " + dictionary.getDic().get(select_text.replaceAll(" ", "_") + ".measure") + " 인가요?";
			map.put("id", "chatbot");
			map.put("text", str);
			map.put("answer", str + "/" + total);
			map.put("createdAt", new Date());
			map.put("user", chatbotInfo);
			map.put("quiz_type", "number");
			map.put("menu", "quiz");
			map.put("ttsUrl", googleDao.synthesizeText(folderName, str));
			break;

		case "word":
			// 답변과 질문
			System.out.println(select_text.replaceAll(" ", "_") + ".ko");
			answer = (String) dictionary.getDic().get(select_text.replaceAll(" ", "_") + ".ko");
			map.put("id", "chatbot");
			map.put("text", select_text + "의 한글 뜻이 어떻게 되나요?");
			map.put("answer", select_text + "의 한글 뜻이 어떻게 되나요?/" + answer);
			map.put("createdAt", new Date());
			map.put("user", chatbotInfo);
			map.put("quiz_type", "word");
			map.put("menu", "quiz");
			map.put("ttsUrl", googleDao.synthesizeText(folderName, select_text + "의 한글 뜻이 어떻게 되나요?"));
			break;

		/*case "wiki":
			// 답변과 위키 내용
			System.out.println("wiki");
			for (int i = 0; i < dictionary.getEc_noun().length; i++) {
				System.out.println(dictionary.getEc_noun()[i]);
				if (dictionary.getEc_noun()[i].equals(select_text)) {
					answer = dictionary.getKo_noun()[i];
		
					break;
				}
			}
		
			String context = "";
			Map<String, Object> result = wiki(answer);
			for (int i = 0; i < result.size(); i++) {
				context += (i + 1 + "번째 설명 : " + result.get(Integer.toString(i + 1)));
				if (i < result.size()) {
					context += "<br/>";
				}
			}
			map.put("id", "chatbot");
			map.put("text", "다음 설명으로 알맞은 단어를 입력하세요. <br/>" + context);
			map.put("answer", answer);
			map.put("createdAt", new Date());
			map.put("user", chatbotInfo);
			map.put("quiz_type", "wiki");
			break;*/
		}

		return map;
	}

	public Map<String, Object> quiz_answer(Map<String, Object> data, HttpServletRequest req) {
		EtriDao chatDao = new EtriDao();
		String folderName = req.getSession().getServletContext().getRealPath("/") + "resources" + File.separator + "tts";
		data = (Map<String, Object>) MapUtils.getMap(data, "data");
		String quiz_type = data.get("quiz_type").toString();
		String answer = data.get("text").toString();
		String[] quiz_QNA = (data.get("quiz_answer").toString()).split("/");
		Map<String, Object> map = null;
		Map chatbotInfo = new HashMap();
		chatbotInfo.put("id", "user");

		String result = "";
		map = chatDao.wiseNLU_spoken(answer);
		Object[] text = chatservice.nlp(map);
		switch (quiz_type) {
		case "number":
			if (Integer.parseInt(quiz_QNA[1].toString()) == Integer.parseInt(text[2].toString())) {
				result = "정답입니다.";
			} else {
				map = chatDao.wiseNLU_spoken(quiz_QNA[0]);
				text = chatservice.nlp(map);
				String[] noun = text[0].toString().split(",");
				if (noun.length != 1) {
					for (String object : noun) {
						result += object;
					}
					noun[0] = result;
				}
				result = dictionary.getDic().get(noun[0]).toString();
				result = noun[0] + "는(은) " + quiz_QNA[1] + " " + dictionary.getDic().get(result.replaceAll(" ", "_") + ".measure") + "입니다.";
			}
			break;
		case "word":
			if (quiz_QNA[1].equals(text[0].toString())) {
				result = "정답입니다.";
			} else {
				result = quiz_QNA[1] + "입니다.";
			}
			break;
		}
		map = new HashMap<String, Object>();
		map.put("id", "chatbot");
		map.put("text", result);
		map.put("createdAt", new Date());
		map.put("user", chatbotInfo);
		map.put("quiz_type", "null");
		map.put("menu", "null");
		map.put("ttsUrl", googleDao.synthesizeText(folderName, result));

		return map;
	}

	@SuppressWarnings("unchecked")
	private void saveObjectToCache(String body) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = mapper.readValue(body, Map.class);
		Map<String, Object> return_object = (Map<String, Object>) MapUtils.getMap(map, "return_object");
		cache.put("image_object", return_object);

		Map<String, Integer> objCntMap = new HashMap<String, Integer>();

		List<Map<String, Object>> dataList = (List<Map<String, Object>>) MapUtils.getObject(return_object, "data");
		for (Map<String, Object> data : dataList) {
			String cl = (String) data.get("class");
			objCntMap.put(cl, objCntMap.getOrDefault(cl, 0) + 1);
		}
		cache.put("objCntMap", objCntMap);
		System.out.println(objCntMap);
	}
}
