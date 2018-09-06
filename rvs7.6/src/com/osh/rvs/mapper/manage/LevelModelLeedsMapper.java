package com.osh.rvs.mapper.manage;

import java.util.List;

import com.osh.rvs.bean.manage.ModelLevelSetEntity;

/**
 * 型号等级拉动台数设置
 * 
 * @author lxb
 * 
 */
public interface LevelModelLeedsMapper {
	/* 型号等级拉动台数一览 */
	public List<ModelLevelSetEntity> searchModelLevelSet(ModelLevelSetEntity entity);

	/* 模拟拉动 */
	public List<ModelLevelSetEntity> searchLeed(ModelLevelSetEntity entity);

	/* 更新设置拉动台数 */
	public void updateForecastSetting(ModelLevelSetEntity entity);

	/* 设置拉动台数统计 */
	public int getForecastResultSetCount(ModelLevelSetEntity entity);
	
	/*波动系数*/
	public Double getCoefficientOfVariation(ModelLevelSetEntity entity);
	
	/*梯队*/
	public Integer getEchelon(ModelLevelSetEntity entity);
	
	/*拉动台数当前设置数量*/
	public Integer getForecastSetting(ModelLevelSetEntity entity);
	
	public ModelLevelSetEntity getModelLevelSet(ModelLevelSetEntity entity);
}
