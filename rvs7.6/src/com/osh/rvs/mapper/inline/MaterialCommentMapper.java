package com.osh.rvs.mapper.inline;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * 维修对象备注信息取得
 * 
 * @author gonglm
 * 
 */
public interface MaterialCommentMapper {

	public String getMaterialComments(@Param("material_id") String material_id, @Param("operator_id") String operator_id);

	public String getMyMaterialComment(@Param("material_id") String material_id, @Param("operator_id") String operator_id);
	
	public void deleteMaterialComment(@Param("material_id") String material_id, @Param("operator_id") String operator_id);

	public void inputMaterialComment(Map<String, Object> materialComment);

	public void updateMaterialComment(Map<String, Object> materialComment);
}
