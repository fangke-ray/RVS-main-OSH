package com.osh.rvs.action.qf;

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
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.qf.FactMaterialForm;
import com.osh.rvs.service.qf.DeliveryOrderService;
import com.osh.rvs.service.qf.FactMaterialService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

/**
 * 出货单制作
 * 
 * @author dell
 * 
 */
public class DeliveryOrderAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	/**
	 * 受理画面初始表示处理
	 * 
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("DeliveryOrderAction.init start");

		// 设定OCM文字
		req.setAttribute("oOptions", CodeListUtils.getGridOptions("material_direct_ocm"));

		// 设定等级文字
		req.setAttribute("lOptions", CodeListUtils.getGridOptions("material_level"));

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("DeliveryOrderAction.init end");
	}

	/**
	 * 查询
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("DeliveryOrderAction.search start");

		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		DeliveryOrderService service = new DeliveryOrderService();

		// 待出货单
		List<MaterialForm> waitings = service.getWaitings(conn);
		// 今日出货单
		List<MaterialForm> finished = service.getFinished(conn);

		callbackResponse.put("waitings", waitings);
		callbackResponse.put("finished", finished);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);

		log.info("DeliveryOrderAction.search end");
	}

	/**
	 * 制作出货单
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doInsert(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("DeliveryOrderAction.doInsert start");

		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		v.delete("af_pf_key");
		List<MsgInfo> errors = v != null ? v.validate() : new ArrayList<MsgInfo>();

		if (errors.size() == 0) {
			FactMaterialService factMaterialService = new FactMaterialService();

			// 当前登录者
			LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
			FactMaterialForm factMaterialForm = (FactMaterialForm) form;
			factMaterialService.insertFactMaterial(user.getOperator_id(), factMaterialForm.getMaterial_id(), 0, conn);
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);

		log.info("DeliveryOrderAction.doInsert end");

	}
}
