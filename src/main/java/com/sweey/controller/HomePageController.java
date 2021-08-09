package com.sweey.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sweey.utils.CommonUtils;

@Controller
public class HomePageController {
	
	@ResponseBody
	@RequestMapping("/homePage")
	public String homePage() {
		String html = CommonUtils.getTemplate("homePage.template");
		return html;
	}
}
