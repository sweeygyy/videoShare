package com.sweey.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("configs")
public class Configs {
	
	@Value("${custom.videoPath}")
	private String videoPath;
	
	@Value("${custom.pictureCachePath}")
	private String pictureCachePath;
	
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
}
