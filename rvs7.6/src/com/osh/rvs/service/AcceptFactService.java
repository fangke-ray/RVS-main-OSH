package com.osh.rvs.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import framework.huiqing.common.util.CodeListUtils;

public class AcceptFactService {

	public static Map<String, String> moduleMap = CodeListUtils.getList("qf_production_module");
	public static Map<String, String> typeMap = CodeListUtils.getList("qf_production_type");

	/**
	 * 取得全部间接作业选项
	 * @param conn
	 * @return
	 */
	public String getOptions(SqlSession conn) {

		List<String[]> lst = new ArrayList<String[]>();
		for (String key : typeMap.keySet()) {
			String[] p = new String[3];
			p[0] = key;
			p[2] = moduleMap.get(key.substring(0, 2));
			p[1] = typeMap.get(key);
			lst.add(p);
		}

		return CodeListUtils.getReferChooser(lst);
	}

	public static void resetMap() {
		moduleMap = CodeListUtils.getList("qf_production_module");
		typeMap = CodeListUtils.getList("qf_production_type");
	}
}
