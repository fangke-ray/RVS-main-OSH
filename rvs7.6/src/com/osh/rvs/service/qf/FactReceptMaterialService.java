package com.osh.rvs.service.qf;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.qf.FactReceptMaterialEntity;
import com.osh.rvs.form.qf.FactReceptMaterialForm;
import com.osh.rvs.mapper.qf.FactReceptMaterialMapper;

import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class FactReceptMaterialService {
	public List<FactReceptMaterialForm> searchReceptMaterial(SqlSession conn) {
		FactReceptMaterialMapper dao = conn.getMapper(FactReceptMaterialMapper.class);

		List<FactReceptMaterialEntity> list = dao.searchReceptMaterial();

		List<FactReceptMaterialForm> respList = new ArrayList<FactReceptMaterialForm>();
		BeanUtil.copyToFormList(list, respList, CopyOptions.COPYOPTIONS_NOEMPTY, FactReceptMaterialForm.class);

		return respList;
	}

	/**
	 * 新建临时维修品实物受理/测漏
	 * 
	 * @param form
	 * @param conn
	 */
	public void insertFactReceptMaterialTemp(ActionForm form, SqlSessionManager conn) {
		FactReceptMaterialMapper dao = conn.getMapper(FactReceptMaterialMapper.class);

		FactReceptMaterialEntity entity = new FactReceptMaterialEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		dao.insertFactReceptMaterialTemp(entity);
	}

	public List<FactReceptMaterialForm> searchFactReceptMaterialTemp(ActionForm form, SqlSession conn) {
		FactReceptMaterialMapper dao = conn.getMapper(FactReceptMaterialMapper.class);
		
		FactReceptMaterialEntity entity = new FactReceptMaterialEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<FactReceptMaterialEntity> list = dao.searchTemp(entity);
		
		List<FactReceptMaterialForm> respList = new ArrayList<FactReceptMaterialForm>();
		BeanUtil.copyToFormList(list, respList, CopyOptions.COPYOPTIONS_NOEMPTY, FactReceptMaterialForm.class);
		
		return respList;
	}

	public void updateFactReceptMaterialTemp(ActionForm form, SqlSessionManager conn) {
		FactReceptMaterialMapper dao = conn.getMapper(FactReceptMaterialMapper.class);
		
		FactReceptMaterialEntity entity = new FactReceptMaterialEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		dao.updateFactReceptMaterialTemp(entity);
	}

}
