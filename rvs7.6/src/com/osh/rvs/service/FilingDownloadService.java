package com.osh.rvs.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

public class FilingDownloadService {

	/**
	 * 文件流输出
	 * @param res 输出目标相应
	 * @param contentType 输出上下文类型
	 * @param fileName 输出文件名
	 * @param filePath 数据源文件
	 * @throws Exception
	 */
	public void writeFile(HttpServletResponse res, String contentType, String fileName, String filePath) throws Exception {
		res.setHeader("Content-Disposition","attachment;filename=\""+fileName + "\"");
		res.setContentType(contentType);
		File file = new File(filePath);
		InputStream is = new BufferedInputStream(new FileInputStream(file));
		byte[] buffer = new byte[is.available()];
		is.read(buffer);
		is.close();
		
		OutputStream os = new BufferedOutputStream(res.getOutputStream());
		os.write(buffer);
		os.flush();
		os.close();
	}
}
