package com.osh.rvs.service.partial;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;

import com.osh.rvs.bean.partial.ConsumableInventoryEntity;
import com.osh.rvs.form.partial.ConsumableManageForm;
import com.osh.rvs.mapper.partial.ConsumableInventoryMapper;

import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class ConsumableInventoryService {

	/**
	 * 盘点记录一览
	 * 
	 * @param entity
	 * @param conn
	 * @throws Exception
	 */
	public List<ConsumableManageForm> searchInventoryList(ConsumableInventoryEntity entity, SqlSession conn)
			throws Exception {

		ConsumableInventoryMapper dao = conn.getMapper(ConsumableInventoryMapper.class);

		List<ConsumableInventoryEntity> inventoryList = dao.searchInventoryList(entity);

		List<ConsumableManageForm> lResultForm = new ArrayList<ConsumableManageForm>();
		BeanUtil.copyToFormList(inventoryList, lResultForm, null, ConsumableManageForm.class);

		return lResultForm;
	}

	/**
	 * 盘点记录详细
	 * 
	 * @param detailForm
	 * @param conn
	 * @throws Exception
	 */
	public void updateInventoryDetail(ConsumableManageForm detailForm, SqlSessionManager conn)
			throws Exception {

		ConsumableInventoryMapper dao = conn.getMapper(ConsumableInventoryMapper.class);

		ConsumableInventoryEntity entity = new ConsumableInventoryEntity();
		BeanUtil.copyToBean(detailForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		dao.updateInventoryDetail(entity);
	}
}
