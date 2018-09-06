package com.osh.rvs.mapper.master;

import java.util.List;

import com.osh.rvs.bean.master.CustomerEntity;

public interface CustomerMapper {

	/** insert single */
	public int insertCustomer(CustomerEntity role) throws Exception;

	public List<CustomerEntity> searchCustomer(CustomerEntity condi);

	public List<CustomerEntity> getAllCustomers();
	
	public List<CustomerEntity> search(CustomerEntity role);
	public void insert(CustomerEntity entity);
	
	/*检查客户名称是否存在*/
	public int checkNameIsExist(CustomerEntity entity);
	
	/*检查是否是当前ID*/
	public String checkIdIsCurrent(CustomerEntity entity);
	
	/*更新*/
	public void update(CustomerEntity entity);
	
	/*查询归并目标*/
	public List<CustomerEntity> searchMergeTarget(CustomerEntity role);
	
	/*删除归并源*/
	public void deleteOriginal(CustomerEntity role);
	
	/*将归并目标变成VIP*/
	public void updateTargetToVip(CustomerEntity role);

}
