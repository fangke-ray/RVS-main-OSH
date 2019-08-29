package com.osh.rvs.mapper.partial;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.partial.PartialWarehouseEntity;

/**
 * 零件入库单
 * 
 * @author liuxb
 * 
 */
public interface PartialWarehouseMapper {

	/**
	 * 查询
	 * 
	 * @param entity
	 * @return
	 */
	public List<PartialWarehouseEntity> search(PartialWarehouseEntity entity);

	/**
	 * 新建零件入库单
	 * 
	 * @param entity
	 */
	public void insert(PartialWarehouseEntity entity);

	/**
	 * 根据DN编号查询零件入库单信息
	 * 
	 * @param dn_no
	 */
	public PartialWarehouseEntity getByDnNo(@Param("dn_no") String dn_no);

	/**
	 * 根据入库进展查询入库单信息
	 * 
	 * @param steps
	 * @return
	 */
	public List<PartialWarehouseEntity> getByStep(String[] steps);

	/**
	 * 根据KEY查询入库单信息
	 * 
	 * @param key
	 * @return
	 */
	public PartialWarehouseEntity getByKey(@Param("key") String key);
	
	/**
	 * 更新step
	 * @param entity
	 */
	public void updateStep(PartialWarehouseEntity entity);
}
