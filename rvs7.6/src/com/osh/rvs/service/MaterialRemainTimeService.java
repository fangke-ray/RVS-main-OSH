package com.osh.rvs.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.form.data.ProductionFeatureForm;
import com.osh.rvs.form.master.PositionPlanTimeForm;
import com.osh.rvs.mapper.manage.MaterialRemainTimeMapper;

public class MaterialRemainTimeService {

	/**
	 * 取得维修对象预计时间
	 * @param material_id
	 * @param user
	 * @param listResponse
	 * @param conn
	 * @return
	 */
	public boolean getMaterialPlan(String material_id, LoginData user, Map<String, Object> listResponse, SqlSession conn) {
		boolean isExistInPlan = false;

		// 检查维修对象预计时间是否存在
		MaterialRemainTimeMapper dao = conn.getMapper(MaterialRemainTimeMapper.class);
		int count = dao.checkMaterialRemainTime(material_id, user.getLine_id());
		if (count == 1) {
			// 取得维修对象计划进度
			PositionPlanTimeService positionPlanTimeService = new PositionPlanTimeService();
			List<PositionPlanTimeForm> positionPlanTimeList = positionPlanTimeService.searchMaterialPositionPlanTime(
					material_id, user.getLine_id(), conn);
			if (positionPlanTimeList.size() > 0) {
				isExistInPlan = true;
			}

			ProductionFeatureService productionFeatureService = new ProductionFeatureService();
			// 查询实际已完成计划工位
			List<ProductionFeatureForm> positionActualList = productionFeatureService
					.getFinishedProductionFeatureByMaterialId(material_id, user.getLine_id(), conn);

			listResponse.put("positionPlanTimeList", positionPlanTimeList);
			listResponse.put("positionActualList", positionActualList);
		}
		return isExistInPlan;
	}
}
