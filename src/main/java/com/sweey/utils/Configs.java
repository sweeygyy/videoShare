package com.sweey.utils;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "custom")
public class Configs {
	
	private String videoPath;
	
	private String pictureCachePath;
	
	private String metaCacheFile;
	
	private int rowsPerPage;
	
	private long maxLengthPerRequest;
	
	private boolean renderPerview;
	
	private long minTimeLength;
	
	private long scanInterval;
	
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
	
	public String getMetaCacheFile() {
		return metaCacheFile;
	}

	public void setMetaCacheFile(String metaCacheFile) {
		this.metaCacheFile = metaCacheFile;
	}

	public List<String> getAcceptTypes() {
		return acceptTypes;
	}

	public void setAcceptTypes(List<String> acceptTypes) {
		this.acceptTypes = acceptTypes;
	}

	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}
	
	public long getMaxLengthPerRequest() {
		return maxLengthPerRequest;
	}

	public void setMaxLengthPerRequest(long maxLengthPerRequest) {
		this.maxLengthPerRequest = maxLengthPerRequest;
	}

	public boolean isRenderPerview() {
		return renderPerview;
	}

	public void setRenderPerview(boolean renderPerview) {
		this.renderPerview = renderPerview;
	}

	public long getMinTimeLength() {
		return minTimeLength;
	}

	public void setMinTimeLength(long minTimeLength) {
		this.minTimeLength = minTimeLength;
	}

	public long getScanInterval() {
		return scanInterval;
	}

	public void setScanInterval(long scanInterval) {
		this.scanInterval = scanInterval;
	}
}
