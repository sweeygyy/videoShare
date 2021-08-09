package com.sweey.utils;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

public class VideoUtils {
	private static final Logger LOGGER = Logger.getLogger(VideoUtils.class);
	
	/**
	 * 获取视频截图
	 * @param filePath 视频路径
	 * @return 视频信息
	 */
	public static Map<String, Object> getVideoScreenshot(String filePath) {
		try {
			String cachePath = CommonUtils.getPictureCachePath();
			LOGGER.info("截取视频帧");
			Map<String, Object> result = new HashMap<String, Object>();
			FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(filePath);
			
			// 第一帧图片存储位置
			String targetFilePath = cachePath;
			// 视频文件名
			String fileName = filePath.substring(filePath.lastIndexOf("\\") + 1);
			// 图片名称
			String targetFileName = fileName.substring(0, fileName.lastIndexOf("."));
			LOGGER.info("视频路径是：" + targetFilePath);
			LOGGER.info("视频文件名：" + fileName);
			LOGGER.info("图片名称是：" + targetFileName);
			
			grabber.start();
			
			// 截取中间帧
			int i = 0;
			int length = grabber.getLengthInFrames();
			int middleFrame = length / 10;
			while (i < middleFrame) {
				grabber.grab();
				i++;
			}
			
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
			// 图片类型
			String imageMat = "jpg";
			// 图片的完整路径
			String imagePath = targetFilePath + File.separator + targetFileName + "." + imageMat;
			// 创建文件
			File output = new File(imagePath);
			ImageIO.write(bi, imageMat, output);
			
			// 拼接Map信息
			result.put("videoWide", bi.getWidth());
			result.put("videoHigh", bi.getHeight());
			result.put("rotate", StringUtils.isBlank(rotate) ? "0" : rotate);
			result.put("format", grabber.getFormat());
			result.put("imgPath", output.getPath());
			LOGGER.info("视频的宽：" + bi.getWidth());
			LOGGER.info("视频的高：" + bi.getHeight());
			LOGGER.info("视频的旋转度：" + rotate);
			LOGGER.info("视频的格式：" + grabber.getFormat());
			grabber.stop();
			LOGGER.info("截取视频截图结束");
			return result;
		} catch (java.lang.Exception e) {
			LOGGER.error("VideoUtils getScreenshot fail", e);
			return null;
		}
	}
	
	/**
	 * 获取视频长度
	 * @param filePath 视频文件路径
	 * @return 时间
	 */
	public static long getVideoDuration(String filePath) {
		try {
			LOGGER.info("获取视频长度");
			FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(filePath);
			
			grabber.start();
			long lengthInTime = grabber.getLengthInTime();
			return lengthInTime;
		} catch (java.lang.Exception e) {
			LOGGER.error("VideoUtils getVideoDuration fail", e);
			return -1;
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
}
