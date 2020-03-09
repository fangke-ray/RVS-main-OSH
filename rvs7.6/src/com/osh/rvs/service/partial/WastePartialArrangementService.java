package com.osh.rvs.service.partial;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.partial.WastePartialArrangementEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.partial.WastePartialArrangementForm;
import com.osh.rvs.mapper.partial.WastePartialArrangementMapper;

import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

/**
 * 废弃零件整理
 * 
 * @Description
 * @author dell
 * @date 2019-12-26 下午4:33:52
 */
public class WastePartialArrangementService {
	/**
	 * 查询
	 * 
	 * @param form
	 * @param conn
	 * @return
	 */
	public List<WastePartialArrangementForm> search(ActionForm form, SqlSession conn) {
		WastePartialArrangementMapper dao = conn.getMapper(WastePartialArrangementMapper.class);

		WastePartialArrangementEntity entity = new WastePartialArrangementEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		List<WastePartialArrangementEntity> list = dao.search(entity);

		List<WastePartialArrangementForm> respFormList = new ArrayList<WastePartialArrangementForm>();
		BeanUtil.copyToFormList(list, respFormList, CopyOptions.COPYOPTIONS_NOEMPTY, WastePartialArrangementForm.class);

		return respFormList;
	}

	/**
	 * 新建记录
	 * 
	 * @param form
	 * @param conn
	 */
	public void inser(ActionForm form, HttpSession session, SqlSessionManager conn) {
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		WastePartialArrangementMapper dao = conn.getMapper(WastePartialArrangementMapper.class);

		WastePartialArrangementEntity entity = new WastePartialArrangementEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		entity.setOperator_id(user.getOperator_id());

		dao.insert(entity);
	}

	public Integer getMaxPartByMaterialId(String materialId, SqlSession conn) {
		WastePartialArrangementMapper dao = conn.getMapper(WastePartialArrangementMapper.class);

		Integer part = dao.getMaxPartByMaterialId(materialId);
		return part;
	}

	public void removeRecord(
			WastePartialArrangementForm wastePartialArrangementForm,
			SqlSessionManager conn) {
		WastePartialArrangementMapper mapper = conn.getMapper(WastePartialArrangementMapper.class);

		WastePartialArrangementEntity entity = new WastePartialArrangementEntity();
		BeanUtil.copyToBean(wastePartialArrangementForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		mapper.removeRecord(entity);
	}
}
