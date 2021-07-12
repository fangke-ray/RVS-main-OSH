package com.osh.rvs.mapper.master;

import java.util.List;

import com.osh.rvs.bean.master.PartialPositionEntity;

public interface PartialPositionMapper {

	/* 查询数据 */
	public List<PartialPositionEntity> searchPartialPosition(PartialPositionEntity partialPositionEntity);

	/* 插入数据 */
	public void insertPartialPosition(PartialPositionEntity partialpositionEntity);

	/* 删除表数据 */
	public void deletePartialPosition(String model_id);

	public List<PartialPositionEntity> getPartialPositionOfModel(String model_id);
	public List<PartialPositionEntity> getPartialPositionRevisionOfModel(String model_id);

	/* 删除；数据 */
	public void clearPartialPositionOfModel(PartialPositionEntity partialpositionEntity);

	/* 更新零件定位表 */
	public void updatePartialPosition(PartialPositionEntity partialPositionEntity);

	/* 更新零件定位表 */
	public void updatePartialPositionByBom(PartialPositionEntity partialPositionEntity);

	/* 更新零件定位数量 */
	public void updatePartialPositionQuantity(PartialPositionEntity partialPositionEntity);

	public void updatePartialPositionOldHistoryLimitDate(PartialPositionEntity partialPositionEntity);

	/* 插入改废订历史管理表 */
	public void insertPartialWasteModifyHistory(PartialPositionEntity partialPositionEntity);

	public List<String> checkPartialPosition(PartialPositionEntity partialPositionEntity);
	
	/*插入数据到废改订历史表之前进行验证是否有本条数据*/
	public List<String> searchPartialPositionBelong(PartialPositionEntity partialPositionEntity);

	public List<PartialPositionEntity> getInstructOfModel(String model_id);

	public List<PartialPositionEntity> getInstructOfCategoryKind(String kind);

	public List<String> getComponentOfModel(String model_id);
}
