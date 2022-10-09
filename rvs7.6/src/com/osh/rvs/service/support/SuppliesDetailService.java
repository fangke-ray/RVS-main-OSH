package com.osh.rvs.service.support;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.master.OperatorEntity;
import com.osh.rvs.bean.support.SuppliesDetailEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.support.SuppliesDetailForm;
import com.osh.rvs.mapper.master.OperatorMapper;
import com.osh.rvs.mapper.support.SuppliesDetailMapper;
import com.osh.rvs.service.RoleService;

import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

/**
 * 
 * @Description 物品申购
 * @author dell
 * @date 2021-12-3 下午1:33:43
 */
public class SuppliesDetailService {
	/**
	 * 检索
	 * 
	 * @param form
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public List<SuppliesDetailForm> search(ActionForm form, SqlSession conn) throws Exception {
		SuppliesDetailMapper dao = conn.getMapper(SuppliesDetailMapper.class);

		SuppliesDetailEntity entity = new SuppliesDetailEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<SuppliesDetailEntity> list = dao.search(entity);
		List<SuppliesDetailForm> respList = new ArrayList<SuppliesDetailForm>();
		BeanUtil.copyToFormList(list, respList, CopyOptions.COPYOPTIONS_NOEMPTY, SuppliesDetailForm.class);

		return respList;
	}

	/**
	 * 新建
	 * 
	 * @param form
	 * @param conn
	 * @throws Exception
	 */
	public void insert(HttpServletRequest req, ActionForm form, SqlSessionManager conn) throws Exception {
		SuppliesDetailMapper dao = conn.getMapper(SuppliesDetailMapper.class);

		SuppliesDetailEntity entity = new SuppliesDetailEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		if (entity.getComments() != null && entity.getComments().length() <= 64) {
			entity.setSupplier(entity.getComments());
		}

		Calendar now = Calendar.getInstance();
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

		// 课室
		entity.setSection_id(user.getSection_id());
		// 申请者
		entity.setApplicator_id(user.getOperator_id());
		// 申请日期
		entity.setApplicate_date(now.getTime());

		dao.insert(entity);
	}

	/**
	 * 获取物品申购明细
	 * 
	 * @param suppliesKey
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public SuppliesDetailForm getSuppliseDetailByKey(String suppliesKey, SqlSession conn) throws Exception {
		SuppliesDetailMapper dao = conn.getMapper(SuppliesDetailMapper.class);

		SuppliesDetailEntity entity = dao.getDetailByKey(suppliesKey);

		SuppliesDetailForm respForm = null;
		if (entity != null) {
			respForm = new SuppliesDetailForm();
			BeanUtil.copyToForm(entity, respForm, CopyOptions.COPYOPTIONS_NOEMPTY);

			OperatorMapper operatorMapper = conn.getMapper(OperatorMapper.class);
			OperatorEntity operatorEntity = operatorMapper.getOperatorByID(respForm.getApplicator_id());

			RoleService roleService = new RoleService();
			List<Integer> privacies = roleService.getUserPrivacies(operatorEntity.getRole_id(), conn);

			// 线长人员数据
			if (privacies.contains(RvsConsts.PRIVACY_LINE) 
					|| privacies.contains(RvsConsts.PRIVACY_RECEPT_EDIT) 
					|| privacies.contains(RvsConsts.PRIVACY_QA_MANAGER)
					|| privacies.contains(RvsConsts.PRIVACY_TECHNOLOGY) 
					|| privacies.contains(RvsConsts.PRIVACY_TECHNICAL_MANAGE)) {
				respForm.setIsLiner("1");
			}
		}

		return respForm;
	}

	/**
	 * 删除申购明细
	 * 
	 * @param form
	 * @param conn
	 * @throws Exception
	 */
	public void delete(ActionForm form, SqlSessionManager conn) throws Exception {
		SuppliesDetailMapper dao = conn.getMapper(SuppliesDetailMapper.class);

		SuppliesDetailEntity entity = new SuppliesDetailEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		dao.delete(entity);
	}

	/**
	 * 查询出所有'物品申购单 Key'为空，且有'上级确认者 ID'的记录
	 * 
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public List<SuppliesDetailForm> searchComfirmAndNoOrderKey(SqlSession conn) throws Exception {
		SuppliesDetailMapper dao = conn.getMapper(SuppliesDetailMapper.class);

		List<SuppliesDetailEntity> list = dao.searchComfirmAndNoOrderKey();
		List<SuppliesDetailForm> respList = new ArrayList<SuppliesDetailForm>();
		BeanUtil.copyToFormList(list, respList, CopyOptions.COPYOPTIONS_NOEMPTY, SuppliesDetailForm.class);

		return respList;
	}

	/**
	 * 确认、驳回
	 * 
	 * @param req
	 * @param form
	 * @param conn
	 * @throws Exception
	 */
	public void confirm(HttpServletRequest req, ActionForm form, SqlSessionManager conn) throws Exception {
		SuppliesDetailMapper dao = conn.getMapper(SuppliesDetailMapper.class);

		SuppliesDetailEntity entity = new SuppliesDetailEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		// 当前登录者
		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);
		// 确认者ID
		entity.setConfirmer_id(user.getOperator_id());

		SuppliesDetailForm suppliesDetailForm = (SuppliesDetailForm) form;
		// 确认标记
		String confirmFlg = suppliesDetailForm.getConfirm_flg();

		// 驳回
		if ("NG".equals(confirmFlg)) {
			// '物品申购单 Key'设定为0。
			entity.setOrder_key("0");
		}

		dao.confirm(entity);
	}

	/**
	 * 加入订购单
	 * 
	 * @param form
	 * @param conn
	 * @throws Exception
	 */
	public void addOrder(ActionForm form, SqlSessionManager conn) throws Exception {
		SuppliesDetailMapper dao = conn.getMapper(SuppliesDetailMapper.class);

		SuppliesDetailEntity entity = new SuppliesDetailEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		dao.updateOrderKey(entity);
	}

	/**
	 * 更新申购明细
	 * 
	 * @param form
	 * @param conn
	 * @throws Exception
	 */
	public void updateApplication(ActionForm form, SqlSessionManager conn) throws Exception {
		SuppliesDetailMapper dao = conn.getMapper(SuppliesDetailMapper.class);

		SuppliesDetailEntity entity = new SuppliesDetailEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		dao.updateApplication(entity);
	}

	/**
	 * 查询出所有待收货申购记录
	 * 
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public List<SuppliesDetailForm> searchWaittingRecept(SqlSession conn) throws Exception {
		SuppliesDetailMapper dao = conn.getMapper(SuppliesDetailMapper.class);

		List<SuppliesDetailEntity> list = dao.searchWaittingRecept();
		List<SuppliesDetailForm> respList = new ArrayList<SuppliesDetailForm>();
		BeanUtil.copyToFormList(list, respList, CopyOptions.COPYOPTIONS_NOEMPTY, SuppliesDetailForm.class);

		return respList;
	}

	/**
	 * 收货
	 * @param form
	 * @param conn
	 * @throws Exception
	 */
	public void recept(ActionForm form,SqlSessionManager conn) throws Exception {
		SuppliesDetailMapper dao = conn.getMapper(SuppliesDetailMapper.class);
		
		SuppliesDetailEntity entity = new SuppliesDetailEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//收货日期为系统日期
		entity.setRecept_date(Calendar.getInstance().getTime());
		
		dao.updateReceptDate(entity);
	}
	
	/**
	 * 验收
	 * @param supplies_keys
	 * @param conn
	 * @throws Exception
	 */
	public void inlineRecept(String supplies_keys, SqlSessionManager conn) throws Exception {
		SuppliesDetailMapper dao = conn.getMapper(SuppliesDetailMapper.class);
		
		String[] arrSuppliesKeys = supplies_keys.split(";");
		//验收日期为系统日期
		dao.updateInlineReceptDate(arrSuppliesKeys);
	}
	
	/**
	 * 更新发票号码
	 * @param form
	 * @param conn
	 * @throws Exception
	 */
	public void updateInvoiceNo(ActionForm form,SqlSessionManager conn) throws Exception {
		SuppliesDetailMapper dao = conn.getMapper(SuppliesDetailMapper.class);
		
		SuppliesDetailEntity entity = new SuppliesDetailEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		dao.updateInvoiceNo(entity);
	}

	/**
	 * 根据物品申购单Key查询申购明细
	 * @param orderKey 物品申购单Key
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public List<SuppliesDetailForm> getSuppliseDetailByOrderKey(String orderKey, SqlSession conn) throws Exception {
		SuppliesDetailMapper dao = conn.getMapper(SuppliesDetailMapper.class);

		List<SuppliesDetailEntity> list = dao.getDetailByOrderKey(orderKey);
		List<SuppliesDetailForm> respFormList = new ArrayList<SuppliesDetailForm>();
		
		BeanUtil.copyToFormList(list, respFormList, CopyOptions.COPYOPTIONS_NOEMPTY, SuppliesDetailForm.class);

		return respFormList;
	}

	/**
	 * 取得本人可签收申购品一览
	 * 经理权限是本人确认者
	 * 无经理权限是本人申购者
	 * 
	 * @param user 登录会话用户
	 * @param conn
	 * @return
	 */
	public List<SuppliesDetailForm> getInlineRecept(LoginData user, SqlSession conn) {
		SuppliesDetailMapper mapper = conn.getMapper(SuppliesDetailMapper.class);

		SuppliesDetailEntity condition = new SuppliesDetailEntity();

		if (user.getPrivacies().contains(RvsConsts.PRIVACY_PROCESSING)) {
			condition.setConfirmer_id(user.getOperator_id());
		} else {
			condition.setApplicator_id(user.getOperator_id());
		}

		List<SuppliesDetailForm> respFormList = new ArrayList<SuppliesDetailForm>();
		List<SuppliesDetailEntity> list = mapper.getInlineRecept(condition);

		BeanUtil.copyToFormList(list, respFormList, CopyOptions.COPYOPTIONS_NOEMPTY, SuppliesDetailForm.class);

		return respFormList;
	}

	/**
	 * 将申请通知发给上级，待确认
	 * @param keys
	 */
	public void sendPostMessage(ActionForm form, LoginData sender) {
		SuppliesDetailForm sdForm = (SuppliesDetailForm) form; 
		RvsUtils.sendTrigger("http://localhost:8080/rvspush/trigger/supplies_apply/" + sender.getOperator_id()
				+ "/" + sdForm.getConfirmer_id() + "/" + sdForm.getSupplies_key()
				);
	}

	/**
	 * 将到货通知发给申请者，待验收
	 * @param keys
	 */
	public void sendMailToApplier(List<String> keys) {
		RvsUtils.sendTrigger("http://localhost:8080/rvspush/trigger/supplies_recieve/0/" 
				+ CommonStringUtil.joinBy(",", keys.toArray(new String[keys.size()]))
				);
	}

	/**
	 * 加急申请时，通知给支援人员
	 * @param keys
	 */
	public void sendUrgentNotice(LoginData sender) {
		RvsUtils.sendTrigger("http://localhost:8080/rvspush/trigger/supplies_urgent/" + sender.getOperator_id()
				+ "/" + sender.getOperator_id() + "/" + sender.getOperator_id()
				);
	}
}