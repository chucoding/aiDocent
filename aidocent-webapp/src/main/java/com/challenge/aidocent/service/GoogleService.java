package com.challenge.aidocent.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.challenge.aidocent.dao.GoogleDao;

@Service
public class GoogleService {


	public String translate(String text) throws IOException {
		GoogleDao dao =new GoogleDao();
		return dao.translateText(text);
	}

	public String vision(HttpServletRequest req, String file_name) throws IOException {
		GoogleDao dao =new GoogleDao();
		String path = req.getSession().getServletContext().getRealPath("/") + "/resources/img/" + file_name;
		return dao.detectText(path);
	}

	public String TTS(HttpServletRequest req, String text) throws Exception {
		GoogleDao dao =new GoogleDao();
		String folder_name = req.getSession().getServletContext().getRealPath("/") + "/resources/tts";
		return dao.synthesizeText(folder_name, text);
	}
}
