package com.osh.rvs.service.partial;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.master.PartialEntity;
import com.osh.rvs.bean.master.PartialPositionEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.master.PartialPositionForm;
import com.osh.rvs.mapper.master.PartialMapper;
import com.osh.rvs.mapper.master.PartialPositionMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

public class PartialPositionService {
	public List<PartialPositionForm> searchPartialPosition(PartialPositionEntity partialPositionEntity, SqlSession conn) {
		PartialPositionMapper dao = conn.getMapper(PartialPositionMapper.class);
		List<PartialPositionForm> resultForm = new ArrayList<PartialPositionForm>();
		List<PartialPositionEntity> resultList = dao.searchPartialPosition(partialPositionEntity);
		BeanUtil.copyToFormList(resultList, resultForm, null, PartialPositionForm.class);
		return resultForm;
	}

	public void updatePartialPosition(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors)
			throws Exception {
		PartialPositionMapper dao = conn.getMapper(PartialPositionMapper.class);
		PartialPositionEntity partialPositionEntity = new PartialPositionEntity();
		BeanUtil.copyToBean(form, partialPositionEntity, null);
		//partialPositionEntity.setNew_partial_id(partialPositionEntity.getPartial_id());
		
		//设置new_partial_id =0，零件选中进行废止
		String new_partial_id ="0";
		partialPositionEntity.setNew_partial_id(new_partial_id);
		
		// 更新定位表
		dao.updatePartialPosition(partialPositionEntity);

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		partialPositionEntity.setUpdated_by(user.getOperator_id());
		// 插入改废订历史管理表
		dao.insertPartialWasteModifyHistory(partialPositionEntity);
	}

	public void updatePartialPositionNewPartial(ActionForm form, HttpSession session, SqlSessionManager conn,
			List<MsgInfo> errors) throws Exception {
		PartialPositionMapper dao = conn.getMapper(PartialPositionMapper.class);
		PartialPositionEntity partialpositionEntity = new PartialPositionEntity();
		BeanUtil.copyToBean(form, partialpositionEntity, null);
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		partialpositionEntity.setUpdated_by(user.getOperator_id());

		PartialMapper partialDao = conn.getMapper(PartialMapper.class);
		PartialEntity partialEntity = new PartialEntity();
		partialEntity.setCode(partialpositionEntity.getCode());
	
		// 更新数据(更新有效截止时间和)
		dao.updatePartialPositionOldHistoryLimitDate(partialpositionEntity);

		// 插入历史管理表
		List<String> list = partialDao.checkPartial(partialEntity);
		String new_partial_id = list.get(0);
		partialpositionEntity.setNew_partial_id(new_partial_id);
		dao.insertPartialWasteModifyHistory(partialpositionEntity);
		partialpositionEntity.setPartial_id(new_partial_id);
		// 插入定位表
		dao.insertPartialPosition(partialpositionEntity);

	}
	/*验证型号*/
	public void modelNameValidate(PartialPositionEntity partialPositionEntity , List<MsgInfo> errors){
		if(CommonStringUtil.isEmpty(partialPositionEntity.getModel_id())){
			MsgInfo error = new MsgInfo();
			error.setComponentid("model_id");
			error.setErrcode("validator.required");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "零件型号",
					partialPositionEntity.getCode(), "零件"));
			errors.add(error);
		}
	}
	/*验证零件是否已经存在*/
	public void customValidate(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		PartialPositionEntity partialPositionEntity = new PartialPositionEntity();
		PartialPositionMapper dao = conn.getMapper(PartialPositionMapper.class);
        BeanUtil.copyToBean(form, partialPositionEntity,(new CopyOptions()).include("history_limit_date","code","model_id", "position_id"));
        
        PartialMapper partialDao = conn.getMapper(PartialMapper.class);
        PartialEntity partialEntity = new PartialEntity();
        partialEntity.setCode(partialPositionEntity.getCode());
        List<String> partialIDList  =partialDao.checkPartial(partialEntity);
        if(partialIDList.size()>0){
        	String partial_id = partialIDList.get(0);
        	partialPositionEntity.setPartial_id(partial_id);
        }
        List<String> result =  dao.checkPartialPosition(partialPositionEntity);
		if (result != null && result.size() > 0) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("code");
			error.setErrcode("dbaccess.columnNotUnique");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.columnNotUnique", "零件编码",
					partialPositionEntity.getCode(), "零件"));
			errors.add(error);
		}
	}

	/*验证零件是否已经存在*/
	public void customValidateChange(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		PartialPositionEntity partialPositionEntity = new PartialPositionEntity();
		PartialPositionMapper dao = conn.getMapper(PartialPositionMapper.class);
        BeanUtil.copyToBean(form, partialPositionEntity,(new CopyOptions()).include("history_limit_date","code","model_id", "position_id"));
        
        PartialMapper partialDao = conn.getMapper(PartialMapper.class);
        PartialEntity partialEntity = new PartialEntity();
        partialEntity.setCode(partialPositionEntity.getCode());
        List<String> partialIDList  =partialDao.checkPartial(partialEntity);
        if(partialIDList.size()>0){
        	String partial_id = partialIDList.get(0);
        	partialPositionEntity.setPartial_id(partial_id);
        }
        List<String> result =  dao.checkPartialPosition(partialPositionEntity);
		if (result != null && result.size() > 0) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("code");
			error.setErrcode("dbaccess.columnNotUnique");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.columnNotUnique", "零件编码",
					partialPositionEntity.getCode(), "零件"));
			errors.add(error);
		}
	}

}
