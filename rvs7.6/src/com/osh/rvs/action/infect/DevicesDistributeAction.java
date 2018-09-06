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

import com.osh.rvs.form.master.DevicesManageForm;
import com.osh.rvs.service.DevicesManageService;
import com.osh.rvs.service.DevicesTypeService;
import com.osh.rvs.service.LineService;
import com.osh.rvs.service.SectionService;
import com.osh.rvs.service.infect.DevicesDistributeService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;

/**
 * 
 * @Project rvs
 * @Package com.osh.rvs.action.infect
 * @ClassName: DevicesDistributeAction
 * @Description: 设备工具分布Action
 * @author lxb
 * @date 2014-8-28 上午10:26:05
 * 
 */
public class DevicesDistributeAction extends BaseAction {
	private Logger log=Logger.getLogger(getClass());
	
	/**
	 * 页面初始化
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void init(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("DevicesDistributeAction.init start");
		
		DevicesTypeService devicesTypeService = new DevicesTypeService();
		//品名
		String nReferChooser=devicesTypeService.getDevicesTypeReferChooser(conn);
		request.setAttribute("nReferChooser", nReferChooser);
		
		SectionService sectionService = new SectionService();
		// 分发课室
		String sectionOptions = sectionService.getAllOptions(conn);
		request.setAttribute("sectionOptions", sectionOptions);
		
		LineService lineService = new LineService();
		// 责任工程
		String lineOptions = lineService.getOptions(conn);
		request.setAttribute("lineOptions", lineOptions);
		
		DevicesManageService service = new DevicesManageService();
		
		// 责任工位
		String pReferChooser = service.getOptionPtions(conn);
		request.setAttribute("pReferChooser", pReferChooser);
		
		
		//管理员
		String oReferChooser = service.getDevicesManageroptions(conn);
		request.setAttribute("oReferChooser", oReferChooser);
		
		// 管理等级
		request.setAttribute("goManageLevel", CodeListUtils.getSelectOptions("devices_manage_level", null, ""));
		request.setAttribute("sManageLevel",CodeListUtils.getGridOptions("devices_manage_level"));
		
		actionForward=mapping.findForward(FW_INIT);
		
		log.info("DevicesDistributeAction.init end");
	}
	
	
	/**
	 * 检索
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("DevicesDistributeAction.search start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		DevicesDistributeService service=new DevicesDistributeService();
		List<DevicesManageForm> finished=service.search(form, conn); 
		
		listResponse.put("errors", errors);
		listResponse.put("finished", finished);
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("DevicesDistributeAction.search end");
	}
	
}
