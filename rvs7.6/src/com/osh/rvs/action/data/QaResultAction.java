package com.osh.rvs.action.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.data.QaResultEntity;
import com.osh.rvs.form.data.QaResultForm;
import com.osh.rvs.service.CategoryService;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.OperatorService;
import com.osh.rvs.service.SectionService;
import com.osh.rvs.service.qa.QaResultService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;

public class QaResultAction extends BaseAction {
	private Logger log = Logger.getLogger(this.getClass());

	/*
	 * 品保一览
	 */
	public void init(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			SqlSession conn) throws Exception {
		log.info("QaResultAction.init start");

		Calendar cal = Calendar.getInstance();
		request.setAttribute("today",
				DateUtil.toString(cal.getTime(), DateUtil.DATE_PATTERN));// 品保通过默认开始日期

		CategoryService categoryService = new CategoryService();
		String cOptions = categoryService.getOptions(conn);
		request.setAttribute("cOptions", cOptions);// 维修对象机种集合

		ModelService modelService = new ModelService();
		String mReferChooser = modelService.getOptions(conn);
		request.setAttribute("mReferChooser", mReferChooser);// 维修对象型号集合

		SectionService sectionService = new SectionService();
		String sOptions = sectionService.getOptions(conn, "(全部)");
		request.setAttribute("sOptions", sOptions);// 维修科室集合

		
		OperatorService operatorService = new OperatorService();
		String sOperation = operatorService.getOptions(conn);
		request.setAttribute("sOperations", sOperation);// 出检人员集合
		
		//修理分类
		request.setAttribute("sMaterialFixType", CodeListUtils.getSelectOptions("material_fix_type",null,""));
		request.setAttribute("goMaterialFixType", CodeListUtils.getGridOptions("material_fix_type"));
		//返修分类
		request.setAttribute("sMaterialServiceRepair", CodeListUtils.getSelectOptions("material_service_repair",null,""));
		request.setAttribute("goMaterialServiceRepaire", CodeListUtils.getGridOptions("material_service_repair"));
		//直送
		request.setAttribute("sMaterialDirect", CodeListUtils.getSelectOptions("material_direct",null,""));
		request.setAttribute("goMaterialDirect", CodeListUtils.getGridOptions("material_direct"));
		
		actionForward = mapping.findForward(FW_INIT);

		log.info("QaResultAction.init end");
	}

	/*
	 * 查询
	 */
	public void search(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			SqlSession conn) throws Exception {
		log.info("QaResultAction.search start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> msgInfos = new ArrayList<MsgInfo>();

		// 获取Form数据
		QaResultForm qaResultForm = (QaResultForm) form;
		QaResultEntity qaResultEntity = new QaResultEntity();
		// 表单复制到数据对象
		BeanUtil.copyToBean(qaResultForm, qaResultEntity,CopyOptions.COPYOPTIONS_NOEMPTY);

		QaResultService qaResultServer = new QaResultService();
		List<QaResultForm> list = qaResultServer.searchQaResult(qaResultEntity,conn, msgInfos);

		listResponse.put("finished", list);
		listResponse.put("errors", new ArrayList<MsgInfo>());
		returnJsonResponse(response, listResponse);

		// actionForward = mapping.findForward("success");
		log.info("QaResultAction.search end");

	}
}
