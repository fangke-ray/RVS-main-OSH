package com.osh.rvs.service.partial;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.partial.FactPartialWarehouseEntity;
import com.osh.rvs.form.partial.FactPartialWarehouseForm;
import com.osh.rvs.mapper.partial.FactPartialWarehouseMapper;

import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

/**
 * 零件入库作业数
 * 
 * @author liux
 * 
 */
public class FactPartialWarehouseService {

	/**
	 * 查询
	 * 
	 * @param form
	 * @param conn
	 * @return
	 */
	public List<FactPartialWarehouseForm> search(ActionForm form, SqlSession conn) {
		FactPartialWarehouseMapper dao = conn.getMapper(FactPartialWarehouseMapper.class);

		FactPartialWarehouseEntity entity = new FactPartialWarehouseEntity();
		// 复制表单数据
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<FactPartialWarehouseForm> respForms = new ArrayList<FactPartialWarehouseForm>();
		List<FactPartialWarehouseEntity> list = dao.search(entity);

		if (list != null && list.size() > 0) {
			BeanUtil.copyToFormList(list, respForms, CopyOptions.COPYOPTIONS_NOEMPTY, FactPartialWarehouseForm.class);
		}

		return respForms;
	}

	/**
	 * 新建零件入库作业数
	 * 
	 * @param form
	 * @param conn
	 */
	public void insert(ActionForm form, SqlSessionManager conn) {
		FactPartialWarehouseMapper dao = conn.getMapper(FactPartialWarehouseMapper.class);

		FactPartialWarehouseEntity entity = new FactPartialWarehouseEntity();
		// 复制表单数据
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		dao.insert(entity);
	}

	/**
	 * 统计每个规格种别入库作业总数
	 * 
	 * @param form
	 * @param conn
	 * @return
	 */
	public List<FactPartialWarehouseForm> countQuantityOfSpecKind(ActionForm form, SqlSession conn) {
		FactPartialWarehouseMapper dao = conn.getMapper(FactPartialWarehouseMapper.class);

		FactPartialWarehouseEntity entity = new FactPartialWarehouseEntity();
		// 复制表单数据
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<FactPartialWarehouseForm> respForms = new ArrayList<FactPartialWarehouseForm>();

		List<FactPartialWarehouseEntity> list = dao.countQuantityOfSpecKind(entity);
		if (list != null && list.size() > 0) {
			BeanUtil.copyToFormList(list, respForms, CopyOptions.COPYOPTIONS_NOEMPTY, FactPartialWarehouseForm.class);
		}

		return respForms;
	}

}
