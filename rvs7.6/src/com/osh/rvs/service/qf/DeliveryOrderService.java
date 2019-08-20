package com.osh.rvs.service.qf;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.mapper.qf.FactMaterialMapper;

import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class DeliveryOrderService {

	/**
	 * 待出货单
	 * 
	 * @param conn
	 * @return
	 */
	public List<MaterialForm> getWaitings(SqlSession conn) {
		FactMaterialMapper dao = conn.getMapper(FactMaterialMapper.class);

		List<MaterialForm> respFormList = new ArrayList<MaterialForm>();
		List<MaterialEntity> list = dao.getDeliveryOrderWaitings();

		if (list != null && list.size() > 0) {
			BeanUtil.copyToFormList(list, respFormList, CopyOptions.COPYOPTIONS_NOEMPTY, MaterialForm.class);
		}

		return respFormList;
	}

	/**
	 * 今日出货单
	 * 
	 * @param conn
	 * @return
	 */
	public List<MaterialForm> getFinished(SqlSession conn) {
		FactMaterialMapper dao = conn.getMapper(FactMaterialMapper.class);

		List<MaterialForm> respFormList = new ArrayList<MaterialForm>();
		List<MaterialEntity> list = dao.getDeliveryOrderFinished();

		if (list != null && list.size() > 0) {
			BeanUtil.copyToFormList(list, respFormList, CopyOptions.COPYOPTIONS_NOEMPTY, MaterialForm.class);
		}

		return respFormList;
	}

}
