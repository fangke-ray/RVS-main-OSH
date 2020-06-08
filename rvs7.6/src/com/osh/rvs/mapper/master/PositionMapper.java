package com.osh.rvs.mapper.master;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.bean.master.PositionGroupEntity;

public interface PositionMapper {

	/** insert single */
	public int insertPosition(PositionEntity line) throws Exception;

	public int updatePosition(PositionEntity line) throws Exception;

	/** search all */
	public List<PositionEntity> getAllPosition();

	public PositionEntity getPositionByID(String position_id);

	/** search */
	public List<PositionEntity> searchPosition(PositionEntity position);

	public void deletePosition(PositionEntity updateBean) throws Exception;

	/** search by inline_flg */
	public List<PositionEntity> getPositionByInlineFlg();

	public List<String> getDividePositions();

	public int insertPositionGroup(PositionGroupEntity entity) throws Exception;

	public int removePositionGroup(String group_position_id) throws Exception;

	public List<String> getGroupPositions();

	public List<PositionGroupEntity> getGroupPositionById(String group_position_id);

	public List<PositionGroupEntity> getAllGroupPositions();

	public List<PositionEntity> getAllMappingPositions();

	public void clearMappingPosition(String mapping_position_id);
	public void setMappingPosition(@Param("position_id") String position_id, @Param("mapping_position_id") String id);

	public void setPositionRevision(String process_code);
}
