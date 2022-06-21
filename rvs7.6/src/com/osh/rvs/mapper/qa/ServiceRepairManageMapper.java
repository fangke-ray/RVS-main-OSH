package com.osh.rvs.mapper.qa;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.qa.ServiceRepairManageEntity;

public interface ServiceRepairManageMapper {

	/** 确认保内QIS管理件数 **/
	public long checkExistsByKey(ServiceRepairManageEntity condition);

	/** 新建保内QIS数据 **/
	public Integer insertServiceRepairManage(ServiceRepairManageEntity insertance)throws Exception;

	/** 新建保内QIS-旧维修对象数据 **/
	public Integer insertServiceRepairPastMaterial(ServiceRepairManageEntity insertance) throws Exception;

	/** 保内QIS型号名字集合 **/
	public List<String> getModelNameAutoCompletes();
	
	/**无QA判定日的数据中最早的一个QA受理日**/
	public Date getMinReceptionTime();
	
	/**保内QIS管理一览**/
	public List<ServiceRepairManageEntity> searchServiceRepair(ServiceRepairManageEntity instance);
	
	/**保内QIS管理等级集合**/
	public List<String> getRankAutoCompletes();
	
	/**维修对象对象集合，集合长度=0 or >1返回null,集合长度=1返回一个对象**/
	public List<MaterialEntity> getRecept(ServiceRepairManageEntity instance);
	
	/**查询住键是否存在**/
	public List<ServiceRepairManageEntity> getPrimaryKey(ServiceRepairManageEntity instance);
	
	public List<MaterialEntity> getMaterialIds(ServiceRepairManageEntity instance);
	
	/**更新service_repair_manage表**/
	public void updateServiceRepairManage(ServiceRepairManageEntity entity) throws Exception;

	/**更新对应维修对象**/
	public void updateMaterialId(@Param("new_material_id") String new_material_id, @Param("material_id") String material_id) throws Exception;
	
	/**删除QIS请款信息**/
	public void deleteQisPayout(ServiceRepairManageEntity entity);
	
	/**更新QIS请款信息**/
	public void updateQisPayout(ServiceRepairManageEntity entity);
	
	/**获取service_repair_manage表中最大material_id**/
	public String getMaxMaterialId(String typeChar);

	/**删除保内QIS数据**/
	public void deleteServiceRepairManage(ServiceRepairManageEntity entity) throws Exception;

	/**
	 * QIS后匹配
	 * @param model_name
	 * @param serial_no
	 * @return
	 */
	public List<String> matchQis(@Param("model_name") String model_name,@Param("serial_no") String serial_no);
	
	//零件分析表详细数据
	public ServiceRepairManageEntity searchServiceRepairAnalysis(ServiceRepairManageEntity serviceRepairManageEntity);
	
	//保内返品分析图像详细
	public List<ServiceRepairManageEntity> searchServiceRepairAnalysisGram(ServiceRepairManageEntity serviceRepairManageEntity);
	
	//零件分析表详细数据更新
	public void updateServiceRepairAnalysis(ServiceRepairManageEntity serviceRepairManageEntity);
	
	//保内返品分析图像数据更新
	public void updateServiceRepairAnalysisGram(ServiceRepairManageEntity serviceRepairManageEntity);
	
	//更新提要
	public void updateMention(ServiceRepairManageEntity serviceRepairManageEntity);

	public List<ServiceRepairManageEntity> searchServiceRepairByMaterial_id(
			String material_id);

	//删除保内返品分析图像
	public void deleteAnalysisGram(ServiceRepairManageEntity conditionEntity);
	
	//获取当前保内返品的最大的seq_no
	public String getMaxSeqNo(ServiceRepairManageEntity conditionEntity);

	public void undoRefeeWork(ServiceRepairManageEntity entity) throws Exception;

	public int updateRcMailsendDateForQisPayout(ServiceRepairManageEntity conditionEntity);
	public Date checkServiceRepairAnalysis(ServiceRepairManageEntity conditionEntity);
	public int updateRcMailsendDateForServiceRepairAnalysis(ServiceRepairManageEntity conditionEntity);
}
