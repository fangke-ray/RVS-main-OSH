package com.osh.rvs.mapper.infect;

import java.util.List;

import com.osh.rvs.bean.infect.ExternalAdjustmentEntity;

public interface ExternalAdjustmentMapper {
	/* 一览 */
	public List<ExternalAdjustmentEntity> search(ExternalAdjustmentEntity entity);

	/* 设备管理编号下拉框 */
	public List<ExternalAdjustmentEntity> getDeviceManageCodeReferChooser();
	
	/* 治具管理编号下拉框 */
	public List<ExternalAdjustmentEntity> getToolsManageCodeReferChooser();

	/* 管理编号change事件 */
	public ExternalAdjustmentEntity searchDeviceBaseInfo(ExternalAdjustmentEntity entity);
	
	public ExternalAdjustmentEntity searchToolsBaseInfo(ExternalAdjustmentEntity entity);

	/* 新建 */
	public void insert(ExternalAdjustmentEntity entity);
	
	/*送检*/
	public void checking(ExternalAdjustmentEntity entity);
	
	/*费用总计*/
	public List<ExternalAdjustmentEntity> getTotalCost();
	
	/*详细信息*/
	public ExternalAdjustmentEntity getDetailById(ExternalAdjustmentEntity entity);
	
	/*更新校验日期*/
	public void update(ExternalAdjustmentEntity entity);
	
	public void stopChecking(ExternalAdjustmentEntity entity);
}
