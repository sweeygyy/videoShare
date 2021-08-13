package com.sweey.beans;

public class VideoItem {
	private String id;
	private String name;
	private long duration;
	private String screenShot;
	private String path;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public String getScreenShot() {
		return screenShot;
	}
	public void setScreenShot(String screenShot) {
		this.screenShot = screenShot;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
}
