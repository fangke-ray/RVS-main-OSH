package com.osh.rvs.service.infect;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.master.DevicesManageEntity;
import com.osh.rvs.form.master.DevicesManageForm;
import com.osh.rvs.mapper.infect.DevicesDistributeMapper;

import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

/**
 * 
 * @Project rvs
 * @Package com.osh.rvs.service
 * @ClassName: DevicesDistributeService
 * @Description: 设备工具分布Service
 * @author lxb
 * @date 2014-8-28 上午11:37:39
 * 
 */
public class DevicesDistributeService {
	/**
	 * 一览
	 * @param form
	 * @param conn
	 * @return
	 */
	public List<DevicesManageForm> search(ActionForm form, SqlSession conn) {
		DevicesManageEntity entity = new DevicesManageEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		DevicesDistributeMapper dao = conn.getMapper(DevicesDistributeMapper.class);
		List<DevicesManageEntity> list = dao.search(entity);

		List<DevicesManageForm> returnFormList = new ArrayList<DevicesManageForm>();

		if (list != null && list.size() > 0) {
			// 复制数据到表单
			BeanUtil.copyToFormList(list, returnFormList, CopyOptions.COPYOPTIONS_NOEMPTY, DevicesManageForm.class);
			return returnFormList;
		} else {
			return null;
		}

	}
}
