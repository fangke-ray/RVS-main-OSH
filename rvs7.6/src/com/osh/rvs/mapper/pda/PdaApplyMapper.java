package com.osh.rvs.mapper.pda;

import java.util.List;

import com.osh.rvs.bean.pda.PdaApplyElementEntity;
import com.osh.rvs.bean.pda.PdaApplyEntity;

public interface PdaApplyMapper {

	/* 查询消耗品申请单一览 */
	public List<PdaApplyEntity> searchApplyList();

	/* 查询消耗品申请单明细 */
	public List<PdaApplyEntity> searchApplyDetailList(String consumable_application_key);
	
	public PdaApplyElementEntity getApplyElementDetail(PdaApplyElementEntity entity);

	public PdaApplyElementEntity getApplyPetitionerElementDetail(PdaApplyElementEntity entity);
}
