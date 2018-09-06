package com.osh.rvs.mapper.inline;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.inline.DryingProcessEntity;

public interface DryingProcessMapper {
	public List<DryingProcessEntity> search(DryingProcessEntity entity);

	public List<DryingProcessEntity> getDryingJobWithUsedSlots(DryingProcessEntity entity);

	public int createProcess(DryingProcessEntity entity) throws Exception;

	public List<DryingProcessEntity> getToFinishProcess(DryingProcessEntity entity);
	public int finishProcess(DryingProcessEntity entity) throws Exception;

	public DryingProcessEntity getProcessByMaterialInPosition(@Param("material_id") String material_id, @Param("position_id") String position_id);
	
}
