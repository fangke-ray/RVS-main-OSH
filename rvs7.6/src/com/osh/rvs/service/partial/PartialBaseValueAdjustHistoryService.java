package com.osh.rvs.service.partial;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.partial.PartialBaseValueAdjustHistoryEntity;
import com.osh.rvs.form.partial.PartialBaseValueAdjustHistoryForm;
import com.osh.rvs.mapper.partial.PartialBaseValueAdjustHistoryMapper;

import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class PartialBaseValueAdjustHistoryService {
	public List<PartialBaseValueAdjustHistoryForm> searchPartialBaseValueAdjustHistory(ActionForm form, SqlSession conn) {
		PartialBaseValueAdjustHistoryEntity entity = new PartialBaseValueAdjustHistoryEntity();
		// 复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		PartialBaseValueAdjustHistoryMapper dao = conn.getMapper(PartialBaseValueAdjustHistoryMapper.class);

		List<PartialBaseValueAdjustHistoryEntity> list = dao.searchPartialBaseValueAdjustHistory(entity);
		List<PartialBaseValueAdjustHistoryForm> returnFormList = new ArrayList<PartialBaseValueAdjustHistoryForm>();

		// 复制数据到表单对象
		BeanUtil.copyToFormList(list, returnFormList, CopyOptions.COPYOPTIONS_NOEMPTY,PartialBaseValueAdjustHistoryForm.class);

		return returnFormList;
	}
}
