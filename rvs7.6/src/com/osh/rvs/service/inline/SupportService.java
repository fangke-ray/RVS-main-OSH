package com.osh.rvs.service.inline;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.mapper.inline.SupportMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.message.ApplicationMessage;

public class SupportService {

	PositionPanelService ppService = new PositionPanelService();

	public void getProccessingData(Map<String, Object> listResponse, String material_id,
			ProductionFeatureEntity pf, LoginData user, SqlSession conn) throws Exception {
		// 取得维修对象信息。
		MaterialForm mform = ppService.getMaterialInfo(material_id, conn);
		listResponse.put("mform", mform);

		// 取到等待作业记录的本次返工总时间
		listResponse.put("spent_mins", ppService.getTotalTimeByRework(pf, conn) / 60);

		// 取得维修对象的作业标准时间。
		listResponse.put("leagal_overline",
				RvsUtils.getZeroOverLine(mform.getModel_name(), mform.getCategory_name(), user, null));

		// 取得维修对象在本工位中断/作业流程信息。
	}

	public ProductionFeatureEntity getSupportingPf(LoginData user, SqlSession conn) {
		String operator_id = user.getOperator_id();
		SupportMapper sDao = conn.getMapper(SupportMapper.class);
		ProductionFeatureEntity pfBean = new ProductionFeatureEntity();
		pfBean.setOperator_id(operator_id);
		pfBean = sDao.searchSupportingProductionFeature(pfBean);
		return pfBean;
	}

	/**
	 * 取得可辅助维修对象一览
	 * @param listResponse
	 * @param section_id
	 * @param conn
	 */
	public void getCanSupport(Map<String, Object> listResponse, String section_id, String line_id, SqlSession conn) {
		SupportMapper sDao = conn.getMapper(SupportMapper.class);
		listResponse.put("workinglist", sDao.getWorkingList(section_id, line_id, null));
	}

	public ProductionFeatureEntity checkMaterialId(String position_id, String operator_id, String section_id,
			List<MsgInfo> errors, SqlSessionManager conn) {
		ProductionFeatureEntity pfBean = new ProductionFeatureEntity();
		pfBean.setPosition_id(position_id);
		pfBean.setOperator_id(operator_id);
		pfBean.setSection_id(section_id);

		SupportMapper sDao = conn.getMapper(SupportMapper.class);
		pfBean = sDao.searchWorkingProductionFeature(pfBean);
		if(pfBean == null) {
			MsgInfo msgInfo = new MsgInfo();
			msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.supportObjectLost"));
			errors.add(msgInfo);
		}
		return pfBean;
	}
}
