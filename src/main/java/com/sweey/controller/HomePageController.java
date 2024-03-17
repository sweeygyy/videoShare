package com.sweey.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
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
	public String index(@RequestHeader("User-Agent") String userAgent) {
		if (CommonUtils.isMobile(userAgent)) {
			return "redirect:mobile";
		}
		return "index";
	}
	
	@RequestMapping("/mobile")
	public String mobile() {
		return "mobile";
	}
	
	@ResponseBody
	@RequestMapping("/list")
	public List<VideoItem> getVideoFirstList() {
		int page = 1;
		return getVideoList(page);
	}
	
	@ResponseBody
	@RequestMapping("/list/{page}")
	public List<VideoItem> getVideoList(@PathVariable(name = "page") int page) {
		int rowsPerPage = CommonUtils.getConfigs().getRowsPerPage();
		int startIndex = (page - 1) * rowsPerPage;
		int endIndex = startIndex + rowsPerPage;
		List<VideoItem> result = new ArrayList<VideoItem>();
		List<VideoItem> all = VideoUtils.getVideoList();
		for (int i = startIndex; i < endIndex && i < all.size(); i++) {
			result.add(all.get(i));
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/pageCount")
	public int getPageCount() {
		int rowsPerPage = CommonUtils.getConfigs().getRowsPerPage();
		int videoCount = VideoUtils.getVideoList().size();
		return (int) Math.ceil(((double)videoCount) / rowsPerPage);
	}
}
