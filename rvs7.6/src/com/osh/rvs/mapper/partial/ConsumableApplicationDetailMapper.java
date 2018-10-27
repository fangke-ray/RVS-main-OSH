package com.osh.rvs.mapper.partial;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.partial.ConsumableApplicationDetailEntity;

public interface ConsumableApplicationDetailMapper {
	public List<ConsumableApplicationDetailEntity> serach(String consumable_application_key);
	
	//更新发放数量
	public void updateSupplyQuantity(ConsumableApplicationDetailEntity entity);
	
	//更新当前有效库存
	public void updateAvailableInventory(ConsumableApplicationDetailEntity entity);

	/** 
	 * 取得编辑用详细信息
	 * @param operator_id 
	 */
	public List<ConsumableApplicationDetailEntity> getDetailForEditById(@Param("consumable_application_key") String consumable_application_key, 
			@Param("operator_id") String operator_id);

	public List<ConsumableApplicationDetailEntity> getDetailForEditByPartial(ConsumableApplicationDetailEntity entity);

	public void insertDetail(ConsumableApplicationDetailEntity detail) throws Exception;

	public void editApplyQuantity(ConsumableApplicationDetailEntity detail) throws Exception;

	public void deleteDetail(ConsumableApplicationDetailEntity detail) throws Exception;

	public ConsumableApplicationDetailEntity checkConsumableApplicationParticular(ConsumableApplicationDetailEntity cadEntity);

	public void insertParticular(ConsumableApplicationDetailEntity cadEntity);

	public void updateParticular(ConsumableApplicationDetailEntity cadEntity);

	public void deleteParticular(ConsumableApplicationDetailEntity cadEntity);
}
