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

import com.osh.rvs.bean.partial.ConsumableSupplyEntity;
import com.osh.rvs.form.pda.PdaSupplyForm;
import com.osh.rvs.service.partial.ConsumableSupplyService;
import com.osh.rvs.service.partial.PdaSupplyService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

/**
 * 
 * @Title PdaSupplyAction.java
 * @Project rvs
 * @Package com.osh.rvs.action.pda
 * @ClassName: PdaSupplyAction
 * @Description: TODO
 * @author lxb
 * @date 2015-5-29 上午10:48:08
 */
public class PdaSupplyAction extends PdaBaseAction {
	private Logger log = Logger.getLogger(getClass());
	
	public void init(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response,SqlSession conn) throws Exception {
		log.info("PdaSupplyAction.init start");
		
		actionForward = mapping.findForward(FW_INIT);
		
		PdaSupplyService service = new PdaSupplyService();
		int supply_num = service.getCurrentDaySupplyNum(conn);
		request.setAttribute("supply_num", supply_num);
		
		log.info("PdaSupplyAction.init end");
	}
	
	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("PdaSupplyAction.search start");
		
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		PdaSupplyService service = new PdaSupplyService();
		PdaSupplyForm pdaSupplyForm  = service.search(form, conn,errors);
		
		int supply_num = service.getCurrentDaySupplyNum(conn);
		
		request.setAttribute("pdaSupplyForm", pdaSupplyForm);
		request.setAttribute("errors", getStrMsgInfo(errors));
		request.setAttribute("supply_num", supply_num);
		
		actionForward = mapping.findForward(FW_INIT);
		
		log.info("PdaSupplyAction.search end");
	}
	
	
	/**
	 * 入库
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doSupply(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response,SqlSessionManager conn)throws Exception{
		log.info("PdaSupplyAction.doSupply start");

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();
		
		PdaSupplyForm detailForm = (PdaSupplyForm) form;
		
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

		
		
		PdaSupplyService pdaSupplyService = new PdaSupplyService();
		int supply_num = pdaSupplyService.getCurrentDaySupplyNum(conn);
		
		
		request.setAttribute("supply_num", supply_num);
		request.setAttribute("errors", getStrMsgInfo(errors));
		
		actionForward = mapping.findForward(FW_INIT);
		
		log.info("PdaSupplyAction.doSupply end");
	}
	
	
}
