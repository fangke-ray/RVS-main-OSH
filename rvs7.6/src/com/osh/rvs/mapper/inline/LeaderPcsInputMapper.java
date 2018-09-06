package com.osh.rvs.mapper.inline;

import java.util.List;

import com.osh.rvs.bean.data.ProductionFeatureEntity;

public interface LeaderPcsInputMapper {

	/** 取得 */
	public List<ProductionFeatureEntity> searchLeaderPcsInput(ProductionFeatureEntity pfBean);
	public ProductionFeatureEntity getLeaderPcsInputByKey(String key);

	public void insert(ProductionFeatureEntity pfBean) throws Exception;

	/** CCD线长确认  */
	public String checkCcdConfirm(String material_id);
	/** 先端组件线长确认  */
	public String checkSnoutConfirm(String serial_no);
}
