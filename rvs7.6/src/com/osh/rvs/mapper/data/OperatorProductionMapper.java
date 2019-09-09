package com.osh.rvs.mapper.data;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.osh.rvs.bean.data.OperatorProductionEntity;


public interface OperatorProductionMapper {

	public List<OperatorProductionEntity> getProductionFeatureByCondition(OperatorProductionEntity entity);
	public List<OperatorProductionEntity> getProductionFeatureByConditionOfDay(OperatorProductionEntity entity);
	
	public OperatorProductionEntity getDetail(OperatorProductionEntity entity);
	
	public List<OperatorProductionEntity> getProductionFeatureByKey(OperatorProductionEntity entity);
	
	public void savePause(OperatorProductionEntity entity);
	
	public void deletePause(OperatorProductionEntity entity);
	
	public String existPause(OperatorProductionEntity entity);
	
	public void updatePause(OperatorProductionEntity entity);
	public void autoFinishPauseFeature(OperatorProductionEntity entity);

	public OperatorProductionEntity getPauseOvertime(OperatorProductionEntity entity);
	
	public void deletePauseOvertime(OperatorProductionEntity entity);
	
	public void updatePauseOvertime(OperatorProductionEntity entity);

	public List<Map<String, Object>> getProductionFeatureByLine(OperatorProductionEntity entity);

	/**
	 * 取得直接作业以外进行中状态
	 * @param operator_id 作业者 ID
	 * @return
	 */
	public OperatorProductionEntity getProcessingPauseStart(String operator_id);

	/**
	 * 取得最近完成的进行状态
	 * @param operator_id 作业者 ID
	 * @return
	 */
	public Date getLastProceedFinish(String operator_id);
	public int getOperatorPauseFinishPast(OperatorProductionEntity condition);

	public List<OperatorProductionEntity> getAfProductionFeatureByCondition(
			OperatorProductionEntity conditionBean);

	public List<OperatorProductionEntity> getAfProductionFeatureByKey(OperatorProductionEntity conditionBean);
}
