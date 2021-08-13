package com.sweey.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sweey.beans.VideoItem;
import com.sweey.utils.CommonUtils;
import com.sweey.utils.VideoUtils;

@Controller
public class HomePageController {
	private static Logger LOG = LoggerFactory.getLogger(HomePageController.class);
	
	@ResponseBody
	@RequestMapping("/homePage")
	public String homePage() {
		String html = CommonUtils.getTemplate("homePage.template");
		return html;
	}
	
	@RequestMapping("/")
	public String index() {
		return "index";
	}
	
	@ResponseBody
	@RequestMapping("/list")
	public List<VideoItem> getVideoList() {
		LOG.info("/list");
		List<VideoItem> result = new ArrayList<VideoItem>();
		Map<String, VideoItem> videoMap = VideoUtils.getVideoMap();
		Collection<VideoItem> values = videoMap.values();
		Iterator<VideoItem> iterator = values.iterator();
		while (iterator.hasNext()) {
			VideoItem next = iterator.next();
			result.add(next);
		}
		return result;
	}
}
