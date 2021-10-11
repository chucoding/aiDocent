package com.challenge.aidocent.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.stereotype.Repository;

@Repository
public class Dictionary {

	public Dictionary() throws IOException {

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream is = classLoader.getResourceAsStream("data/dictionary.properties");
		Properties properties = new java.util.Properties();
		properties.load(is);
		setEc_noun(properties.getProperty("en_noun").split(","));
		setKo_noun(properties.getProperty("ko_noun").split(","));
		setMeasure(properties.getProperty("measure").split(","));
	}

	private String[] ec_noun;
	private String[] ko_noun;
	private String[] measure;

	public String[] getEc_noun() {
		return ec_noun;
	}

	public void setEc_noun(String[] ec_noun) {
		this.ec_noun = ec_noun;
	}

	public String[] getKo_noun() {
		return ko_noun;
	}

	public void setKo_noun(String[] ko_noun) {
		this.ko_noun = ko_noun;
	}

	public String[] getMeasure() {
		return measure;
	}

	public void setMeasure(String[] measure) {
		this.measure = measure;
	}

}
