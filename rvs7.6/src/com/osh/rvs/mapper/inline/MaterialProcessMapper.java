package com.osh.rvs.mapper.inline;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.inline.MaterialProcessEntity;

public interface MaterialProcessMapper {

	public MaterialProcessEntity loadMaterialProcess(String id);
	
	public void updateMaterialProcess(MaterialProcessEntity entity) throws Exception;
	public void finishMaterialProcess(MaterialProcessEntity entity) throws Exception;
	
	public void insertMaterialProcess(MaterialProcessEntity entity) throws Exception;

	public void undoLineComplete(MaterialProcessEntity entity) throws Exception;

	public void removeByBreak(String material_id);

	public MaterialProcessEntity loadMaterialProcessOfLine(@Param("material_id")String material_id, @Param("line_id")String line_id);

	public void updateReworkPositionId(MaterialProcessEntity entity) throws Exception;

	/** 先设定AssignDate的建立mp法，不是mpa表 */
	public void insertMaterialProcessAssign(MaterialProcessEntity entity) throws Exception;


	/**
	 * 切换平行线位
	 * @param material_id
	 */
	public int updatePx(@Param("material_id") String material_id,@Param("line_id") String line_id);

	public List<MaterialProcessEntity> loadMaterialProcessLines(String id);
}
