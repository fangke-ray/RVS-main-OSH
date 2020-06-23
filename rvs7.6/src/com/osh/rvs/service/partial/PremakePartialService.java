package com.osh.rvs.service.partial;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.master.PartialEntity;
import com.osh.rvs.bean.partial.PremakePartialEntity;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.partial.PremakePartialForm;
import com.osh.rvs.mapper.master.PartialMapper;
import com.osh.rvs.mapper.partial.PremakePartialMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

public class PremakePartialService {
	// NS 组件 子零件
	private static final Integer NS_COMP = 3;

	public List<PremakePartialForm> search(ActionForm form, SqlSession conn) {
		PremakePartialMapper dao = conn.getMapper(PremakePartialMapper.class);

		// 复制表单数据到对象
		PremakePartialEntity entity = new PremakePartialEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<PremakePartialEntity> list = dao.search(entity);
		List<PremakePartialForm> returnFormList = new ArrayList<PremakePartialForm>();

		// 将数据复制到表单对象
		BeanUtil.copyToFormList(list, returnFormList, null,	PremakePartialForm.class);
		return returnFormList;
	}

	public void update(ActionForm form, List<MsgInfo> errors, SqlSessionManager conn) {
		PremakePartialMapper dao = conn.getMapper(PremakePartialMapper.class);

		// 复制表单数据到对象
		PremakePartialEntity entity = new PremakePartialEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		// 数量
		if (entity.getQuantity() == 0) {
			MsgInfo msg = new MsgInfo();
			msg.setComponentid("quantity");
			msg.setErrcode("validator.invalidParam.invalidMoreThanZero");
			msg.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidMoreThanZero", "“数量”"));
			errors.add(msg);
		} else {
			dao.update(entity);
		}
	}
	
	/**
	 * 新建零件预制
	 * @param form
	 * @param errors
	 * @param conn
	 */
	public void insert(ActionForm form, List<MsgInfo> errors,SqlSessionManager conn){
		PremakePartialMapper dao = conn.getMapper(PremakePartialMapper.class);
		
		// 复制表单数据到对象
		PremakePartialEntity entity = new PremakePartialEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		// 数量
		if (entity.getQuantity() == 0) {
			MsgInfo msg = new MsgInfo();
			msg.setComponentid("quantity");
			msg.setErrcode("validator.invalidParam.invalidMoreThanZero");
			msg.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidMoreThanZero", "“数量”"));
			errors.add(msg);
		}

		//零件编码
		String code = entity.getCode();
		
		/*
		 * 如果没有选择零件编码,而是手动输入或者复制粘贴
		 * 则根据零件编码查询零件是否存在,零件不存在返回提示信息
		 */
		if(CommonStringUtil.isEmpty(entity.getPartial_id())){
			PartialMapper partialMapper = conn.getMapper(PartialMapper.class);
			List<PartialEntity> listPartial = partialMapper.getPartialByCode(code);
			
			//零件不存在
			if(listPartial.size() == 0){				
				MsgInfo msg = new MsgInfo();
				msg.setComponentid("partial_id");
				msg.setErrcode("dbaccess.recordNotExist");
				msg.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", "零件编码为:" + code));
				errors.add(msg);
				return;
			}
			
			entity.setPartial_id(listPartial.get(0).getPartial_id());
		}
		
		//检查零件预制记录是否存在
		PremakePartialEntity premakePartialEntity = dao.checkExist(entity);
		
		//零件预制记录存在,返回提示信息
		if(premakePartialEntity!=null && premakePartialEntity.getStandard_flg() != PremakePartialService.NS_COMP){
			MsgInfo msg = new MsgInfo();
			msg.setComponentid("partial");
			msg.setErrcode("dbaccess.recordDuplicated");
			msg.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordDuplicated", "零件编码为:" + code + "型号名称为:" + RvsUtils.getSnoutModels(conn).get(entity.getModel_id())));
			errors.add(msg);
			return;
		}
		
		dao.insert(entity);
	}

	public void delete(ActionForm form, SqlSessionManager conn) {
		PremakePartialMapper dao = conn.getMapper(PremakePartialMapper.class);

		// 复制表单数据到对象
		PremakePartialEntity entity = new PremakePartialEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		entity.setStandard_flg(PremakePartialService.NS_COMP);

		// 删除指定型号ID的NS组件子零件
		dao.delete(entity);
	}
}
