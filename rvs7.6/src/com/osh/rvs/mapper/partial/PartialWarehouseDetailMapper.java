package com.osh.rvs.mapper.partial;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.partial.PartialWarehouseDetailEntity;

/**
 * 零件入库明细
 * 
 * @author liuxb
 * 
 */
public interface PartialWarehouseDetailMapper {
	/**
	 * 新建零件入库明细
	 * 
	 * @param entity
	 */
	public void insert(PartialWarehouseDetailEntity entity);

	/**
	 * 根据零件入库单KEY查询零件入库明细
	 * 
	 * @param key
	 * @return
	 */
	public List<PartialWarehouseDetailEntity> searchByKey(@Param("key") String key);

	/**
	 * 根据零件入库单KEY查询零件入库分装明细
	 * 
	 * @param key
	 * @return
	 */
	public List<PartialWarehouseDetailEntity> searchUnpackByKey(@Param("key") String key);

	/**
	 * 根据KEY统计每种规格种别核对/上架总数
	 * 
	 * @param key
	 * @return
	 */
	public List<PartialWarehouseDetailEntity> countCollactionQuantityOfKind(@Param("key") String key);

	/**
	 * 根据KEY统计每种规格种别上架总数
	 * 
	 * @param key
	 * @return
	 */
	public List<PartialWarehouseDetailEntity> countUnpackQuantityOfKind(@Param("key") String key);

}
