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

import com.osh.rvs.bean.partial.ConsumableInventoryEntity;
import com.osh.rvs.form.partial.ConsumableManageForm;
import com.osh.rvs.service.partial.ConsumableInventoryService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.common.util.copy.BeanUtil;

public class ConsumableInventoryAction extends BaseAction {

	private Logger log = Logger.getLogger(this.getClass());

	/**
	 * 消耗品仓库盘点记录
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		log.info("ConsumableInventoryAction.search start");

		ConsumableManageForm searchForm = (ConsumableManageForm)form;
		Map<String, Object> listResponse = new HashMap<String, Object>();

		ConsumableInventoryService service = new ConsumableInventoryService();

		// 检索条件设定
		ConsumableInventoryEntity search_entity = new ConsumableInventoryEntity();
		BeanUtil.copyToBean(searchForm, search_entity, null);

		// 盘点记录一览
		List<ConsumableManageForm> inventory_list = service.searchInventoryList(search_entity, conn);
		listResponse.put("inventory_list", inventory_list);

		listResponse.put("errors", new ArrayList<>());

		returnJsonResponse(res, listResponse);
		log.info("ConsumableInventoryAction.search end");
	}

	/**
	 * 消耗品仓库盘点记录更新
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
		log.info("ConsumableInventoryAction.doUpdate start");

		ConsumableManageForm detailForm = (ConsumableManageForm) form;
		Map<String, Object> listResponse = new HashMap<String, Object>();

		ConsumableInventoryService service = new ConsumableInventoryService();
		service.updateInventoryDetail(detailForm, conn);
		
		listResponse.put("errors", new ArrayList<>());

		returnJsonResponse(res, listResponse);
		log.info("ConsumableInventoryAction.doUpdate end");
	}
}
