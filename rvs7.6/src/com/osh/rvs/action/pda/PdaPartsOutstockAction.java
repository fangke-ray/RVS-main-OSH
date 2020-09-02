package com.osh.rvs.action.pda;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.partial.ComponentManageEntity;
import com.osh.rvs.form.partial.ComponentManageForm;
import com.osh.rvs.service.partial.ComponentManageService;

import framework.huiqing.action.BaseAction;

/**
 * 
 * @Title PdaPartsOutstockAction.java
 * @Project rvs
 * @Package com.osh.rvs.action.pda
 * @ClassName: PdaApplyAction
 * @author Gonglm
 * @date 2020-7-11 上午5:22:11
 */
public class PdaPartsOutstockAction extends BaseAction {
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
		log.info("PdaPartsOutstockAction.init start");

		ComponentManageForm partsForm = (ComponentManageForm)form;

		ComponentManageEntity cond = new ComponentManageEntity();
		cond.setSearch_step("1");
		ComponentManageService service = new ComponentManageService();

		// 组装完成NS组件一览
		List<ComponentManageForm> lstComposed = service.searchComponentManage(cond, conn);
		partsForm.setCmpt_list(lstComposed);
		partsForm.setCount("" + lstComposed.size());

		actionForward = mapping.findForward(FW_INIT);

		log.info("PdaPartsOutstockAction.init end");
	}
}
