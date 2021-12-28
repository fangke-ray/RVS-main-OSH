package com.osh.rvs.mapper.master;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.master.SuppliesReferListEntity;

/**
 * 
 * @Description 常用采购清单
 * @author liuxb
 * @date 2021-11-30 上午10:09:19
 */
public interface SuppliesReferListMapper {
	/**
	 * 查询常用采购清单
	 * 
	 * @param entity
	 * @return
	 */
	public List<SuppliesReferListEntity> search(SuppliesReferListEntity entity) throws Exception;

	/**
	 * 新建常用采购清单记录
	 * 
	 * @param entity
	 * @throws Exception
	 */
	public void insert(SuppliesReferListEntity entity) throws Exception;

	/**
	 * 更新常用采购清单记录
	 * 
	 * @param entity
	 * @throws Exception
	 */
	public void update(SuppliesReferListEntity entity) throws Exception;

	/**
	 * 根据采购清单KEY查询
	 * 
	 * @param refer_key
	 * @throws Exception
	 */
	public SuppliesReferListEntity getSuppliesReferByKey(@Param("refer_key") String refer_key) throws Exception;

	/**
	 * 删除采购清单
	 * 
	 * @param refer_key
	 * @throws Exception
	 */
	public void delete(@Param("refer_key") String refer_key) throws Exception;

	/**
	 * 查询规格为空的记录
	 * 
	 * @param entity
	 * @return
	 */
	public List<SuppliesReferListEntity> searchEmptyModel(SuppliesReferListEntity entity) throws Exception;

	/**
	 * 更新图片UUID
	 * 
	 * @param entity
	 * @throws Exception
	 */
	public void updatePhotoUuid(SuppliesReferListEntity entity) throws Exception;
}