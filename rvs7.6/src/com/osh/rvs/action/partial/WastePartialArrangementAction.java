package com.osh.rvs.action.partial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.form.partial.WastePartialArrangementForm;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.partial.WastePartialArrangementService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

/**
 * 废弃零件整理
 * 
 * @Description
 * @author dell
 * @date 2019-12-27 下午12:56:00
 */
public class WastePartialArrangementAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());

	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("WastePartialArrangementAction.init end");

		ModelService modelService = new ModelService();
		String mReferChooser = modelService.getOptions(conn);
		request.setAttribute("mReferChooser", mReferChooser);

		actionForward = mapping.findForward(FW_INIT);

		log.info("WastePartialArrangementAction.init end");
	}

	/**
	 * 查询
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SqlSession conn) throws Exception {
		log.info("WastePartialArrangementAction.search start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		WastePartialArrangementService service = new WastePartialArrangementService();
		List<WastePartialArrangementForm> list = service.search(form, conn);
		listResponse.put("finished", list);

		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);

		log.info("WastePartialArrangementAction.search end");
	}

	/**
	 * 新建
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doInsert(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, SqlSessionManager conn) throws Exception {
		log.info("WastePartialArrangementAction.doInsert start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		// 取得用户信息
		HttpSession session = request.getSession();

		WastePartialArrangementForm wastePartialArrangementForm = (WastePartialArrangementForm) form;
		// 回收箱用途种类
		String collectKind = wastePartialArrangementForm.getCollect_kind();
		String materialID = wastePartialArrangementForm.getMaterial_id();

		if ("1".equals(collectKind)) {
			Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
			v.add("material_id", v.required("维修品ID"));
			v.add("collect_case_id", v.required("废弃零件回收箱ID"));
			errors = v.validate();
		}

		if (errors.size() == 0) {
			WastePartialArrangementService service = new WastePartialArrangementService();
			// 回收部分最大值
			Integer part = service.getMaxPartByMaterialId(materialID, conn);

			if (part == null)
				part = 0;

			if ("1".equals(collectKind)) {
				part++;

				wastePartialArrangementForm.setPart(Integer.toString(part));
				service.inser(wastePartialArrangementForm, session, conn);
			} else {
				Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");
				List<WastePartialArrangementForm> formList = new AutofillArrayList<WastePartialArrangementForm>(WastePartialArrangementForm.class);

				Map<String, String[]> parameters = request.getParameterMap();
				// 整理提交数据
				for (String parameterKey : parameters.keySet()) {
					Matcher m = p.matcher(parameterKey);
					if (m.find()) {
						String entity = m.group(1);
						if ("waste_partial_arrangement".equals(entity)) {
							String column = m.group(2);
							int icounts = Integer.parseInt(m.group(3));
							String[] value = parameters.get(parameterKey);
							if ("collect_case_id".equals(column)) {
								formList.get(icounts).setCollect_case_id(value[0]);
							}
							formList.get(icounts).setMaterial_id(materialID);
						}
					}
				}

				for (int i = 0; i < formList.size(); i++) {
					wastePartialArrangementForm = formList.get(i);
					wastePartialArrangementForm.setPart(Integer.toString(part + (i + 1)));
					service.inser(wastePartialArrangementForm, session, conn);
				}
			}
		}

		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);

		log.info("WastePartialArrangementAction.doInsert end");
	}

}
