package com.osh.rvs.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.osh.rvs.service.MaterialTagService;

public class TriggerServlet extends HttpServlet {

	private static final long serialVersionUID = 2075667347923603158L;

	Logger log = Logger.getLogger("TriggerServlet");

	/** 更新配置文件 **/
	private static final String METHOD_ANML_REFRESH = "anml_exp";

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse arg1) throws ServletException, IOException {
		String uri = req.getRequestURI();
		uri = uri.replaceFirst(req.getContextPath(), "");
		uri = uri.replaceFirst(req.getServletPath(), "");
		String addr = req.getRemoteAddr();
		log.info("Get finger on :" + uri + " from " + addr);

		// 只有本机可以访问
		if (!"0:0:0:0:0:0:0:1".equals(addr) && !"127.0.0.1".equals(addr)) {
			log.warn("推送只限服务器本机触发");
			return;
		}
		String[] parameters = uri.split("\\/");
		if (parameters.length > 1) {
			String method = parameters[1];

			if (METHOD_ANML_REFRESH.equals(method)){
				MaterialTagService.clearAnmlMaterials();
			}
		}
	}
}