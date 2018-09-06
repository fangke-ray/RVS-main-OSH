package com.osh.rvs.mapper.inline;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.inline.MaterialProcessAssignEntity;
import com.osh.rvs.bean.master.LineEntity;
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.bean.master.ProcessAssignEntity;

public interface MaterialProcessAssignMapper {
	//查询维修对象选用小修理
	public List<MaterialProcessAssignEntity> searchMaterialLightFix(MaterialProcessAssignEntity entity);
	
	//删除维修对象选用小修理
	public void deleteMaterialLightFix(String material_id)throws Exception;
	
	//新建维修对象选用小修理
	public void insertMaterialLightFix(MaterialProcessAssignEntity entity)throws Exception;
	
	//查询维修对象独有修理流程
	public List<MaterialProcessAssignEntity> searchMaterialProcessAssign(MaterialProcessAssignEntity entity);
	
	//删除维修对象独有修理流程
	public void deleteMaterialProcessAssign(String material_id)throws Exception;
	
	//新建维修对象独有修理流程
	public void insertMaterialProcessAssign(MaterialProcessAssignEntity entity)throws Exception;

	// 维修对象已选择流程项目 
	public String getLightFixesByMaterial(String material_id);

	public List<ProcessAssignEntity> getProcessAssignByMaterialID(
			String material_id);

	public ProcessAssignEntity getFirstPosition(String material_id);

	public boolean checkWorked(@Param("material_id") String material_id, @Param("position_id") String position_id);

	public List<PositionEntity> getPrevPositions(@Param("material_id") String material_id,
			@Param("position_id") String position_id);

	public List<PositionEntity> getNextPositions(@Param("material_id") String material_id,
			@Param("position_id") String position_id);

	public ProcessAssignEntity getProcessAssign(@Param("material_id") String material_id,
			@Param("position_id") String position_id);

	public List<String> getPartStart(@Param("material_id") String material_id, @Param("line_id") String line_id);

	public List<String> getPartAll(@Param("material_id") String material_id, @Param("line_id") String line_id);

	public List<LineEntity> getWorkedLines(String materialId);
}
