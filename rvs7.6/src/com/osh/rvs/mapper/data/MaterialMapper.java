package com.osh.rvs.mapper.data;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.MaterialTimeNodeEntity;


public interface MaterialMapper {

	/**
	 * 维修对象一览，根据条件查询对象(不包含纳期）
	 * 
	 * @param entity
	 * @return
	 */
	public List<MaterialEntity> searchMaterial(MaterialEntity entity);

	/**
	 * 维修对象一览，根据条件查询对象(归档视图）
	 * 
	 * @param entity
	 * @return
	 */
	public List<MaterialEntity> searchMaterialFiling(MaterialEntity entity);

	/**
	 * 维修对象一览，根据对象ID获取对象
	 * 
	 * @param ids
	 *            符合所有查询条件的ID集合
	 * @return
	 */
	public List<MaterialEntity> getMaterialDetail(List<String> ids);

	public List<MaterialEntity> getMaterialDetailTicket(List<String> ids);

	/**
	 * 维修对象一览，根据条件查询对象ID(不包含纳期）
	 * 
	 * @param entity
	 * @return
	 */
	public List<String> searchMaterialIds(MaterialEntity entity);

	/**
	 * 维修对象一览，根据纳期查询对象ID
	 * 
	 * @param entity
	 * @return
	 */
	public List<String> searchMaterialProcessIds(MaterialEntity entity);

	/**
	 * 详细画面内容,维修对象基本信息,根据ID查询详细
	 * 
	 * @param id
	 * @return
	 */
	public MaterialEntity loadMaterialDetail(String id);

	public MaterialEntity loadMaterialDetailAccpetance(String id);

	public MaterialEntity loadMaterialByOmrNotifiNo(String omr_notifi_no);

	/**
	 * 维修对象一览，修改维修对象
	 * 
	 * @param entity
	 */
	public void updateMaterial(MaterialEntity entity);

	/**
	 * 维修对象本表数据按主键参照
	 * 
	 * @param id
	 * @return
	 */
	public MaterialEntity getMaterialEntityByKey(String id);

	/**
	 * 维修对象本表数据按主键参照
	 * 
	 * @param id
	 * @return
	 */
	public MaterialEntity getMaterialNamedEntityByKey(String id);

	public void insertMaterial(MaterialEntity entity);

	public void updateMaterialTicket(List<String> ids) throws Exception;

	public void updateMaterialReturn(List<String> ids) throws Exception;

	public void updateMaterialExpedite(List<String> ids) throws Exception;

	public void updateMaterialNsPartial(String material_id) throws Exception;

	public void updateMaterialPat(@Param("material_id") String material_id,@Param("pat_id") String pat_id) throws Exception;

	/**
	 * 零件签收详细数据
	 * 
	 * @param material_id
	 * @return
	 */
	public MaterialEntity searchMaterialReceptByMaterialID(@Param("material_id") String material_id,@Param("occur_times") String occur_times);
	
	/**
	 * WIP库存
	 * @param entity
	 * @return
	 */
	public int getWipCount(MaterialEntity entity);
	
	/**
	 * 受理数
	 * @return
	 */
	public int getReceptCount(MaterialEntity entity);
	
	/**
	 * 同意数
	 * @param entoty
	 * @return
	 */
	public int getAgreeCount(MaterialEntity entoty);
	
	/**
	 * 等级型号拉动台数平均同意数
	 * @param entoty
	 * @return
	 */
	public double getAverageAgreeOfLevelModelLeeds(MaterialEntity entity);

	/**
	 * 更新注释
	 * fixtype=1 前方追加 fixtype=2 后方追加
	 * @param material_id
	 * @return
	 */
	public void updateMaterialComment(MaterialEntity entity);
	
	/**
	 * 客户管理归并操作 将归并源客户替换成归并目标客户
	 * @param original_customer_id 归并源
	 * @param target_customer_id 归并目标
	 */
	public void updateCustomerId(@Param("target_customer_id") String target_customer_id ,@Param("original_customer_id") String original_customer_id);

	/**
	 * outline_time为空的维修对象
	 * 
	 * @return
	 */
	public List<MaterialEntity> searchMaterialByOutlineTime();

	/**
	 * 取得在线计划出货日
	 * @param material_id
	 */
	public List<MaterialEntity> getInlineScheduled();
	/**
	 * 更新计划出货日
	 * @param material_id
	 */
	public void updateScheduledDate(MaterialEntity entity);

	/**
	 * outline_time设成当前时间
	 */
	public void updateMaterialOutlineTime(@Param("material_id") String material_id);

	public List<MaterialEntity> searchMaterialPerlTempFiling();

	public MaterialTimeNodeEntity getMaterialTimeNode(String material_id);	
	public void createMaterialTimeNode(MaterialTimeNodeEntity material);	
	public void updateMaterialTimeNode(MaterialTimeNodeEntity material);	
}
