package com.osh.rvs.service.partial;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PartialFilingService {
	/**
	 *零件归档所有的文件名字
	 * @param filepath
	 * @return
	 */
	public List<Map<String, Object>> searchPartialFilingFileName(String filepath) {

		List<Map<String, Object>> fileList = new ArrayList<Map<String, Object>>();

		File file = new File(filepath);
		if (file.exists()) {
			File[] fs = file.listFiles();
			Map<String, Object> fileMap = new HashMap<String, Object>();
			// 遍历文件
			for (int i = 0; i < fs.length; i++) {
				String fileName = fs[i].getName();
				if(fileName.contains("零件订购表")){
					fileMap.put("partial_order", fileName);
				}else if(fileName.contains("BO缺品零件表")){
					fileMap.put("bo_shortage", fileName);
				}else if(fileName.contains("零件追加明细表")){
					fileMap.put("partial_additional", fileName);
				}else if(fileName.contains("BO分析表")){
					fileMap.put("bo_analysis", fileName);
				}
				fileMap.put("fileDayTime",fileName.replaceAll(".*(\\d{4}\\-\\d{2}\\-\\d{2}).*", "$1"));
			}
			fileList.add(fileMap);
		}
		return fileList;
	}
}
