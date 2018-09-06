package com.osh.rvs.common;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class InitFilePathServlet extends HttpServlet {

	private static final long serialVersionUID = -2767673525463234044L;

	public void init(ServletConfig arg0) throws ServletException {
		PathConsts.BASE_PATH = arg0.getInitParameter("BASE_PATH");
		PathConsts.REPORT_TEMPLATE = arg0.getInitParameter("REPORT_TEMPLATE");
		PathConsts.REPORT = arg0.getInitParameter("REPORT");
		PathConsts.LOAD_TEMP = arg0.getInitParameter("LOAD_TEMP");
		PathConsts.PCS_TEMPLATE = arg0.getInitParameter("PCS_TEMPLATE");
		PathConsts.PCS = arg0.getInitParameter("PCS");
		PathConsts.PROPERTIES = arg0.getInitParameter("PROPERTIES");
		PathConsts.QU_BOOKS = arg0.getInitParameter("QU_BOOKS");
		PathConsts.PHOTOS = arg0.getInitParameter("PHOTOS");
		PathConsts.DEVICEINFECTION = arg0.getInitParameter("DEVICEINFECTION");
		PathConsts.INFECTIONS = arg0.getInitParameter("INFECTIONS");
		PathConsts.IMAGES = arg0.getInitParameter("IMAGES");
		PathConsts.load();
	}

	public void destroy() {
		super.destroy();
	}
}
