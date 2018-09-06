package com.osh.rvs.service.partial;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.partial.ConsumableApplicationDetailEntity;
import com.osh.rvs.form.partial.ConsumableApplicationForm;
import com.osh.rvs.mapper.partial.ConsumableApplicationDetailMapper;

import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class ConsumableApplicationDetailService {
	public List<ConsumableApplicationForm> search(ActionForm form, SqlSession conn){
		
		
		ConsumableApplicationForm consumableApplicationForm =(ConsumableApplicationForm)form;
		
		ConsumableApplicationDetailMapper dao = conn.getMapper(ConsumableApplicationDetailMapper.class);
		List<ConsumableApplicationDetailEntity> consumableApplicationDetailEntityList = dao.serach(consumableApplicationForm.getConsumable_application_key());
		
		List<ConsumableApplicationForm>  list = new ArrayList<ConsumableApplicationForm>();
		BeanUtil.copyToFormList(consumableApplicationDetailEntityList, list, CopyOptions.COPYOPTIONS_NOEMPTY, ConsumableApplicationForm.class);
		
		return list;
	}
}
