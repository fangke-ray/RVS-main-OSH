package com.osh.rvs.mapper.inline;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.inline.WaitingEntity;

public interface PositionPanelMapper {

	/**
	 * 取得今天开始作业的时间
	 * @where section_id 等于当前课室
	 * @where position_id 等于当前工位
	 * @where action_time 大于 current_date
	 * @return 今天开始作业到当时的秒数
	 */
	public int checkPositionStartedWorkTime(@Param("section_id") String section_id,
			@Param("position_id") String position_id, @Param("level") String level);

	/**
	 * 取得今天开始的直接作业的总时间
	 * @where section_id 等于当前课室
	 * @where position_id 等于当前工位
	 * @where finish_time NOT Null
	 * @where action_time 大于 current_date
	 * @return Sum（作业开始到终了之间秒数）
	 */
	public int checkTodayWorkCost(@Param("section_id") String section_id, @Param("position_id") String position_id,
			@Param("level") String level);

	/**
	 * 取得完成的件数
	 * @where section_id 等于当前课室
	 * @where position_id 等于当前工位
	 * @where operate_result 等于 2=完成
	 * @where action_time 大于 current_date
	 * @return Count（operate_result）
	 */
	public int getFinishCount(@Param("section_id") String section_id, @Param("position_id") String position_id,
			@Param("level") String level);

	/**
	 * 主要角色不包含107权限的用户完成的操作 TODO 主要技能
	 */
	public int getLeaderSupportFinishCount(@Param("section_id") String section_id,
			@Param("position_id") String position_id, @Param("level") String level);

	/**
	 * 取得完成的件数
	 * @where section_id 等于当前课室
	 * @where position_id 等于当前工位
	 * @where operate_result 等于 2=完成
	 * @where action_time 大于 current_date
	 * @return Count（operate_result）
	 */
	public List<Map<String, Number>> getTodayBreak(@Param("section_id") String section_id,
			@Param("position_id") String position_id, @Param("level") String level);

	public int getWaitingCount(@Param("section_id") String section_id, @Param("position_id") String position_id);

	public List<WaitingEntity> getWaitingMaterial(@Param("line_id") String line_id,
			@Param("section_id") String section_id, @Param("position_id") String position_id, @Param("group_position_id") String group_position_id,
			@Param("operator_id") String operator_id, @Param("level") String level, @Param("division") String division);

	public List<ProductionFeatureEntity> getWaiting(@Param("material_id") String material_id,
			@Param("section_id") String section_id, @Param("position_id") String position_id,
			@Param("level") String level);

	public List<WaitingEntity> getGroupCompleteMaterial(@Param("section_id") String section_id,
			@Param("group_position_id") String group_position_id, @Param("level") String level);

	public MaterialEntity getMaterialDetail(String material_id);

	public List<ProductionFeatureEntity> getPositionWorksByMaterial(@Param("material_id") String material_id, @Param("position_id") String position_id);

	public Integer getTotalTimeByRework(ProductionFeatureEntity waitingPf);

	public ProductionFeatureEntity getWorking(String operator_id); // TODO operate_result = 5 Why
	public ProductionFeatureEntity getSupporting(String operator_id); // TODO 别放这里
	public ProductionFeatureEntity getPausing(String operator_id);
	public ProductionFeatureEntity getProcessing(String operator_id);

	public List<ProductionFeatureEntity> getWorkingBatch(@Param("position_id") String position_id, @Param("operator_id") String operator_id);

}
