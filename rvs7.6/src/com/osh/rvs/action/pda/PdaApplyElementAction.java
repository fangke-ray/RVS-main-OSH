package com.osh.rvs.action.pda;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.form.pda.PdaApplyDetailForm;
import com.osh.rvs.form.pda.PdaApplyElementForm;
import com.osh.rvs.service.partial.PdaApplyService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

public class PdaApplyElementAction extends PdaBaseAction {

	private Logger log = Logger.getLogger(getClass());

	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,SqlSession conn) throws Exception {
		log.info("PdaApplyElementAction.init start");

		PdaApplyElementForm elementForm = (PdaApplyElementForm) form;

		PdaApplyDetailForm detailForm = (PdaApplyDetailForm)req.getSession().getAttribute("pdaApplyDetailForm");
		String consumable_application_key = "";
		String code = "";
		if (detailForm != null) {
			consumable_application_key = detailForm.getConsumable_application_key();
			code = detailForm.getCode();
		}
		
		elementForm.setConsumable_application_key(consumable_application_key);
		elementForm.setCode(code);

		PdaApplyService service = new PdaApplyService();
		PdaApplyElementForm pdaApplyElementForm = service.getApplyElementDetail(elementForm,req, conn);

		req.setAttribute("pdaApplyElementForm", pdaApplyElementForm);

		actionForward = mapping.findForward(FW_INIT);

		log.info("PdaApplyElementAction.init end");
	}
	
	
	
	public void update(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,SqlSession conn)throws Exception {
		log.info("PdaApplyElementAction.update start");
		
		
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();
		
		if(errors.size()==0){
			PdaApplyService service = new PdaApplyService();
			service.updateSupply(form,req);
		}
		
		req.setAttribute("errors", getStrMsgInfo(errors));
		
		actionForward = mapping.findForward(FW_SUCCESS);
		
		log.info("PdaApplyElementAction.update end");
	}
}
