package com.osh.rvs.service.partial;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;

import com.osh.rvs.bean.partial.ConsumableSupplyEntity;
import com.osh.rvs.form.partial.ConsumableManageForm;
import com.osh.rvs.mapper.partial.ConsumableSupplyMapper;

import framework.huiqing.common.util.copy.BeanUtil;

public class ConsumableSupplyService {

	/**
	 * 入库记录一览
	 * 
	 * @param entity
	 * @param conn
	 * @throws Exception
	 */
	public List<ConsumableManageForm> searchSupplyList(ConsumableSupplyEntity entity, SqlSession conn) throws Exception {

		ConsumableSupplyMapper dao = conn.getMapper(ConsumableSupplyMapper.class);

		List<ConsumableSupplyEntity> supplyList = dao.searchSupplyList(entity);

		List<ConsumableManageForm> lResultForm = new ArrayList<ConsumableManageForm>();
		BeanUtil.copyToFormList(supplyList, lResultForm, null, ConsumableManageForm.class);

		return lResultForm;
	}

	/** 零件集合 **/
	public List<ConsumableManageForm> getPartial(String code, SqlSession conn) throws Exception {
		ConsumableSupplyMapper dao = conn.getMapper(ConsumableSupplyMapper.class);

		List<ConsumableSupplyEntity> resultList = dao.getPartialByCode(code);

		List<ConsumableManageForm> lResultForm = new ArrayList<ConsumableManageForm>();
		BeanUtil.copyToFormList(resultList, lResultForm, null, ConsumableManageForm.class);

		return lResultForm;
	}

	/** 零件集合 **/
	public String searchPartialSupply(ConsumableSupplyEntity entity, SqlSessionManager conn)
			throws Exception {
		ConsumableSupplyMapper dao = conn.getMapper(ConsumableSupplyMapper.class);

		return dao.searchPartialSupply(entity);
	}

	/** 零件补充记录_插入 **/
	public void insertPartialSupply(ConsumableSupplyEntity entity, SqlSessionManager conn) throws Exception {
		ConsumableSupplyMapper dao = conn.getMapper(ConsumableSupplyMapper.class);

		dao.insertPartialSupply(entity);
	}

	/** 零件补充记录_更新 **/
	public void updatePartialSupply(ConsumableSupplyEntity entity, SqlSessionManager conn) throws Exception {
		ConsumableSupplyMapper dao = conn.getMapper(ConsumableSupplyMapper.class);

		dao.updatePartialSupply(entity);
	}

	/** 消耗品修正数据_更新 **/
	public void updateConsumableManage(ConsumableSupplyEntity entity, SqlSessionManager conn) throws Exception {
		ConsumableSupplyMapper dao = conn.getMapper(ConsumableSupplyMapper.class);

		dao.updateConsumableManage(entity);
	}

}
