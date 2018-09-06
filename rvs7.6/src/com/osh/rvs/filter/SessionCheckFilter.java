package com.osh.rvs.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.arnx.jsonic.JSON;

import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.RedirectRes;

public class SessionCheckFilter implements Filter {

	private FilterConfig filterConfig;
	protected static final Logger logger = Logger.getLogger("ACCOUNT");

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	/**
	 * 过滤处理
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {

		@SuppressWarnings("unused")
		ServletContext context = this.filterConfig.getServletContext();
		HttpServletRequest req = (HttpServletRequest) request;
		String path = req.getServletPath();

		// 登录事件除外 TODO consts AND 对外接口
		if (path != null && (path.contains("/login") || path.contains("/logout") || path.contains("_interface.do"))) {
			chain.doFilter(request, response);
			return;
		}
		if (path != null && path.contains("/download")) {
			String method = req.getParameter("method");
			if ("file".equals(method) || "savePdf".equals(method) || "saveRPdf".equals(method)) {
				chain.doFilter(request, response);
				return;
			}
		} else
		if (path != null && path.contains("/filingdownload")) {
			if ("make".equals(req.getParameter("method"))) {
				chain.doFilter(request, response);
				return;
			}
		}
		HttpSession session = req.getSession();

		if (session.getAttribute(RvsConsts.SESSION_USER) != null) {
			// 系统已登录的场合通过
			chain.doFilter(request, response);
		} else {
			// 系统未登录的场合
			logger.info("Session已经超期");

			// 读取提交的方式
			String requestType = req.getHeader("RequestType");
			if ("ajax".equals(requestType)) {
				// 如果是以AJAX方式提交
				// 建立返回的Bean
				RedirectRes rres = new RedirectRes();

				// 迁移画面设定为登录画面
				rres.setRedirect(RvsConsts.PAGE_LOGIN);

				// 返回Json格式回馈信息
				PrintWriter out;
				try {
					response.setCharacterEncoding("UTF-8");
					out = response.getWriter();
					out.print(JSON.encode(rres));
					out.flush();
				} catch (IOException e) {
					logger.info("Response作成异常");
				}
			} else if (path.contains("/pda_")) {// ("consumable".equals(requestType)) {
				// 消耗品扫描仪
				request.getRequestDispatcher(RvsConsts.PAGE_LOGIN + "?method=consumable").forward(request, response);
			} else {
				// 页面整体提交的场合转到登录画面
				request.getRequestDispatcher(RvsConsts.PAGE_LOGIN + "?method=sessionTimeout").forward(request, response);
			}
		}
	}

	public void destroy() {
		this.filterConfig = null;
	}

}
