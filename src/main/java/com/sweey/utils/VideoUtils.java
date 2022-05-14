package com.sweey.utils;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import com.luciad.imageio.webp.WebPWriteParam;
import com.sweey.beans.VideoItem;

@Component
public class VideoUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(VideoUtils.class);

	private static Map<String, VideoItem> videoMap;

	private static List<VideoItem> videoList;

	public VideoUtils(CommonUtils commonUtils) {
		initVideos(CommonUtils.getVideoPath());
		startScanThread(CommonUtils.getVideoPath());
	}

	/**
	 * 获取视频截图
	 * 
	 * @param filePath 视频路径
	 * @return 视频信息
	 */
	public static byte[] getVideoScreenshot(String targetName, String filePath) {
		ByteArrayOutputStream output = null;
		BufferedInputStream input = null;
		FileImageOutputStream fout = null;
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
			String imageMat = "webp";
			// 图片的完整路径
			String imagePath = targetFilePath + File.separator + targetFileName + "." + imageMat;
			LOGGER.info("图片路径：" + imagePath);

			File file = new File(imagePath);
			if (file.exists()) {
				LOGGER.info("使用目录存在的图片：" + imagePath);
			} else {
				long start = System.currentTimeMillis();
				grabber.start();

				// 截取中间帧
				int length = grabber.getLengthInFrames();
				int middleFrame = length / 10;
				// 设置视频截取帧
				grabber.setFrameNumber(middleFrame);

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
				fout = new FileImageOutputStream(file);
				img2webp(bi, fout);
				// 拼接Map信息
				LOGGER.info("视频的宽：" + bi.getWidth());
				LOGGER.info("视频的高：" + bi.getHeight());
				LOGGER.info("视频的旋转度：" + rotate);
				LOGGER.info("视频的格式：" + grabber.getFormat());
				grabber.stop();
				LOGGER.info("截取视频截图结束, 耗时" + (System.currentTimeMillis() - start) + "毫秒");
			}
			output = new ByteArrayOutputStream();
			input = new BufferedInputStream(new FileInputStream(file));
			byte[] buffer = new byte[4096];
			int len = 0;
			while ((len = input.read(buffer)) > -1) {
				output.write(buffer, 0, len);
			}
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
			if (fout != null) {
				try {
					fout.close();
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
	 * 获取视频截图
	 * 
	 * @param filePath 视频路径
	 * @return 视频信息
	 */
	@Deprecated
	public static byte[] getVideoScreenshotByOpenCV(String targetName, String filePath) {
		ByteArrayOutputStream output = null;
		BufferedInputStream input = null;
		FileImageOutputStream fout = null;
		VideoCapture cap = null;
		try {
			String cachePath = CommonUtils.getPictureCachePath();
			LOGGER.info("==============开始截取视频帧(OpenCV)==============");

			// 第一帧图片存储位置
			String targetFilePath = cachePath;
			// 图片名称
			String targetFileName = targetName;
			LOGGER.info("视频路径是：" + filePath);
			LOGGER.info("图片名称是：" + targetFileName);
			// 图片类型
			String imageMat = "webp";
			// 图片的完整路径
			String imagePath = targetFilePath + File.separator + targetFileName + "." + imageMat;
			LOGGER.info("图片路径：" + imagePath);

			File file = new File(imagePath);
			if (file.exists()) {
				LOGGER.info("使用目录存在的图片：" + imagePath);
			} else {
				long start = System.currentTimeMillis();
				// 读取视频文件
				cap = new org.opencv.videoio.VideoCapture(filePath);
				LOGGER.info("打开结果：" + cap.isOpened());
				// 判断视频是否打开
				if (cap.isOpened()) {
					// 总帧数
					double length = cap.get(OpenCVUtils.CAP_PROP_FRAME_COUNT);
					// 截取中间帧
					int middleFrame = (int) length / 10;
					Mat frame = new Mat();
					// 设置视频截取帧
					cap.set(OpenCVUtils.CAP_PROP_POS_FRAMES, middleFrame);
					// 读取下一帧画面
					BufferedImage bi = null;
					if (cap.read(frame)) {
						bi = (BufferedImage) HighGui.toBufferedImage(frame);
					}
					// 创建文件
					fout = new FileImageOutputStream(file);
					img2webp(bi, fout);
					// 创建文件
					fout = new FileImageOutputStream(file);
					// 拼接Map信息
					cap.release();
					LOGGER.info("截取视频截图结束, 耗时" + (System.currentTimeMillis() - start) + "毫秒");
				}
			}
			output = new ByteArrayOutputStream();
			input = new BufferedInputStream(new FileInputStream(file));
			byte[] buffer = new byte[4096];
			int len = 0;
			while ((len = input.read(buffer)) > -1) {
				output.write(buffer, 0, len);
			}
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
			if (fout != null) {
				try {
					fout.close();
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
	 * 获取视频预览图
	 * 
	 * @param filePath 视频id；hash码
	 * @return 视频信息
	 */
	public static byte[] getVideoPreview(String videoId) {
		VideoItem videoItem = getVideoMap().get(videoId);
		ByteArrayOutputStream output = null;
		BufferedInputStream input = null;
		FileImageOutputStream fout = null;
		String targetFilePath = CommonUtils.getPictureCachePath();
		String targetFileName = videoId + "_preview";
		String imageMat = "webp";
		String imagePath = targetFilePath + File.separator + targetFileName + "." + imageMat;
		long start = System.currentTimeMillis();
		try {
			LOGGER.info("==============开始视频预览帧截取==============");
			File file = new File(imagePath);
			if (file.exists()) {
				LOGGER.info("使用目录存在的图片：" + imagePath);
			} else {
				long duration = videoItem.getDuration();
				int frames = videoItem.getFramesCount();
				String filePath = videoItem.getPath();
				long step = frames * 1000l * 1000 * 5 / duration; // 默认5秒截取一帧
				if (duration > 1000 * 1000 * 50 * 10) {
					step = (frames - 1) / 99; // 超过500秒的视频，只截取100张图片
				}
				int currentFrame = 1;
				int threadCount = 8;
				ConcurrentHashMap<Integer, BufferedImage> threadImgs = new ConcurrentHashMap<Integer, BufferedImage>();
				int index = 0;
				List<PreviewScreenshotThread> threadList = new ArrayList<PreviewScreenshotThread>();
				while (currentFrame <= frames) {
					if (threadList.size() < threadCount) {
						FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(filePath);
						grabber.start();
						PreviewScreenshotThread thread = new PreviewScreenshotThread(grabber, threadImgs);
						threadList.add(thread);
					}
					PreviewScreenshotThread thread = threadList.get(index % threadCount);
					thread.addProcessFrame(index, currentFrame);
					currentFrame += step;
					index++;
				}
				CountDownLatch latch = new CountDownLatch(threadList.size());
				for (int i = 0; i < threadList.size(); i++) {
					PreviewScreenshotThread thread = threadList.get(i);
					thread.setDownLatch(latch);
					new Thread(thread).start();
				}
				latch.await();
				int x = 0;
				int y = 0;
				BufferedImage previewPic = new BufferedImage(1600, 900, BufferedImage.TYPE_INT_RGB);
				for (int i = 0; i < threadImgs.size(); i++) {
					BufferedImage bi = threadImgs.get(i);
					previewPic.getGraphics().drawImage(bi, x, y, x + 160, y + 90, 0, 0, bi.getWidth(), bi.getHeight(),
							null);
					x += 160;
					if (x >= 1600) {
						y += 90;
						x = 0;
					}
				}
//				}
				fout = new FileImageOutputStream(file);
				img2webp(previewPic, fout);
			}
			output = new ByteArrayOutputStream();
			input = new BufferedInputStream(new FileInputStream(file));
			byte[] buffer = new byte[4096];
			int len = 0;
			while ((len = input.read(buffer)) > -1) {
				output.write(buffer, 0, len);
			}
			return output.toByteArray();
		} catch (Exception e) {
			LOGGER.error("VideoUtils getVideoPreview fail", e);
			return null;
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			if (fout != null) {
				try {
					fout.close();
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
			LOGGER.info("截取预览耗时:" + (System.currentTimeMillis() - start) + "毫秒");
			LOGGER.info("==============结束视频预览帧截取==============");
		}
	}

	/**
	 * 获取视频长度
	 * 
	 * @param filePath 视频文件路径
	 * @return 时间
	 */
	public static Map<String, Number> getVideoDuration(String filePath) {
		Map<String, Number> result = new HashMap<String, Number>();
		try {
			LOGGER.info("*************开始获取视频长度*************");
			long start = System.currentTimeMillis();
			FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(filePath);

			grabber.start();
			long lengthInTime = grabber.getLengthInTime();
			int lengthInFrames = grabber.getLengthInFrames();
			grabber.stop();
			result.put("time", lengthInTime);
			result.put("frames", lengthInFrames);
			LOGGER.info("获取耗时：" + (System.currentTimeMillis() - start) + "毫秒");
			return result;
		} catch (java.lang.Exception e) {
			LOGGER.error("VideoUtils getVideoDuration fail", e);
			result.put("time", -1);
			result.put("frames", -1);
			return result;
		} finally {
			LOGGER.info("*************结束获取视频长度*************");
		}
	}
	
	/**
	 * 获取视频长度
	 * 
	 * @param filePath 视频文件路径
	 * @return 时间
	 */
	@Deprecated
	public static Map<String, Number> getVideoDurationByOpenCV(String filePath) {
		Map<String, Number> result = new HashMap<String, Number>();
		VideoCapture cap = new VideoCapture(filePath);
		try {
			LOGGER.info("*************开始获取视频长度(OpenCV)*************");
			long start = System.currentTimeMillis();
			// 判断视频是否打开
			if (cap.isOpened()) {
				// 总帧数
				int lengthInFrames = (int) cap.get(OpenCVUtils.CAP_PROP_FRAME_COUNT);
				// 帧率
				double fps = cap.get(OpenCVUtils.CAP_PROP_FPS);
				// 时间长度
				long lengthInTime = Math.round(lengthInFrames / fps) * 1000 * 1000L;
				cap.release();
				result.put("time", lengthInTime);
				result.put("frames", lengthInFrames);
			}
			LOGGER.info("获取耗时：" + (System.currentTimeMillis() - start) + "毫秒");
			return result;
		} catch (java.lang.Exception e) {
			LOGGER.error("VideoUtils getVideoDurationByOpenCV fail", e);
			result.put("time", -1);
			result.put("frames", -1);
			return result;
		} finally {
			LOGGER.info("*************结束获取视频长度*************");
		}
	}

	/**
	 * 根据视频旋转度来调整图片
	 * 
	 * @param src   BufferedImage
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

	public static void img2webp(BufferedImage image, FileImageOutputStream out) {
		// Obtain a WebP ImageWriter instance
		ImageWriter writer = ImageIO.getImageWritersByMIMEType("image/webp").next();

		// Configure encoding parameters
		WebPWriteParam writeParam = new WebPWriteParam(writer.getLocale());
		writeParam.setCompressionMode(WebPWriteParam.MODE_DEFAULT);

		// Configure the output on the ImageWriter
		writer.setOutput(out);

		// Encode
		try {
			writer.write(null, new IIOImage(image, null, null), writeParam);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	public static void initVideos(String videoPath) {
		VideoUtils.videoMap = new HashMap<String, VideoItem>();
		VideoUtils.videoList = new ArrayList<VideoItem>();
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
			VideoItem item = file2VideoItem(file);
			if (item != null) {
				if (VideoUtils.videoMap.get(item.getId()) != null) {
					VideoItem oldItem = VideoUtils.videoMap.get(item.getId());
					oldItem.setName(item.getName());
					oldItem.setDuration(item.getDuration());
					oldItem.setFramesCount(item.getFramesCount());
					oldItem.setPath(item.getPath());
					oldItem.setScreenShot(item.getScreenShot());
				} else {
					VideoUtils.videoMap.put(item.getId(), item);
					VideoUtils.videoList.add(item);
				}
			}
		}
	}
	
	private static VideoItem file2VideoItem(File file) {
		String fileType = file.getName().toUpperCase().substring(file.getName().lastIndexOf(".") + 1);
		List<String> acceptTypes = CommonUtils.getConfigs().getAcceptTypes();
		long minTimeLength = CommonUtils.getConfigs().getMinTimeLength();
		if (acceptTypes.contains(fileType)) {
			LOGGER.info("[" + file.getName() + "]");
			Map<String, Number> duration = getVideoDuration(file.getAbsolutePath());
			if ((long)duration.get("time") < minTimeLength * 1000) {
				LOGGER.info("时长小于" + minTimeLength + ", 忽略该文件");
				return null;
			}
			VideoItem item = new VideoItem();
			String id = CommonUtils.getFileHashCode(file.getAbsolutePath());
			item.setId(id);
			item.setName(file.getName());
			item.setDuration((long) duration.get("time"));
			item.setFramesCount((int) duration.get("frames"));
			item.setScreenShot(
					Base64Utils.encodeToString(getVideoScreenshot(id + "_cover", file.getAbsolutePath())));
			item.setPath(file.getAbsolutePath());
			return item;
		}
		return null;
	}
	
	private static void startScanThread(String VideoPath) {
		new Thread(new ScanFolderTimer(VideoPath)).start();
	}

	public static Map<String, VideoItem> getVideoMap() {
		return videoMap;
	}

	public static List<VideoItem> getVideoList() {
		return videoList;
	}
	
	static class ScanFolderTimer implements Runnable {
		
		private String videoPath;
		
		public ScanFolderTimer(String videoPath) {
			this.videoPath = videoPath;
		}
		
		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(CommonUtils.getConfigs().getScanInterval());
				} catch (InterruptedException e) {
					LOGGER.error(e.getMessage(), e);
				}
				synchronized (videoMap) {
					LOGGER.info("重新扫描目录信息");
					File file = new File(videoPath);
					traverseFolder(file);
				}
			}
		}
		
	}
	
	static class PreviewScreenshotThread implements Runnable {
		private Map<Integer, Integer> currentFrames = new TreeMap<Integer, Integer>();
		private CountDownLatch latch = null;
		private FFmpegFrameGrabber grabber = null;
		private ConcurrentHashMap<Integer, BufferedImage> threadImgs = null;
		
		public PreviewScreenshotThread(FFmpegFrameGrabber grabber, ConcurrentHashMap<Integer, BufferedImage> threadImgs) {
			this.grabber = grabber;
			this.threadImgs = threadImgs;
		}
		
		public void addProcessFrame(int index, int frame) {
			currentFrames.put(index, frame);
		}
		
		public void setDownLatch(CountDownLatch latch) {
			this.latch = latch;
		}
		
		public void run() {
			try {
				Set<Entry<Integer, Integer>> entrySet = currentFrames.entrySet();
				Iterator<Entry<Integer, Integer>> iterator = entrySet.iterator();
				while (iterator.hasNext()) {
					Entry<Integer, Integer> next = iterator.next();
					int index = next.getKey();
					int currentFrame = next.getValue();
					LOGGER.info("==============截取第 " + index + " 张，第 " + currentFrame + " 帧==============");
					Lock lock = new ReentrantLock();
					lock.lock();
					grabber.setFrameNumber(currentFrame);
					Frame frame = grabber.grabImage();
					String rotate = grabber.getVideoMetadata("rotate");
					lock.unlock();
					// 视频旋转度
					Java2DFrameConverter converter = new Java2DFrameConverter();
					// 绘制图片
					BufferedImage bi = converter.getBufferedImage(frame);
					if (rotate != null) {
						// 旋转图片
						bi = rotate(bi, Integer.parseInt(rotate));
					}
					threadImgs.put(index, bi);
				}
			} catch (Exception e) {
				LOGGER.info(e.getMessage());
			} finally {
				try {
					grabber.close();
				} catch (Exception e) {
					LOGGER.info(e.getMessage());
				}
				latch.countDown();
			}
		}
	}
}
