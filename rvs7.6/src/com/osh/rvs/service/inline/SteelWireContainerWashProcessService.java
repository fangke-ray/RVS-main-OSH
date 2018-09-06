package com.osh.rvs.service.inline;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.inline.SteelWireContainerWashProcessEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.inline.SteelWireContainerWashProcessForm;
import com.osh.rvs.mapper.inline.SteelWireContainerWashProcessMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

/**
 * @Description: 钢丝固定件清洗记录
 * @author liuxb
 * @date 2018-5-14 下午1:16:51
 */
public class SteelWireContainerWashProcessService {
	/**
	 * 查询钢丝固定件清洗记录
	 * @param form 表单
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public List<SteelWireContainerWashProcessForm> search(ActionForm form,SqlSession conn)throws Exception {
		SteelWireContainerWashProcessMapper dao= conn.getMapper(SteelWireContainerWashProcessMapper.class);
		
		SteelWireContainerWashProcessEntity entity = new SteelWireContainerWashProcessEntity();
		// 拷贝表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		List<SteelWireContainerWashProcessForm> retLIst = new ArrayList<SteelWireContainerWashProcessForm>();
		
		List<SteelWireContainerWashProcessEntity> list = dao.search(entity);
		if(list!=null && list.size() > 0){
			BeanUtil.copyToFormList(list, retLIst, CopyOptions.COPYOPTIONS_NOEMPTY, SteelWireContainerWashProcessForm.class);
		}
		
		return retLIst;
	}
	
	/**
	 * 新建钢丝固定件清洗记录
	 * @param form 表单
	 * @param req
	 * @param conn
	 * @throws Exception
	 */
	public void insert(ActionForm form,HttpServletRequest req,SqlSessionManager conn,List<MsgInfo> errors)throws Exception{
		SteelWireContainerWashProcessMapper dao= conn.getMapper(SteelWireContainerWashProcessMapper.class);
		
		SteelWireContainerWashProcessEntity entity = new SteelWireContainerWashProcessEntity();
		// 拷贝表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		if(entity.getQuantity() <= 0){
			MsgInfo msgInfo = new MsgInfo();
			msgInfo.setErrcode("validator.invalidParam.invalidMoreThanZero");
			msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidMoreThanZero", "清洗数量"));
			errors.add(msgInfo);
			return;
		}
		
		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		
		// 担当者
		entity.setOperator_id(user.getOperator_id());
		
		// 处理时间
		entity.setProcess_time(Calendar.getInstance().getTime());
		
		dao.insert(entity);
	}
	
	/**
	 * 更新钢丝固定件清洗记录
	 * @param form
	 * @param conn
	 * @throws Exception
	 */
	public void update(ActionForm form,SqlSessionManager conn,List<MsgInfo> errors)throws Exception{
		SteelWireContainerWashProcessMapper dao= conn.getMapper(SteelWireContainerWashProcessMapper.class);
		
		SteelWireContainerWashProcessEntity entity = new SteelWireContainerWashProcessEntity();
		// 拷贝表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		if(entity.getQuantity() <= 0){
			MsgInfo msgInfo = new MsgInfo();
			msgInfo.setErrcode("validator.invalidParam.invalidMoreThanZero");
			msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidMoreThanZero", "清洗数量"));
			errors.add(msgInfo);
			return;
		}
		
		dao.update(entity);
	}
	
}
