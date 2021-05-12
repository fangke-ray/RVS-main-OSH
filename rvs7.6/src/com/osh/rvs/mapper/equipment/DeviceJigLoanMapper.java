package com.osh.rvs.mapper.equipment;

import java.util.List;

import com.osh.rvs.bean.equipment.DeviceJigLoanEntity;

public interface DeviceJigLoanMapper {
	public List<DeviceJigLoanEntity> getAllLoaned(DeviceJigLoanEntity condition);

	public List<DeviceJigLoanEntity> countLoanedOfOperator(String operator_id);

	public List<String> getLoaningUnregisting(DeviceJigLoanEntity condition);

	public int insertLoan(DeviceJigLoanEntity entity);

	public int finishLoan(String[] keys);

	public int insertApplyTrace(DeviceJigLoanEntity entity);

	public List<DeviceJigLoanEntity> getLoanApplyTraceByMaterial(DeviceJigLoanEntity condition);
}
