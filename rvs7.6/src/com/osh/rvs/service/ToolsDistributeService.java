package com.osh.rvs.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.infect.ToolsDistributeEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.form.infect.ToolsDistributeForm;
import com.osh.rvs.mapper.infect.ToolsDistributeMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class ToolsDistributeService {

	/**
	 * 治具分布一览详细
	 * 
	 * @param form
	 * @param conn
	 * @param errors
	 * @return
	 */
	public List<ToolsDistributeForm> searchToolsDistribute(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		ToolsDistributeEntity toolsDistributeEntity = new ToolsDistributeEntity();
	
		BeanUtil.copyToBean(form, toolsDistributeEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<ToolsDistributeForm> toolsDistributeForms = new ArrayList<ToolsDistributeForm>();

		ToolsDistributeMapper dao = conn.getMapper(ToolsDistributeMapper.class);

		List<ToolsDistributeEntity>  distributeEntities= dao.searchToolsDistribute(toolsDistributeEntity);

		for (ToolsDistributeEntity distributeEntitie : distributeEntities) {
			ToolsDistributeForm toolsDistributeForm = new ToolsDistributeForm();
			BeanUtil.copyToForm(distributeEntitie, toolsDistributeForm, CopyOptions.COPYOPTIONS_NOEMPTY);

			boolean hasPhoto = new File(PathConsts.BASE_PATH + PathConsts.PHOTOS + "\\jig\\" + distributeEntitie.getTools_no()).exists();

			if (hasPhoto) {
				toolsDistributeForm.setPhoto_exists("exists");
			}
			toolsDistributeForms.add(toolsDistributeForm);
		}

		return toolsDistributeForms;
	}

}
