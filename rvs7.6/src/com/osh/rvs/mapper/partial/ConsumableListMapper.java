package com.osh.rvs.mapper.partial;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.partial.ConsumableListEntity;

public interface ConsumableListMapper {
	
	/* 查询数据 */
	public List<ConsumableListEntity> searchConsumableList(ConsumableListEntity ConsumableListEntity);

	/* 插入数据 */
	public int insertConsumable(ConsumableListEntity consumableEntity);

	/* 获得消耗率警报线 */
	public String getcost_rate_alram_belowline();
	
	/*查询所有的零件code和name*/
	/** search partial
	 * @param code */
	public List<ConsumableListEntity> getAllPartial(String code); 
	
	/*查询盘点数据*/
	public List<ConsumableListEntity> getAdjustSearch(String code); 
	
	/* 修改库存设置_查询 */
	public List<ConsumableListEntity> getConsumableDetail(@Param("partial_id") Integer partial_id);
	
	/* 修改库存设置_更新 */
	public void updateConsumableManage(ConsumableListEntity consumableEntity);
	
	/* 移出消耗品库存 */
	public void deleteConsumable(ConsumableListEntity consumableEntity);

	/* 取得消耗品计量单位信息 */
	public List<ConsumableListEntity> getMeasuringUnit(@Param("partial_id") Integer partial_id);

	/* 消耗品计量单位信息存在check */
	public Integer getMeasuringUnitCnt(@Param("partial_id") Integer partial_id);
	
	/* 消耗品计量单位信息更新 */
	public void updateMeasurementUnit(ConsumableListEntity consumableListEntity);
	
	/* 消耗品计量单位信息插入 */
	public void insertMeasurementUnit(ConsumableListEntity consumableListEntity);
	
	/* 消耗品修正数据_更新 */
	public void doAdjust(ConsumableListEntity consumableEntity);
	
	/* 消耗品修正数据_记录 */
	public void doAdjustInsert(ConsumableListEntity consumableEntity);

	/**
	 * 取得常用消耗品
	 */
	public List<ConsumableListEntity> getPopularItems();

	public List<ConsumableListEntity> getStatistic();
	
	/**更新消耗目标**/
	public void updateConsumptQuota(ConsumableListEntity consumableListEntity);

	public List<ConsumableListEntity> getAvailableInventories(
			@Param("set") Set<String> targetPartialIds);

	public String getHeatshrinkableLengthString(String partialId);

	public void clearHeatshrinkableLength(@Param("partial_id") String partial_id);
	public void setHeatshrinkableLength(@Param("partial_id") String partial_id, @Param("cut_length") String cut_length);
	
	public List<ConsumableListEntity> searchAllHeatshrinkable();
}
