package com.osh.rvs.service.manage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DailyWorkSheetService {
	public List<Map<String, Object>> searchFileName(String filepath) {

		List<Map<String, Object>> fileList = new ArrayList<Map<String, Object>>();

		File file = new File(filepath);
		if (file.exists()) {
			File[] fs = file.listFiles();
			// 遍历文件
			for (int i = 0; i < fs.length; i++) {
				Map<String, Object> fileMap = new HashMap<String, Object>();				
				if (!fs[i].isDirectory()) {
					
					String filename = fs[i].getName();
					// 文件名字
					fileMap.put("fileName", filename);
					// 文件生成时间
					fileMap.put("fileDayTime",filename.replaceAll(".*(\\d{4}\\-\\d{2}\\-\\d{2}).*", "$1"));
					File readfile = new File(filepath + "\\confirm\\" + filename);
					if (readfile.exists()) {
						fileMap.put("confirmfilename", filename);
					} else {
						fileMap.put("confirmfilename", "");
					}
					fileList.add(fileMap);
				}

			}

		}

		return fileList;
	}
}