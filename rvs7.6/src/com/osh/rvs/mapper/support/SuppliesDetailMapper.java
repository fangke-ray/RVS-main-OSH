package com.osh.rvs.mapper.support;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.support.SuppliesDetailEntity;

/**
 * 
 * @Description 物品申购明细
 * @author liuxb
 * @date 2021-12-2 下午2:08:09
 */
public interface SuppliesDetailMapper {
	/**
	 * 查询物品申购明细
	 * 
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	public List<SuppliesDetailEntity> search(SuppliesDetailEntity entity) throws Exception;

	/**
	 * 新建物品申购明细
	 * 
	 * @param entity
	 * @throws Exception
	 */
	public void insert(SuppliesDetailEntity entity) throws Exception;

	/**
	 * 查询单个申购明细
	 * 
	 * @return
	 * @throws Exception
	 */
	public SuppliesDetailEntity getDetailByKey(@Param("supplies_key") String supplies_key) throws Exception;

	/**
	 * 删除
	 * 
	 * @param entity
	 * @throws Exception
	 */
	public void delete(SuppliesDetailEntity entity) throws Exception;

	/**
	 * 查询出所有'物品申购单 Key'为空，且有'上级确认者 ID'的记录
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<SuppliesDetailEntity> searchComfirmAndNoOrderKey() throws Exception;

	/**
	 * 确认、驳回
	 * 
	 * @throws Exception
	 */
	public void confirm(SuppliesDetailEntity entity) throws Exception;

	/**
	 * 加入订购单
	 * 
	 * @param entity
	 * @throws Exception
	 */
	public void updateOrderKey(SuppliesDetailEntity entity) throws Exception;

	/**
	 * 更新申购明细
	 * 
	 * @param entity
	 * @throws Exception
	 */
	public void updateApplication(SuppliesDetailEntity entity) throws Exception;

	/**
	 * 查询待收货
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<SuppliesDetailEntity> searchWaittingRecept() throws Exception;

	/**
	 * 更新收货日期
	 * 
	 * @param entity
	 * @throws Exception
	 */
	public void updateReceptDate(SuppliesDetailEntity entity) throws Exception;

	/**
	 * 更新验收日期
	 * 
	 * @param entity
	 * @throws Exception
	 */
	public void updateInlineReceptDate(SuppliesDetailEntity entity) throws Exception;

	/**
	 * 更新发票号码
	 * 
	 * @param entity
	 * @throws Exception
	 */
	public void updateInvoiceNo(SuppliesDetailEntity entity) throws Exception;

	/**
	 * 根据订购单KEY查询
	 * 
	 * @param order_key
	 * @return
	 * @throws Exception
	 */
	public List<SuppliesDetailEntity> getDetailByOrderKey(@Param("order_key") String order_key) throws Exception;

}