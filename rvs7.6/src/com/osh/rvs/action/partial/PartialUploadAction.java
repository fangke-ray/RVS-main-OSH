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
import com.osh.rvs.service.partial.PartialSupplyService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;

public class PartialUploadAction extends BaseAction {

	private static Logger logger = Logger.getLogger("Upload");

	/**
	 * 上传基准值设定
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doUploadSupply(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		logger.info("PartialUploadAction.doUploadSupply start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		//文件名称
		UploadService uService = new UploadService();
		String tempfilename = uService.getFile2Local(form, errors);
		
		// 转换2003格式
		if (tempfilename.endsWith(".xlsx")) {
			tempfilename = toXls2003(tempfilename);
		}
		List<MsgInfo> dateList = new ArrayList<MsgInfo>();
		
		if(errors.size()==0){
			PartialSupplyService service = new PartialSupplyService();
			service.readUploadFile(tempfilename, conn, dateList);
			listResponse.put("dateList", dateList);
		}
		
		
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		
		logger.info("PartialUploadAction.doUploadSupply end");
	}
}
