package com.osh.rvs.mapper.master;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.master.PcsRequestEntity;
import com.osh.rvs.bean.master.PositionEntity;

public interface PcsRequestMapper {

	/** 建立修改依赖 */
	public int createPcsRequest(PcsRequestEntity entity) throws Exception;

	/** 取得修改依赖 */
	public PcsRequestEntity getPcsRequest(String pcs_request_key);

	/** 发布修改 */
	public int resolvePcsRequest(PcsRequestEntity entity) throws Exception; 

	/** 查询 */
	public List<PcsRequestEntity> searchPcsRequests(PcsRequestEntity condition);

	/** 取得修改依赖 */
	public PcsRequestEntity disbandPcsRequest(String pcs_request_key);

	/** 取得测试用工位 */
	public List<PositionEntity> getTestOflines(String line_id);

	/** 移除修改依赖 */
	public int removePcsRequest(String pcs_request_key);

	/** 导入修改依赖 */
	public int importPcsRequest(PcsRequestEntity condition);

	public List<MaterialEntity> getWorkingByModel(String model_id);

	public int setOldType(@Param("pcs_request_key")String pcs_request_key, @Param("material_id")String material_id);

	public int setReactedModels(@Param("pcs_request_key")String pcs_request_key, @Param("model_id")String model_id);
	public List<String> getReactedModelsByKey(String pcs_request_key);

	public void updatePcsRequest(PcsRequestEntity update);

	/** 查询维修对象是否被指定为使用旧版本 */
	public List<PcsRequestEntity> checkMaterialAssignAsOld(String material_id);

	public List<PcsRequestEntity> getFixHistoryOfMaterial(String material_id);
}
