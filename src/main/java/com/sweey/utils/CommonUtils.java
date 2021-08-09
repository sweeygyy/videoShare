package com.sweey.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommonUtils {
	private static final Logger LOG = Logger.getLogger(CommonUtils.class);

	private static Configs configs;

	@Autowired
	public CommonUtils(Configs configs) {
		CommonUtils.configs = configs;
	}

	public static byte[] longToByte(long num) {
		return null;
	}

	public static String getTemplate(String name) {
		InputStream inputStream = CommonUtils.class.getResourceAsStream("/templates/" + name);
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
			}
			return sb.toString();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	public static String getPictureCachePath() {
		return configs.getPictureCachePath();
	}

	public static String getVideoPath() {
		return configs.getVideoPath();
	}

	public static Configs getConfigs() {
		return configs;
	}
}
