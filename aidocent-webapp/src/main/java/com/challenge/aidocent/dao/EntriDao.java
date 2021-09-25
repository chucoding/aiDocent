package com.challenge.aidocent.dao;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

public class EntriDao {

	private static final String OPEN_API_URL = "http://aiopen.etri.re.kr:8000/";
	private static final String ACCESS_KEY = "417ac904-4b08-4ba6-9f5e-ea214b0994ad";

	public Map chatopen() {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, String> argument = new HashMap<String, String>();

		argument.put("name", "Genie_Pizza");
		argument.put("access_method", "internal_data");
		argument.put("method", "open_dialog");

		params.put("access_key", ACCESS_KEY);
		params.put("argument", argument);

		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Map> entity = new HttpEntity<Map>(params, headers);
		RestTemplate rt = new RestTemplate();
		Map resp = rt.postForObject(OPEN_API_URL + "Dialog", entity, Map.class);

		return resp;
		// throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public Map chatmessage(Map map) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, String> argument = new HashMap<String, String>();

		System.out.println(map);
		argument.put("uuid", (String) map.get("uuid"));
		argument.put("text", (String) map.get("text"));
		argument.put("method", "dialog");

		params.put("access_key", ACCESS_KEY);
		params.put("argument", argument);

		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Map> entity = new HttpEntity<Map>(params, headers);
		RestTemplate rt = new RestTemplate();
		Map resp = rt.postForObject(OPEN_API_URL + "Dialog", entity, Map.class);
		System.out.println(resp);

		return resp;
	}

	// ObjectDetect API
	public String ObjectDetect(String path_) {
		File file = new File(path_);
		String result = "";
		String type = file.getName().substring(file.getName().lastIndexOf(".") + 1);
		String imageContents = "";
		Gson gson = new Gson();

		Map<String, Object> request = new HashMap<>();
		Map<String, String> argument = new HashMap<>();

		try {
			Path path = Paths.get(path_);
			byte[] imageBytes = Files.readAllBytes(path);
			imageContents = Base64.getEncoder().encodeToString(imageBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}

		argument.put("type", type);
		argument.put("file", imageContents);

		request.put("access_key", ACCESS_KEY);
		request.put("argument", argument);

		URL url;
		Integer responseCode = null;
		String responBody = null;
		try {
			url = new URL(OPEN_API_URL + "ObjectDetect");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.write(gson.toJson(request).getBytes("UTF-8"));
			wr.flush();
			wr.close();

			responseCode = con.getResponseCode();
			InputStream is = con.getInputStream();
			byte[] buffer = new byte[is.available()];
			int byteRead = is.read(buffer);
			responBody = new String(buffer);

			System.out.println("[responseCode] " + responseCode);
			System.out.println("[responBody]");
			System.out.println(responBody);
			result = responBody;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			result = "";
		} catch (IOException e) {
			e.printStackTrace();
			result = "";
		}
		return result;
	}

	// Wiki QA API
	public String wiki(String question) {

		/*
		 * irqa:언어분석 기반과 기계독해 기반의 질의응답을 통합한 질의응답 방식 kbqa: 지식베이스 기반의 질의응답 방식 hybridqa :
		 * irqa와 kbqa를 통합한 질의응답 방식
		 */
		String result = "";
		String type = "hybridqa"; // 분석할 문단 데이터

		Gson gson = new Gson();

		Map<String, Object> request = new HashMap<>();
		Map<String, String> argument = new HashMap<>();

		argument.put("question", question);
		argument.put("type", type);

		request.put("access_key", ACCESS_KEY);
		request.put("argument", argument);

		URL url;
		Integer responseCode = null;
		String responBody = null;
		try {
			url = new URL(OPEN_API_URL + "WikiQA");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.write(gson.toJson(request).getBytes("UTF-8"));
			wr.flush();
			wr.close();

			responseCode = con.getResponseCode();
			InputStream is = con.getInputStream();
			byte[] buffer = new byte[is.available()];
			int byteRead = is.read(buffer);
			responBody = new String(buffer);

			System.out.println("[responseCode] " + responseCode);
			System.out.println("[responBody]");
			System.out.println(responBody);
			result = responBody;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			result = "";
		} catch (IOException e) {
			e.printStackTrace();
			result = "";
		}
		return result;
	}
}
