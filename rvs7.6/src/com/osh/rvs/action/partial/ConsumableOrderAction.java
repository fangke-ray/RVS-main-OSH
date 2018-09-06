/**
 * 系统名：OSH-RVS<br>
 * 模块名：系统管理<br>
 * 机能名：消耗品仓库管理记录<br>
 * @author 龚镭敏
 * @version 0.01
 */
package com.osh.rvs.action.partial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.partial.ConsumableOrderEntity;
import com.osh.rvs.form.partial.ConsumableManageForm;
import com.osh.rvs.service.partial.ConsumableOrderService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.message.ApplicationMessage;
import framework.huiqing.common.util.validator.Validators;

public class ConsumableOrderAction extends BaseAction {

	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * 消耗品仓库管理记录
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		log.info("ConsumableOrderAction.search start");

		ConsumableManageForm searchForm = (ConsumableManageForm) form;
		Map<String, Object> listResponse = new HashMap<String, Object>();

		ConsumableOrderService service = new ConsumableOrderService();

		// 检索条件设定
		ConsumableOrderEntity search_entity = new ConsumableOrderEntity();
		BeanUtil.copyToBean(searchForm, search_entity, null);

		// 订购记录一览
		List<ConsumableManageForm> order_list = service.searchOrderList(search_entity, conn);
		listResponse.put("order_list", order_list);

		listResponse.put("errors", new ArrayList<>());

		returnJsonResponse(res, listResponse);
		log.info("ConsumableOrderAction.search end");
	}

	/**
	 * 消耗品仓库管理记录
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
		log.info("ConsumableOrderAction.getDetail start");

		ConsumableManageForm detailForm = (ConsumableManageForm) form;
		Map<String, Object> listResponse = new HashMap<String, Object>();

		ConsumableOrderService service = new ConsumableOrderService();
		List<ConsumableManageForm> detail_list = service.searchOrderDetail(detailForm, conn);
		listResponse.put("detail_list", detail_list);

		listResponse.put("errors", new ArrayList<>());

		returnJsonResponse(res, listResponse);
		log.info("ConsumableOrderAction.getDetail end");
	}

	/**
	 * 消耗品仓库管理记录
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		log.info("ConsumableOrderAction.doUpdate start");

		Map<String, Object> listResponse = new HashMap<String, Object>();

		/* 表单合法性检查 */
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();
	
		ConsumableOrderService service = new ConsumableOrderService();

		List<ConsumableManageForm> formList = new AutofillArrayList<>(ConsumableManageForm.class);		

		Map<String,String[]> map = (Map<String,String[]>)req.getParameterMap();
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");
		// 整理提交数据
		for (String parameterKey : map.keySet()) {
			 Matcher m = p.matcher(parameterKey);
			 if (m.find()) {
				 String entity = m.group(1);
				 if("consumable_order".equals(entity)){
					 String column = m.group(2);
					 int i = Integer.parseInt(m.group(3));
					 String[] value = map.get(parameterKey);
					 
					 if ("consumable_order_key".equals(column)) {
						 formList.get(i).setConsumable_order_key(value[0]);
					 } else if ("partial_id".equals(column)) {
						 formList.get(i).setPartial_id(value[0]);
					 } else if ("code".equals(column)) {
						 formList.get(i).setCode(value[0]);
					 } else if ("order_quantity".equals(column)) {
						 formList.get(i).setOrder_quantity(value[0]);
					 } else if ("db_flg".equals(column)) {
						 formList.get(i).setDb_flg(value[0]);
					 } else if ("report_flg".equals(column)) {
						 formList.get(i).setReport_flg(value[0]);
					 }
				 }
			 }
		}

		// 数据check
		for (ConsumableManageForm cmform:formList) {
			if (!CommonStringUtil.isEmpty(cmform.getPartial_id())) {
				String order_quantity = cmform.getOrder_quantity();//订购数量
				if (CommonStringUtil.isEmpty(order_quantity)) {
					MsgInfo msg = new MsgInfo();
					msg.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "消耗品代码为: "
							+ cmform.getCode() + "的订购数量"));
					errors.add(msg);
					break;
				}
			}
		}
		
		if (errors.size() == 0) {
			service.updateOrder(formList, conn);
		}

		listResponse.put("errors", errors);

		returnJsonResponse(res, listResponse);
		log.info("ConsumableOrderAction.doUpdate end");
	}

	/**
	 * 消耗品加入库存check查询(autocomplete)
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void getAutocomplete(ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("ConsumableOrderAction.getAutocomplete start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		ConsumableManageForm detailForm = (ConsumableManageForm) form;
		ConsumableOrderService service = new ConsumableOrderService();

		List<ConsumableManageForm> list = service.getPartial(detailForm.getCode(), conn);
		if (list.size() != 1) {
			listResponse.put("reasult_flg", "0");
		} else {
			listResponse.put("reasult_flg", "1");
			ConsumableManageForm resultForm = new ConsumableManageForm();
			resultForm.setPartial_id(list.get(0).getPartial_id());
			resultForm.setDescription(list.get(0).getDescription());
			resultForm.setCm_partial_id(list.get(0).getCm_partial_id());
			resultForm.setBenchmark(list.get(0).getBenchmark());
			resultForm.setSafety_lever(list.get(0).getSafety_lever());
			resultForm.setAvailable_inventory(list.get(0).getAvailable_inventory());
			resultForm.setOn_passage(list.get(0).getOn_passage());
			listResponse.put("partial", resultForm);// 零件集合
		}
		returnJsonResponse(res, listResponse);

		log.info("ConsumableOrderAction.getAutocomplete end");
	}

	/**
	 * 生成消耗品订购单
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void createOrder(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		log.info("ConsumableOrderAction.createOrder start");

		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 调用后台程序生成订单
		HttpClient httpclient = new DefaultHttpClient();
		try {
			HttpGet request = new HttpGet("http://localhost:8080/rvspush/trigger/consumableOrder/0/0");
			logger.info("finger:" + request.getURI());
			HttpResponse response = httpclient.execute(request);
			if (response != null) {
				logger.info("Response: " + response.getStatusLine());
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		listResponse.put("errors", new ArrayList<>());

		returnJsonResponse(res, listResponse);
		log.info("ConsumableOrderAction.createOrder end");
	}

	/**
	 * 取消消耗品订购单
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doDelete(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		log.info("ConsumableOrderAction.doDelete start");

		Map<String, Object> listResponse = new HashMap<String, Object>();

		ConsumableOrderService service = new ConsumableOrderService();

		String code = req.getParameter("code");
		// 删除订购单
		service.deleteOrder(code, conn);

		listResponse.put("errors", new ArrayList<>());

		returnJsonResponse(res, listResponse);
		log.info("ConsumableOrderAction.doDelete end");
	}

	/**
	 * 消耗品订购单导出
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void report(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		log.info("ConsumableOrderAction.report start");

		Map<String, Object> listResponse = new HashMap<String, Object>();

		ConsumableManageForm consumableManageForm = (ConsumableManageForm) form;

		ConsumableOrderService service = new ConsumableOrderService();
		String fileName = consumableManageForm.getOrder_no() + ".xls";// 文件名称
		String filePath = service.report(consumableManageForm, conn);// 文件路径

		listResponse.put("fileName", fileName);
		listResponse.put("filePath", filePath);

		returnJsonResponse(res, listResponse);

		log.info("ConsumableOrderAction.report end");
	}

}
