package com.osh.rvs.mapper.partial;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.partial.MaterialPartialDetailEntity;
import com.osh.rvs.bean.partial.MaterialPartialEntity;

public interface MaterialPartialMapper {
	public MaterialPartialEntity loadMaterialPartial(MaterialPartialEntity entity);
	public MaterialPartialEntity loadMaterialPartialGroup(MaterialPartialEntity entity);
	
	public void updateMaterialPartial(MaterialPartialEntity entity) throws Exception;
	
	public void insertMaterialPartial(MaterialPartialEntity entity) throws Exception;

	public List<MaterialPartialEntity> searchMaterial(MaterialPartialEntity entity);
	public List<MaterialPartialEntity> searchMaterialReport(MaterialPartialEntity entity);
	public List<MaterialPartialEntity> searchMaterialBoReport(MaterialPartialEntity entity);

	public List<MaterialPartialEntity> searchMaterialByKey(String id);
	
	public MaterialPartialEntity getMaterialByKey(@Param("material_id") String id, @Param("occur_times") String occur_times);

	public MaterialPartialEntity getMaterialByMaterialId(@Param("material_id") String id);

	public List<String> getOccurTimesById(String id);

	public void updateReachDateBySorc(Map<String, Object> paramMap) throws Exception;
	
	public Integer getTotalBo();
	public Double getTodayBoRate(@Param("from") Date from, @Param("to") Date to);
	public Double get3daysBoRate(@Param("from") Date from, @Param("to") Date to);
	
	public List<MaterialPartialDetailEntity> searchMaterialPartialDetail(MaterialPartialDetailEntity entity);
	/**
	 * 更新零件BO
	 * @param entity
	 */
	public void updateBoFlg(MaterialPartialEntity entity);
	
	/**
	 * 更新零件BO和零件订购日期
	 * @param entity
	 */
	public void updateOrderDate(MaterialPartialEntity entity);

	/**
	 * 更新零件BO和零件订购日期
	 * @param entity
	 */
	public void updateBoFlgAndOrderDate(MaterialPartialEntity entity);
	
	/**
	 * 零件签收对象一览
	 * @param entity
	 * @return
	 */
	public List<MaterialPartialEntity> searchMaterialPartialRecept(MaterialPartialEntity entity);

	public List<MaterialPartialDetailEntity> searchWaitingMaterialPartialDetail(@Param("material_id") String material_id, @Param("line_id") String line_id);
	/**
	 * 消耗品用
	 * @param entity
	 * @return
	 */
	public List<MaterialPartialDetailEntity> getMpdForConsumable(MaterialPartialEntity entity);
	public List<MaterialPartialDetailEntity> getMpdForSnout(MaterialPartialEntity conditionBean);

	public int updateMaterialInstead(MaterialPartialDetailEntity entity) throws Exception;

	public int sparedMaterialInstead(MaterialPartialDetailEntity entity) throws Exception;

	public int insertMaterialInstead(MaterialPartialDetailEntity entity) throws Exception;

	public int resetBoStatusByInstead(MaterialPartialEntity mpEntity) throws Exception;

	public int updateArrivePlanDateNoBoOnPartial();

	public int updateArrivePlanDateBoOnPartial(@Param("material_id") String material_id); // 只能更新最后的occur_times

	public int updateArrivedPlanDateBoResolvedOnPartial();

	public int updatePartialArrivalPlanDateByKey(MaterialPartialDetailEntity entity);

	/*零件签收归档*/
	public List<MaterialPartialDetailEntity> archiveOfPartialRecept(String material_id);
	
	/*零件订购item*/
	public List<MaterialPartialEntity> searchMaterialItemReport(MaterialPartialEntity entity);
	public MaterialPartialEntity getMaterialWithPositionedByMaterialId(
			MaterialPartialDetailEntity materialPartialDetailEntity);
	public MaterialPartialDetailEntity getMaterialPartialDetailByKey(String material_partial_detail_key);
	
	/*零件追加明细*/
	public List<MaterialPartialEntity> searchPartialAddtionalInf(MaterialPartialEntity entity);
	
	//**//查询零件定位表
	public List<MaterialPartialDetailEntity> searchPartialPosition(MaterialPartialDetailEntity MaterialPartialDetailEntity);

	public int updateBoFlgWithDetail(MaterialPartialEntity entity) throws Exception;
	public int updateBoFlgWithDetailMantains(MaterialPartialEntity entity) throws Exception;
	public String getBoPartialOfPosition(@Param("material_id") String material_id, @Param("position_id") String position_id);
	public String getBoPartialOfLineOfPosition(@Param("material_id") String material_id, @Param("position_id") String position_id);
}
