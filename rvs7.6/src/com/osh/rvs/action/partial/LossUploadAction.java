package com.osh.rvs.action.partial;

import static com.osh.rvs.service.UploadService.toXls2003;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.service.UploadService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.message.ApplicationMessage;

public class LossUploadAction extends BaseAction {

	private static Logger logger = Logger.getLogger("Upload");

	private UploadService uService = new UploadService();
	/**
	 * 单元/保内返修损金 文件 导入
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 */
	public void doloss(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
	SqlSessionManager conn) throws Exception {
		logger.info("UploadAction.doloss start");
		
		// Json响应信息
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		List<MsgInfo> msgInfo = new ArrayList<MsgInfo>();
		// 实际文件名字
		String tempfilename = uService.getFile2Local(form, errors);

		// 转换2003格式
		if (tempfilename.endsWith(".xlsx")) {
			tempfilename = toXls2003(tempfilename);
		}

		if (errors.size() == 0) {
			uService.readLossExcel(tempfilename, conn, errors,msgInfo);
			if(errors.size()>0){
				MsgInfo error = new MsgInfo();
				error.setErrcode("file.invalidFormat");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.invalidFormat"));
				errors.add(error);
				listResponse.put("error", errors);
			}
			if(msgInfo.size()>0){
				listResponse.put("msgInfo", msgInfo);
			}			
		}
		listResponse.put("errors", errors);
		
		listResponse.put("msgInfo", msgInfo);
		
		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		logger.info("UploadAction.doloss end");
	}
}
