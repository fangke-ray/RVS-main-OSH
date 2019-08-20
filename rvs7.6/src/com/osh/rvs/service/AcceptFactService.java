package com.osh.rvs.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;

import com.osh.rvs.bean.qf.AfProductionFeatureEntity;
import com.osh.rvs.form.qf.AfProductionFeatureForm;
import com.osh.rvs.mapper.qf.AfProductionFeatureMapper;

import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class AcceptFactService {

	public static Map<String, String> moduleMap = CodeListUtils.getList("qf_production_module");
	public static Map<String, String> typeMap = CodeListUtils.getList("qf_production_type");

	/**
	 * 取得全部间接作业选项
	 * 
	 * @param conn
	 * @return
	 */
	public String getOptions(SqlSession conn) {

		List<String[]> lst = new ArrayList<String[]>();
		for (String key : typeMap.keySet()) {
			String[] p = new String[3];
			p[0] = key;
			p[2] = moduleMap.get(key.substring(0, 2));
			p[1] = typeMap.get(key);
			lst.add(p);
		}

		return CodeListUtils.getReferChooser(lst);
	}

	public static void resetMap() {
		moduleMap = CodeListUtils.getList("qf_production_module");
		typeMap = CodeListUtils.getList("qf_production_type");
	}

	/**
	 * 根据操作者ID查找未结束作业信息
	 * 
	 * @param operatorID
	 * @param conn
	 * @return
	 */
	public AfProductionFeatureForm getUnFinish(String operatorID, SqlSession conn) {
		AfProductionFeatureMapper dao = conn.getMapper(AfProductionFeatureMapper.class);

		AfProductionFeatureForm respForm = null;
		AfProductionFeatureEntity entity = dao.getUnfinishByOperator(operatorID);

		if (entity != null) {
			respForm = new AfProductionFeatureForm();
			BeanUtil.copyToForm(entity, respForm, CopyOptions.COPYOPTIONS_NOEMPTY);
		}

		return respForm;
	}

	/**
	 * 结束当前作业或间歇事件
	 */
	public void finishCurrentAfWorkingOrPausingForOperator(String operatorId, boolean assertWorking, SqlSessionManager conn) throws Exception {
		AfProductionFeatureMapper afMapper = conn.getMapper(AfProductionFeatureMapper.class);
		AfProductionFeatureEntity entity = afMapper.getUnfinishByOperator(operatorId);

		if (entity != null) {
			afMapper.updateFinishTime(entity.getAf_pf_key());
		} else if (!assertWorking) {
			PauseFeatureService pfService = new PauseFeatureService();
			pfService.finishPauseFeature(null, null, null, operatorId, conn);
		}
	}
}
