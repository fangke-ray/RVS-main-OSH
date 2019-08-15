package com.osh.rvs.mapper.partial;

import java.util.List;

import com.osh.rvs.bean.partial.FactPartialWarehouseEntity;

/**
 * 现品入库作业数
 * 
 * @author liuxb
 * 
 */
public interface FactPartialWarehouseMapper {

	/**
	 * 查询
	 * 
	 * @param entity
	 * @return
	 */
	public List<FactPartialWarehouseEntity> search(FactPartialWarehouseEntity entity);

	/**
	 * 新建现品入库作业数
	 * 
	 * @param entity
	 * @throws Exception
	 */
	public void insert(FactPartialWarehouseEntity entity);

	/**
	 * 更新现品入库作业数
	 * 
	 * @param entity
	 * @throws Exception
	 */
	public void update(FactPartialWarehouseEntity entity);

	/**
	 * 统计每个规格种别入库作业总数
	 * 
	 * @param entity
	 * @return
	 */
	public List<FactPartialWarehouseEntity> countQuantityOfSpecKind(FactPartialWarehouseEntity entity);

}
