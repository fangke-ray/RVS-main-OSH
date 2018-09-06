package com.osh.rvs.action.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.common.PathConsts;
import com.osh.rvs.service.partial.PartialFilingService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.DateUtil;

public class PartialFilingAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());
	private PartialFilingService service = new PartialFilingService();
	/**
	 * 初始化零件相关归档
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("PartialFilingAction.init start");

		actionForward = mapping.findForward(FW_INIT);

		String sYearMonth = DateUtil.toString(new Date(), "yyyyMM");
		req.setAttribute("sYearMonth", sYearMonth);

		log.info("PartialFilingAction.init end");
	}
	/**
	 * 取得零件归档工作表
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void searchPartialFiling(ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("PartialFilingAction.searchPartialFiling start");
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		// 文件路径
		String filePath = "";
		String type = req.getParameter("type");
		
		if("partial_order".equals(type)){
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\partial\\" + req.getParameter("year_month");
		}
		
		if (errors.size() == 0) {
			// 获取路径下所有的的文件的名字
			List<Map<String, Object>> fileNameList = service.searchPartialFilingFileName(filePath);

			listResponse.put("fileNameList", fileNameList);
		}

		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		log.info("PartialFilingAction.searchPartialFiling end");
	}
}
