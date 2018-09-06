package com.osh.rvs.common;

import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

public class FseBridgeUtil {
	static Logger _log = Logger.getLogger(FseBridgeUtil.class);

	public static void toUpdateMaterial(String material_id, String operator_id) {
		try {
			URL url = new URL("http://localhost:8080/fseBridge/trigger/rpl_mtr/"+material_id+"/"+operator_id+"");
			url.getQuery();
			URLConnection urlconn = url.openConnection();
			urlconn.setReadTimeout(1); // 不等返回
			urlconn.connect();
			urlconn.getContentType(); // 这个就能触发
		} catch (Exception e) {
			_log.error("Failed", e);
		}
	}

	public static void toUpdateMaterialProcess(String material_id, String operator_id) {
		try {
			URL url = new URL("http://localhost:8080/fseBridge/trigger/sbm_pro/"+material_id+"/"+operator_id+"");
			url.getQuery();
			URLConnection urlconn = url.openConnection();
			urlconn.setReadTimeout(1); // 不等返回
			urlconn.connect();
			urlconn.getContentType(); // 这个就能触发
		} catch (Exception e) {
			_log.error("Failed", e);
		}
	}

	/**
	 * 系统返回的OCM出货时间/受理时间/消毒灭菌时间复制
	 * @param material_id
	 * @param new_material_id
	 * @param operator_id
	 */
	public static void toResystemNeo(String material_id, String new_material_id, String operator_id) {
		try {
			URL url = new URL("http://localhost:8080/fseBridge/trigger/rsy_mtr/"+material_id+"/"+new_material_id+"");
			url.getQuery();
			URLConnection urlconn = url.openConnection();
			urlconn.setReadTimeout(1); // 不等返回
			urlconn.connect();
			urlconn.getContentType(); // 这个就能触发
		} catch (Exception e) {
			_log.error("Failed", e);
		}
	}

	/**
	 * 单元的完成时间
	 * @param material_id
	 * @param new_material_id
	 * @param operator_id
	 */
	public static void toFinishWorkOfCell(String material_id, String new_material_id, String operator_id) {
		try {
			URL url = new URL("http://localhost:8080/fseBridge/trigger/cfn_mtr/"+material_id+"/"+new_material_id+"");
			url.getQuery();
			URLConnection urlconn = url.openConnection();
			urlconn.setReadTimeout(1); // 不等返回
			urlconn.connect();
			urlconn.getContentType(); // 这个就能触发
		} catch (Exception e) {
			_log.error("Failed", e);
		}
	}

	/**
	 * 客户信息同步
	 * @param material_id
	 * @param new_material_id
	 * @param operator_id
	 */
	public static void createCustomer(String customer_id, String material_id, String operator_id) {
		try {
			URL url = new URL("http://localhost:8080/fseBridge/trigger/crt_ctm/"+customer_id+"/"+material_id+"");
			url.getQuery();
			URLConnection urlconn = url.openConnection();
			urlconn.setReadTimeout(1); // 不等返回
			urlconn.connect();
			urlconn.getContentType(); // 这个就能触发
		} catch (Exception e) {
			_log.error("Failed", e);
		}
	}
}
