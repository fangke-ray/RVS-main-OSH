package com.osh.rvs.mapper.inline;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.data.SnoutEntity;
import com.osh.rvs.bean.inline.SoloProductionFeatureEntity;
import com.osh.rvs.bean.inline.WaitingEntity;
import com.osh.rvs.bean.master.OperatorNamedEntity;
import com.osh.rvs.bean.qa.ServiceRepairManageEntity;

public interface SoloProductionFeatureMapper {

	/** 取得 */
	public List<SoloProductionFeatureEntity> searchSoloProductionFeature(SoloProductionFeatureEntity pfBean);

	public List<SoloProductionFeatureEntity> getSnoutsByModel(String model_id);
	public List<ProductionFeatureEntity> findUsedSnoutsByMaterial(String material_id);
	public String findUsedSnoutsBySnouts(String serial_no);
	/** 新建 */
	public void insert(SoloProductionFeatureEntity pfBean) throws Exception;
	
	public void breakWork(SoloProductionFeatureEntity entity) throws Exception;
	public void finish(SoloProductionFeatureEntity entity) throws Exception;
	
	public void finishOnOperator(SoloProductionFeatureEntity entity) throws Exception;
	// public void normalBreak(SoloProductionFeatureEntity entity) throws Exception;

	public void use(String serial_no) throws Exception;
	public void unuse(String serial_no) throws Exception;

	public void useto(ProductionFeatureEntity pfBean) throws Exception;
	public void unuseto(@Param("material_id") String material_id, @Param("rework") String rework) throws Exception;

	public void leaderuseto(ProductionFeatureEntity pfBean) throws Exception;

	public Integer getTotalTime(SoloProductionFeatureEntity pfBean);
	public Date getFirstStartTime(SoloProductionFeatureEntity pfBean);

	public SoloProductionFeatureEntity getPausing(String operator_id);

	public List<OperatorNamedEntity> getSnoutsMaker();

	public List<SnoutEntity> searchSnouts(SnoutEntity condition);

	public void deleteSnouts(@Param("model_id") String model_id, @Param("serial_no") String serial_no) throws Exception;

	public Integer getMaxPace(SoloProductionFeatureEntity pfBean);

	public void updateToResume(SoloProductionFeatureEntity pfBean) throws Exception;

	
	public List<String> checkWorkingByModelName(ServiceRepairManageEntity pfBean);
	public void undoWorkingByModelName(ServiceRepairManageEntity pfBean) throws Exception;

	public void pauseWaitProductionFeature(
			SoloProductionFeatureEntity workwaitingPf);

	public List<WaitingEntity> getWaitingMaterial(@Param("section_id") String section_id,
			@Param("position_id") String position_id, @Param("operator_id") String operator_id);

	public MaterialEntity checkSnoutOrigin(@Param("material_id") String material_id, @Param("manage_serial_no") String serial_no);

	public List<MaterialEntity> getSnoutOriginOnMonth(String month);

	public void registSnoutOrigin(@Param("material_id") String material_id, @Param("manage_serial_no") String serial_no);
	public void removeSnoutOrigin(@Param("manage_serial_no") String serial_no);

	public List<SnoutEntity> searchSnoutsOnMonth(@Param("start_date") Date start_date, @Param("end_date") Date end_date);

	/** 新建 */
	public void forbid(ProductionFeatureEntity pfBean) throws Exception;
}
