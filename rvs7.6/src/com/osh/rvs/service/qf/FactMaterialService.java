package com.osh.rvs.service.qf;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.qf.AfProductionFeatureEntity;
import com.osh.rvs.bean.qf.FactMaterialEntity;
import com.osh.rvs.form.qf.FactMaterialForm;
import com.osh.rvs.mapper.qf.FactMaterialMapper;
import com.osh.rvs.service.AcceptFactService;

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
		FactMaterialEntity entity = new FactMaterialEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<FactMaterialForm> respFormList = new ArrayList<FactMaterialForm>();
		List<FactMaterialEntity> list = searchEntities(entity, conn);

		if (list != null && list.size() > 0) {
			BeanUtil.copyToFormList(list, respFormList, CopyOptions.COPYOPTIONS_NOEMPTY, FactMaterialForm.class);
		}

		return respFormList;
	}
	public List<FactMaterialEntity> searchEntities(FactMaterialEntity condition, SqlSession conn) {
		FactMaterialMapper dao = conn.getMapper(FactMaterialMapper.class);
		
		List<FactMaterialEntity> list = dao.search(condition);

		return list;
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
	/**
	 * 新建现品作业维修对象
	 * @param operator_id 作业人员 ID
	 * @param material_id 维修对象 ID
	 * @param check_flg 0 = 不需检查/ 1=检查作业类别中是否存在/ 2=检查作业时段内是否存在
	 * @param conn
	 * @return 插入件数
	 */
	public int insertFactMaterial(String operator_id, String material_id,
			int check_flg, SqlSessionManager conn) {
		if (material_id == null) return 0;

		// 根据操作者ID查找未结束作业信息
		AcceptFactService acceptFactService = new AcceptFactService();
		AfProductionFeatureEntity afpfEntity = acceptFactService.getUnFinishEntity(operator_id, conn);
		if (afpfEntity == null) return 0;

		// 作业KEY
		FactMaterialEntity entity = new FactMaterialEntity();
		entity.setMaterial_id(material_id);

		FactMaterialMapper dao = conn.getMapper(FactMaterialMapper.class);

		int exists = 0;
		if (check_flg == 1) {
			entity.setProduction_type(afpfEntity.getProduction_type());
			exists = dao.search(entity).size();
		} else if (check_flg == 2) {
			entity.setAf_pf_key(afpfEntity.getAf_pf_key());
			exists = dao.search(entity).size();
		}

		// 已经存在时不建立
		if (exists > 0) return 0;

		// 新建
		entity.setAf_pf_key(afpfEntity.getAf_pf_key());
		entity.setProduction_type(afpfEntity.getProduction_type());
		dao.insert(entity);

		return 1;
	}

	/**
	 * 当日完成实物受理件数
	 * @param form
	 * @param conn
	 * @return
	 */
	public int countFinished(ActionForm form,SqlSession conn){
		FactMaterialMapper dao = conn.getMapper(FactMaterialMapper.class);
		
		FactMaterialEntity entity = new FactMaterialEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		int count = dao.countFinished(entity) + dao.countTempFinished(entity);
		
		return count;
	}
}
