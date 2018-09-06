package com.osh.rvs.mapper.partial;

import java.util.List;

import com.osh.rvs.bean.partial.ConsumableInventoryEntity;

public interface ConsumableInventoryMapper {

	/* 查询数据 */
	public List<ConsumableInventoryEntity> searchInventoryList(ConsumableInventoryEntity entity);

	/* 查询明细数据 */
	public void updateInventoryDetail(ConsumableInventoryEntity entity);

}
