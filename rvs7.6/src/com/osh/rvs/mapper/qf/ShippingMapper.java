package com.osh.rvs.mapper.qf;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.data.MaterialEntity;

public interface ShippingMapper {

	public List<MaterialEntity> getWaitings(String postion_id);

	public List<MaterialEntity> getFinished(String postion_id);

	public MaterialEntity getMaterialDetail(String material_id);

	public int putIntoTrolley(@Param("trolley_code") String trolley_code, @Param("material_id")  String material_id);
	
	public List<MaterialEntity> getInTrolleyMaterials();
	
	public int clearTrolley(String trolley_code);
	
	public int updateMaterialTimeNodeShipment(String trolley_code);

}
