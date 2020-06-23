package com.osh.rvs.service.partial;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.osh.rvs.bean.partial.ComponentManageEntity;
import com.osh.rvs.form.partial.ComponentManageForm;
import com.osh.rvs.mapper.partial.ComponentManageMapper;

import framework.huiqing.common.util.copy.BeanUtil;

public class ComponentManageService {
	
	/**
	 * NS组件库存管理数据检索
	 * @param componentManageEntity
	 * @param conn
	 * @return
	 */
	public List<ComponentManageForm> searchComponentManage(ComponentManageEntity componentManageEntity, SqlSession conn) {
		ComponentManageMapper dao = conn.getMapper(ComponentManageMapper.class);
		
		// NS组件状态检索条件设置（String To List）
		String steps = componentManageEntity.getSearch_step();
		List<String> searchSteps = new ArrayList<String>();
		if (steps != null) {
			String[] arrStep = steps.split(",");
			for (String step : arrStep) {
				searchSteps.add(step);
			}
			componentManageEntity.setSearch_step_list(searchSteps);
		}
		List<ComponentManageForm> resultForm = new ArrayList<ComponentManageForm>();
		List<ComponentManageEntity> resultList = dao.searchComponentManage(componentManageEntity);
		BeanUtil.copyToFormList(resultList, resultForm, null, ComponentManageForm.class);
		return resultForm;
	}
	
	/** 零件集合 **/
	public List<ComponentManageForm> getPartialAutoCompletes(String code, SqlSession conn) {
		ComponentManageMapper dao = conn.getMapper(ComponentManageMapper.class);
		List<ComponentManageForm> resultForm = new ArrayList<ComponentManageForm>();
		List<ComponentManageEntity> resultList =dao.getAllPartial(code);
		BeanUtil.copyToFormList(resultList, resultForm, null, ComponentManageForm.class);
		return resultForm;
	}
	
	/**
	 * 加入消耗品库存
	 * 
	 * @param componentManageEntity
	 * @param conn
	 * @return 如果存在返回消耗品详细信息
	 */
//	public void insert(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors)
//			throws Exception {
//		ComponentManageEntity insertBean = new ComponentManageEntity();
//		BeanUtil.copyToBean(form, insertBean, null);
//
//		/* Consumable_manage表插入数据 */
//		ComponentManageMapper dao = conn.getMapper(ComponentManageMapper.class);
//		dao.insertConsumable(insertBean);
//
//
//	}
//	

}
