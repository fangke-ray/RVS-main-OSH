package com.osh.rvs.mapper.inline;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.data.ProductionFeatureEntity;

public interface SupportMapper {

	/** 取得/确认可辅助=进行中的工位 */
	public List<ProductionFeatureEntity> getWorkingList(@Param("section_id") String section_id, @Param("line_id") String line_id, @Param("material_id") String material_id);

	public ProductionFeatureEntity searchWorkingProductionFeature(ProductionFeatureEntity pfBean);
	public int getSupportPace(ProductionFeatureEntity pfBean);

	public ProductionFeatureEntity searchSupportingProductionFeature(ProductionFeatureEntity pfBean);

	public ProductionFeatureEntity searchComposeStorageWork(ProductionFeatureEntity pfBean);
	/**  */
	
}
