package com.osh.rvs.service.manage;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.manage.ModelLevelSetHistoryEntity;
import com.osh.rvs.form.manage.ModelLevelSetHistoryForm;
import com.osh.rvs.mapper.manage.ModelLevelSetHistoryMapping;

import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class ModelLevelSetHistoryService {
	public List<ModelLevelSetHistoryForm> searchModelLevelSetHistory(ActionForm form, SqlSession conn) {
		ModelLevelSetHistoryEntity entity = new ModelLevelSetHistoryEntity();
		// 复制表单到数据对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		ModelLevelSetHistoryMapping dao = conn.getMapper(ModelLevelSetHistoryMapping.class);

		List<ModelLevelSetHistoryEntity> list = dao.searchModelLevelSetHistoty(entity);
		List<ModelLevelSetHistoryForm> returnList = new ArrayList<ModelLevelSetHistoryForm>();

		// 复制数据到表单对象
		BeanUtil.copyToFormList(list, returnList, CopyOptions.COPYOPTIONS_NOEMPTY, ModelLevelSetHistoryForm.class);
		return returnList;

	}
}
