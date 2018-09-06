package com.osh.rvs.mapper.infect;

import java.util.List;

import com.osh.rvs.bean.infect.CheckResultEntity;
import com.osh.rvs.bean.infect.ElectricIronDeviceEntity;
import com.osh.rvs.bean.master.DevicesManageEntity;

public interface ElectricIronDeviceMapper {

	/**初始查询**/
	public List<ElectricIronDeviceEntity> searchElectricIronDevice(ElectricIronDeviceEntity conditionEntity);
	
	/**新建电烙铁工具**/
	public void insertElectricIronDevice(ElectricIronDeviceEntity conditionEntity);
	
	/**管理编号下拉选择数据**/
	public  List<ElectricIronDeviceEntity> searchManageCodes();
	
	/**设备品名下拉选择数据**/
	public List<ElectricIronDeviceEntity> searchDeviceNames();

	/**双击修改**/
	public void updateElectricIronDevice(ElectricIronDeviceEntity conditionEntity);
	
	/**删除**/
	public void deleteElectricIronDevice(ElectricIronDeviceEntity conditionEntity);
	
	/**判断是否已经存在电烙铁工具**/
	public List<ElectricIronDeviceEntity> ElectricIronDeviceisExist(ElectricIronDeviceEntity conditionEntity);

	public List<DevicesManageEntity> searchElectricIronDeviceOnLineByManager(
			CheckResultEntity condEntity);

	public List<DevicesManageEntity> searchElectricIronDeviceOnLineByOperator(
			CheckResultEntity condEntity);

	/**
	 * 取得需要做的定期点检
	 */
	public List<CheckResultEntity> getNeedRegularEICheck(CheckResultEntity cond);
}
