package com.osh.rvs.mapper.infect;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.infect.DryingJobEntity;

public interface DryingJobMapper {
	// 一览
	public List<DryingJobEntity> search(DryingJobEntity entity);

	// 新建烘干作业
	public void insertDryingJob(DryingJobEntity entity) throws Exception;

	// 新建烘干作业机种
	public void insertDryingJobOfCategory(DryingJobEntity entity) throws Exception;

	// 获取烘干作业详细信息
	public DryingJobEntity getDryingJobDetail(DryingJobEntity entity);

	//获取机种
	public List<DryingJobEntity> getDryingJobOfCategory(DryingJobEntity entity);
	
	//更新烘干作业
	public void updateDryingJob(DryingJobEntity entity) throws Exception;
	
	// 删除烘干作业机种
	public void deleteDryingJobCategoryById(String drying_job_id)throws Exception;
	
	// 删除烘干作业
	public void deleteDryingJobById(String drying_job_id) throws Exception;

	// 根据维修对象所在工位取得干燥作业
	public List<DryingJobEntity> getDryingJobWithMaterialInPosition(@Param("material_id") String material_id
			, @Param("section_id") String section_id , @Param("position_id") String position_id);

	// 根据维修对象所在工位取得干燥作业
	public List<DryingJobEntity> getDryingJobWithModelInPosition(@Param("model_id") String model_id
			, @Param("section_id") String section_id , @Param("position_id") String position_id);

}
