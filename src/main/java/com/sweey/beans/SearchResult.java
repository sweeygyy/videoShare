package com.sweey.beans;

import java.util.List;

public class SearchResult {
	private int pageCount;
	private List<VideoItem> resultItems;
	public int getPageCount() {
		return pageCount;
	}
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	public List<VideoItem> getResultItems() {
		return resultItems;
	}
	public void setResultItems(List<VideoItem> resultItems) {
		this.resultItems = resultItems;
	}
}
