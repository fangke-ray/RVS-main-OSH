package com.osh.rvs.mapper.qf;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.qf.AfProductionFeatureEntity;

/**
 * 间接人员作业信息
 * 
 * @author liuxb
 * 
 */
public interface AfProductionFeatureMapper {
	/**
	 * 新建间接人员作业信息
	 * 
	 * @param entity
	 */
	public void insert(AfProductionFeatureEntity entity);

	/**
	 * 更新结束时间
	 * 
	 * @param af_pf_key
	 */
	public void updateFinishTime(@Param("af_pf_key") String af_pf_key);

	/**
	 * 根据操作者ID查找未结束作业信息
	 * 
	 * @param operator_id
	 * @return
	 */
	public AfProductionFeatureEntity getUnfinishByOperator(@Param("operator_id") String operator_id);

	/**
	 * 结束操作者的当前作业信息
	 * @param operator_id
	 * @return
	 */
	public int finishProductionOfOperator(String operator_id);

	/**
	 * 根据作业内容查找未结束作业信息
	 * 
	 * @param production_type
	 * @return
	 */
	public AfProductionFeatureEntity getUnfinishByType(@Param("production_type") int production_type);
}
