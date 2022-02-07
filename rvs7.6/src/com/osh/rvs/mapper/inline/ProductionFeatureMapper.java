package com.osh.rvs.mapper.inline;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.ProductionFeatureEntity;

public interface ProductionFeatureMapper {

	/**
	 * 添加作业信息
	 * @param entity
	 * @return
	 */
	public int insertProductionFeature(ProductionFeatureEntity entity);

	/**
	 * 添加受理作业信息
	 * @param entity
	 * @return
	 */
	public int insertAcceptanceProductionFeature(ProductionFeatureEntity entity);

	/**
	 * 受理一览
	 * @param startTime
	 * @return
	 */
	public List<MaterialEntity> getMaterialDetailForRecept(Timestamp startTime);
	
	/**
	 * 检查是否存在operater_result=1的作业报告
	 * @param materialId
	 * @return
	 */
	public int checkOperateResult(String materialId);

	public void startProductionFeature(ProductionFeatureEntity entity) throws Exception;
	public void supportProductionFeature(ProductionFeatureEntity entity) throws Exception;
	public void startBatchProductionFeature(ProductionFeatureEntity entity) throws Exception;
	public void finishProductionFeature(ProductionFeatureEntity entity) throws Exception;
	public void finishPatchProductionFeature(ProductionFeatureEntity entity) throws Exception;
	public void updatePcsProductionFeature(ProductionFeatureEntity entity) throws Exception;
	public void pauseWaitProductionFeature(ProductionFeatureEntity entity) throws Exception;
	public void finishProductionFeatureSetFinish(ProductionFeatureEntity workingPf) throws Exception;

	/**
	 * 更改处理结果
	 * 中断->暂停
	 * （因为每个维修对象每工位 未处理中断的记录只会有一条，因此与pace和rework无关）
	 * @param entity
	 * @throws Exception
	 */
	public int breakOverOperateResult(@Param("material_id") String material_id, @Param("position_id") String position_id) throws Exception;
	/**
	 * 中断返工
	 * 删除当前工位中断等待记录
	 * @throws Exception
	 */
	public int removeBreakWaiting(ProductionFeatureEntity pfBean) throws Exception;
	/**
	 * 更改处理结果
	 * 完成->不通过
	 * （因为每个维修对象每工位 完成的记录只会有一条，因此与pace和rework无关）
	 * @throws Exception
	 */
	public int reworkOperateResult(@Param("material_id") String material_id, @Param("position_id") String position_id) throws Exception;

	public List<ProductionFeatureEntity> getProductionFeatureByMaterialId(ProductionFeatureEntity entity);
	public List<ProductionFeatureEntity> getNoBeforeRework(String id);
	public List<ProductionFeatureEntity> getFinishProductionFeature(String id);

	public List<ProductionFeatureEntity> searchProductionFeature(ProductionFeatureEntity entity);

	/**
	 * 维修对象单位当前最大的Rework值
	 * @param material_id
	 * @return
	 */
	public int getReworkCount(String materialId);
	/**
	 * 维修对象单位在指定工位内最大的Rework值
	 * @param material_id
	 * @return
	 */
	public int getReworkCountWithPositions(Map<String, Object> params);
	public int getReworkCountWithLine(@Param("material_id") String material_id, @Param("line_id") String line_id);

	public List<ProductionFeatureEntity> getProductionPcsOnRework(ProductionFeatureEntity condEntity);

	public List<String> checkSupporting(@Param("material_id") String material_id, @Param("position_id") String position_id);

	public boolean checkPositionDid(@Param("material_id") String material_id, @Param("position_id") String position_id
			, @Param("operate_result")  String operate_result, @Param("rework") String rework);
	public List<String> checkSpecPositionDid(@Param("material_id") String material_id, @Param("special_page") String special_page
			, @Param("operate_result")  String operate_result, @Param("rework") String rework, @Param("finish_time_status") String finish_time_status);
	/**
	 * 修改现有的作业课室
	 * @param material_id
	 */
	public void changeSection(@Param("material_id") String material_id, @Param("section_id") String section_id) throws Exception;

	public void removeWaiting(@Param("material_id") String material_id, @Param("position_id") String position_id) throws Exception;

	public void removeFirstWaiting(@Param("material_id") String material_id, @Param("position_id") String position_id) throws Exception;

	public List<Map<String, String>> getLastPositionAndStatus(String material_id);

	public List<ProductionFeatureEntity> getWorkedPositionOfMaterial(String materialId);

	public boolean checkLineDid(@Param("material_id") String material_id, @Param("line_id") String line_id);

	/**
	 * 取得现存工位上的维修对象
	 * (目前消耗品申请用)
	 * @param section_id
	 * @param position_id
	 * @return
	 */
	public List<MaterialEntity> getSikakeMaterialOfPosition(@Param("section_id") String section_id, @Param("position_id") String position_id);

	/*	@SuppressWarnings("unused") */
	public int getPositionHeap(@Param("section_id") String section_id, @Param("position_id") String position_id,
			@Param("px") String px);
	
	public List<ProductionFeatureEntity> getFinishedProductionFeatureByMaterialId(@Param("material_id") String material_id, @Param("line_id") String line_id);

	public int checkFinishedDisinfection(@Param("material_id") String material_id);

	public ProductionFeatureEntity getPeriodInPositionOfMaterialId(ProductionFeatureEntity entity);
}
