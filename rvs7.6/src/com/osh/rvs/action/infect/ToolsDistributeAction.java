package com.osh.rvs.action.infect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.form.infect.ToolsDistributeForm;
import com.osh.rvs.service.DevicesManageService;
import com.osh.rvs.service.LineService;
import com.osh.rvs.service.OperatorService;
import com.osh.rvs.service.SectionService;
import com.osh.rvs.service.ToolsDistributeService;
import com.osh.rvs.service.ToolsTypeService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;

public class ToolsDistributeAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	private ToolsTypeService toolsTypeService = new ToolsTypeService();
	private SectionService sectionService = new SectionService();
	private LineService lineService = new LineService();
	private DevicesManageService devicesManageService = new DevicesManageService();
	private OperatorService operatorService = new OperatorService();

	private ToolsDistributeService service = new ToolsDistributeService();
	/**
	 * 治具分布初始化
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit = { 1, 0 })
	public void init(ActionMapping mapping, ActionForm form,HttpServletRequest req, HttpServletResponse res, SqlSession conn)throws Exception {

		log.info("ToolsDistributeAction.init start");

		actionForward = mapping.findForward(FW_INIT);

		// 治具品名 referChooser
		String nReferChooser = toolsTypeService.getToolsNameReferChooser(conn);
		req.setAttribute("nReferChooser", nReferChooser);

		// 分发课室
		String sectionOptions = sectionService.getAllOptions(conn);
		req.setAttribute("sectionOptions", sectionOptions);

		// 责任工程
		String lineOptions = lineService.getOptions(conn);
		req.setAttribute("lineOptions", lineOptions);

		// 工位
		String pReferChooser = devicesManageService.getOptionPtions(conn);
		req.setAttribute("pReferChooser", pReferChooser);

		// 责任人
		String rReferChooser = operatorService.getAllOperatorName(conn);
		req.setAttribute("rReferChooser", rReferChooser);
		
		//状态
		req.setAttribute("goStatus", CodeListUtils.getGridOptions("devices_status"));

		log.info("ToolsDistributeAction.init end");
	}

	/**
	 * 治具分布一览
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response,SqlSession conn) throws Exception {
		log.info("ToolsDistributeAction.search start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		// 设备工具管理详细数据
		List<ToolsDistributeForm> toolsDistributes = service.searchToolsDistribute(form, conn, errors);

		listResponse.put("toolsDistributes", toolsDistributes);
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		log.info("ToolsDistributeAction.search end");
	}

}
