package com.osh.rvs.service.manage;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.PostMessageEntity;
import com.osh.rvs.bean.master.OperatorEntity;
import com.osh.rvs.bean.master.PcsFixOrderEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.manage.PcsFixOrderForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.data.PostMessageMapper;
import com.osh.rvs.mapper.inline.ProductionFeatureMapper;
import com.osh.rvs.mapper.manage.PcsFixOrderMapper;
import com.osh.rvs.mapper.master.OperatorMapper;
import com.osh.rvs.service.PostMessageService;

import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class PcsFixOrderService {

	/**
	 * 等待处理的工程检查票修改请求
	 * @param conn
	 * @return
	 * @throws Exception 
	 */
	public List<PcsFixOrderForm> findWaiting(SqlSession conn) throws Exception {
		PcsFixOrderMapper mapper = conn.getMapper(PcsFixOrderMapper.class);

		PcsFixOrderEntity condition = new PcsFixOrderEntity();
		// 状态为等待处理
		condition.setStatus(0);

		// 查询
		List<PcsFixOrderEntity> retBeans0 = mapper.searchPcsFixOrders(condition);

		condition.setStatus(5);
		List<PcsFixOrderEntity> retBeans5 = mapper.searchPcsFixOrders(condition);
		retBeans0.addAll(retBeans5);
		for (PcsFixOrderEntity retBean5 : retBeans5) {
			retBean5.setComment(retBean5.getComment().substring(12));
		}

		List<PcsFixOrderForm> ret = new ArrayList<PcsFixOrderForm>();
		BeanUtil.copyToFormList(retBeans0, ret, CopyOptions.COPYOPTIONS_NOEMPTY, PcsFixOrderForm.class);
		return ret;
	}

	public PcsFixOrderForm getRet5(String key, SqlSession conn) throws Exception {
		PcsFixOrderMapper mapper = conn.getMapper(PcsFixOrderMapper.class);

		PcsFixOrderEntity retBean5 = mapper.getPcsFixOrder(key);
		if (retBean5 != null) {
			PcsFixOrderForm ret = new PcsFixOrderForm();
			BeanUtil.copyToForm(retBean5, ret, CopyOptions.COPYOPTIONS_NOEMPTY);
			return ret;
		} else {
			return null;
		}
	}

	public List<PcsFixOrderForm> findFinished(ActionForm form, SqlSession conn) {
		List<PcsFixOrderForm> ret = new ArrayList<PcsFixOrderForm>();
		return ret;
	}

	/**
	 * 建立工程检查票修正申请数据
	 * @param form
	 * @param session
	 * @param conn
	 * @throws Exception 
	 */
	public void create(ActionForm form, HttpSession session, SqlSessionManager conn) throws Exception {
		// 画面表单赋值
		PcsFixOrderEntity entity = new PcsFixOrderEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		// 发送人设定为登录用户
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		entity.setSender_id(user.getOperator_id());
		entity.setStatus(0);

		// 建立申请
		PcsFixOrderMapper mapper = conn.getMapper(PcsFixOrderMapper.class);
		mapper.createPcsFixOrder(entity);

		// 信息推送
		PostMessageMapper pmMapper = conn.getMapper(PostMessageMapper.class);
		PostMessageEntity pmEntity = new PostMessageEntity();
		pmEntity.setSender_id(user.getOperator_id());
		pmEntity.setContent(user.getName() + " 发送了一个工程检查票修改的请求，请确认！");
		pmEntity.setLevel(1);
		pmEntity.setReason(PostMessageService.PCS_FIX_ORDER);

		pmMapper.createPostMessage(pmEntity);

		CommonMapper commonMapper = conn.getMapper(CommonMapper.class);
		String lastInsertID = commonMapper.getLastInsertID();
		pmEntity.setPost_message_id(lastInsertID);

		// 查询系统管理员
		OperatorMapper oMapper = conn.getMapper(OperatorMapper.class);

		List<OperatorEntity> systemmers = oMapper.getOperatorWithRole("00000000012");

		for (OperatorEntity systemmer : systemmers) {
			pmEntity.setReceiver_id(systemmer.getOperator_id());
			pmMapper.createPostMessageSendation(pmEntity);
		}
	}

	public void createPositionClean(ActionForm form, HttpSession session, SqlSessionManager conn) throws Exception {
		// 画面表单赋值
		PcsFixOrderEntity entity = new PcsFixOrderEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		// 发送人设定为登录用户
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		entity.setSender_id(user.getOperator_id());
		entity.setStatus(5);
		entity.setComment(entity.getPosition_id() + "|" + entity.getComment());

		// 建立申请
		PcsFixOrderMapper mapper = conn.getMapper(PcsFixOrderMapper.class);
		mapper.createPcsFixOrder(entity);

		// 信息推送
		PostMessageMapper pmMapper = conn.getMapper(PostMessageMapper.class);
		PostMessageEntity pmEntity = new PostMessageEntity();
		pmEntity.setSender_id(user.getOperator_id());
		pmEntity.setContent(user.getName() + " 发送了一个工位作业清除的请求，请确认！");
		pmEntity.setLevel(1);
		pmEntity.setReason(2);

		pmMapper.createPostMessage(pmEntity);

		CommonMapper commonMapper = conn.getMapper(CommonMapper.class);
		String lastInsertID = commonMapper.getLastInsertID();
		pmEntity.setPost_message_id(lastInsertID);

		// 查询系统管理员
		OperatorMapper oMapper = conn.getMapper(OperatorMapper.class);

		List<OperatorEntity> systemmers = oMapper.getOperatorWithRole("00000000012");

		for (OperatorEntity systemmer : systemmers) {
			pmEntity.setReceiver_id(systemmer.getOperator_id());
			pmMapper.createPostMessageSendation(pmEntity);
		}
	}

	public void resolvePc(HttpServletRequest req, LoginData user, SqlSessionManager conn, String dispatch) throws Exception {

		PcsFixOrderMapper pfoMapper = conn.getMapper(PcsFixOrderMapper.class);

		ProductionFeatureMapper pfMapper = conn.getMapper(ProductionFeatureMapper.class);

		String pcs_fix_order_key = req.getParameter("pcs_fix_order_key");

		if (("pc_rewait").equals(dispatch)) {
			// 重新作业
		} else if (("pc_delete").equals(dispatch)) {
			// 删除工位
			// pfMapper
		} else if (("pc_cancel").equals(dispatch)) {
			// 取消指派
		}

		// 处理完毕
		PcsFixOrderEntity resolveBean = new PcsFixOrderEntity();
		if ("pc_no".equals(dispatch)) {
			resolveBean.setStatus(7);
		} else {
			resolveBean.setStatus(6);
		}
		resolveBean.setPcs_fix_order_key(pcs_fix_order_key);
		pfoMapper.resolvePcsFixOrder(resolveBean);

		// 信息推送
		PostMessageMapper pmMapper = conn.getMapper(PostMessageMapper.class);
		PostMessageEntity pmEntity = new PostMessageEntity();
		pmEntity.setSender_id(user.getOperator_id());
		pmEntity.setContent("你的工位作业修改已处理，请确认！");
		pmEntity.setLevel(1);
		pmEntity.setReason(2);

		pmMapper.createPostMessage(pmEntity);

		CommonMapper commonMapper = conn.getMapper(CommonMapper.class);
		String lastInsertID = commonMapper.getLastInsertID();
		pmEntity.setPost_message_id(lastInsertID);

		// 回信给申请者
		PcsFixOrderEntity pcsFixOrder = pfoMapper.getPcsFixOrder(pcs_fix_order_key);
		if (pcsFixOrder != null && !isEmpty(pcsFixOrder.getSender_id())) {
			pmEntity.setReceiver_id(pcsFixOrder.getSender_id());
			pmMapper.createPostMessageSendation(pmEntity);
		}
	}
}
