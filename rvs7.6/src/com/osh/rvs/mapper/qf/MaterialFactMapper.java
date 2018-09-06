package com.osh.rvs.mapper.qf;

import java.util.List;

import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.inline.MaterialFactEntity;
public interface MaterialFactMapper {

	public List<MaterialFactEntity> searchMaterial(MaterialFactEntity entity);
	
	public List<MaterialFactEntity> searchInlineMaterial();
	public String getTwoDaysOfLines(String material_id);
	public void updateAgreedDate(MaterialFactEntity entity);
	public int updateAgreedDateBySorc(MaterialFactEntity entity);
	public int updateUnrepairBySorc(MaterialFactEntity entity);
	public void updateInline(MaterialFactEntity entity);

	public void assginCCDChange(String material_id);

	public List<MaterialEntity> getInlinePlan();

	public void changeInlinePlan(MaterialFactEntity conditionBean);

	public List<MaterialFactEntity> getInlinePlanInfo(String[] material_ids);
}
