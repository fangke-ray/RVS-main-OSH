package com.osh.rvs.service.partial;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.osh.rvs.bean.partial.PartialWarehouseDetailEntity;
import com.osh.rvs.form.partial.PartialWarehouseDetailForm;
import com.osh.rvs.mapper.partial.PartialWarehouseDetailMapper;

import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

/**
 * 零件入库单明细
 * 
 * @author liuxb
 * 
 */
public class PartialWarehouseDetailService {

	/**
	 * 根据零件入库单KEY查询零件入库明细
	 * 
	 * @param key
	 * @param conn
	 * @return
	 */
	public List<PartialWarehouseDetailForm> searchByKey(String key, SqlSession conn) {
		// 数据库连接对象
		PartialWarehouseDetailMapper dao = conn.getMapper(PartialWarehouseDetailMapper.class);

		// 查询零件入库明细信息
		List<PartialWarehouseDetailEntity> list = dao.searchByKey(key);
		List<PartialWarehouseDetailForm> respList = new ArrayList<PartialWarehouseDetailForm>();

		if (list != null && list.size() > 0) {
			// 数据模型数据到表单
			BeanUtil.copyToFormList(list, respList, CopyOptions.COPYOPTIONS_NOEMPTY, PartialWarehouseDetailForm.class);
		}

		return respList;
	}

	/**
	 * 根据零件入库单KEY查询零件入库分装明细
	 * 
	 * @param key
	 * @param conn
	 * @return
	 */
	public List<PartialWarehouseDetailForm> searchUnpackByKey(String key, SqlSession conn) {
		// 数据库连接对象
		PartialWarehouseDetailMapper dao = conn.getMapper(PartialWarehouseDetailMapper.class);

		// 查询零件入库明细信息
		List<PartialWarehouseDetailEntity> list = dao.searchUnpackByKey(key);
		List<PartialWarehouseDetailForm> respList = new ArrayList<PartialWarehouseDetailForm>();

		if (list != null && list.size() > 0) {
			// 数据模型数据到表单
			BeanUtil.copyToFormList(list, respList, CopyOptions.COPYOPTIONS_NOEMPTY, PartialWarehouseDetailForm.class);
		}

		return respList;
	}

	/**
	 * 根据KEY统计每种规格种别【核对/上架】总数
	 * 
	 * @param key
	 * @param conn
	 * @return
	 */
	public List<PartialWarehouseDetailForm> countCollactionQuantityOfKind(String key, SqlSession conn) {
		// 数据库连接对象
		PartialWarehouseDetailMapper dao = conn.getMapper(PartialWarehouseDetailMapper.class);

		// 查询零件入库明细信息
		List<PartialWarehouseDetailEntity> list = dao.countCollactionQuantityOfKind(key);
		List<PartialWarehouseDetailForm> respList = new ArrayList<PartialWarehouseDetailForm>();

		if (list != null && list.size() > 0) {
			// 数据模型数据到表单
			BeanUtil.copyToFormList(list, respList, CopyOptions.COPYOPTIONS_NOEMPTY, PartialWarehouseDetailForm.class);
		}

		return respList;
	}

	/**
	 * 根据KEY统计每种规格种别【分装】总数
	 * 
	 * @param key
	 * @param conn
	 * @return
	 */
	public List<PartialWarehouseDetailForm> countUnpackQuantityOfKind(String key, SqlSession conn) {
		// 数据库连接对象
		PartialWarehouseDetailMapper dao = conn.getMapper(PartialWarehouseDetailMapper.class);

		// 查询零件入库明细信息
		List<PartialWarehouseDetailEntity> list = dao.countUnpackQuantityOfKind(key);
		List<PartialWarehouseDetailForm> respList = new ArrayList<PartialWarehouseDetailForm>();

		if (list != null && list.size() > 0) {
			// 数据模型数据到表单
			BeanUtil.copyToFormList(list, respList, CopyOptions.COPYOPTIONS_NOEMPTY, PartialWarehouseDetailForm.class);
		}

		return respList;
	}

}
