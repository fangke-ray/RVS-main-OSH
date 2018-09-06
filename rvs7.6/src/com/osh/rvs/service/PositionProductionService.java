package com.osh.rvs.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.data.PositionProductionEntity;
import com.osh.rvs.form.data.PositionProductionForm;
import com.osh.rvs.mapper.data.PositionProductionMapper;

import framework.huiqing.common.util.copy.BeanUtil;

public class PositionProductionService {
	
//	private static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	public List<PositionProductionForm> searchByCondition(ActionForm form, SqlSession conn) {
		PositionProductionEntity conditionBean = new PositionProductionEntity();
		BeanUtil.copyToBean(form, conditionBean, null);
		
		PositionProductionMapper dao = conn.getMapper(PositionProductionMapper.class);
		List<PositionProductionEntity> list = dao.getProductionFeatureByPosition(conditionBean);
		
		List<PositionProductionForm> rtList = new ArrayList<PositionProductionForm>();
		
		BeanUtil.copyToFormList(list, rtList, null, PositionProductionForm.class);
		
		return rtList;
	}
	
	public PositionProductionForm getDetail(ActionForm form, SqlSession conn) throws Exception {
		PositionProductionEntity conditionBean = new PositionProductionEntity();
		BeanUtil.copyToBean(form, conditionBean, null);
		
//		PositionProductionForm f = (PositionProductionForm) form;
//		conditionBean.setAction_time(new Timestamp(format.parse(f.getAction_time()).getTime()));
		
		PositionProductionMapper dao = conn.getMapper(PositionProductionMapper.class);
		PositionProductionEntity entity = dao.getDetail(conditionBean);
		
		PositionProductionForm rtForm = new PositionProductionForm();
		BeanUtil.copyToForm(entity, rtForm, null);
		
		return rtForm;
	}
	
	public List<PositionProductionForm> getProductionFeatureByKey(ActionForm form, SqlSession conn) throws Exception {
		PositionProductionEntity conditionBean = new PositionProductionEntity();
		BeanUtil.copyToBean(form, conditionBean, null);
		
//		PositionProductionForm f = (PositionProductionForm) form;
//		conditionBean.setAction_time(new Timestamp(format.parse(f.getAction_time()).getTime()));
		
		PositionProductionMapper dao = conn.getMapper(PositionProductionMapper.class);
		List<PositionProductionEntity> list = dao.getProductionFeatureByKey(conditionBean);
		
		List<PositionProductionForm> rtList = new ArrayList<PositionProductionForm>();
		
		BeanUtil.copyToFormList(list, rtList, null, PositionProductionForm.class);
		
		return rtList;
	}
}
