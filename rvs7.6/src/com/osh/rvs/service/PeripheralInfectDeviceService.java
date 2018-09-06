package com.osh.rvs.service;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.infect.PeripheralInfectDeviceEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.infect.PeripheralInfectDeviceForm;
import com.osh.rvs.mapper.infect.PeripheralInfectDeviceMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

public class PeripheralInfectDeviceService {

	/**
	 * 周边设备点检关系一览详细
	 * 
	 * @param form
	 * @param conn
	 * @param errors
	 * @return
	 */
	public List<PeripheralInfectDeviceForm> search(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		PeripheralInfectDeviceEntity entity = new PeripheralInfectDeviceEntity();
	
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<PeripheralInfectDeviceForm> resultForms = new ArrayList<PeripheralInfectDeviceForm>();

		PeripheralInfectDeviceMapper dao = conn.getMapper(PeripheralInfectDeviceMapper.class);

		List<PeripheralInfectDeviceEntity> resultEntities = dao.search(entity);

		String currentModelId = null; String deviceTName = null; PeripheralInfectDeviceEntity currentEntity = null;
		for (PeripheralInfectDeviceEntity resultEntitiy : resultEntities) {
			if (!resultEntitiy.getModel_id().equals(currentModelId)) {
				if (currentModelId != null) {
					PeripheralInfectDeviceForm resultForm = new PeripheralInfectDeviceForm();
					BeanUtil.copyToForm(currentEntity, resultForm, CopyOptions.COPYOPTIONS_NOEMPTY);
					resultForm.setSeq("" + currentEntity.getGroup());
					resultForm.setDevice_type_name(deviceTName);
					resultForms.add(resultForm);

					deviceTName = null;
				}
				currentModelId = resultEntitiy.getModel_id();
				currentEntity = resultEntitiy;
			}
			if (deviceTName == null) {
				deviceTName = resultEntitiy.getDevice_type_name();
				if (!isEmpty(resultEntitiy.getModel_name())) {
					deviceTName += " / " + resultEntitiy.getModel_name();
				}
			} else if (!deviceTName.endsWith("等")) {
				deviceTName += " 等";
			}
		}

		if (currentModelId != null) {
			PeripheralInfectDeviceForm resultForm = new PeripheralInfectDeviceForm();
			PeripheralInfectDeviceEntity resultEntitiy = resultEntities.get(resultEntities.size() - 1);
			BeanUtil.copyToForm(resultEntitiy, resultForm, CopyOptions.COPYOPTIONS_NOEMPTY);
			resultForm.setSeq("" + resultEntitiy.getGroup());
			resultForm.setDevice_type_name(deviceTName);
			resultForms.add(resultForm);
		}

		return resultForms;
	}

	/**
	 * 周边设备点检关系一览详细
	 * 
	 * @param form
	 * @param conn
	 * @param errors
	 * @return
	 */
	public List<PeripheralInfectDeviceForm> searchDetail(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		PeripheralInfectDeviceEntity entity = new PeripheralInfectDeviceEntity();
	
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<PeripheralInfectDeviceForm> resultForms = new ArrayList<PeripheralInfectDeviceForm>();

		PeripheralInfectDeviceMapper dao = conn.getMapper(PeripheralInfectDeviceMapper.class);

		List<PeripheralInfectDeviceEntity> resultEntities = dao.search(entity);

		BeanUtil.copyToFormList(resultEntities, resultForms, CopyOptions.COPYOPTIONS_NOEMPTY,
				PeripheralInfectDeviceForm.class);

		return resultForms;
	}

	/**
	 * 执行插入
	 * @param form 提交表单
	 * @param devices 
	 * @param session 当前用户会话
	 * @param conn 数据库连接
	 * @param errors
	 * @throws Exception
	 */
	public void insert(List<PeripheralInfectDeviceEntity> devices, HttpSession session, 
			SqlSessionManager conn, List<MsgInfo> errors) throws Exception {
//		// 表单复制到数据对象
//		PeripheralInfectDeviceEntity insertBean = new PeripheralInfectDeviceEntity();
//		BeanUtil.copyToBean(form, insertBean, CopyOptions.COPYOPTIONS_NOEMPTY);

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		PeripheralInfectDeviceMapper dao = conn.getMapper(PeripheralInfectDeviceMapper.class);

		for (PeripheralInfectDeviceEntity insertBean : devices) {
			insertBean.setUpdated_by(user.getOperator_id());
			
			// 新建记录插入到数据库中
			dao.insert(insertBean);
		}
//		//获取最大的seq
//		String maxSeqNo = dao.getMaxSeq(insertBean);
//		if (!CommonStringUtil.isEmpty(maxSeqNo)) {
//			insertBean.setSeq(Integer.parseInt(maxSeqNo) + 1);
//		} else {
//			insertBean.setSeq(1);
//		}
	}

	/**
	 * 执行删除
	 * @param form 提交表单
	 * @param conn 数据库连接
	 * @param errors
	 * @throws Exception
	 */
	public void delete(ActionForm form, SqlSessionManager conn, List<MsgInfo> errors) throws Exception {
		// 表单复制到数据对象
		PeripheralInfectDeviceEntity deleteBean = new PeripheralInfectDeviceEntity();
		BeanUtil.copyToBean(form, deleteBean, null);

		// 在数据库中删除记录
		PeripheralInfectDeviceMapper dao = conn.getMapper(PeripheralInfectDeviceMapper.class);
		dao.delete(deleteBean);
	}

	/**
	 * 重复性检查
	 * @param req
	 * @param conn
	 * @param errors
	 * @return 
	 */
	public List<PeripheralInfectDeviceEntity> customValidateEdit(HttpServletRequest req, SqlSession conn,
			List<MsgInfo> errors) {
		List<PeripheralInfectDeviceEntity> rets = new AutofillArrayList<>(PeripheralInfectDeviceEntity.class);

		String modelId = req.getParameter("model_id");

		Map<String, String[]> parameterMap = req.getParameterMap();
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");

		// 整理提交数据
		for (String parameterKey : parameterMap.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("device".equals(entity)) {
					String column = m.group(2);
					Integer index = Integer.parseInt(m.group(3));

					String[] value = parameterMap.get(parameterKey);

					if ("seq".equals(column)) {
						rets.get(index).setSeq(Integer.parseInt(value[0]));
					} else if ("device_type_id".equals(column)) {
						rets.get(index).setDevice_type_id(value[0]);
					} else if ("model_name".equals(column)) {
						rets.get(index).setModel_name(value[0]);
					}
				}
			}
		}

		if (rets.size() == 0) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("model_id");
			error.setErrcode("validator.required.multidetail");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required.multidetail", "点检用设备"));
			errors.add(error);
			return null;
		}

		// 比对稽核
		Map<Integer, Set<String>> checkMap = new HashMap<Integer, Set<String>>();
		for (PeripheralInfectDeviceEntity ret : rets) {
			ret.setModel_id(modelId);
			// check
			if (!checkMap.containsKey(ret.getSeq())) {
				checkMap.put(ret.getSeq(), new HashSet<String>());
			}

			String checkKey = ret.getDevice_type_id();
			// 检查点检用设备类别
			if (isEmpty(checkKey)) {
				MsgInfo error = new MsgInfo();
				error.setComponentid("device_type_id");
				error.setErrcode("validator.required");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "点检用设备类别"));
				errors.add(error);
				return null;
			}
			if (!isEmpty(ret.getModel_name())) {
				// 设定特定型号时，附加到比对KEY
				checkKey += "@" + ret.getModel_name();
			}
			Set<String> checkSet = checkMap.get(ret.getSeq());
			// 存在完全一致
			if (checkSet.contains(checkKey)) {
				MsgInfo error = new MsgInfo();
				error.setComponentid("device_type_id");
				error.setErrcode("info.peripheralInfectDevice.duplicatedInSeq");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.peripheralInfectDevice.duplicatedInSeq", 
						CommonStringUtil.fillChar("" + ret.getSeq(), '0', 2, true)));
				errors.add(error);
				return null;
			}
			if (!isEmpty(ret.getModel_name())) {
				// 设定特定型号
				if (checkSet.contains(ret.getDevice_type_id())) {
					// 如果有无特定型号数据
					MsgInfo error = new MsgInfo();
					error.setComponentid("device_type_id");
					error.setErrcode("info.peripheralInfectDevice.duplicatedInSeq");
					error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.peripheralInfectDevice.duplicatedInSeq", 
							CommonStringUtil.fillChar("" + ret.getSeq(), '0', 2, true)));
					errors.add(error);
					return null;
				}
			} else {
				// 无特定型号
				for (String existskey : checkSet) {
					// 设定了同种类
					if (existskey.startsWith(checkKey + "@")) {
						MsgInfo error = new MsgInfo();
						error.setComponentid("device_type_id");
						error.setErrcode("info.peripheralInfectDevice.duplicatedInSeq");
						error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.peripheralInfectDevice.duplicatedInSeq", 
								CommonStringUtil.fillChar("" + ret.getSeq(), '0', 2, true)));
						errors.add(error);
						return null;
					}
				}
			}

			// 加入比对对象
			checkSet.add(checkKey);
		}

		return rets;
	}
}
