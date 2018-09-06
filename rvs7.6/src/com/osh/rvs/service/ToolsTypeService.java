package com.osh.rvs.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.master.ToolsTypeEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.master.ToolsTypeForm;
import com.osh.rvs.mapper.master.ToolsTypeMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class ToolsTypeService {
	/**
	 * 治具品名 详细
	 * 
	 * @param toolsCheckEntity
	 * @param conn
	 * @return
	 */
	public List<ToolsTypeForm> searchToolsType(ToolsTypeEntity toolsTypeEntity, SqlSession conn) {

		ToolsTypeMapper dao = conn.getMapper(ToolsTypeMapper.class);
		
		List<ToolsTypeForm> toolsTypeForms = new ArrayList<ToolsTypeForm>();

		List<ToolsTypeEntity> toolsTypeEntities = dao.searchToolsType(toolsTypeEntity);

		BeanUtil.copyToFormList(toolsTypeEntities, toolsTypeForms, CopyOptions.COPYOPTIONS_NOEMPTY,
				ToolsTypeForm.class);

		return toolsTypeForms;
	}

	/**
	 * 插入治具品名
	 * 
	 * @param form
	 * @param conn
	 * @param errors
	 * @throws Exception
	 */
	public void insertToolsType(ActionForm form, SqlSessionManager conn,HttpSession session,List<MsgInfo> errors) throws Exception {

		ToolsTypeForm toolsTypeForm = (ToolsTypeForm) form;
		ToolsTypeEntity toolsTypeEntity = new ToolsTypeEntity();

		BeanUtil.copyToBean(toolsTypeForm, toolsTypeEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//当前操作者ID
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		toolsTypeEntity.setUpdated_by(user.getOperator_id());
		
		ToolsTypeMapper dao = conn.getMapper(ToolsTypeMapper.class);
		
		dao.insertToolsType(toolsTypeEntity);
	}

	/**
	 * 删除治具品名
	 * 
	 * @param form
	 * @param conn
	 * @param errors
	 * @throws Exception
	 */
	public void deleteToolsType(ActionForm form, SqlSessionManager conn,HttpSession session,List<MsgInfo> errors) throws Exception {

		ToolsTypeForm toolsTypeForm = (ToolsTypeForm) form;
		ToolsTypeEntity  toolsTypeEntity= new ToolsTypeEntity();

		BeanUtil.copyToBean(toolsTypeForm, toolsTypeEntity, CopyOptions.COPYOPTIONS_NOEMPTY);

		//当前操作者ID
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		toolsTypeEntity.setUpdated_by(user.getOperator_id());
		
		ToolsTypeMapper dao = conn.getMapper(ToolsTypeMapper.class);
		dao.deleteToolsType(toolsTypeEntity);
	}

	/**
	 * 更新治具品名
	 * 
	 * @param form
	 * @param conn
	 * @param errors
	 * @throws Exception
	 */
	public void updateToolsType(ActionForm form, SqlSessionManager conn,HttpSession session,List<MsgInfo> errors) throws Exception {

		ToolsTypeForm toolsTypeForm = (ToolsTypeForm) form;
		ToolsTypeEntity toolsTypeEntity = new ToolsTypeEntity();

		BeanUtil.copyToBean(toolsTypeForm, toolsTypeEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//当前操作者ID
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		toolsTypeEntity.setUpdated_by(user.getOperator_id());
		
		ToolsTypeMapper dao = conn.getMapper(ToolsTypeMapper.class);
		dao.updateToolsType(toolsTypeEntity);
	}
	
	// 查询所有的治具品名
	public String getToolsNameReferChooser(SqlSession conn) {
		List<String[]> tlist = new ArrayList<String[]>();
		ToolsTypeMapper dao = conn.getMapper(ToolsTypeMapper.class);
		List<ToolsTypeEntity> toolsTypeEntities = dao.getAllToolsName();

		List<ToolsTypeForm> toolsTypeForms = new ArrayList<ToolsTypeForm>();
		if (toolsTypeEntities != null && toolsTypeEntities.size() > 0) {
			BeanUtil.copyToFormList(toolsTypeEntities, toolsTypeForms, CopyOptions.COPYOPTIONS_NOEMPTY,
					ToolsTypeForm.class);
			for (ToolsTypeForm toolsTypeForm : toolsTypeForms) {
				String[] tline = new String[2];
				tline[0] = toolsTypeForm.getTools_type_id();
				tline[1] = toolsTypeForm.getName();
				tlist.add(tline);
			}
			String tReferChooser = CodeListUtils.getReferChooser(tlist);
			return tReferChooser;
		} else {
			return "";
		}
	}
}
