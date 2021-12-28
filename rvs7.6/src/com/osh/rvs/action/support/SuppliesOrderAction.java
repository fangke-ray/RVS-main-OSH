package com.osh.rvs.action.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.support.SuppliesDetailForm;
import com.osh.rvs.form.support.SuppliesOrderForm;
import com.osh.rvs.service.DownloadService;
import com.osh.rvs.service.support.SuppliesDetailService;
import com.osh.rvs.service.support.SuppliesOrderService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.message.ApplicationMessage;
import framework.huiqing.common.util.validator.Validators;

/**
 * 
 * @Description 物品申购单
 * @author dell
 * @date 2021-12-16 上午10:06:39
 */
public class SuppliesOrderAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());
	private SuppliesOrderService service = new SuppliesOrderService();
	private SuppliesDetailService suppliesDetailService = new SuppliesDetailService();

	/**
	 * 检索
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("SuppliesOrderAction.search start");

		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		List<SuppliesOrderForm> list = service.search(form, conn);
		listResponse.put("list", list);

		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("SuppliesOrderAction.search end");
	}

	/**
	 * 新建订购单
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doInsert(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("SuppliesOrderAction.doInsert start");

		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		v.add("order_no", v.required("申购单号"));

		List<MsgInfo> errors = v.validate();
		if (errors.size() == 0) {
			SuppliesOrderForm pageForm = (SuppliesOrderForm) form;

			SuppliesOrderForm dbForm = service.getOrderByOrderNo(pageForm.getOrder_no(), conn);
			// 申请单号重复检查
			if (dbForm != null) {
				MsgInfo error = new MsgInfo();
				error.setComponentid("order_no");
				error.setErrcode("dbaccess.recordDuplicated");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordDuplicated", "申购单号【" + pageForm.getOrder_no() + "】"));
				errors.add(error);
			} else {
				// 新建订购单
				String orderKey = service.insert(req, pageForm, conn);

				Map<String, String[]> parameters = req.getParameterMap();
				List<String> keys = new AutofillArrayList<String>(String.class);
				Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");

				for (String parameterKey : parameters.keySet()) {
					Matcher m = p.matcher(parameterKey);
					if (m.find()) {
						String entity = m.group(1);
						if ("keys".equals(entity)) {
							String column = m.group(2);
							int icounts = Integer.parseInt(m.group(3));
							String[] value = parameters.get(parameterKey);
							if ("supplies_key".equals(column)) {
								keys.set(icounts, value[0]);
							}
						}
					}
				}

				// 将订购单KEY更新到订购明细
				for (String suppliesKey : keys) {
					SuppliesDetailForm suppliesDetailForm = new SuppliesDetailForm();
					suppliesDetailForm.setSupplies_key(suppliesKey);
					suppliesDetailForm.setOrder_key(orderKey);
					suppliesDetailService.addOrder(suppliesDetailForm, conn);
				}
			}
		}

		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("SuppliesOrderAction.doInsert end");
	}

	/**
	 * 一般物品申购单下载
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public ActionForward output(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("SuppliesOrderAction.output start");

		String orderKey = req.getParameter("orderKey");
		String fileName = req.getParameter("fileName");
		fileName = RvsUtils.charRecorgnize(fileName);
		
		String filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\supplies_order\\" + orderKey + "\\" + fileName;

		DownloadService downloadService = new DownloadService();
		downloadService.writeFile(res, "", fileName, filePath);

		log.info("SuppliesOrderAction.output end");

		return null;
	}

	/**
	 * 获取订购单信息
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void getDetail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("SuppliesOrderAction.getDetail start");

		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ONLYKEY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			SuppliesOrderForm pageForm = (SuppliesOrderForm) form;
			// 物品申购单Key
			String orderKey = pageForm.getOrder_key();

			// 订购单信息
			SuppliesOrderForm suppliesOrderForm = service.getOrderByOrderKey(orderKey, conn);
			listResponse.put("order", suppliesOrderForm);

			// 订购明细
			List<SuppliesDetailForm> orderDetails = suppliesDetailService.getSuppliseDetailByOrderKey(orderKey, conn);
			listResponse.put("orderDetails", orderDetails);
		}

		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("SuppliesOrderAction.getDetail end");
	}

	/**
	 * 盖章（经理、部长）
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doSign(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception {
		log.info("SuppliesOrderAction.doSign start");

		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();

		if (errors.size() == 0) {
			service.sign(form, req, conn);
		}

		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("SuppliesOrderAction.doSign end");
	}

}