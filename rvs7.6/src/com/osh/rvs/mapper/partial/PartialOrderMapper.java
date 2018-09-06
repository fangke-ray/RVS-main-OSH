package com.osh.rvs.mapper.partial;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.partial.MaterialPartialDetailEntity;
import com.osh.rvs.bean.partial.MaterialPartialEntity;

public interface PartialOrderMapper {
	/* 插入订购信息 */
	public void insertMaterialPartialDetail(MaterialPartialDetailEntity materialPartialDetailEntity);

	/* 查询订购详细信息 */
	public List<MaterialPartialDetailEntity> searchPartialOrder(MaterialPartialDetailEntity materialPartialDetailEntity);

	/*查询该维修对象是否已经完成过导入过*/
	public  List<MaterialPartialDetailEntity> checkMaterialPartialDetailIsNull(MaterialPartialDetailEntity materialPartialDetailEntity);
	
	/* 删除表 */
	public void deleteMaterialPartialDetail(MaterialPartialDetailEntity materialPartialDetailEntity);

	/* 作为分室追加 */
	public void updateByBelongs(MaterialPartialDetailEntity materialPartialDetailEntity);

	/* 查询material_partial表是否已经存在本条数据 如果存在就不要再页面加载再进行重复操作了 */
	public List<MaterialPartialEntity> searchMaterialPartial(
			MaterialPartialDetailEntity materialPartialDetailEntity);

	/* 更新到material_partial */
	public void insertMaterialPartial(MaterialPartialEntity entity);

	/* 根据 material_partial_detail_key 查询出code、qty、process_code */
	public List<MaterialPartialDetailEntity> searchMaterialPartialDetailCode(
			Map<String, Object> param);
	

	/* 根据 material_partial_detail_key 查询出code、qty、process_code */
	public MaterialPartialDetailEntity searchMaterialPartialDetailByKey(
			String key);

	/* 更新成导入订购后的状态 */
	public void formalizeMaterialPartialDetail(MaterialPartialDetailEntity materialPartialDetailEntity);
	
	/*分配零件到BOM(quantity相等的情况是所有的都更新成BOM；小于的时候除去多余部分，拿相等的去更新成BOM，剩下的是给未分配) */
	public void updateMaterialPartialDetailWithBom(MaterialPartialEntity materialPartialEntity);
	
	/*由比较两表的quantity查询零件订购信息(quantity小于的时候，取出剩下的零件订购详细信息)*/
	public List<MaterialPartialDetailEntity> searchMaterialPartialDetailQuantityUnmatch(MaterialPartialEntity materialPartialEntity);

	/**更新工位已确定的零件到不良追加*/
	public void updateMaterialPartialDetailWithNoGood(MaterialPartialEntity materialPartialEntity);

	/*查出可定位工位*/
	public String getPositionSimply(MaterialPartialDetailEntity materialPartialDetailEntity);

	/*bom超出quantity*/
	public void setOverBom(MaterialPartialDetailEntity resultBean);
	
	/*根据维修对象ID和零件ID查询出process_code*/
	public List<MaterialPartialDetailEntity> searchPartialPositionBelongProcessCode(@Param("model_id")  String model_id,@Param("partial_id") String partial_id);

	/*根据待确定状态找出上一次未完成分配的零件*/
	public List<MaterialPartialDetailEntity> searchMaterialPartialDetailWithStatus();

	/** 移动定位数量后剩下的 */
	public void substractSelf(MaterialPartialDetailEntity materialPartialDetailEntity);

	/** 去除未确定的维修对象订购信息 */
	public void cancelPartialOrder(@Param("material_id") String material_id,@Param("occur_times") String occur_times);
	
	//根据输入订购数量和新订购工位确认新建新的一条数据
	public void insertDetail(MaterialPartialDetailEntity materialPartialDetailEntity);
}
