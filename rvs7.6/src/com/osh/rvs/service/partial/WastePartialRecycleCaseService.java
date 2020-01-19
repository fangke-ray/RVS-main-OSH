package com.osh.rvs.service.partial;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.partial.WastePartialRecycleCaseEntity;
import com.osh.rvs.form.partial.WastePartialRecycleCaseForm;
import com.osh.rvs.mapper.partial.WastePartialRecycleCaseMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

/**
 * 废弃零件回收箱
 * 
 * @Description
 * @author dell
 * @date 2019-12-26 下午4:37:45
 */
public class WastePartialRecycleCaseService {
	/**
	 * 检索
	 * 
	 * @param form
	 * @param conn
	 * @return
	 */
	public List<WastePartialRecycleCaseForm> search(ActionForm form, SqlSession conn) {
		WastePartialRecycleCaseMapper dao = conn.getMapper(WastePartialRecycleCaseMapper.class);
		WastePartialRecycleCaseForm wastePartialRecycleCaseForm = (WastePartialRecycleCaseForm) form;

		WastePartialRecycleCaseEntity entity = new WastePartialRecycleCaseEntity();
		BeanUtil.copyToBean(wastePartialRecycleCaseForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<WastePartialRecycleCaseEntity> list = dao.search(entity);
		List<WastePartialRecycleCaseForm> respList = new ArrayList<WastePartialRecycleCaseForm>();

		BeanUtil.copyToFormList(list, respList, CopyOptions.COPYOPTIONS_NOEMPTY, WastePartialRecycleCaseForm.class);

		return respList;
	}

	/**
	 * 新建记录
	 * 
	 * @param form
	 * @param conn
	 * @param errors
	 */
	public void insert(ActionForm form, SqlSessionManager conn, List<MsgInfo> errors) {
		WastePartialRecycleCaseMapper dao = conn.getMapper(WastePartialRecycleCaseMapper.class);

		WastePartialRecycleCaseEntity connd = new WastePartialRecycleCaseEntity();
		BeanUtil.copyToBean(form, connd, CopyOptions.COPYOPTIONS_NOEMPTY);

		// 装箱编号
		String caseCode = connd.getCase_code();
		// 回收箱用途种类
		Integer collectKind = connd.getCollect_kind();

		WastePartialRecycleCaseEntity entity = dao.getCaseByCode(caseCode, null);
		// 装箱编号重复检查
		if (entity != null) {
			MsgInfo error = new MsgInfo();
			error.setErrcode("dbaccess.recordDuplicated");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordDuplicated", "装箱编号【" + caseCode + "】"));
			errors.add(error);
		}

		if (errors.size() == 0) {
			// 内窥镜
			if (collectKind == 1) {
				entity = new WastePartialRecycleCaseEntity();
				entity.setCollect_kind(collectKind);
				entity.setPackage_flg(1);
				entity.setWaste_flg(1);
				List<WastePartialRecycleCaseEntity> list = dao.search(entity);
				if (list.size() == 1) {
					MsgInfo error = new MsgInfo();
					error.setErrcode("dbaccess.recordDuplicated");
					error.setErrmsg("内窥镜回收箱已存在，请勿重复创建。");
					errors.add(error);
				} else {
					dao.insert(connd);
				}
			} else {
				dao.insert(connd);
			}
		}
	}

	/**
	 * 更新
	 * 
	 * @param form
	 * @param conn
	 */
	public void update(ActionForm form, SqlSessionManager conn, List<MsgInfo> errors) {
		WastePartialRecycleCaseMapper dao = conn.getMapper(WastePartialRecycleCaseMapper.class);

		WastePartialRecycleCaseEntity connd = new WastePartialRecycleCaseEntity();
		BeanUtil.copyToBean(form, connd, CopyOptions.COPYOPTIONS_NOEMPTY);

		// 装箱编号
		String caseCode = connd.getCase_code();

		WastePartialRecycleCaseEntity entity = dao.getCaseByCode(caseCode, connd.getCase_id());
		// 装箱编号重复检查
		if (entity != null) {
			MsgInfo error = new MsgInfo();
			error.setErrcode("dbaccess.recordDuplicated");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordDuplicated", "装箱编号【" + caseCode + "】"));
			errors.add(error);
		}

		if (errors.size() == 0) {
			dao.update(connd);
		}
	}

	/**
	 * 打包
	 * 
	 * @param form
	 * @param conn
	 */
	public void updatePackageDate(ActionForm form, SqlSessionManager conn) {
		WastePartialRecycleCaseMapper dao = conn.getMapper(WastePartialRecycleCaseMapper.class);
		
		WastePartialRecycleCaseEntity entity = new WastePartialRecycleCaseEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		dao.updatePackageDate(entity);
	}

	/**
	 * 更新重量
	 * 
	 * @param form
	 * @param conn
	 */
	public void updateWeight(ActionForm form, SqlSessionManager conn) {
		WastePartialRecycleCaseMapper dao = conn.getMapper(WastePartialRecycleCaseMapper.class);

		WastePartialRecycleCaseEntity entity = new WastePartialRecycleCaseEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		dao.updateWeight(entity);
	}

	/**
	 * 申请废弃
	 * 
	 * @param form
	 * @param conn
	 */
	public void waste(ActionForm form, SqlSessionManager conn) {
		WastePartialRecycleCaseMapper dao = conn.getMapper(WastePartialRecycleCaseMapper.class);

		WastePartialRecycleCaseEntity entity = new WastePartialRecycleCaseEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		dao.updateWaste(entity);
	}

}
