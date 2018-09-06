/**
 * 系统名：OSH-RVS<br>
 * 模块名：系统管理<br>
 * 机能名：维修对象型号系统管理事件<br>
 * @author 龚镭敏
 * @version 0.01
 */
package com.osh.rvs.action.extra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.service.MaterialService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;

public class MaterialAngleAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	/**
	 * 设备类别管理画面初始表示处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("MaterialAngleAction.init start");
		// Ajax回馈对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String material_id = req.getParameter("id");
		if (material_id == null || "".equals(material_id)) {
			MsgInfo info = new MsgInfo();
			info.setComponentid("material_id");
			info.setErrcode("model.notExist");
			errors.add(info);
		} else {
			/*
			 * 维修对象型号系统管理处理
			 */
			MaterialService service = new MaterialService();
			MaterialForm ret = service.loadSimpleMaterialDetail(conn, material_id);
			if (ret.getMaterial_id() == null) {
				MsgInfo info = new MsgInfo();
				info.setComponentid("material_id");
				info.setErrcode("model.notExist");
				errors.add(info);
			} else {
				listResponse.put("model_name", ret.getModel_name());
				listResponse.put("serial_no", ret.getSerial_no());
			}
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("MaterialAngleAction.init end");
	}
}
