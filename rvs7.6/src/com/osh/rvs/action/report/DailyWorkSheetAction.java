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

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.service.manage.DailyWorkSheetService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.DateUtil;

/**
 * 工作表查询
 * @author Liwy
 * 
 */
public class DailyWorkSheetAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());
	private DailyWorkSheetService service = new DailyWorkSheetService();

	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("DailyWorkSheetAction.init start");

		actionForward = mapping.findForward(FW_INIT);

		String sYearMonth = DateUtil.toString(new Date(), "yyyyMM");
		req.setAttribute("sYearMonth", sYearMonth);
		
        //获取当前操作者的工位权限
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		List<PositionEntity> positions =user.getPositions();
		if(positions.size()>0){
			for(int i=0;i<positions.size();i++){
				PositionEntity positionEntity = positions.get(i);
				//如果可操作工位包括111，当前操作人就可以上传受理、消毒、灭菌、出货文件，否则只提供受理、消毒、灭菌、出货文件的下载
				if("111".equals(positionEntity.getProcess_code())){
					req.setAttribute("accept", "accept");
				}
				if("121".equals(positionEntity.getProcess_code())){
					req.setAttribute("disinfection", "disinfection");
				}
				if("131".equals(positionEntity.getProcess_code())){
					req.setAttribute("disinfect", "disinfect");
				}
				if("711".equals(positionEntity.getProcess_code())){
					req.setAttribute("shipment", "shipment");
				}
			}
		}
		if (user.getPrivacies().contains(RvsConsts.PRIVACY_FACT_MATERIAL)) {
			req.setAttribute("inline", "inline");
		}
		log.info("DailyWorkSheetAction.init end");
	}

	/**
	 * 取得月工作表
	 */
	public void searchDailyWorkSheet(ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("DailyWorkSheetAction.searchDailyWorkSheet start");
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		// 文件路径
		String filePath = "";
		String type = req.getParameter("type");
		switch (type) {
		case "report_inline":
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\inline\\" + req.getParameter("year_month");
			break;
		case "report_accept":
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\accept\\" + req.getParameter("year_month");
			break;
		case "report_sterilize":
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\sterilize\\" + req.getParameter("year_month");
			break;
		case "report_disinfect":
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\disinfect\\" + req.getParameter("year_month");
			break;
		case "report_schedule":
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\schedule\\" + req.getParameter("year_month");
			break;
		case "report_shipping":
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\shipping\\" + req.getParameter("year_month");
			break;
		case "report_unrepaire":
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\wip\\" + req.getParameter("year_month");
			break;
		case "report_snout":
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\snout\\";
			break;
		case "report_wash":
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\steel_wire_container_wash\\" + req.getParameter("year_month");
			break;
		}

		if (errors.size() == 0) {
			// 获取所有的路径下的文件的名字
			List<Map<String, Object>> fileNameList = service.searchFileName(filePath);

			listResponse.put("fileNameList", fileNameList);
		}

		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		log.info("DailyWorkSheetAction.searchDailyWorkSheet end");
	}
}
