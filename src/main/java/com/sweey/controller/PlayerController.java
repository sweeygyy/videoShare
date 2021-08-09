package com.sweey.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sweey.utils.CommonUtils;
import com.sweey.utils.Configs;

@Controller
public class PlayerController {

	private static final Logger LOG = Logger.getLogger(PlayerController.class);
	
	@RequestMapping("/play/{id}")
	public String play(@PathVariable(name = "id") String id, Model model) {
		model.addAttribute("title", "播放");
		if (id != null) {
			model.addAttribute("videoPath", "/videoStream/" + id);
		} else {
			model.addAttribute("videoPath", "/videoStream/1");
		}
		return "player";
	}
	
	@RequestMapping("/videoStream/{id}")
	public ResponseEntity<byte[]> videoStream(HttpServletRequest request, @PathVariable(name = "id") String id) {
		String videoPath = CommonUtils.getVideoPath();
		videoPath = "F:\\戎晓栋\\待挖掘\\[ThZu.Cc]七天高端外围12-3两场合集\\[ThZu.Cc]七天高端外围12-3骚气眼镜御姐.mp4";
		if (StringUtils.equals(id, "1")) {
			videoPath = "F:\\戎晓栋\\母带\\ABP-554_Uncen.mp4";
		} else if (StringUtils.equals(id, "2")) {
			videoPath = "F:\\戎晓栋\\待挖掘\\[ThZu.Cc]七天高端外围12-4两场合集\\[ThZu.Cc]七天高端外围12-4再操眼镜骚妹.mp4";
		} else if (StringUtils.equals(id, "3")) {
			videoPath = "F:\\戎晓栋\\母带\\bhd1080.com@[無碼流出全套.原版母帶無水印]03.爱音まりあ, ひなた澪, みなみ菜々, 朝川奈穂[86GB]\\bhd1080.com@爱音まりあ.原版母帶無水印[55GB]\\bhd1080.com@0G8A9681.MOV";
		}
		String range = request.getHeader("Range");
		long maxLength = 10485759; // 10MB
		long startPos = 0;
		long endPos = maxLength;
		if (!StringUtils.isBlank(range)) {
			if (range.startsWith("bytes=")) {
				int spliterIndex = range.indexOf("-");
				String startPosStr = range.substring("bytes=".length(), spliterIndex);
				String endPosStr = "";
				if (spliterIndex < range.length()) {
					endPosStr = range.substring(spliterIndex + 1);
				}
				try {
					startPos = Long.parseLong(startPosStr);
					endPos = Long.parseLong(endPosStr);
				} catch (Exception e) {
					if (endPos < startPos) {
						endPos = startPos + maxLength;
					}
				}
			}
		}
		if (endPos - startPos > maxLength) {
			// 限制10MB
			endPos = startPos + maxLength;
		}
		File videoFile = new File(videoPath);
		long length = videoFile.length();
		if (endPos > length) {
			endPos = length - 1;
		}
		long needRead = endPos - startPos + 1;
		long readed = 0;
		
		LOG.info(videoFile.getName() + " : " + startPos +"-"+ endPos +"/" + length);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(videoPath));
			in.skip(startPos);
			byte[] buffer = new byte[4096];
			int len = 0;
			while ((len = in.read(buffer)) > -1) {
				if (readed + len > needRead) {
					out.write(buffer, 0, (int) (needRead - readed));
					break;
				}
				out.write(buffer, 0, len);
				readed += len;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
		
		byte[] data = out.toByteArray();
		HttpHeaders header = new HttpHeaders();
		header.add("Content-Range", "bytes " + startPos +"-"+ endPos +"/" + length);
		header.add("content-type", " video/mp4");
		header.add("Cache-Control", "no-cache, no-store, must-revalidate");
		
		ResponseEntity<byte[]> resp = new ResponseEntity<byte[]>(data, header, HttpStatus.PARTIAL_CONTENT);
		return resp;
	}
	
}
