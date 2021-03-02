package com.osh.rvs.mapper.inline;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.inline.ComposeStorageEntity;

/**
 * 总组签收管理
 * 
 * @author lxb
 * 
 */
public interface ComposeStorageMapper {
	// 检索
	public List<ComposeStorageEntity> searchComposeStroage(ComposeStorageEntity instance);

	// 总组展示
	public List<ComposeStorageEntity> getComposNotEmpty();
	public List<ComposeStorageEntity> getComposeStorageEntities(ComposeStorageEntity condition);

	// 根据material_id判断总组签收对象是否存在
	public ComposeStorageEntity checkMaterialExist(@Param("material_id") String material_id);

	public String checkMaterialPutin(@Param("material_id") String material_id, @Param("line_id") String line_id);

	// 更新旧库位
	public void updateLocation(@Param("scan_code") String scan_code);

	// 移入新库位
	public void changeLocation(@Param("goods_id") String goods_id, @Param("scan_code") String scan_code);

	// 入库
	public void insertCom(@Param("material_id") String material_id, @Param("case_code") String case_code);

	/**
	 * 出库
	 * 
	 * @param goods_id
	 */
	public void stockRemoval(String goods_id);

	/**
	 * 查询货架是否存在
	 * @param scan_code
	 * @return 
	 */
	public ComposeStorageEntity searchShelfNameExits(String case_code);
	
	/**
	 * 查询货架是否放置了对象
	 * @param scan_code
	 * @return
	 */
	public ComposeStorageEntity searchGoodsExits(String case_code);

	public List<ComposeStorageEntity> showWaitingWarehousingOfPosition(String position_id);
}
