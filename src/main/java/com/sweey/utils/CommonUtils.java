package com.sweey.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommonUtils {
	private static final Logger LOG = LoggerFactory.getLogger(CommonUtils.class);

	private static Configs configs;

	@Autowired
	public CommonUtils(Configs configs) {
		CommonUtils.configs = configs;
	}

	public static byte[] longToByte(long num) {
		return null;
	}
	
	public static String getFileHashCode(String filePath) {
		File video = new File(filePath);
		if (!video.exists()) {
			return "";
		}
		LOG.info(">>>>>>>>>>>>>>开始计算hash值<<<<<<<<<<<<<");
		FileInputStream fis;
		try {
			fis = new FileInputStream(video);
			return md5HashCode32(fis);
		} catch (FileNotFoundException e) {
			LOG.error(e.getMessage());
			return "";
		}
	}
	
	/**
     * java计算文件32位md5值
     * @param fis 输入流
     * @return
     */
  	public static String md5HashCode32(InputStream fis) {
  		try {
  			long start = System.currentTimeMillis();
  			//拿到一个MD5转换器,如果想使用SHA-1或SHA-256，则传入SHA-1,SHA-256  
  			MessageDigest md = MessageDigest.getInstance("MD5");
  			
  			//分多次将一个文件读入，对于大型文件而言，比较推荐这种方式，占用内存比较少。
  			byte[] buffer = new byte[4096];
  			int length = -1;
  			// 文件过大，隔50MB取一次
  			long step = 10485760 * 5;
  			while ((length = fis.read(buffer, 0, 1024)) != -1) {
  				md.update(buffer, 0, length);
  				fis.skip(step);
  			}
  			fis.close();
  			
  			//转换并返回包含16个元素字节数组,返回数值范围为-128到127
  			byte[] md5Bytes  = md.digest();
  			StringBuffer hexValue = new StringBuffer();
  			for (int i = 0; i < md5Bytes.length; i++) {
  				int val = ((int) md5Bytes[i]) & 0xff;//解释参见最下方
  				if (val < 16) {
  					/**
  					 * 如果小于16，那么val值的16进制形式必然为一位，
  					 * 因为十进制0,1...9,10,11,12,13,14,15 对应的 16进制为 0,1...9,a,b,c,d,e,f;
  					 * 此处高位补0。
  					 */
  					hexValue.append("0");
  				}
  				//这里借助了Integer类的方法实现16进制的转换 
  				hexValue.append(Integer.toHexString(val));
  			}
  			LOG.info("获取hash耗时：" + (System.currentTimeMillis() - start) + "毫秒");
  			return hexValue.toString();
  		} catch (Exception e) {
  			e.printStackTrace();
  			return "";
  		} finally {
  			if (fis != null) {
  				try {
  					fis.close();
  				} catch (Exception e) {
  					LOG.error(e.getMessage());
  				}
  			}
  			LOG.info(">>>>>>>>>>>>>>结束计算hash值<<<<<<<<<<<<<");
  		}
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
	
	public static String getMetaCacheFile() {
		return getPictureCachePath() + File.separator + configs.getMetaCacheFile();
	}

	public static String getVideoPath() {
		return configs.getVideoPath();
	}

	public static Configs getConfigs() {
		return configs;
	}
}
