package com.sweey.utils;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "custom")
public class Configs {
	
	private String videoPath;
	
	private String pictureCachePath;
	
	private List<String> acceptTypes;
	
	public String getVideoPath() {
		return videoPath;
	}
	
	public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
	}

	public String getPictureCachePath() {
		return pictureCachePath;
	}

	
	public void setPictureCachePath(String pictureCachePath) {
		this.pictureCachePath = pictureCachePath;
	}

	public List<String> getAcceptTypes() {
		return acceptTypes;
	}

	public void setAcceptTypes(List<String> acceptTypes) {
		this.acceptTypes = acceptTypes;
	}
	
}
