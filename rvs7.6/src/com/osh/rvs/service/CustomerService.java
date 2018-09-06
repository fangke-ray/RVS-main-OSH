package com.osh.rvs.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.master.CustomerEntity;
import com.osh.rvs.common.FseBridgeUtil;
import com.osh.rvs.common.ReverseResolution;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.master.CustomerForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.data.MaterialMapper;
import com.osh.rvs.mapper.master.CustomerMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

public class CustomerService {

	public List<String> getAutoComplete(SqlSession conn) {
		CustomerMapper mapper = conn.getMapper(CustomerMapper.class);
		List<String> ret = new ArrayList<>();
		List<CustomerEntity> retEntities = mapper.getAllCustomers();
		for (CustomerEntity retEntity : retEntities) {
			ret.add(retEntity.getName());
		}
		return ret;
	}

	public String getCustomerStudiedId(String customer_name, Integer ocm,
			SqlSession conn) throws Exception {
		String customer_id = ReverseResolution.getCustomerByName(customer_name,
				conn);
		if (customer_id == null) {
			CustomerMapper mapper = conn.getMapper(CustomerMapper.class);
			CommonMapper cmapper = conn.getMapper(CommonMapper.class);
			CustomerEntity condi = new CustomerEntity();
			condi.setName(customer_name);
			condi.setOcm(ocm);
			condi.setUpdated_by("0"); // SYSTEM
			mapper.insertCustomer(condi);

			customer_id = cmapper.getLastInsertID();
			FseBridgeUtil.createCustomer(customer_id, customer_name, "" + ocm);
		}
		return customer_id;
	}

	/**
	 * 一览
	 * 
	 * @param form
	 * @param conn
	 * @return
	 */
	public List<CustomerForm> search(ActionForm form, SqlSession conn) {
		CustomerEntity entity = new CustomerEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		CustomerMapper dao = conn.getMapper(CustomerMapper.class);
		List<CustomerEntity> list = dao.search(entity);

		List<CustomerForm> returnForms = new ArrayList<CustomerForm>();
		if (list.size() > 0) {
			BeanUtil.copyToFormList(list, returnForms,CopyOptions.COPYOPTIONS_NOEMPTY, CustomerForm.class);
			return returnForms;
		} else {
			return null;
		}
	}

	/**
	 * 检查客户名称是否存在
	 * @param form
	 * @param conn
	 * @param errors
	 */
	public void checkNameIsExist(ActionForm form, SqlSession conn, List<MsgInfo> errors){
		CustomerEntity entity = new CustomerEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		CustomerMapper dao = conn.getMapper(CustomerMapper.class);
		int result=dao.checkNameIsExist(entity);
		
		if(result>=1){//存在
			MsgInfo error = new MsgInfo();
			error.setComponentid("add_name");
			error.setErrcode("dbaccess.recordDuplicated");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordDuplicated", "客户名称"+entity.getName()));
			errors.add(error);
		}
	}
	
	/**
	 * 新建客户
	 * @param form
	 * @param conn
	 * @param request
	 */
	public void insert(ActionForm form,SqlSessionManager conn,HttpServletRequest request)throws Exception{
		CustomerEntity entity = new CustomerEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		LoginData loginData = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
		String operator_id = loginData.getOperator_id();// 最后更新人
		
		entity.setUpdated_by(operator_id);
		CustomerMapper dao = conn.getMapper(CustomerMapper.class);
		dao.insert(entity);
	}
	
	
	/**
	 * 更新时候check 是否为当前ID,
	 * @param form
	 * @param conn
	 * @param errors
	 */
	public void checkIdIsCurrent(ActionForm form, SqlSession conn, List<MsgInfo> errors){
		CustomerEntity entity = new CustomerEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		CustomerMapper dao = conn.getMapper(CustomerMapper.class);
		String currentID=dao.checkIdIsCurrent(entity);
		
		if(!CommonStringUtil.isEmpty(currentID)){//不为空
			if(!currentID.equals(entity.getCustomer_id())){//不是当前ID
				int result=dao.checkNameIsExist(entity);
				if(result>=1){//存在
					MsgInfo error = new MsgInfo();
					error.setComponentid("add_name");
					error.setErrcode("dbaccess.recordDuplicated");
					error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordDuplicated", "客户名称"+entity.getName()));
					errors.add(error);
				}
			}
		}
	}
	
	/**
	 * 更新
	 * @param form
	 * @param request
	 * @param conn
	 */
	public void update(ActionForm form,SqlSessionManager conn,HttpServletRequest request){
		CustomerEntity entity = new CustomerEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		LoginData loginData = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
		String operator_id = loginData.getOperator_id();// 最后更新人
		
		entity.setUpdated_by(operator_id);
		
		CustomerMapper dao = conn.getMapper(CustomerMapper.class);
		dao.update(entity);
		
	}
	
	/**
	 * 查询归并目标
	 * @param form
	 * @param conn
	 * @return
	 */
	public List<CustomerForm> searchMergeTarget(CustomerForm form,SqlSession conn){
		CustomerEntity entity = new CustomerEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		CustomerMapper dao = conn.getMapper(CustomerMapper.class);
		
		List<CustomerEntity> list = dao.searchMergeTarget(entity);

		List<CustomerForm> returnForms = new ArrayList<CustomerForm>();
		if (list.size() > 0) {
			BeanUtil.copyToFormList(list, returnForms,CopyOptions.COPYOPTIONS_NOEMPTY, CustomerForm.class);
			return returnForms;
		} else {
			return null;
		}
	}
	
	/**
	 * 归并
	 * @param form
	 * @param conn
	 * @param request
	 */
	public void merge(ActionForm form,SqlSessionManager conn,HttpServletRequest request){
		CustomerEntity entity = new CustomerEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		
		LoginData loginData = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
		String operator_id = loginData.getOperator_id();// 最后更新人
		entity.setUpdated_by(operator_id);
		
		CustomerMapper customerDao = conn.getMapper(CustomerMapper.class);
		MaterialMapper materialDao=conn.getMapper(MaterialMapper.class);
		
		customerDao.deleteOriginal(entity);//删除归并源
		materialDao.updateCustomerId(entity.getTarget_customer_id(), entity.getOriginal_customer_id());//将归并源客户替换成归并目标客户
		
		Integer original_vip=entity.getOriginal_vip();
		Integer targer_vip=entity.getTarger_vip();
		
		if(original_vip==1 && targer_vip!=1){//归并源是VIP   归并目标不是VIP
			customerDao.updateTargetToVip(entity);
		}
		
	}
	

}
