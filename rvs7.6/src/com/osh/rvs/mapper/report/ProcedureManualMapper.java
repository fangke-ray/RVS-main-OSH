package com.osh.rvs.mapper.report;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.report.ProcedureManualEntity;

public interface ProcedureManualMapper {

	public List<ProcedureManualEntity> searchWithPersonalList(ProcedureManualEntity cond);

	public int insertProcedureManual(ProcedureManualEntity entity);
	
	public int updateProcedureManual(ProcedureManualEntity entity);

	public int updateProcedureManualName(ProcedureManualEntity entity);

	public int deleteProcedureManual(String procedure_manual_id);

	public int insertBooklist(ProcedureManualEntity entity);
	
	public int deleteBooklist(@Param("procedure_manual_id") String procedure_manual_id, @Param("operator_id") String operator_id);
}
