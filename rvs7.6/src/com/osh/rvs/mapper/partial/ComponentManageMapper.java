package com.osh.rvs.mapper.partial;

import java.util.List;

import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.partial.ComponentManageEntity;

public interface ComponentManageMapper {

	// 查询数据
	public List<ComponentManageEntity> searchComponentManage(ComponentManageEntity ComponentManageEntity);
	
	/*查询所有的零件code和name*/
	public List<ComponentManageEntity> getAllPartial(String code); 

	/* 插入数据 */
	public int insert(ComponentManageEntity entity);

	/* 更新数据 */
	public int update(ComponentManageEntity entity);

	/* 取得新的序列号后五位 */
	public String getNewSerialNo(String identifyCode);

	/* 子零件出库更新 */
	public int partialOutstock(ComponentManageEntity entity);

	/* 子零件入库更新 */
	public int partialInstock(ComponentManageEntity entity);

	/* 组件出库更新 */
	public int componentOutstock(ComponentManageEntity entity);

	/* 组件入库更新 */
	public int componentInstock(ComponentManageEntity entity);
	
	
	// 查询数据详细
	public ComponentManageEntity searchComponentManageDetail(String componentKey);

	/* 组件废弃处理 */
	public int cancleManage(ComponentManageEntity entity);
	
	/* 取得已有库位编号 */
	public List<String> getNSStock();

	public String getComponentByTargetMaterial(String target_material_id);

	public List<MaterialEntity> getTargetMaterials(ComponentManageEntity entity);
}
