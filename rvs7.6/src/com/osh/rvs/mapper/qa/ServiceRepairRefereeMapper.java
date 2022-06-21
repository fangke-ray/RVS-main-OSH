package com.osh.rvs.mapper.qa;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.qa.ServiceRepairManageEntity;


public interface ServiceRepairRefereeMapper {
	public List<ServiceRepairManageEntity> searchServiceRepair(@Param("material_id") String material_id, 
			@Param("kind") String kind, @Param("anml_exp") String anml_exp);
	
	
	//检查维修对象是否存在
	public ServiceRepairManageEntity checkServiceRepairManageExist(@Param("material_id") String material_id);
	
	public void updateQareceptionTime(@Param("material_id") String material_id);
	
	//查询QIS请款信息
	public ServiceRepairManageEntity searchQisPayout(ServiceRepairManageEntity entity);

	public void updateServiceRepair(ServiceRepairManageEntity entity);


	/**
	 * 寻找暂停区中数据
	 * @param material_id 有值的时候为扫描对象确认
	 * @return
	 */
	public List<ServiceRepairManageEntity> findPausing(@Param("material_id") String material_id, @Param("position_id") String position_id);

//	/**删除QIS请款信息**/
//	public void deleteQisPayout(ServiceRepairManageEntity entity);
//	
//	/**更新QIS请款信息**/
//	public void updateQisPayout(ServiceRepairManageEntity entity);


	public List<ServiceRepairManageEntity> checkSoloPf(ServiceRepairManageEntity entity);
	public int updateRcMailsendDateForSoloPf(ServiceRepairManageEntity entity);

}
