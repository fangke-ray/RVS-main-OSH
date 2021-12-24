package com.osh.rvs.mapper.partial;

import java.util.List;

import com.osh.rvs.bean.partial.ComponentSettingEntity;

public interface ComponentSettingMapper {

	public List<ComponentSettingEntity> getAllComponentSettings();
	
	public List<ComponentSettingEntity> searchComponentSetting();

	/* 插入数据 */
	public int insertSetting(ComponentSettingEntity settingEntity);
	
	/* 根据型号ID取得NS组件设置信息 */
	public ComponentSettingEntity searchComponentSettingByModelId(String model_id);
	
	/* 根据识别代码取得NS组件设置信息 */
	public ComponentSettingEntity searchComponentSettingByIdentifyCode(String identify_code);
	
	// NS配件设置信息取得
	public List<ComponentSettingEntity> getSettingDetail(String model_id);

	/* 更新数据 */
	public int updateSetting(ComponentSettingEntity settingEntity);

	/* 删除数据 */
	public int deleteSetting(ComponentSettingEntity settingEntity);

	public List<ComponentSettingEntity> getAllSnoutComponentModel();
	public List<ComponentSettingEntity> searchSnoutComponentSetting();

	/* 插入先端头数据 */
	public int insertSnoutSetting(ComponentSettingEntity settingEntity);
	
	// 先端头配件设置信息取得
	public ComponentSettingEntity getSnoutSettingDetail(String model_id);

	/* 更新先端头数据 */
	public int updateSnoutSetting(ComponentSettingEntity settingEntity);

	/* 子零件盘点 */
	public int inventSnoutSubPartSets(ComponentSettingEntity settingEntity);

	/* 删除先端头数据 */
	public int deleteSnoutSetting(ComponentSettingEntity settingEntity);
}
