package com.challenge.aidocent.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.MapUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.challenge.aidocent.dao.EtriDao;
import com.challenge.aidocent.dao.GoogleDao;
import com.challenge.aidocent.util.Dictionary;

@Service
public class ChatService {
	@Autowired
	Dictionary dictionary;

	@SuppressWarnings("unchecked")
	public Map<String, Object> chatopen(Map<String, Object> data) {

		Map<String, Object> map = (Map<String, Object>) MapUtils.getMap(data, "data");

		String text = "";
		String menu = "";
		if (map.get("text").equals("1")) {
			text = "사진 또는 그림에 대해 궁금한 것을 물어보세요.";
			menu = "dialog";
		} else if (map.get("text").equals("2")) {
			text = "지금부터 퀴즈를 시작하겠습니다.";
			menu = "quiz";
		} else {
			text = "숫자 1 또는 2를 클릭해주세요";
		}

		Map<String, Object> answer = new HashMap<String, Object>();
		answer.put("position", "left");
		answer.put("type", "text");
		answer.put("text", text);
		answer.put("date", new Date());
		answer.put("menu", menu);
		return answer;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> chatmessage(Map<String, Object> data, HttpServletRequest servletReq) {

		GoogleDao googleDao = new GoogleDao();
		EtriDao chatDao = new EtriDao();

		Map<String, Object> datamap = (Map<String, Object>) MapUtils.getMap(data, "data");
		Map<String, Object> answer = new HashMap<String, Object>();
		String folderName = servletReq.getSession().getServletContext().getRealPath("/") + "resources" + File.separator + "tts";

		Map<String, Object> map = chatDao.wiseNLU_spoken((String) datamap.get("text"));

		Object[] text = nlp(map);
		String[] str = text[0].toString().split(",");
		String[] is_str = new String[str.length];
		String result = "";
		
		if ((boolean) text[1]) {
			String body;
			JSONObject json;
			JSONObject WiKiInfo;
			JSONArray jArray;
			for (int i = 0; i < str.length; i++) {
				body = chatDao.wiki(str[i]);

				json = new JSONObject(body);
				WiKiInfo = json.getJSONObject("return_object").getJSONObject("WiKiInfo");
				jArray = WiKiInfo.getJSONArray("AnswerInfo");
				if (jArray.length() == 0) {
					is_str[i] = str[i] + "의 위키백과내용이 없습니다.";
				} else {
					for (int j = 0; j < jArray.length(); j++) {
						is_str[i] = str[i] + "의 위키백과 내용 : " + jArray.getJSONObject(j).getString("answer");
					}
				}
			}
		} else {
			// 객체 검색
			JSONArray arr = new JSONObject(datamap.get("translate").toString()).getJSONArray("data");

			// 사용자가 입력한 단어 영어로 변역
			for (int j = 0; j < str.length; j++) {
				for (int i = 0; i < dictionary.getKo_noun().length; i++) {
					if (dictionary.getKo_noun()[i].equals(str[j])) {
						is_str[j] = dictionary.getEc_noun()[i];
						break;
					} else {
						is_str[j] = "";
					}
				}
			}
			String temp = "";
			// 사용자가 입력한 단어와 객체검출해서 나온 데이터 비교
			for (int i = 0; i < is_str.length; i++) {
				for (int j = 0; j < arr.length(); j++) {
					if (is_str[i].equals(arr.getJSONObject(j).get("class").toString())) {
						temp = str[i] + "는(은) 있습니다.";
						break;
					} else {
						temp = str[i] + "는(은) 없습니다.";
					}
				}
				is_str[i] = temp;
			}
		}

		for (String object : is_str) {
			if (!object.contains("화면") && !object.contains("이미지") && !object.contains("그림")) {
				result += object + "<br/>";
			}
		}
		answer.put("position", "left");
		answer.put("type", "text");
		answer.put("text", result);
		answer.put("date", new Date());
		answer.put("menu", "dialog");
		answer.put("ttsUrl", googleDao.synthesizeText(folderName, result));

		return answer;
	}

	@SuppressWarnings("unchecked")
	private Object[] nlp(Map<String, Object> data) {
		Object[] result = { "", false };
		JSONObject body = new JSONObject(MapUtils.getMap(data, "return_object"));
		JSONArray arr = body.getJSONArray("sentence");
		body = arr.getJSONObject(0);
		arr = body.getJSONArray("morp");

		System.out.println(arr);

		for (int i = 0; i < arr.length(); i++) {
			if (arr.getJSONObject(i).getString("type").equals("NNG")) {
				if (!result[0].toString().isEmpty())
					result[0] += ",";
				result[0] += arr.getJSONObject(i).getString("lemma").toString();
			}

			if (arr.getJSONObject(i).getString("type").equals("NP")) {
				result[1] = true;
			}
		}
		return result;
	}

}
