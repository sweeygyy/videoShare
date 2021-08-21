package com.sweey.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sweey.beans.VideoItem;
import com.sweey.utils.VideoUtils;

@Controller
public class PlayerController {

	private static final Logger LOG = LoggerFactory.getLogger(PlayerController.class);
	
	public static final String BASE64IMGHEAD = "data:image/png;base64,";
	
	@RequestMapping("/play/{id}")
	public String play(@PathVariable(name = "id") String id, Model model) {
		if (id != null) {
			VideoItem videoItem = VideoUtils.getVideoMap().get(id);
			model.addAttribute("title", videoItem.getName());
			model.addAttribute("poster", BASE64IMGHEAD + videoItem.getScreenShot());
			model.addAttribute("videoPath", "/videoStream/" + id);
		} else {
			model.addAttribute("videoPath", "/video/test2.MOV");
		}
		return "player";
	}
	
	@RequestMapping("/videoStream/{id}")
	public ResponseEntity<byte[]> videoStream(HttpServletRequest request, @PathVariable(name = "id") String id) {
		VideoItem videoItem = VideoUtils.getVideoMap().get(id);
		String videoPath = videoItem.getPath();
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
		header.add("content-type", "video/mp4");
		header.add("Cache-Control", "no-cache, no-store, must-revalidate");
		
		ResponseEntity<byte[]> resp = new ResponseEntity<byte[]>(data, header, HttpStatus.PARTIAL_CONTENT);
		return resp;
	}
	
	@RequestMapping("/preview/{id}")
	public ResponseEntity<byte[]> preview(@PathVariable(name = "id") String id) {
		VideoItem videoItem = VideoUtils.getVideoMap().get(id);
		byte[] data = null;
		if (videoItem.getPreview() != null) {
			data = videoItem.getPreview();
		} else {
			synchronized(videoItem) {
				if (videoItem.getPreview() == null) {
					data = VideoUtils.getVideoPreview(id);
					// 缓存一下
					videoItem.setPreview(data);
				} else {
					data = videoItem.getPreview();
				}
			}
		}
		HttpHeaders header = new HttpHeaders();
		header.add("content-type", "image/webp");
		header.add("Cache-Control", "no-cache, no-store, must-revalidate");
		ResponseEntity<byte[]> resp = new ResponseEntity<byte[]>(data, header, HttpStatus.PARTIAL_CONTENT);
		return resp;
	}
}
