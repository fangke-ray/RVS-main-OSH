package com.osh.rvs.mapper.support;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.support.SuppliesOrderEntity;

/**
 * 
 * @Description 物品申购单
 * @author liuxb
 * @date 2021-12-2 下午3:19:48
 */
public interface SuppliesOrderMapper {

	/**
	 * 查询
	 * 
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	public List<SuppliesOrderEntity> search(SuppliesOrderEntity entity) throws Exception;

	/**
	 * 新建物品申购单
	 * 
	 * @param entity
	 * @throws Exception
	 */
	public void insert(SuppliesOrderEntity entity) throws Exception;

	/**
	 * 根据申购单号查询
	 * 
	 * @param order_no 申购单号
	 * @return
	 * @throws Exception
	 */
	public SuppliesOrderEntity getByOrderNo(@Param("order_no") String order_no) throws Exception;

	/**
	 * 根据申购单号查询
	 * 
	 * @param order_key申购单号
	 * @return
	 * @throws Exception
	 */
	public SuppliesOrderEntity getByOrderKey(@Param("order_key") String order_key) throws Exception;

	/**
	 * 盖章确认
	 * 
	 * @param entity
	 * @throws Exception
	 */
	public void sign(SuppliesOrderEntity entity) throws Exception;
}