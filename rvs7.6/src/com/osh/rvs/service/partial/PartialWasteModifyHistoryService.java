package com.osh.rvs.service.partial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.master.OperatorEntity;
import com.osh.rvs.bean.master.OperatorNamedEntity;
import com.osh.rvs.bean.partial.PartialWasteModifyHistoryEntity;
import com.osh.rvs.form.partial.PartialWasteModifyHistoryForm;
import com.osh.rvs.mapper.master.OperatorMapper;
import com.osh.rvs.mapper.partial.PartialWasteModifyHistoryMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class PartialWasteModifyHistoryService {

	public List<PartialWasteModifyHistoryForm> search(ActionForm form,SqlSession conn,
			List<MsgInfo> errors) throws NumberFormatException, Exception {
		List<PartialWasteModifyHistoryForm> partialWasteModifyHistoryFormList = new ArrayList<PartialWasteModifyHistoryForm>();
		PartialWasteModifyHistoryMapper dao = conn.getMapper(PartialWasteModifyHistoryMapper.class);
		PartialWasteModifyHistoryEntity partialWasteModifyHistoryEntity = new PartialWasteModifyHistoryEntity();
		BeanUtil.copyToBean(form, partialWasteModifyHistoryEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<PartialWasteModifyHistoryEntity> partialWasteModifyHistoryEntityList = dao
				.searchPartialWasteModifyHistory(partialWasteModifyHistoryEntity);
		BeanUtil.copyToFormList(partialWasteModifyHistoryEntityList, partialWasteModifyHistoryFormList,
				CopyOptions.COPYOPTIONS_NOEMPTY, PartialWasteModifyHistoryForm.class);

		return partialWasteModifyHistoryFormList;
	}
	
	/***
	 * 操作者options
	 * 
	 */
	public String operatorNameOptions(SqlSession conn){
		//操作者ID和name放入到map中
		Map<String, String> operatorIdNameMap = new HashMap<String, String>();
		OperatorMapper dao = conn.getMapper(OperatorMapper.class);
		
		//获取所有的操作者
		List<OperatorNamedEntity> osperatorNamedList =  dao.searchOperator(null);
		for(OperatorNamedEntity operatorNamedEntity:osperatorNamedList){
			operatorIdNameMap.put(operatorNamedEntity.getOperator_id(), operatorNamedEntity.getName());
		}
		String opertorIdName = CodeListUtils.getSelectOptions(operatorIdNameMap, "", "", false);
		
		return opertorIdName;
	}
	
	/**
	 * 取得操作者选择项
	 * @param conn
	 * @return
	 */
	public String getOpertorIdNameptions(SqlSession conn) {
		List<String[]> lst = new ArrayList<String[]>();
		OperatorMapper dao = conn.getMapper(OperatorMapper.class);
		String role_id ="16";
		List<OperatorEntity> operatorEntityList = dao.getOperatorWithRole(role_id);
		
		for (OperatorEntity operatorEntity: operatorEntityList) {
			String[] p = new String[3];
			p[0] = operatorEntity.getOperator_id();
			p[1] = operatorEntity.getName();
			p[2]=operatorEntity.getRole_name();
			lst.add(p);
		}
		
		String opertorIdNameReferChooser = CodeListUtils.getReferChooser(lst);
		
		return opertorIdNameReferChooser;
	}
}
