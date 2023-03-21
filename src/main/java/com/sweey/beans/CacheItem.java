package com.sweey.beans;

public class CacheItem {
	private String filePatch;
	private String hashCode;
	private long lastModified;
	private long duration;
	private int framesCount;
	public String getFilePatch() {
		return filePatch;
	}
	public void setFilePatch(String filePatch) {
		this.filePatch = filePatch;
	}
	public String getHashCode() {
		return hashCode;
	}
	public void setHashCode(String hashCode) {
		this.hashCode = hashCode;
	}
	public long getLastModified() {
		return lastModified;
	}
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public int getFramesCount() {
		return framesCount;
	}
	public void setFramesCount(int framesCount) {
		this.framesCount = framesCount;
	}
}
