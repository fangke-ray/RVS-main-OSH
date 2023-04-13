package com.osh.rvs.mapper.qf;

import java.util.List;

import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.qf.FactMaterialEntity;

/**
 * 现品维修对象作业
 * 
 * @author liuxb
 * 
 */
public interface FactMaterialMapper {

	public List<FactMaterialEntity> search(FactMaterialEntity entity);

	/**
	 * 新建现品维修对象作业
	 * 
	 * @param entity
	 */
	public void insert(FactMaterialEntity entity);

	/**
	 * 待出货单
	 * 
	 * @return
	 */
	public List<MaterialEntity> getDeliveryOrderWaitings();

	/**
	 * 今日出货单
	 * 
	 * @return
	 */
	public List<MaterialEntity> getDeliveryOrderFinished();
	
	/**
	 * 统计完成件数
	 * @param entity
	 * @return
	 */
	public int countFinished(FactMaterialEntity entity);
	public int countTempFinished(FactMaterialEntity entity);

	/**
	 * 统计完成数量（按课室区分，5件1车）
	 * 
	 * @param af_pf_key
	 * @return
	 */
	public Integer countByTrolley(String af_pf_key);
}
