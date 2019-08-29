package com.osh.rvs.mapper.partial;

import java.util.List;

import com.osh.rvs.bean.partial.FactConsumableWarehouseEntity;

/**
 * 现品入库作业数
 * 
 * @author liuxb
 * 
 */
public interface FactConsumableWarehouseMapper {

	/**
	 * 查询
	 * 
	 * @param entity
	 * @return
	 */
	public List<FactConsumableWarehouseEntity> search(FactConsumableWarehouseEntity entity);

	/**
	 * 新建现品入库作业数
	 * 
	 * @param entity
	 * @throws Exception
	 */
	public void insert(FactConsumableWarehouseEntity entity);

	/**
	 * 更新现品入库作业数
	 * 
	 * @param entity
	 * @throws Exception
	 */
	public void update(FactConsumableWarehouseEntity entity);

}
