package com.osh.rvs.mapper.partial;

import java.util.List;

import com.osh.rvs.bean.partial.FactPartialReleaseEntity;

/**
 * 零件出库作业数
 * 
 * @author liuxb
 * 
 */
public interface FactPartialReleaseMapper {
	/**
	 * 新建零件出库作业数
	 * 
	 * @param entity
	 */
	public void insert(FactPartialReleaseEntity entity);

	/**
	 * 更新零件出库作业数
	 * 
	 * @param entity
	 */
	public void update(FactPartialReleaseEntity entity);

	/**
	 * 获取零件出库作业数
	 * 
	 * @param entity
	 * @return
	 */
	public FactPartialReleaseEntity getPartialRelease(FactPartialReleaseEntity entity);

	/**
	 * 获得维修品零件订单
	 */
	public List<FactPartialReleaseEntity> getTodayPartialOrderEdit(String operator_id);
}
