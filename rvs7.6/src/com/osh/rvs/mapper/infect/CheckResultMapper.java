package com.osh.rvs.mapper.infect;

import java.util.List;

import com.osh.rvs.bean.infect.CheckResultEntity;
import com.osh.rvs.bean.master.ToolsManageEntity;

public interface CheckResultMapper {

	/**
	 * 取得需要做的日常点检
	 */
	public List<CheckResultEntity> getNeedDailyCheck(CheckResultEntity cond);

	/**
	 * 取得需要做的定期点检
	 */
	public List<CheckResultEntity> getNeedRegularCheck(CheckResultEntity cond);

	/**
	 * 取得可以做的使用前点检
	 */
	public List<CheckResultEntity> getCapacyBeforeUseCheck(CheckResultEntity cond);

	/**
	 * 建立等待更新的日常/定期点检记录
	 */
	public int createDeviceWaitingCheck(CheckResultEntity createWait) throws Exception;

	/**
	 * 保存治具点检记录
	 */
	public void insertToolCheck(CheckResultEntity entity);

	/**
	 * 取得治具责任者的签章
	 */
	public List<CheckResultEntity> getResponseStamp(CheckResultEntity cond);

	/**
	 * 查找点检治具对象(担当者)
	 */
	public List<ToolsManageEntity> searchToolCheckPositionsByOperator(
			CheckResultEntity result);

	/**
	 * 按工位查找未点检的治具
	 */
	public List<CheckResultEntity> searchToolUncheckedOnPosition(CheckResultEntity position);
	/**
	 * 按工位查找未点检的日常点检设备工具
	 */
	public String searchDailyDeviceUncheckedOnPosition(CheckResultEntity position);
	/**
	 * 按工位查找未点检的定期点检设备工具
	 */
	public String searchRegularyDeviceUncheckedOnPosition(CheckResultEntity cond);
	public String searchTorsionDeviceUncheckedOnPosition(CheckResultEntity cond);

	/**
	 * 取得上级(治具管理者)的签章
	 */
	public List<CheckResultEntity> getUpperStamp(CheckResultEntity cond);

	/**
	 * 上级(治具管理者)确认
	 */
	public int setUpperConfirm(CheckResultEntity entity);

	/**
	 * 取得设备工具点检记录
	 */
	public List<CheckResultEntity> getDeviceCheckInPeriod(CheckResultEntity cre);

	/**
	 * 取得设备工具(力矩工具)点检记录
	 */
	public List<CheckResultEntity> getTorsionDeviceCheckInPeriod(CheckResultEntity cre);

	/**
	 * 取得设备工具(力矩工具)参照值
	 */
	public List<CheckResultEntity> getDeviceReferInPeriod(CheckResultEntity cre);

	/**
	 * 保存设备工具点检记录
	 */
	public void insertDeviceCheck(CheckResultEntity entity);

	/**
	 * 删除设备工具待点检记录
	 */
	public int removeWaitDeviceCheck(CheckResultEntity wait) throws Exception;

	/**
	 * 保存设备工具参照值
	 */
	public void insertDeviceCheckRefer(CheckResultEntity entity);

	/**
	 * 取得需要做的定期点检(力矩工具)
	 */
	public List<CheckResultEntity> getNeedTorsionCheck(CheckResultEntity cond);

	/**
	 * 取得设备工具(电烙铁)点检记录
	 */
	public List<CheckResultEntity> getEIDeviceCheckOfDate(CheckResultEntity cre);

	public List<CheckResultEntity> getDeviceCheckCommentInPeriodByManageId(CheckResultEntity entity);
	public List<CheckResultEntity> getJigCheckCommentInPeriodByManageId(CheckResultEntity entity);

	public String getDeviceCheckCommentInPeriodByManageIdGroup(CheckResultEntity entity);
	public String getJigCheckCommentInPeriodByManageIdGroup(CheckResultEntity entity);

	public void inputCheckComment(CheckResultEntity condition);

	/**
	 * 取得需要做的定期点检(电烙铁)
	 */
	public List<CheckResultEntity> getNeedElectricIronCheck(CheckResultEntity condition);

	public int getWeekCheck(CheckResultEntity entity);
}
