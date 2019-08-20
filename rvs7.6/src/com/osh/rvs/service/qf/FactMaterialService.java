package com.osh.rvs.service.qf;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.qf.FactMaterialEntity;
import com.osh.rvs.form.qf.FactMaterialForm;
import com.osh.rvs.mapper.qf.FactMaterialMapper;

import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class FactMaterialService {

	/**
	 * 查询现品维修对象作业
	 * 
	 * @param form
	 * @param conn
	 * @return
	 */
	public List<FactMaterialForm> search(ActionForm form, SqlSession conn) {
		FactMaterialMapper dao = conn.getMapper(FactMaterialMapper.class);

		FactMaterialEntity entity = new FactMaterialEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<FactMaterialForm> respFormList = new ArrayList<FactMaterialForm>();
		List<FactMaterialEntity> list = dao.search(entity);

		if (list != null && list.size() > 0) {
			BeanUtil.copyToFormList(list, respFormList, CopyOptions.COPYOPTIONS_NOEMPTY, FactMaterialForm.class);
		}

		return respFormList;
	}

	/**
	 * 新建现品维修对象作业
	 * 
	 * @param form
	 * @param conn
	 */
	public void insert(ActionForm form, SqlSessionManager conn) {
		FactMaterialMapper dao = conn.getMapper(FactMaterialMapper.class);

		FactMaterialEntity entity = new FactMaterialEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		dao.insert(entity);
	}
}
