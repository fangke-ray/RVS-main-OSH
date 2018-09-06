package com.osh.rvs.action.partial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.partial.PartialSupplyEntity;
import com.osh.rvs.form.partial.PartialSupplyForm;
import com.osh.rvs.service.partial.PartialSupplyService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.validator.Validators;

public class PartialSupplyAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("PartialSupplyAction.init start");

		actionForward = mapping.findForward(FW_INIT);

		log.info("PartialSupplyAction.init end");
	}

	/**
	 * 按日期查询零件补充内容
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("PartialSupplyAction.search start");

		/* Ajax回馈对象 */
		Map<String, Object> callResponse = new HashMap<String, Object>();

		List<MsgInfo> infos = new ArrayList<MsgInfo>();

		PartialSupplyService service = new PartialSupplyService();
		PartialSupplyEntity entity = new PartialSupplyEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		/* 查询 */
		List<PartialSupplyEntity> slist = service.searchByDate(entity, conn);

		List<PartialSupplyForm> ret = new ArrayList<PartialSupplyForm>();
		BeanUtil.copyToFormList(slist, ret, CopyOptions.COPYOPTIONS_NOEMPTY, PartialSupplyForm.class);
		/* 查询数据保存 */
		callResponse.put("partialSupplyList", ret);

		/* errors */
		callResponse.put("errors", infos);
		returnJsonResponse(response, callResponse);

		log.info("PartialSupplyAction.search end");
	}

	
	/**
	 * 更新数量
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn	数据库会话
	 * @throws Exception
	 */
	public void doUpdateQuantity(ActionMapping mapping,ActionForm form,HttpServletRequest req,HttpServletResponse res,SqlSessionManager conn)throws Exception{
		log.info("PartialSupplyAction.doUpdateQuantity start");
		
		/* Ajax回馈对象 */
		Map<String, Object> callResponse = new HashMap<String, Object>();
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		List<MsgInfo> errors=v.validate();
		PartialSupplyService service=new PartialSupplyService();
		
		if(errors.size()==0){
			service.updateQuantity(form, conn);
		}
		// 检查发生错误时报告错误信息
		callResponse.put("errors", errors);
		
		returnJsonResponse(res, callResponse);
		
		log.info("PartialSupplyAction.doUpdateQuantity end");
	}


	@Privacies(permit={2, 0})
	public void doDelete(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("PartialSupplyAction.doDelete start");
		
		/* Ajax回馈对象 */
		Map<String, Object> callResponse = new HashMap<String, Object>();

		List<MsgInfo> infos = new ArrayList<MsgInfo>();

		PartialSupplyService service = new PartialSupplyService();
		PartialSupplyEntity entity = new PartialSupplyEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		service.delete(entity, req, conn);

		/* 查询 */
		List<PartialSupplyEntity> slist = service.searchByDate(entity, conn);

		List<PartialSupplyForm> ret = new ArrayList<PartialSupplyForm>();
		BeanUtil.copyToFormList(slist, ret, CopyOptions.COPYOPTIONS_NOEMPTY, PartialSupplyForm.class);
		/* 查询数据保存 */
		callResponse.put("partialSupplyList", ret);

		/* errors */
		callResponse.put("errors", infos);
		returnJsonResponse(res, callResponse);

		log.info("PartialSupplyAction.doDelete end");
	}


}