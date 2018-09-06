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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.partial.ConsumableSupplyEntity;
import com.osh.rvs.form.partial.ConsumableManageForm;
import com.osh.rvs.service.partial.ConsumableSupplyService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.message.ApplicationMessage;
import framework.huiqing.common.util.validator.Validators;

public class ConsumableSupplyAction extends BaseAction {

	private Logger log = Logger.getLogger(this.getClass());

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
		log.info("ConsumableSupplyAction.search start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		ConsumableManageForm searchForm = (ConsumableManageForm) form;
		ConsumableSupplyService service = new ConsumableSupplyService();

		// 检索条件设定
		ConsumableSupplyEntity search_entity = new ConsumableSupplyEntity();
		BeanUtil.copyToBean(searchForm, search_entity, null);

		// 入库记录一览
		List<ConsumableManageForm> supply_list = service.searchSupplyList(search_entity, conn);
		listResponse.put("supply_list", supply_list);

		listResponse.put("errors", new ArrayList<>());

		returnJsonResponse(res, listResponse);
		log.info("ConsumableSupplyAction.search end");
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
		log.info("ConsumableSupplyAction.getAutocomplete start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		ConsumableManageForm detailForm = (ConsumableManageForm) form;
		ConsumableSupplyService service = new ConsumableSupplyService();

		List<ConsumableManageForm> list = service.getPartial(detailForm.getCode(), conn);
		if (list.size() != 1) {
			listResponse.put("reasult_flg", "0");
		} else {
			listResponse.put("reasult_flg", "1");
			ConsumableManageForm resultForm = new ConsumableManageForm();
			resultForm.setName(list.get(0).getName());
			resultForm.setPartial_id(list.get(0).getPartial_id());
			listResponse.put("partial", resultForm);// 零件集合
		}
		returnJsonResponse(res, listResponse);

		log.info("ConsumableSupplyAction.getAutocomplete end");
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
	public void doAdd(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		log.info("ConsumableSupplyAction.doAdd start");

		Map<String, Object> listResponse = new HashMap<String, Object>();

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();

		// 数据check
		ConsumableManageForm detailForm = (ConsumableManageForm) form;
		// 入库数量
		String quantity = detailForm.getQuantity();
		if (CommonStringUtil.isEmpty(quantity)) {
			MsgInfo msg = new MsgInfo();
			msg.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "入库数量"));
			errors.add(msg);
		}

		if (errors.size() == 0) {
			ConsumableSupplyService service = new ConsumableSupplyService();
	
			ConsumableSupplyEntity entity = new ConsumableSupplyEntity();
			entity.setPartial_id(Integer.parseInt(detailForm.getPartial_id()));
			entity.setQuantity(Integer.parseInt(detailForm.getQuantity()));
			String partial_id = service.searchPartialSupply(entity, conn);
			if (CommonStringUtil.isEmpty(partial_id)) {
				service.insertPartialSupply(entity, conn);
			} else {
				service.updatePartialSupply(entity, conn);
			}
			service.updateConsumableManage(entity, conn);
		}

		listResponse.put("errors", errors);
		returnJsonResponse(res, listResponse);

		log.info("ConsumableSupplyAction.doAdd end");
	}

}
