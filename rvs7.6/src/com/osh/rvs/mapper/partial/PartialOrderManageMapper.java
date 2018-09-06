package com.osh.rvs.mapper.partial;

import java.util.List;

import com.osh.rvs.bean.partial.PartialOrderManageEntity;

public interface PartialOrderManageMapper {
	public List<PartialOrderManageEntity> searchMaterial(PartialOrderManageEntity entity);
	/* 查询订购详细信息 */
	public List<PartialOrderManageEntity> searchPartialOrder(PartialOrderManageEntity entity);

	/* 根据 material_partial_detail_key 查询出code、qty、process_code */
	public PartialOrderManageEntity searchMaterialPartialDetailByKey(
			String key);
	/*更新工位*/
	public void setOverBom(PartialOrderManageEntity partialOrderManageEntity);
	
	/*新建插入零件订购*/
	public void insertMaterialPartialDetail(PartialOrderManageEntity partialOrderManageEntity);
	
	/*根据material_partial_detail_key删除零件*/
	public void deletePartial(PartialOrderManageEntity partialOrderManageEntity);
	
	/*取消零件发放（更新待发放数量等于订购数量、最后发放时间为null）*/
	public void updateMaterialPartialDetail(PartialOrderManageEntity partialOrderManageEntity);
	
	/*取消订购对象*/
	public void deleteMaterialPartial(PartialOrderManageEntity partialOrderManageEntity);
	
	/*取消订购对象--删除material_partial_detail表的订购对象记录*/
	public void deleteMaterialPartialDetail(PartialOrderManageEntity partialOrderManageEntity);
	
	/*修改订购数量+订购工位+订购者--时的订购数量分配到不同订购工位*/
	public void insertDividedDetail(PartialOrderManageEntity partialOrderManageEntity);
	
	/*更新零件订购对象bo_flg*/
	public void updateMaterialPartialBoflg(PartialOrderManageEntity partialOrderManageEntity);
	
	/*更新零件订购签收明细状态*/
	public void updateMaterialPartialDetailStatus(PartialOrderManageEntity partialOrderManageEntity);
}
