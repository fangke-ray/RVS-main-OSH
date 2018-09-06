package com.osh.rvs.mapper.inline;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.inline.PauseFeatureEntity;

public interface PauseFeatureMapper {

	public int makePauseFeature(PauseFeatureEntity entity) throws Exception;
	
	public void finishPauseFeature(PauseFeatureEntity entity) throws Exception;
	
	public PauseFeatureEntity getPauseFeatureByKey(PauseFeatureEntity entity);

	public List<PauseFeatureEntity> searchPauseFeature(PauseFeatureEntity entity);

	public void stopPauseFeature(@Param("material_id") String material_id, @Param("section_id") String section_id, @Param("position_id") String position_id, @Param("finisher_id") String finish_operator_id);
	public void stopBreakFeature(@Param("material_id") String material_id, @Param("section_id") String section_id, @Param("position_id") String position_id, @Param("finisher_id") String finish_operator_id);

	public String checkPauseFeature(@Param("material_id") String material_id, @Param("section_id") String section_id, @Param("position_id") String position_id);

	public void stopOperatorPauseFeature(String finish_operator_id);

	public String checkOperatorPauseFeature(String operator_id);

	public List<String> getAllPositionBreaked(PauseFeatureEntity entity) throws Exception;

	public void stopPauseFeatureSnout(@Param("snout_serial_no") String snout_serial_no,
			@Param("section_id") String section_id, @Param("position_id") String position_id, @Param("finisher_id") String finish_operator_id);

}
