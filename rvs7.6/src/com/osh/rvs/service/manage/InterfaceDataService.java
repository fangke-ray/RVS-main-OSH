package com.osh.rvs.service.manage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.arnx.jsonic.JSON;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.manage.InterfaceDataEntity;
import com.osh.rvs.form.manage.InterfaceDataForm;
import com.osh.rvs.mapper.manage.InterfaceDataMapper;

import framework.huiqing.common.util.copy.BeanUtil;

public class InterfaceDataService {

	protected static JSON json = new JSON();
	static {
		json.setSuppressNull(true);
	}

	/**
	 * 未处理接口数据查询
	 * 
	 * @param conn
	 * @return
	 */
	public List<InterfaceDataForm> searchAllContent(SqlSession conn)
			throws Exception {
		List<InterfaceDataEntity> lResultBean = new ArrayList<InterfaceDataEntity>();
		InterfaceDataMapper dao = conn.getMapper(InterfaceDataMapper.class);
		lResultBean = dao.searchAllContent();

		List<InterfaceDataForm> lResultForm = new ArrayList<InterfaceDataForm>();

		BeanUtil.copyToFormList(lResultBean, lResultForm, null,
				InterfaceDataForm.class);

		for (InterfaceDataForm resultForm : lResultForm) {
			InterfaceDataForm tmpForm = JSON.decode(resultForm.getContent(), InterfaceDataForm.class);
			resultForm.setOMRNotifiNo(tmpForm.getOMRNotifiNo());
		}

		return lResultForm;
	}

	/**
	 * 详细数据查询
	 * 
	 * @param if_sap_message_key
	 * @param seq
	 * @param conn
	 * @return
	 */
	public InterfaceDataForm searchContentByKey(String if_sap_message_key,
			String seq, SqlSession conn) throws Exception {
		InterfaceDataEntity resultBean = new InterfaceDataEntity();
		InterfaceDataMapper dao = conn.getMapper(InterfaceDataMapper.class);
		resultBean = dao.searchContentByKey(if_sap_message_key, seq);

		InterfaceDataForm resultForm = new InterfaceDataForm();

		BeanUtil.copyToForm(resultBean, resultForm, null);

		return resultForm;
	}

	/**
	 * 更新content
	 * 
	 * @param if_sap_message_key
	 * @param seq
	 * @param content
	 * @param conn
	 * @return
	 */
	public void updateContent(String if_sap_message_key, String seq,
			HashMap<String, Object> content, SqlSessionManager conn)
			throws Exception {
		InterfaceDataMapper dao = conn.getMapper(InterfaceDataMapper.class);
		InterfaceDataEntity entity = new InterfaceDataEntity();
		entity.setIf_sap_message_key(Integer.parseInt(if_sap_message_key));
		entity.setSeq(Integer.parseInt(seq));
		entity.setContent(json.format(content));

		dao.updateContent(entity);
	}

	/**
	 * 删除content
	 * 
	 * @param if_sap_message_key
	 * @param seq
	 * @param conn
	 * @return
	 */
	public void deleteContent(String if_sap_message_key, String seq,
			SqlSessionManager conn) throws Exception {
		InterfaceDataMapper dao = conn.getMapper(InterfaceDataMapper.class);
		dao.deleteContent(if_sap_message_key, seq);
	}

	/**
	 * 忽略content
	 * 
	 * @param if_sap_message_key
	 * @param seq
	 * @param user
	 * @param conn
	 * @return
	 */
	public void ignoreContent(String if_sap_message_key, String seq, LoginData user,
			SqlSessionManager conn) throws Exception {
		InterfaceDataMapper dao = conn.getMapper(InterfaceDataMapper.class);
		InterfaceDataEntity entity = new InterfaceDataEntity();
		entity.setIf_sap_message_key(Integer.parseInt(if_sap_message_key));
		entity.setSeq(Integer.parseInt(seq));
		entity.setResolved(0);
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY年MM月dd日HH时mm分");
		entity.setInvalid_message(user.getName() + "于" + sdf.format(date) + "将此信息作忽略处理");

		dao.updateResolved(entity);
	}
}
