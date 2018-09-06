package com.osh.rvs.mapper.manage;

import java.util.Date;
import java.util.List;

import com.osh.rvs.bean.manage.EchelonAllocateEntity;


public interface EchelonAllocateMapper {
	
	//梯队设定历史
	public List<EchelonAllocateEntity> searchEchelonAllocate();
	
	//梯队设定历史记录
	public List<EchelonAllocateEntity> searchEchelonHistorySet(EchelonAllocateEntity echelonAllocateEntity);
	
	//梯队划分所有信息
	public List<EchelonAllocateEntity> searchModelLevelSet(EchelonAllocateEntity echelonAllocateEntity);
	
	//更新梯队
	public void updateEchelonHistorySet(EchelonAllocateEntity echelonAllocateEntity);
	
	//最后时间
	public Date searchEndDate();
	
	//查询离本次最后时间最近的一次end_date时间
	public Date searchLastEndDate(String endDate);
}
