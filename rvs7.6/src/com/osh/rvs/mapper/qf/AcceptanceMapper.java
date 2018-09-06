package com.osh.rvs.mapper.qf;

import java.util.List;
import java.util.Map;

import com.osh.rvs.bean.data.MaterialEntity;

public interface AcceptanceMapper {

	public int insertMaterial(MaterialEntity entity);
	
	public void updateMaterial(MaterialEntity entity);
	
	public String checkSorcNo(MaterialEntity entity);
	
	public String checkEsasNo(MaterialEntity entity);
	
	public String checkModelSerialNo(MaterialEntity entity);

	public List<MaterialEntity> getTodayMaterialDetail();

	public void updateFormalReception(String[] material_ids) throws Exception;

	public int importOgz(Map<String, String> map);

	public int updatePastOgzShipped(Map<String, String> map) throws Exception;

	public int updateOcmShippingBySorc(MaterialEntity cond) throws Exception;

	public int updateOcmShippingByID(MaterialEntity cond) throws Exception;

	public Map<String, String> loadOgz();
}
