package com.osh.rvs.service.qf;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.qf.SteelWireContainerWashProcessEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.qf.SteelWireContainerWashProcessForm;
import com.osh.rvs.mapper.qf.SteelWireContainerWashProcessMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

/**
 * @Description: 物料加工
 * @author liuxb
 * @date 2018-5-14 下午1:16:51
 */
public class SteelWireContainerWashProcessService {
	/**
	 * 查询物料加工
	 * 
	 * @param form 表单
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public List<SteelWireContainerWashProcessForm> search(ActionForm form, SqlSession conn) throws Exception {
		SteelWireContainerWashProcessMapper dao = conn.getMapper(SteelWireContainerWashProcessMapper.class);

		SteelWireContainerWashProcessEntity entity = new SteelWireContainerWashProcessEntity();
		// 拷贝表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<SteelWireContainerWashProcessForm> retLIst = new ArrayList<SteelWireContainerWashProcessForm>();

		List<SteelWireContainerWashProcessEntity> list = dao.search(entity);
		if (list != null && list.size() > 0) {
			BeanUtil.copyToFormList(list, retLIst, CopyOptions.COPYOPTIONS_NOEMPTY, SteelWireContainerWashProcessForm.class);
		}

		return retLIst;
	}

	/**
	 * 新建物料加工记录
	 * 
	 * @param form 表单
	 * @param req
	 * @param conn
	 * @throws Exception
	 */
	public void insert(ActionForm form, HttpServletRequest req, SqlSessionManager conn, List<MsgInfo> errors) throws Exception {
		SteelWireContainerWashProcessMapper dao = conn.getMapper(SteelWireContainerWashProcessMapper.class);

		SteelWireContainerWashProcessEntity entity = new SteelWireContainerWashProcessEntity();
		// 拷贝表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		// 作业数量
		Integer quantity = entity.getQuantity();

		if (quantity <= 0) {
			MsgInfo msgInfo = new MsgInfo();
			msgInfo.setErrcode("validator.invalidParam.invalidMoreThanZero");
			msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidMoreThanZero", "作业数量"));
			errors.add(msgInfo);
			return;
		}

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		// 担当者
		entity.setOperator_id(user.getOperator_id());

		dao.insert(entity);
	}

	/**
	 * 更新物料加工记录
	 * 
	 * @param form
	 * @param conn
	 * @throws Exception
	 */
	public void update(ActionForm form, SqlSessionManager conn, List<MsgInfo> errors) throws Exception {
		SteelWireContainerWashProcessMapper dao = conn.getMapper(SteelWireContainerWashProcessMapper.class);

		SteelWireContainerWashProcessEntity entity = new SteelWireContainerWashProcessEntity();
		// 拷贝表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		if (entity.getQuantity() <= 0) {
			MsgInfo msgInfo = new MsgInfo();
			msgInfo.setErrcode("validator.invalidParam.invalidMoreThanZero");
			msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.invalidParam.invalidMoreThanZero", "清洗数量"));
			errors.add(msgInfo);
			return;
		}

		dao.update(entity);
	}
	
	
	/**
	 * 分配维修对象
	 * @param form
	 * @param conn
	 * @throws Exception
	 */
	public void updateMaterial(ActionForm form, SqlSessionManager conn) throws Exception {
		SteelWireContainerWashProcessMapper dao = conn.getMapper(SteelWireContainerWashProcessMapper.class);

		SteelWireContainerWashProcessEntity entity = new SteelWireContainerWashProcessEntity();
		// 拷贝表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		dao.updateMaterial(entity);
	}
	
	
	public List<MaterialForm> searchMaterial(SqlSession conn) throws Exception {
		SteelWireContainerWashProcessMapper dao = conn.getMapper(SteelWireContainerWashProcessMapper.class);

		List<MaterialForm> retLIst = new ArrayList<MaterialForm>();

		List<MaterialEntity> list = dao.searchMaterial();
		if (list != null && list.size() > 0) {
			BeanUtil.copyToFormList(list, retLIst, CopyOptions.COPYOPTIONS_NOEMPTY, MaterialForm.class);
		}

		return retLIst;
	}

}
