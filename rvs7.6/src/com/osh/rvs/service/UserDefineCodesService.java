package com.osh.rvs.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;

import com.osh.rvs.bean.manage.UserDefineCodesEntity;
import com.osh.rvs.form.manage.UserDefineCodesForm;
import com.osh.rvs.mapper.manage.UserDefineCodesMapper;

import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class UserDefineCodesService {

	private static HashMap<String, String> cache = new HashMap<String, String>();

	/* 查询用户定义数值 */
	public List<UserDefineCodesForm> searchUserDefineCodes(SqlSession conn) {
		List<UserDefineCodesForm> userDefineCodesFormList = new ArrayList<UserDefineCodesForm>();

		UserDefineCodesMapper dao = conn.getMapper(UserDefineCodesMapper.class);

		/* 查询用户定义数值 */
		List<UserDefineCodesEntity> userDefineCodesEntityList = dao.searchUserDefineCodes();

		BeanUtil.copyToFormList(userDefineCodesEntityList, userDefineCodesFormList, CopyOptions.COPYOPTIONS_NOEMPTY,
				UserDefineCodesForm.class);
		return userDefineCodesFormList;
	}

	/* 更新用户定义 设定值 */
	public void updateUserDefineCodes(
			UserDefineCodesEntity userDefineCodesEntity, SqlSessionManager conn) {

		synchronized (cache) {
			UserDefineCodesMapper dao = conn.getMapper(UserDefineCodesMapper.class);

			// 更新用户定义 设定值
			dao.updateUserDefineCodes(userDefineCodesEntity);

			cache.put(userDefineCodesEntity.getCode(), userDefineCodesEntity.getValue());
		}
	}

	/* 取得用户定义 设定值 */
	public String searchUserDefineCodesValueByCode(String code, SqlSession conn) {

		synchronized (cache) {
			if (!cache.containsKey(code)) {
				UserDefineCodesMapper dao = conn.getMapper(UserDefineCodesMapper.class);
				String value = dao.searchUserDefineCodesValueByCode(code);
				if (value == null) value = "";
				cache.put(code, value);
				// 更新用户定义 设定值
				return value;
			} else {
				return cache.get(code);
			}
		}
	}
}
