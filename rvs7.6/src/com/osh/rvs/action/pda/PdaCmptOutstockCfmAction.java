package com.osh.rvs.action.pda;

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
import framework.huiqing.common.util.copy.BeanUtil;

/**
 * 
 * @Title PdaCmptOutstockAction.java
 * @Project rvs
 * @Package com.osh.rvs.action.pda
 * @ClassName: PdaApplyAction
 * @author Gonglm
 * @date 2020-7-9 上午8:45:11
 */
public class PdaCmptOutstockCfmAction extends BaseAction {
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
		log.info("PdaCmptOutstockCfmAction.init start");

		actionForward = mapping.findForward(FW_INIT);

		log.info("PdaCmptOutstockCfmAction.init end");
	}

	public void doOutStock(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		log.info("PdaCmptOutstockCfmAction.doOutStock start");

		ComponentManageService service = new ComponentManageService();
		// 画面数据转换
		ComponentManageEntity componentBean = new ComponentManageEntity();
		BeanUtil.copyToBean(form, componentBean, null);

		// 设定出库
		String confirmMessage = service.componentOutstock(componentBean, conn);
		// 通知信息
		req.setAttribute("errors", confirmMessage);
		// 清除表单中的传递
		ComponentManageForm cmptForm = (ComponentManageForm)form;
		cmptForm.setSerial_no(null);
		cmptForm.setTarget_material_id(null);

		actionForward = mapping.findForward(FW_SUCCESS);

		log.info("PdaCmptOutstockCfmAction.doOutStock end");
	}

}
