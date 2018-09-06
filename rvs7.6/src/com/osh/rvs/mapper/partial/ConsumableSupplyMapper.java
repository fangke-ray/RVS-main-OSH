package com.osh.rvs.mapper.partial;

import java.util.List;

import com.osh.rvs.bean.partial.ConsumableSupplyEntity;

public interface ConsumableSupplyMapper {

	/* 查询数据 */
	public List<ConsumableSupplyEntity> searchSupplyList(ConsumableSupplyEntity entity);

	/* 查询所有的零件code和name */
	public List<ConsumableSupplyEntity> getPartialByCode(String code);

	/* 零件补充记录_查询  */
	public String searchPartialSupply(ConsumableSupplyEntity entity);

	/* 零件补充记录_插入  */
	public void insertPartialSupply(ConsumableSupplyEntity entity);

	/* 零件补充记录_更新 */
	public void updatePartialSupply(ConsumableSupplyEntity entity);

	/* 消耗品修正数据_更新  */
	public void updateConsumableManage(ConsumableSupplyEntity entity);
}
