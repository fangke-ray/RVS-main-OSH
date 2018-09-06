package com.osh.rvs.common;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class PathConsts {
	public static String BASE_PATH = "";
	public static String REPORT_TEMPLATE = "";
	public static String REPORT = "";
	public static String LOAD_TEMP = "";
	public static String PCS_TEMPLATE = "";
	public static String PCS = "";
	public static String PROPERTIES = "";
	public static String QU_BOOKS = "";
	public static String PHOTOS = "";
	public static String DEVICEINFECTION = "";
	public static String INFECTIONS ="";
	public static String IMAGES ="";

	public static Properties POSITION_SETTINGS = new Properties();
	public static Properties OCM_FSE_SETTINGS = new Properties();
	public static Properties SCHEDULE_SETTINGS = new Properties();

	public static void load() {
		try {
			InputStream in = new BufferedInputStream(new
					FileInputStream(PathConsts.BASE_PATH + PathConsts.PROPERTIES + "\\positionsettings.properties"));

			PathConsts.POSITION_SETTINGS.load(in);

//			in = new BufferedInputStream(new
//					FileInputStream(PathConsts.BASE_PATH + PathConsts.PROPERTIES + "\\ocmfse.properties"));
//			PathConsts.OCM_FSE_SETTINGS.load(in);

			in = new BufferedInputStream(new
					FileInputStream(PathConsts.BASE_PATH + PathConsts.PROPERTIES + "\\schedule.properties"));
			PathConsts.SCHEDULE_SETTINGS.load(in);

		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	/**
	 * 更新配置文件，需检查内容是否一致
	 * @throws Exception
	 */
	public static void loadWithCheck() throws Exception {
		try {
			InputStream in = new BufferedInputStream(new
					FileInputStream(PathConsts.BASE_PATH + PathConsts.PROPERTIES + "\\positionsettings.properties"));

			// 检查一致性的Map
			Set<String> checkProsList = new HashSet<String>();
			if (!PathConsts.POSITION_SETTINGS.isEmpty()) {
				int count = 10;
				for (Object key : PathConsts.POSITION_SETTINGS.keySet()) {
					if (Math.random() > 0.3) {
						checkProsList.add("" + key);
						count--; if (count < 0) break;
					}
				}
			}
			PathConsts.POSITION_SETTINGS.clear();
			PathConsts.POSITION_SETTINGS.load(in);
			int unmatchCount = 0;
			for (String key : checkProsList) {
				if (PathConsts.POSITION_SETTINGS.getProperty(key) == null) {
					unmatchCount++;
				}
			}
			if (unmatchCount > 2) throw new Exception("文件内容大改动，需确认上传文件！");

//			mail.properties

			checkProsList.clear();
			if (!PathConsts.SCHEDULE_SETTINGS.isEmpty()) {
				int count = 10;
				for (Object key : PathConsts.SCHEDULE_SETTINGS.keySet()) {
					if (Math.random() > 0.3) {
						checkProsList.add("" + key);
						count--; if (count < 0) break;
					}
				}
			}
			in = new BufferedInputStream(new
					FileInputStream(PathConsts.BASE_PATH + PathConsts.PROPERTIES + "\\schedule.properties"));
			PathConsts.SCHEDULE_SETTINGS.clear();
			PathConsts.SCHEDULE_SETTINGS.load(in);

			unmatchCount = 0;
			for (String key : checkProsList) {
				if (PathConsts.SCHEDULE_SETTINGS.getProperty(key) == null) {
					unmatchCount++;
				}
			}
			if (unmatchCount > 2) throw new Exception("文件内容大改动，需确认上传文件！");

		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
	}
}
