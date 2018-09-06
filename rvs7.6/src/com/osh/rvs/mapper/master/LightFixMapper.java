package com.osh.rvs.mapper.master;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.master.LightFixEntity;

public interface LightFixMapper {

	public List<LightFixEntity> searchLightFix(LightFixEntity entity);

	public LightFixEntity getLightFix(String light_fix_id);

	public int checkCodeIsExist(@Param("activity_code")String activity_code, @Param("light_fix_id")String light_fix_id);

	public void insertLightFix(LightFixEntity entity);

	public void updateLightFix(LightFixEntity entity);

	public void deleteLightFix(String light_fix_id);

	public List<String> getKinds(String light_fix_id);

	public void insertKind(LightFixEntity entity);

	public void deleteKind(String light_fix_id);

	public List<String> getPositions(String light_fix_id);

	public void insertPosition(LightFixEntity entity);

	public void deletePosition(String light_fix_id);
	
	public List<LightFixEntity> getLightFixByMaterialId(LightFixEntity entity);
	
	public List<LightFixEntity> checkCodeAndCategoryIsExist(@Param("activity_code")String activity_code);
}
