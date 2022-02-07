/**
 * 系统名：OSH-RVS<br>
 * 模块名：系统管理<br>
 * 机能名：系统图片管理事件<br>
 * @author 李琬云
 */
package com.osh.rvs.action.master;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.master.SystemImageManageForm;
import com.osh.rvs.service.SystemImageManageService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.validator.Validators;

public class SystemImageManageAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	private SystemImageManageService service  = new SystemImageManageService();
	
	/**
	 * 系统图片管理初始画面
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={3, 0})
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn)
			throws Exception {

		log.info("SystemImageManageAction.init start");

		//文件类型
		req.setAttribute("sClassify",CodeListUtils.getSelectOptions("system_image_type"));
		req.setAttribute("gClassify",CodeListUtils.getGridOptions("system_image_type"));
		
		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("SystemImageManageAction.init end");
	}

	/**
	 * 系统图片说明一览及检索处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={3, 0})
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("SystemImageManageAction.search start");
		// Ajax响应对象
		Map<String, Object> listResponse = new HashMap<String, Object>();

		SystemImageManageForm conditionForm =(SystemImageManageForm)form;
		
		List<SystemImageManageForm> fileNameList = service.searchSystemImageName(conditionForm,conn);
		
		listResponse.put("fileNameList", fileNameList);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("SystemImageManageAction.search end");
	}
	
	/**
	 * 上传系统图片
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param request 页面请求
	 * @param response 页面响应
	 * @param conn 数据库会话
	 * @throws Exception Exception
	 */
	public void importPic(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn) throws Exception{
		log.info("SystemImageManageAction.importPic start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		
		// 表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		
		// 错误信息集合
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();
		
		// 判断文件是否已经存在的错误信息集合
		List<MsgInfo> fileExistsErrors = new ArrayList<MsgInfo>();
		
	    SystemImageManageForm systemImageManageForm =(SystemImageManageForm)form;
	    
	    service.validateImportFile(systemImageManageForm,conn,errors,fileExistsErrors);
	    
	    if(errors.size()==0){
	    	service.getFileLocal(form, errors);
	    }
	    
	    listResponse.put("errors", errors);
	    
	    listResponse.put("fileExistsErrors", fileExistsErrors);
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("SystemImageManageAction.importPic end");
	}
	
	/**
	 * 确认覆盖已经存在的图片
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param request 页面请求
	 * @param response 页面响应
	 * @param conn 数据库会话
	 */
	public void coverPic(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn){
		log.info("SystemImageManageAction.coverPic start");
		
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		service.getFileLocal(form, errors);
		
		listResponse.put("errors", errors);
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
		
		log.info("SystemImageManageAction.coverPic end");
	}
	
	/**
	 * 编辑
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param request 页面请求
	 * @param response 页面响应
	 * @param conn 数据库会话
	 */
    public void doReplace(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn){
    	log.info("SystemImageManageAction.output start");

		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 表单合法性检查
		Validators v=BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);

		// 错误信息集合
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();
		
		SystemImageManageForm systemImageManageForm = (SystemImageManageForm)form;
	
		if(errors.size()==0){
			service.replaceImageDescription(systemImageManageForm, conn);
		}
		
		listResponse.put("errors", errors);
		
		// 返回Json格式响应信息
		returnJsonResponse(response,listResponse);
    	
    	log.info("SystemImageManageAction.doReplace end");
    }
    
    /**
     * 图片点击下载
     * @param mapping ActionMapping
     * @param form 表单
     * @param req 页面请求
     * @param res 页面响应
     * @param conn 数据库会话
     * @return 
     * @throws Exception
     */
    public ActionForward output(ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse res, SqlSession conn) throws Exception {
    	log.info("SystemImageManageAction.output start");

		String from = req.getParameter("from");
		String fileName =RvsUtils.charRecorgnize(req.getParameter("fileName"));

		String contentType = "";
		if (CommonStringUtil.isEmpty(fileName)) {
			return null;
		}
		
		String filePath = "";
		String folder ="";
		if ("pcs".equals(from)) {
			folder = "pcs";
		}else if("sign".equals(from)){
			folder = "sign";
		}else if("tcs".equals(from)){
			folder = "tcs";
		}
		filePath = PathConsts.BASE_PATH + PathConsts.IMAGES + "\\"+folder+"\\" + fileName;

		res.setHeader("Content-Disposition","attachment;filename=\""+ RvsUtils.charEncode(fileName) + "\"");
		res.setContentType(contentType);
		File file = new File(filePath);
		InputStream is = new BufferedInputStream(new FileInputStream(file));
		byte[] buffer = new byte[is.available()];
		is.read(buffer);
		is.close();
		
		OutputStream os = new BufferedOutputStream(res.getOutputStream());
		os.write(buffer);
		os.flush();
		os.close();

		log.info("SystemImageManageAction.output end");
		return null;
	}
}
