package com.osh.rvs.mapper.partial;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.master.PartialPositionEntity;
import com.osh.rvs.bean.partial.MaterialPartInstructEntity;
import com.osh.rvs.bean.partial.MaterialPartPrelistEntity;

public interface MaterialPartInstructMapper {
	public List<MaterialPartInstructEntity> searchMaterialPartInstructInline(MaterialPartInstructEntity entity);
	public List<MaterialPartInstructEntity> searchMaterialPartInstructOutline(MaterialPartInstructEntity entity);

	public List<PartialPositionEntity> getFocusPartialsByOperator(@Param("operator_id") String operator_id);

	public PartialPositionEntity getPartialById(@Param("partial_id") String partial_id);

	public void removeFocusPartialListByOperator(@Param("operator_id") String operator_id);

	public MaterialPartPrelistEntity checkPartPrelist(MaterialPartPrelistEntity entity);

	public void insertFocusPartialListByOperator(@Param("operator_id") String operator_id,
			@Param("partial_id") String partial_id);

	public List<MaterialPartPrelistEntity> getInstuctListForMaterial(
			@Param("material_id") String material_id);

	public int insertPartPrelist(MaterialPartPrelistEntity entity);
	public int updatePartPrelist(MaterialPartPrelistEntity entity);
	public int deletePartPrelist(MaterialPartPrelistEntity entity);

	public MaterialPartInstructEntity getPartProcedure(String material_id);

	public List<MaterialPartPrelistEntity> getPartNeedNotice(String material_id);

	public List<MaterialPartPrelistEntity> getPartCommentsFromLossDetail(String material_id);

	public List<MaterialPartPrelistEntity> getAdditionalOrder(String material_id);

	public MaterialPartInstructEntity getMaterialPartProcedure(String material_id);
	public int insertMaterialPartProcedure(String material_id);
	public int updateMaterialPartProcedure(MaterialPartInstructEntity entity);
}
