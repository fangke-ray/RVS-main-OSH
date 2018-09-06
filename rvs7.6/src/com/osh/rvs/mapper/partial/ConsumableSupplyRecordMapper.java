package com.osh.rvs.mapper.partial;

import java.util.List;

import com.osh.rvs.bean.partial.ConsumableSupplyRecordEntity;

/**
 * @Description: 消耗品发放记录
 * @author liuxb
 * @date 2018-5-18 上午8:36:47
 */
public interface ConsumableSupplyRecordMapper {
	public List<ConsumableSupplyRecordEntity> search(ConsumableSupplyRecordEntity entity) throws Exception;
	
	public List<ConsumableSupplyRecordEntity> searchConsumableSubstitute(ConsumableSupplyRecordEntity entity) throws Exception;
	
	/**
	 * 消耗品Top10
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	public List<ConsumableSupplyRecordEntity> searchConsumableTopTen(ConsumableSupplyRecordEntity entity)throws Exception;
	
	/**
	 * 查询产出台数
	 * @param entity
	 * @return
	 */
	public Integer getOutLineQuantity(ConsumableSupplyRecordEntity entity);

}
