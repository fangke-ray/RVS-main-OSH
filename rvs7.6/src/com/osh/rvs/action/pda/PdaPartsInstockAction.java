package com.osh.rvs.action.pda;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.partial.ComponentManageEntity;
import com.osh.rvs.form.partial.ComponentManageForm;
import com.osh.rvs.service.partial.ComponentManageService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;

/**
 * 
 * @Title PdaPartsInstockAction.java
 * @Project rvs
 * @Package com.osh.rvs.action.pda
 * @ClassName: PdaApplyAction
 * @author Gonglm
 * @date 2020-7-11 上午5:22:11
 */
public class PdaPartsInstockAction extends BaseAction {
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
		log.info("PdaPartsInstockAction.init start");

		ComponentManageForm partsForm = (ComponentManageForm)form;

		ComponentManageEntity cond = new ComponentManageEntity();
		cond.setSearch_step("0");
		ComponentManageService service = new ComponentManageService();

		// 组装完成NS组件一览
		List<ComponentManageForm> lstComposed = service.searchComponentManage(cond, conn);
		partsForm.setCmpt_list(lstComposed);
		partsForm.setCount("" + lstComposed.size());

		actionForward = mapping.findForward(FW_INIT);

		log.info("PdaPartsInstockAction.init end");
	}

	public void doInstock(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		log.info("PdaPartsInstockAction.doInstock start");

		ComponentManageService service = new ComponentManageService();
		// 画面数据转换
		ComponentManageEntity componentBean = new ComponentManageEntity();
		BeanUtil.copyToBean(form, componentBean, null);

		ComponentManageForm cmptForm = (ComponentManageForm)form;

		List<String> confirmMessages = new ArrayList<String>();

		// 查询库位是否使用
		service.checkStockCode(componentBean.getStock_code(), "0", confirmMessages, conn);

		if (confirmMessages.size() == 0) {
			// 设定入库
			service.partialInstock(componentBean, null, conn);

			// 清除表单中的传递
			cmptForm.setComponent_key(null);
			cmptForm.setStock_code(null);
		} else {
			// 清除表单中的传递
			cmptForm.setStock_code(null);

			// 通知信息
			req.setAttribute("errors", CommonStringUtil.joinBy("\r\n", confirmMessages.toArray()));
		}

		actionForward = mapping.findForward(FW_SUCCESS);

		log.info("PdaPartsInstockAction.doInstock end");
	}
}
