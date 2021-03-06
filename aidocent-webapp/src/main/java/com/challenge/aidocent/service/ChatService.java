package com.challenge.aidocent.service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.MapUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.challenge.aidocent.dao.EtriDao;
import com.challenge.aidocent.dao.GoogleDao;
import com.challenge.aidocent.util.CacheUtils;
import com.challenge.aidocent.util.Dictionary;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Service
public class ChatService {

	@Autowired
	Dictionary dictionary;

	@Autowired
	CacheUtils cache;

	private static final GoogleDao googleDao = new GoogleDao();
	private static final EtriDao etriDao = new EtriDao();

	@SuppressWarnings("unchecked")
	public Map<String, Object> chatopen(Map<String, Object> data, HttpServletRequest servletReq) {
		String folderName = servletReq.getSession().getServletContext().getRealPath("/") + "resources" + File.separator + "tts";
		Map<String, Object> map = (Map<String, Object>) MapUtils.getMap(data, "data");
		System.out.println(map);
		String text = "";
		String menu = "";
		if (map.get("text").toString().replaceAll(" ", "").equals("질문하기")) {
			text = "사진 또는 그림에 대해 궁금한 것을 물어보세요.";
			menu = "dialog";
		} else if (map.get("text").toString().replaceAll(" ", "").equals("퀴즈풀기")) {
			text = "지금부터 퀴즈를 시작하겠습니다.";
			menu = "quiz";
		} else {
			text = "\"질문하기\" 또는  \"퀴즈풀기\"로만 입력해주세요. ";
		}

		Map<String, Object> resp = etriDao.openDialog();
		Map<String, Object> return_object = (Map<String, Object>) MapUtils.getMap(resp, "return_object");
		String uuid = MapUtils.getString(return_object, "uuid");
		cache.put("uuid", uuid);

		Map<String, Object> answer = new HashMap<String, Object>();
		answer.put("position", "left");
		answer.put("type", "text");
		answer.put("text", text);
		answer.put("date", new Date());
		answer.put("menu", menu);
		answer.put("ttsUrl", googleDao.synthesizeText(folderName, text));

		return answer;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> dialog(Map<String, Object> req, HttpServletRequest servletReq) throws JsonParseException, JsonMappingException, IOException {

		Map<String, Object> answerMap = new HashMap<String, Object>();
		String folderName = servletReq.getSession().getServletContext().getRealPath("/") + "resources" + File.separator + "tts";

		Map<String, Object> data = (Map<String, Object>) MapUtils.getMap(req, "data");
		data.put("uuid", (String) cache.get("uuid"));

		Map<String, Object> resp = etriDao.getDialog(data);
		Map<String, Object> return_object = (Map<String, Object>) MapUtils.getMap(resp, "return_object");
		Map<String, Object> resultMap = (Map<String, Object>) MapUtils.getMap(return_object, "result");
		String system_text = MapUtils.getString(resultMap, "system_text");
		String text = getAnswer(system_text, (String) data.get("text"));

		answerMap.put("position", "left");
		answerMap.put("type", "text");
		answerMap.put("text", text);
		answerMap.put("date", new Date());
		answerMap.put("menu", "dialog");
		answerMap.put("ttsUrl", googleDao.synthesizeText(folderName, text));

		return answerMap;
	}

	@SuppressWarnings("unchecked")
	private String getAnswer(String system_text, String question) throws JsonParseException, JsonMappingException, IOException {
		String answer = "";
		StringTokenizer st = new StringTokenizer(system_text, " ");
		String intent = st.nextToken().trim();
		
		// 복합질문 처리 어떻게??
		switch (intent) {

		case "wiki":
			String body = etriDao.wiki(question);
			JSONObject json = new JSONObject(body);
			JSONObject WiKiInfo = json.getJSONObject("return_object").getJSONObject("WiKiInfo");
			JSONArray jArray = WiKiInfo.getJSONArray("AnswerInfo");
			answer = jArray.getJSONObject(0).getString("answer");
			break;
		case "exist":
			Map<String, Integer> objCntMap = (Map<String, Integer>) cache.get("objCntMap");
			String entity = st.nextToken().trim();
			System.out.println(MapUtils.getIntValue(objCntMap, entity));
			System.out.println(objCntMap.get(entity));
			int cnt = MapUtils.getIntValue(objCntMap, entity);

			String meaure = (String) dictionary.getDic().get(entity + ".measure");
			String name = (String) dictionary.getDic().get(entity + ".ko");

			if (cnt < 1) {
				answer = String.format("화면에 %s이(가) 없습니다.", name);
			} else {
				answer = String.format("화면에 %s이(가) %d%s 있습니다.", name, cnt, meaure);
			}
			break;
		default:
			String ambiguity = removeTags(intent);
			answer = ambiguity.substring(0, intent.length() - 1);
			if ("".equals(answer) || answer.isEmpty()) {
				answer = "말씀해주신 부분에 대해 잘 모르겠어요.";
			}
		}

		return answer;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> chatmessage(Map<String, Object> data, HttpServletRequest servletReq) {

		Map<String, Object> datamap = (Map<String, Object>) MapUtils.getMap(data, "data");
		Map<String, Object> answer = new HashMap<String, Object>();
		String folderName = servletReq.getSession().getServletContext().getRealPath("/") + "resources" + File.separator + "tts";

		Map<String, Object> map = etriDao.wiseNLU_spoken((String) datamap.get("text"));

		Object[] text = nlp(map);
		String[] str = text[0].toString().split(",");
		String[] is_str = new String[str.length];
		String result = "";

		// 사용자가 입력한 단어 영어로 변역
		for (int j = 0; j < str.length; j++) {
			is_str[j] = dictionary.getDic().containsKey(str[j]) ? dictionary.getDic().get(str[j]).toString() : "";
		}
		JSONArray jArray = new JSONObject(datamap.get("translate").toString()).getJSONArray("data");
		String temp = "";
		// 사용자가 입력한 단어와 객체검출해서 나온 데이터 비교
		for (int i = 0; i < is_str.length; i++) {
			for (int j = 0; j < jArray.length(); j++) {
				if (is_str[i].equals(jArray.getJSONObject(j).get("class").toString())) {
					temp = str[i] + "는(은) 있습니다.";
					break;
				} /*else {
					temp = str[i] + "는(은) 없습니다.";
					}*/
			}
			is_str[i] = temp;
		}

		if ((boolean) text[1]) {
			String body;
			JSONObject json;
			JSONObject WiKiInfo;

			for (int i = 0; i < str.length; i++) {
				if (!is_str[i].isEmpty()) {
					body = etriDao.wiki(str[i]);

					json = new JSONObject(body);
					WiKiInfo = json.getJSONObject("return_object").getJSONObject("WiKiInfo");
					jArray = WiKiInfo.getJSONArray("AnswerInfo");
					/*if (jArray.length() == 0) {
						is_str[i] = str[i] + "의 위키백과내용이 없습니다.";
					} else {*/
					for (int j = 0; j < jArray.length(); j++) {
						is_str[i] = str[i] + "의 위키백과 내용 : " + jArray.getJSONObject(j).getString("answer");
					}
					/*	}*/
				}
			}
		}

		for (String object : is_str) {
			if (!object.isEmpty() && (!object.contains("화면") && !object.contains("이미지") && !object.contains("그림"))) {
				result += object + "<br/>";
			}
		}

		if ("".equals(result) || result.isEmpty()) {
			datamap.put("uuid", (String) cache.get("uuid"));
			Map<String, Object> resp = etriDao.getDialog(datamap);
			Map<String, Object> return_object = (Map<String, Object>) MapUtils.getMap(resp, "return_object");
			Map<String, Object> resultMap = (Map<String, Object>) MapUtils.getMap(return_object, "result");
			String system_text = MapUtils.getString(resultMap, "system_text").trim();
			system_text = system_text.substring(0, system_text.length() - 1);
			result = removeTags(system_text);

			if ("".equals(result) || result.isEmpty()) {
				result = "말씀해주신 부분에 대해 잘 모르겠어요.";
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

	public String removeTags(String str) {
		String s = StringUtils.delete(str, "(chat)");
		return StringUtils.delete(s, "(/chat)");
	}

	@SuppressWarnings("unchecked")
	public Object[] nlp(Map<String, Object> data) {
		Object[] result = { "", false, 0 };

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
			if (arr.getJSONObject(i).getString("type").equals("SN")) {
				result[2] = arr.getJSONObject(i).getString("lemma").toString();
			}

		}
		return result;
	}

}
