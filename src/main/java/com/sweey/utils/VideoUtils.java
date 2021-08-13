package com.sweey.utils;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator;
import com.sweey.beans.VideoItem;

@Component
public class VideoUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(VideoUtils.class);
	
	private static Map<String, VideoItem> videoMap;
	
	public VideoUtils(CommonUtils commonUtils) {
		initVideoMap(CommonUtils.getVideoPath());
	}
	
	/**
	 * 获取视频截图
	 * @param filePath 视频路径
	 * @return 视频信息
	 */
	public static byte[] getVideoScreenshot(String targetName, String filePath) {
		ByteArrayOutputStream output = null;
		BufferedInputStream input = null;
		File out = null;
		try {
			String cachePath = CommonUtils.getPictureCachePath();
			LOGGER.info("==============开始截取视频帧==============");
//			Map<String, Object> result = new HashMap<String, Object>();
			FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(filePath);
			
			// 第一帧图片存储位置
			String targetFilePath = cachePath;
			// 图片名称
			String targetFileName = targetName;
			LOGGER.info("视频路径是：" + filePath);
			LOGGER.info("图片名称是：" + targetFileName);
			// 图片类型
			String imageMat = "jpg";
			// 图片的完整路径
			String imagePath = targetFilePath + File.separator + targetFileName + "." + imageMat;
			LOGGER.info("图片路径：" + imagePath);
			
			out = new File(imagePath);
			if (out.exists()) {
				output = new ByteArrayOutputStream();
				input = new BufferedInputStream(new FileInputStream(out));
				byte[] buffer = new byte[4096];
				int len = 0;
				while ((len = input.read(buffer)) > -1) {
					output.write(buffer, 0, len);
				}
				LOGGER.info("使用目录存在的图片：" + imagePath);
				return output.toByteArray();
			}
			
			long start = System.currentTimeMillis();
			grabber.start();
			
			// 截取中间帧
			int length = grabber.getLengthInFrames();
			int middleFrame = length / 10;
			grabber.setFrameNumber(middleFrame);
			
			// 设置视频截取帧（默认取第一帧）
			Frame frame = grabber.grabImage();
			// 视频旋转度
			String rotate = grabber.getVideoMetadata("rotate");
			Java2DFrameConverter converter = new Java2DFrameConverter();
			// 绘制图片
			BufferedImage bi = converter.getBufferedImage(frame);
			if (rotate != null) {
				// 旋转图片
				bi = rotate(bi, Integer.parseInt(rotate));
			}
			// 创建文件
			output = new ByteArrayOutputStream();
			
			ImageIO.write(bi, imageMat, output);
			ImageIO.write(bi, imageMat, out);
			
			// 拼接Map信息
			LOGGER.info("视频的宽：" + bi.getWidth());
			LOGGER.info("视频的高：" + bi.getHeight());
			LOGGER.info("视频的旋转度：" + rotate);
			LOGGER.info("视频的格式：" + grabber.getFormat());
			grabber.stop();
			LOGGER.info("截取视频截图结束, 耗时" + (System.currentTimeMillis() - start) + "毫秒");
			return output.toByteArray();
		} catch (java.lang.Exception e) {
			LOGGER.error("VideoUtils getScreenshot fail", e);
			return null;
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			if (input != null) {
				try {
					input.close();
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			LOGGER.info("==============结束截取视频帧==============");
		}
	}
	
	/**
	 * 获取视频长度
	 * @param filePath 视频文件路径
	 * @return 时间
	 */
	public static long getVideoDuration(String filePath) {
		try {
			LOGGER.info("*************开始获取视频长度*************");
			long start = System.currentTimeMillis();
			FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(filePath);
			
			grabber.start();
			long lengthInTime = grabber.getLengthInTime();
			grabber.stop();
			LOGGER.info("获取耗时：" + (System.currentTimeMillis() - start) + "毫秒");
			return lengthInTime;
		} catch (java.lang.Exception e) {
			LOGGER.error("VideoUtils getVideoDuration fail", e);
			return -1;
		} finally {
			LOGGER.info("*************结束获取视频长度*************");
		}
	}
		
	/**
	 * 根据视频旋转度来调整图片
	 * 
	 * @param src BufferedImage
	 * @param angel 视频旋转度
	 * @return BufferedImage
	 */
	public static BufferedImage rotate(BufferedImage src, int angel) {
		int src_width = src.getWidth();
		int src_height = src.getHeight();
		int type = src.getColorModel().getTransparency();
		Rectangle rect_des = calcRotatedSize(new Rectangle(new Dimension(src_width, src_height)), angel);
		BufferedImage bi = new BufferedImage(rect_des.width, rect_des.height, type);
		Graphics2D g2 = bi.createGraphics();
		g2.translate((rect_des.width - src_width) / 2, (rect_des.height - src_height) / 2);
		g2.rotate(Math.toRadians(angel), src_width / 2, src_height / 2);
		g2.drawImage(src, 0, 0, null);
		g2.dispose();
		return bi;
	}
	
	public static Rectangle calcRotatedSize(Rectangle src, int angel) {
		if (angel >= 90) {
			if (angel / 90 % 2 == 1) {
				int temp = src.height;
				src.height = src.width;
				src.width = temp;
			}
			angel = angel % 90;
		}
		double r = Math.sqrt(src.height * src.height + src.width * src.width);
		double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
		double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
		double angel_dalta_width = Math.atan((double) src.height / src.width);
		double angel_dalta_height = Math.atan((double) src.width / src.height);
		int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_width));
		int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_height));
		int des_width = src.width + len_dalta_width * 2;
		int des_height = src.height + len_dalta_height * 2;
		return new java.awt.Rectangle(new Dimension(des_width, des_height));
	}
	
	public static void initVideoMap(String videoPath) {
		VideoUtils.videoMap = new HashMap<String, VideoItem>();
		File file = new File(videoPath);
		traverseFolder(file);
	}
	
	private static void traverseFolder(File file) {
		if (!file.exists()) {
			return;
		}
		if (file.isDirectory()) {
			File[] listFiles = file.listFiles();
			for (File child : listFiles) {
				traverseFolder(child);
			}
		} else {
			String fileType = file.getName().toUpperCase().substring(file.getName().lastIndexOf(".") + 1);
			List<String> acceptTypes = CommonUtils.getConfigs().getAcceptTypes();
			if (acceptTypes.contains(fileType)) {
				VideoItem item = new VideoItem();
				LOGGER.info("["+ file.getName() +"]");
				String id = CommonUtils.getFileHashCode(file.getAbsolutePath());
				item.setId(id);
				item.setName(file.getName());
				item.setDuration(getVideoDuration(file.getAbsolutePath()));
				item.setScreenShot(Base64Utils.encodeToString(getVideoScreenshot(id + "_cover", file.getAbsolutePath())));
				item.setPath(file.getAbsolutePath());
				VideoUtils.videoMap.put(item.getId(), item);
				LOGGER.info("");
			}
		}
	}

	public static Map<String, VideoItem> getVideoMap() {
		return videoMap;
	}

	public static void setVideoMap(Map<String, VideoItem> videoMap) {
		VideoUtils.videoMap = videoMap;
	}
	
}
