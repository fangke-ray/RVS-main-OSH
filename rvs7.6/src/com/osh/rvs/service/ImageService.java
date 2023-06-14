package com.osh.rvs.service;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import sun.misc.BASE64Decoder;

import com.lowagie.text.pdf.codec.Base64;
import com.osh.rvs.common.PathConsts;
import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.FileUtils;

public class ImageService {

	public String cutImage(HttpServletRequest req, List<MsgInfo> msgs) {
		String sStartX = req.getParameter("startX");
		String sStartY = req.getParameter("startY");
		String sHeight = req.getParameter("height");
		String sWidth = req.getParameter("width");
		String sShowWidth = req.getParameter("showWidth");
		String sTrueWidth = req.getParameter("trueWidth");
		double dStartX = 0, dStartY = 0, dCutHeight = 0, dCutWidth = 0, dShowWidth = 0, dTrueWidth = 0;
		try {
			dStartX = Double.parseDouble(sStartX);
			dStartY = Double.parseDouble(sStartY);
			dCutHeight = Double.parseDouble(sHeight);
			dCutWidth = Double.parseDouble(sWidth);
			dShowWidth = Double.parseDouble(sShowWidth);
			dTrueWidth = Double.parseDouble(sTrueWidth);
		} catch (NumberFormatException pe) {
			MsgInfo info = new MsgInfo();
			info.setErrmsg(pe.getMessage());
			msgs.add(info);
			return "";
		}

		if (dCutHeight == 0.0 || dCutWidth == 0.0) {
			MsgInfo info = new MsgInfo();
			info.setErrmsg("无图或无选择区域");
			msgs.add(info);
			return "";
		}

		double fator = dShowWidth / dTrueWidth;

		int iRealityStartX = (int) (dStartX / fator);
		int iRealityStartY = (int) (dStartY / fator);
		int iCutWidth = (int) (dCutWidth / fator);
		int iCutHeight = (int) (dCutHeight / fator);

		String sFileName = req.getParameter("fileName");
		String dividing = req.getParameter("dividing");

		String ipath = getImagePath(sFileName, dividing, 0);

		String dpath = ipath + "_fix.jpg";
		// 截取
		cutImageTo(dpath, dpath,
				iRealityStartX, iRealityStartY, iCutWidth, iCutHeight);



		return sFileName;
	}

	public String resetImage(HttpServletRequest req, List<MsgInfo> msgs) {
		String sFileName = req.getParameter("fileName");

		String dividing = req.getParameter("dividing");
		String ipath = getImagePath(sFileName, dividing, 0);

		String spath = ipath + ".jpg";
		String dpath = ipath + "_fix.jpg";
		// 截取
		FileUtils.copyFile(spath, dpath);

		return sFileName;
	}

	private String getImagePath(String sFileName, String sType, int iCutHeight) {
		if (CommonStringUtil.isEmpty(sType)) {
			return PathConsts.BASE_PATH + PathConsts.PHOTOS + "/uuid/" + sFileName.substring(0,4) + "/" + sFileName;
		}
		return PathConsts.BASE_PATH + PathConsts.PHOTOS + "/" + sType + "/" + sFileName;
	}

	public void getOriginalImageSize(File confFile,
			Map<String, Object> jsonResponse, int infoSize) {
		try {
			BufferedImage source = ImageIO.read(confFile);

			int srcWidth = source.getWidth();
			int srcHeight = source.getHeight();

			float factorS = (srcWidth + 0.0f) / srcHeight;

			if (srcWidth >= srcHeight && srcWidth > infoSize) {
				srcWidth = infoSize;
				srcHeight = (int) (srcWidth / factorS);
			} else if (srcHeight > infoSize) {
				srcHeight = infoSize;
				srcWidth = (int) (srcHeight * factorS);
			}

			jsonResponse.put("oWidth", srcWidth);
			jsonResponse.put("oHeight", srcHeight);
		} catch (Exception e) {
			e.printStackTrace(); // TODO
		}
	}

	/**
	 * 剪切图片位置
	 */
	public static void cutImageTo(String sourceFile, String targetDir, int startX, int startY, int width, int height) {
		try {
			cutImageTo(new File(sourceFile), targetDir, startX, startY, width, height);
		} catch (Exception e) {
		}
	}

	public static void cutImageTo(File sourceFile, String targetDir, int startX, int startY, int width, int height)
			throws Exception {
		cutImageTo(ImageIO.read(sourceFile), targetDir, startX, startY, width, height);
	}

	private static void cutImageTo(BufferedImage source, String targetDir, int startX, int startY, int width, int height)
			throws Exception {
		int sWidth = source.getWidth();
		// 	取得图片宽度	 */
		int sHeight = source.getHeight();
		if (sWidth >= width && sHeight >= height) {

			String fileName = null;
			File file = new File(targetDir);
			if (!file.exists()) {
				if (targetDir.endsWith("/") || targetDir.endsWith("\\")) {
					file.mkdirs();
				} else {
					file.createNewFile();
				}
			}
			BufferedImage image = source.getSubimage(startX, startY, width, height);
			fileName = targetDir;
			saveAsJpeg(fileName, image, 0.92f);
		}
	}

	public static void rotateImageTo(File sourceFile, String targetDir, float degree)
			throws Exception {
		rotateImageTo(ImageIO.read(sourceFile), targetDir, degree);
	}
	private static void rotateImageTo(BufferedImage source, String targetDir, float degree)
			throws Exception {
		int sWidth = source.getWidth();
		// 	取得图片宽度	 */
		int sHeight = source.getHeight();
		if (degree != 0.0f) {

			String fileName = null;
			File file = new File(targetDir);
			if (!file.exists()) {
				if (targetDir.endsWith("/") || targetDir.endsWith("\\")) {
					file.mkdirs();
				} else {
					file.createNewFile();
				}
			}
			// int[] in = {1,4,5,8,9};
			BufferedImage image  = new BufferedImage(sHeight,sWidth,8); // TYPE_INT_BGR
			Graphics2D g2d = image.createGraphics();
			// TODO 目前只是给旋转90的
			g2d.translate(sHeight, 0);
			g2d.rotate(degree);
			g2d.drawImage(source, 0, 0, null); 
			fileName = targetDir;
			saveAsJpeg(fileName, image, 0.92f);
		}
	}

	/**
	 * 图片大小调整
	 * @param sourceFile
	 * @param targetPath
	 * @param width
	 * @param height
	 */
	public static void fixWidthImageTo(File sourceFile, String targetPath, int width, int height)
			throws Exception {
		BufferedImage source = ImageIO.read(sourceFile);
		BufferedImage scaleImage = new BufferedImage(width,height,source.getType());
		Graphics g = scaleImage.getGraphics();
		g.drawImage(source, 0,0,width,height,null);
		g.dispose();
		saveAsJpeg(targetPath, scaleImage, 1f);
	}

	/**
	 * 设定质量后保存为jpeg
	 * 
	 * @param fileName
	 *            输出文件路径
	 * @param image
	 *            图象
	 * @param quality
	 *            质量
//	 * @throws ImageFormatException
	 * @throws IOException
	 */
	public static void saveAsJpeg(String fileName, BufferedImage image, float quality) throws ImageFormatException,
			IOException {
		FileOutputStream newimage = new FileOutputStream(fileName);
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(newimage);
		JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(image);
		/* 设定压缩质量 */
		jep.setQuality(quality, true);
		encoder.encode(image, jep);
		// encoder.encode(tag);
		// JPEG格式保存
		newimage.close();
	}
	public static String createCaptcha() throws IOException {

		BufferedImage bi = new BufferedImage(300, 48, BufferedImage.TYPE_3BYTE_BGR);
		Graphics g = bi.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 300, 48);

		Random rand = new Random();

		Color[] colors = {new Color(64, 64, 192), new Color(192, 64, 64), Color.GREEN};
		for (int i = 0; i < 255; i++) {
			g.setColor(colors[rand.nextInt(3)]);
			int x1 = rand.nextInt(298);
			int y1 = rand.nextInt(46);
			g.drawLine(x1, y1, x1 + 2, y1 + 2);
		}

		int x = rand.nextInt(100);
		File fileOlympus = new File(PathConsts.BASE_PATH + PathConsts.IMAGES + "/verify/logo.png");
		BufferedImage imgOlympus = ImageIO.read(fileOlympus);
		g.drawImage(imgOlympus, x, 0, null);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(bi, "png", out);
		byte[] bytes = out.toByteArray();
		
		return x + ";" + Base64.encodeBytes(bytes).replaceAll("\n", "");
	}

	public static String getCaptchaKey(int keyIdx) throws IOException {
		File fileOlympusChar = new File(PathConsts.BASE_PATH + PathConsts.IMAGES + "/verify/chara-" + keyIdx + ".png");
		BufferedImage imgOlympusChar = ImageIO.read(fileOlympusChar);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(imgOlympusChar, "png", out);
		byte[] bytes = out.toByteArray();
		
		return Base64.encodeBytes(bytes).replaceAll("\n", "");
	}
}
