package com.osh.rvs.action.inline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.form.inline.DryingProcessForm;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.PositionService;
import com.osh.rvs.service.SectionService;
import com.osh.rvs.service.inline.DryingJobService;
import com.osh.rvs.service.inline.DryingProcessService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

/**
 * 
 * @Title DryingProcessAction.java
 * @Project rvs
 * @Package com.osh.rvs.action.inline
 * @ClassName: DryingProcessAction
 * @Description: 烘干进程
 * @author lxb
 * @date 2016-8-17 下午2:58:01
 */
public class DryingProcessAction extends BaseAction {

	/**
	 * 初始化
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void init(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("DryingProcessAction.init start");
		
		//维修对象型号
		ModelService modelService = new ModelService();
		String mReferChooser = modelService.getOptions(conn);
		request.setAttribute("mReferChooser", mReferChooser);
		
		//工位
		PositionService positionService = new PositionService();
		String pReferChooser = positionService.getOptions(conn);
		request.setAttribute("pReferChooser",pReferChooser);
		
		//课室
		SectionService sectionService = new SectionService();
		String sOptions = sectionService.getOptions(conn,"");
		request.setAttribute("sOptions", sOptions);
		
		//烘干设备
		DryingJobService service = new DryingJobService();
		String dryingOvenDeviceReferChooser = service.getDryingOvenDeviceReferChooser(conn);
		request.setAttribute("dryingOvenDeviceReferChooser", dryingOvenDeviceReferChooser);
		
		actionForward=mapping.findForward(FW_INIT);

		log.info("DryingProcessAction.init end");
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
		log.info("DryingProcessAction.search start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		DryingProcessService service = new DryingProcessService();
		List<DryingProcessForm> finished =service.search(form, conn);
		
		listResponse.put("errors", errors);
		listResponse.put("finished", finished);
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("DryingProcessAction.search end");
		
	}
	
	/**
	 * 取得设备当前库位情况
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void getSlotsByDevice(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("DryingProcessAction.getSlotsByDevice start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		DryingProcessService service = new DryingProcessService();
		List<DryingProcessForm> slotsByDevice = service.getSlotsByDevice(request, conn);
		
		listResponse.put("errors", errors);
		listResponse.put("slotsByDevice", slotsByDevice);
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("DryingProcessAction.getSlotsByDevice end");
		
	}
	
	/**
	 * 烘干完成
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doFinish(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("DryingProcessAction.doFinish start");
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();
		
		if(errors.size()==0){
			DryingProcessForm dryingProcessForm = (DryingProcessForm)form;
			String material_id = dryingProcessForm.getMaterial_id();
			String position_id = dryingProcessForm.getPosition_id();
			
			DryingProcessService service = new DryingProcessService();
			service.finishDryingProcess(material_id, position_id, conn);
		}

		listResponse.put("errors", errors);
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("DryingProcessAction.doFinish end");
	}
}
