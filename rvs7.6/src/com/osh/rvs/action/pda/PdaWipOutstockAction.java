package com.osh.rvs.action.pda;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.inline.MaterialFactEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.pda.PdaMaterialForm;
import com.osh.rvs.service.AcceptFactService;
import com.osh.rvs.service.MaterialService;
import com.osh.rvs.service.qf.FactMaterialService;
import com.osh.rvs.service.qf.MaterialFactService;
import com.osh.rvs.service.qf.WipService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

public class PdaWipOutstockAction extends PdaBaseAction {

	public void init(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res, SqlSession conn)
			throws Exception {
		log.info("PdaWipOutstockAction.init start");

		req.setAttribute("notice", "");
		req.setAttribute("storageMap", "");

		actionForward = mapping.findForward(FW_INIT);

		log.info("PdaWipOutstockAction.init end");
	}

	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn)throws Exception {
		log.info("PdaWipOutstockAction.search start");

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();
		
		if(errors.size()==0){
			WipService service = new WipService();
			service.searchForPda(req, form, conn, errors);
		}

		req.setAttribute("errors", getStrMsgInfo(errors));
		
		actionForward = mapping.findForward(FW_SUCCESS);
		
		log.info("PdaWipOutstockAction.search end");
	}

	public void doOut(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn)throws Exception {
		log.info("PdaWipOutstockAction.doOut start");

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();
		
		if(errors.size()==0){
			PdaMaterialForm pdaMaterialForm = (PdaMaterialForm) form;
			String omrNotifiNo = pdaMaterialForm.getOmr_notifi_no();
//			String wipLocation = pdaMaterialForm.getWip_location();

			WipService service = new WipService();
			service.warehousing(conn, pdaMaterialForm.getMaterial_id(), null);

			pdaMaterialForm.reset();

			req.setAttribute("notice", omrNotifiNo + "已从库位取出。");
			req.setAttribute("storageMap", "");
		}

		req.setAttribute("errors", getStrMsgInfo(errors));
		
		actionForward = mapping.findForward(FW_SUCCESS);
		
		log.info("PdaWipOutstockAction.doOut end");
	}

	public void doInline(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn)throws Exception {
		log.info("PdaWipOutstockAction.doInline start");

		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v.validate();
		
		if(errors.size()==0){
			LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

			PdaMaterialForm pdaMaterialForm = (PdaMaterialForm) form;
			List<String> triggerList = new ArrayList<String>();

			// 切换作业
			AcceptFactService afService = new AcceptFactService();
			afService.switchTo(null,  //当前是；NULL=不明
					true, // 要切换到TRUE=直接作业
					"202", user.getOperator_id(), 
					true, // 判断是否已经是需要切换的状态了
					conn);
			
			// 更新到现品作业记录（维修品）
			FactMaterialService fmsService = new FactMaterialService();
			fmsService.insertFactMaterial(user.getOperator_id(), pdaMaterialForm.getMaterial_id(), 1, conn);

			MaterialService mService = new MaterialService();
			MaterialFactService inlineService = new MaterialFactService();
			MaterialEntity mBean = mService.loadSimpleMaterialDetailEntity(conn, pdaMaterialForm.getMaterial_id());

			MaterialFactEntity inlineEntity = new MaterialFactEntity();
			inlineEntity.setMaterial_id(pdaMaterialForm.getMaterial_id());
			inlineEntity.setSection_id(pdaMaterialForm.getSection_id());
			inlineEntity.setPat_id(pdaMaterialForm.getPat_id());

			inlineEntity.setLevel(mBean.getLevel());
			inlineEntity.setFix_type(mBean.getFix_type());
			inlineEntity.setAgreed_date(mBean.getAgreed_date());
			inlineEntity.setModel_id(mBean.getModel_id());

			inlineEntity.setScheduled_expedited(mBean.getScheduled_expedited());
			inlineEntity.setWip_location(mBean.getWip_location());

			inlineService.updateInline(inlineEntity, conn, triggerList);

			pdaMaterialForm.reset();

			req.setAttribute("notice", mBean.getSorc_no() + "完成投线");
			req.setAttribute("storageMap", "");

			// 更新到现品作业记录（维修品）
			int updateCount = fmsService.insertFactMaterial(user.getOperator_id(), pdaMaterialForm.getMaterial_id(), 1, conn);
			// 通知后台刷新作业标记
			if (updateCount > 0) {
				conn.commit();
				afService.fingerOperatorRefresh(user.getOperator_id());
			}
		}

		req.setAttribute("errors", getStrMsgInfo(errors));
		
		actionForward = mapping.findForward(FW_SUCCESS);
		
		log.info("PdaWipOutstockAction.doInline end");
	}
}
