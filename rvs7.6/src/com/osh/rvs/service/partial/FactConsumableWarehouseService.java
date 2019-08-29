package com.osh.rvs.service.partial;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;

import com.osh.rvs.bean.partial.FactConsumableWarehouseEntity;
import com.osh.rvs.mapper.partial.FactConsumableWarehouseMapper;

/**
 * 消耗品入出库作业数
 * 
 * @author liuxb
 * 
 */
public class FactConsumableWarehouseService {

//	/**
//	 * 查询
//	 * 
//	 * @param form
//	 * @param conn
//	 * @return
//	 */
//	public List<FactConsumableWarehouseForm> search(ActionForm form, SqlSession conn) {
//		FactConsumableWarehouseMapper dao = conn.getMapper(FactConsumableWarehouseMapper.class);
//
//		FactConsumableWarehouseEntity entity = new FactConsumableWarehouseEntity();
//		// 复制表单数据
//		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
//
//		List<FactConsumableWarehouseForm> respForms = new ArrayList<FactConsumableWarehouseForm>();
//		List<FactConsumableWarehouseEntity> list = dao.search(entity);
//
//		if (list != null && list.size() > 0) {
//			BeanUtil.copyToFormList(list, respForms, CopyOptions.COPYOPTIONS_NOEMPTY, FactConsumableWarehouseForm.class);
//		}
//
//		return respForms;
//	}

	/**
	 * 统计耗时种别出入库作业总数
	 * 
	 * @param af_pf_key
	 * @param shelf_cost
	 * @param conn
	 * @return
	 */
	public Integer getQuantity(String af_pf_key, Integer shelf_cost, SqlSession conn) {
		FactConsumableWarehouseMapper mapper = conn.getMapper(FactConsumableWarehouseMapper.class);

		FactConsumableWarehouseEntity condition = new FactConsumableWarehouseEntity();
		condition.setAf_pf_key(af_pf_key);
		condition.setShelf_cost(shelf_cost);

		List<FactConsumableWarehouseEntity> list = mapper.search(condition);

		if (list.size() == 0) {
			return null;
		} else {
			return list.get(0).getQuantity();
		}
	}

	/**
	 * 新建消耗品出入库作业数
	 * 
	 * @param entity
	 * @param conn
	 */
	public void insert(FactConsumableWarehouseEntity entity, SqlSessionManager conn) {
		FactConsumableWarehouseMapper dao = conn.getMapper(FactConsumableWarehouseMapper.class);

		dao.insert(entity);
	}

	/**
	 * 更新消耗品出入库作业数
	 * 
	 * @param entity
	 * @param conn
	 */
	public void update(FactConsumableWarehouseEntity entity, SqlSessionManager conn) {
		FactConsumableWarehouseMapper dao = conn.getMapper(FactConsumableWarehouseMapper.class);

		dao.update(entity);
	}

	/**
	 * 统计耗时种别出入库作业总数
	 * 
	 * @param af_pf_key
	 * @param conn
	 * @return
	 */
	public List<FactConsumableWarehouseEntity> countQuantityOfSpecKind(String af_pf_key, SqlSession conn) {
		FactConsumableWarehouseMapper mapper = conn.getMapper(FactConsumableWarehouseMapper.class);

		FactConsumableWarehouseEntity condition = new FactConsumableWarehouseEntity();
		condition.setAf_pf_key(af_pf_key);

		List<FactConsumableWarehouseEntity> list = mapper.search(condition);

		return list;
	}

}
