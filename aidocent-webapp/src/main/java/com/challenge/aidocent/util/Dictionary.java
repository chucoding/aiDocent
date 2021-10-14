package com.challenge.aidocent.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.stereotype.Repository;

@Repository
public class Dictionary {

	public Dictionary() throws IOException {

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream is = classLoader.getResourceAsStream("data/dictionary.properties");
		Properties properties = new java.util.Properties();
		properties.load(is);
		setDic(properties);
	}

	private Map<String, Object> dic;

	public Map<String, Object> getDic() {
		return dic;
	}

	public void setDic(Properties properties) {
		Map<String, Object> map = new HashMap<String, Object>();

		for (String key : properties.stringPropertyNames()) {
			map.put(key, properties.getProperty(key));
		}

		this.dic = map;
	}

}
