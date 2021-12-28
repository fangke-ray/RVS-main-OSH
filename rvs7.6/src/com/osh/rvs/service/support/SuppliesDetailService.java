package com.osh.rvs.service.support;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.master.OperatorEntity;
import com.osh.rvs.bean.support.SuppliesDetailEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.support.SuppliesDetailForm;
import com.osh.rvs.mapper.master.OperatorMapper;
import com.osh.rvs.mapper.support.SuppliesDetailMapper;
import com.osh.rvs.service.RoleService;

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
	 * @param form
	 * @param conn
	 * @throws Exception
	 */
	public void inlineRecept(ActionForm form,SqlSessionManager conn) throws Exception {
		SuppliesDetailMapper dao = conn.getMapper(SuppliesDetailMapper.class);
		
		SuppliesDetailEntity entity = new SuppliesDetailEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//验收日期为系统日期
		entity.setInline_recept_date(Calendar.getInstance().getTime());
		
		dao.updateInlineReceptDate(entity);
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
}