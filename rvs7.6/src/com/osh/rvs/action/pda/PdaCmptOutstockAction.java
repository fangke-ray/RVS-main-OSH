package com.osh.rvs.action.pda;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.partial.ComponentManageEntity;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.partial.ComponentManageForm;
import com.osh.rvs.service.partial.ComponentManageService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

/**
 * 
 * @Title PdaCmptOutstockAction.java
 * @Project rvs
 * @Package com.osh.rvs.action.pda
 * @ClassName: PdaApplyAction
 * @author Gonglm
 * @date 2020-7-9 上午8:45:11
 */
public class PdaCmptOutstockAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());

	/**
	 * 初始化
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		log.info("PdaCmptOutstockAction.init start");

		ComponentManageForm cmptForm = (ComponentManageForm)form;

		ComponentManageEntity cond = new ComponentManageEntity();
		cond.setSearch_step("3");
		ComponentManageService service = new ComponentManageService();

		// 组装完成NS组件一览
		List<ComponentManageForm> lstComposed = service.searchComponentManage(cond, conn);
		cmptForm.setCmpt_list(lstComposed);
		cmptForm.setCount("" + lstComposed.size());

		actionForward = mapping.findForward(FW_INIT);

		log.info("PdaCmptOutstockAction.init end");
	}

	/**
	 * 取得组件信息并跳转
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void getDetail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		log.info("PdaCmptOutstockAction.getDetail start");

		ComponentManageForm cmptForm = (ComponentManageForm)form;
		ComponentManageService service = new ComponentManageService();

		// 检查
		List<String> errors = new ArrayList<String>();
		ComponentManageEntity detail = service.checkSerialNo(cmptForm.getSerial_no(), errors, conn);

		if (errors.size() == 0) {
			// 取得符合的维修品
			List<MaterialForm> targetMaterials = service.getTargetMaterials(detail, conn);
			if (targetMaterials.size() == 0) {
				// 检查发生错误时报告错误信息
				req.setAttribute("errors", "线上无此型号的S1级维修品。");
				actionForward = mapping.findForward(FW_INIT);
			} else {
				req.setAttribute("targetMaterials", targetMaterials); // .getSession()

				// 提交Form
				BeanUtil.copyToForm(detail, cmptForm, CopyOptions.COPYOPTIONS_NOEMPTY);

				actionForward = mapping.findForward(FW_SUCCESS);
			}
		} else {

			// 检查发生错误时报告错误信息
			req.setAttribute("errors", errors.get(0));
			actionForward = mapping.findForward(FW_INIT);
		}

		log.info("PdaCmptOutstockAction.getDetail end");
	}
}
