package com.sweey.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sweey.beans.SearchResult;
import com.sweey.beans.VideoItem;
import com.sweey.utils.CommonUtils;
import com.sweey.utils.VideoUtils;

@Controller
public class SearchController {
	private static Logger LOG = LoggerFactory.getLogger(HomePageController.class);
	
	@ResponseBody
	@RequestMapping("/search/{keywords}")
	public SearchResult doSearch(@PathVariable(name = "keywords") String keywords) {
		return doSearch(keywords, 1);
	}
	
	@ResponseBody
	@RequestMapping("/search/{keywords}/{page}")
	public SearchResult doSearch(@PathVariable(name = "keywords") String keywords, @PathVariable(name = "page") int page) {
		SearchResult result = new SearchResult();
		List<VideoItem> videoItems = new ArrayList<VideoItem>();
		List<VideoItem> all = VideoUtils.getVideoList();
		int rowsPerPage = CommonUtils.getConfigs().getRowsPerPage();
		int startIndex = (page - 1) * rowsPerPage;
		int endIndex = startIndex + rowsPerPage;
		int index = 0;
		for (int i = 0; i < all.size(); i++) {
			VideoItem item = all.get(i);
			if (item.getName().toUpperCase().contains(keywords.toUpperCase())) {
				if (index >= startIndex && index < endIndex) {
					videoItems.add(item);
				}
				index++;
			}
		}
		int pageCount =  (int) Math.ceil(((double)index) / rowsPerPage);
		result.setResultItems(videoItems);
		result.setPageCount(pageCount);
		return result;
	}
}
