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

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.partial.MaterialPartialEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.partial.PartialOrderManageForm;
import com.osh.rvs.service.MaterialPartialService;
import com.osh.rvs.service.partial.PartialOrderManageService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.IntegerConverter;
import framework.huiqing.common.util.validator.Validators;

public class PartialOrderManageAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	private PartialOrderManageService service = new PartialOrderManageService();
	
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("PartialOrderManageAction.init start");

		actionForward = mapping.findForward(FW_INIT);

		log.info("PartialOrderManageAction.init end");
	}		
	/**
	 * 查询未完成和已经完成的维修对象
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {

		log.info("PartialOrderManageAction.search start");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);

		List<MsgInfo> errors = v.validate();

		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		String fact = RvsConsts.ROLE_FACTINLINE.equals(user.getRole_id()) ? "fact" : null;

		if (errors.size() == 0) {
			// 执行检索
			List<PartialOrderManageForm> partialOrderManageFormList = service.searchMaterial(form, fact, conn, errors);
			
			// 查询结果放入Ajax响应对象
			listResponse.put("partialOrderManageFormList", partialOrderManageFormList);

		}

		listResponse.put("fact", fact);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("PartialOrderManageAction.search end");
	}
	/**
	 * 维修对象详细信息
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void detail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse response,
			SqlSession conn) throws Exception {
		log.info("PartialOrderManageAction.detail start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		MaterialEntity materialEntity = new MaterialEntity();
		PartialOrderManageForm partialOrderManageForm = (PartialOrderManageForm) form;

		materialEntity.setMaterial_id(partialOrderManageForm.getMaterial_id());
		
		//判断双击时才出发维修详细信息的查找，其他情况就只对订购零件的信息查询
		if(req.getParameter("append").equals("true")){
			/* 查询维修对象的详细显示信息 */
			List<MaterialForm> materialDetail = service.searchMaterialDetail(materialEntity, conn);
			for (int i = 0; i < materialDetail.size(); i++) {
				MaterialForm materialForm = materialDetail.get(0);
				listResponse.put("materialDetail", materialForm);
			}
		}	
		/*根据belongs查询所有零件 */
		List<PartialOrderManageForm> partialOrderManageFormList = service.searchPartialOrder(partialOrderManageForm,conn);
		listResponse.put("partialOrderManageFormList", partialOrderManageFormList);
		
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		log.info("PartialOrderManageAction.detail end");
	}
	/**
	 * 双击根据KEY获取material_partial_detail详细数据
	 * @param mapping
	 * @param form
	 * @param req
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void materialPartialDetail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse response,
			SqlSession conn) throws Exception {
		log.info("PartialOrderManageAction.materialPartialDetail start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		 
		//维修对象零件详细信息显示-零件追加分配的时候弹出的dialog
		PartialOrderManageForm partialOrderManageForm= service.getDBMaterialPartialDetail(req.getParameter("material_partial_detail_key"),req.getParameter("modelID"),conn,errors,listResponse);

		listResponse.put("partialOrderManageForm", partialOrderManageForm);
		
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		log.info("PartialOrderManageAction.materialPartialDetail end");
	}
	
	/**
	 * 进行追加编辑（分配工位）
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doupdatePosition(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,
			SqlSessionManager conn) throws Exception {
		log.info("PartialOrderManageAction.doupdatePosition start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		PartialOrderManageForm partialOrderManageForm = (PartialOrderManageForm) form;
		
		//直接更新双击的零件的工位
		service.updateMaterialPartialDetailProcesscode(partialOrderManageForm, conn,errors);
		
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		log.info("PartialOrderManageAction.doupdatePosition end");
	}
	
	/**
	 * 选择零件任意零件点击删除按钮，进行零件的删除
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void dodeletePartial(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,
			SqlSessionManager conn) throws Exception {
		log.info("PartialOrderManageAction.dodeletePartial start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		PartialOrderManageForm partialOrderManageForm = (PartialOrderManageForm) form;
		
		//直接删除零件
		service.deletePartial(partialOrderManageForm, conn,errors);

		// bo状态重新设定
		MaterialPartialService mpservice = new MaterialPartialService();

		MaterialPartialEntity materialPartialEntity = new MaterialPartialEntity();
		CopyOptions cos = new CopyOptions();
		cos.include("material_id", "occur_times");
		cos.converter(IntegerConverter.getInstance(), "occur_times");
		BeanUtil.copyToBean(partialOrderManageForm, materialPartialEntity, cos);

		mpservice.updateBoFlgWithDetail(materialPartialEntity, conn);

		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		log.info("PartialOrderManageAction.dodeletePartial end");
	}
	/**
	 * 取消零件发放
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doupdatedetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,
			SqlSessionManager conn) throws Exception {
		log.info("PartialOrderManageAction.doupdatedetail start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		PartialOrderManageForm partialOrderManageForm = (PartialOrderManageForm) form;
		
		//更新零件待发放数量是订购数量、没有最后发放时间
		service.updateMaterialPartialDetail(partialOrderManageForm, conn,errors);

		// bo状态重新设定
		MaterialPartialService mpservice = new MaterialPartialService();

		MaterialPartialEntity materialPartialEntity = new MaterialPartialEntity();
		CopyOptions cos = new CopyOptions();
		cos.include("material_id", "occur_times");
		cos.converter(IntegerConverter.getInstance(), "occur_times");
		BeanUtil.copyToBean(partialOrderManageForm, materialPartialEntity, cos);

		mpservice.updateBoFlgWithDetailMaintance(materialPartialEntity, conn);

		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		log.info("PartialOrderManageAction.doupdatedetail end");
	}
	/**
	 * 取消订购
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void dodeletematerialpartial(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,
			SqlSessionManager conn) throws Exception {
		log.info("PartialOrderManageAction.dodeletematerialpartial start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		PartialOrderManageForm partialOrderManageForm = (PartialOrderManageForm) form;
		
		//更新零件待发放数量是订购数量、没有最后发放时间
		service.deleteMaterialPartial(partialOrderManageForm,conn,errors);
		
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		log.info("PartialOrderManageAction.dodeletematerialpartial end");
	}
	
	/**
	 * 无BO
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doNoBo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,SqlSessionManager conn) throws Exception {
		log.info("PartialOrderManageAction.doNoBo start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		service.nobo(form, conn);
		
		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);
		
		log.info("PartialOrderManageAction.doNoBo start");
	}
}