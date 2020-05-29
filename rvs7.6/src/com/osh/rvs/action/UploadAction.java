package com.osh.rvs.action;

import static com.osh.rvs.service.UploadService.toXls2003;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import com.osh.rvs.bean.partial.MaterialPartialDetailEntity;
import com.osh.rvs.bean.partial.PartialBaseLineValueEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.common.ZipUtility;
import com.osh.rvs.form.UploadForm;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.partial.MaterialPartialForm;
import com.osh.rvs.service.AcceptFactService;
import com.osh.rvs.service.PauseFeatureService;
import com.osh.rvs.service.UploadService;
import com.osh.rvs.service.partial.PartialAssignService;
import com.osh.rvs.service.partial.PartialBaseLineValueService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.FileUtils;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;

public class UploadAction extends BaseAction {

	private static Logger logger = Logger.getLogger("Upload");

	/**
	 * 受理文档上传 execute
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 */
	public void doAccept(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		logger.info("UploadAction.accept start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		List<MsgInfo> infoes = new ArrayList<MsgInfo>();

		UploadService uService = new UploadService();
		String tempfilename = uService.getFile2Local(form, errors);
		// 转换2003格式
		if (tempfilename.endsWith(".xlsx")) {
			tempfilename = toXls2003(tempfilename);
		}
		if (errors.size() == 0) {
			List<MaterialForm> readList = new ArrayList<MaterialForm>();
			
			// 新的文件导入
			Map<String, Integer> readMap = uService.readSetInlineStatus(readList, tempfilename, conn, errors, infoes);

			if (errors.size() == 1 && "notSummaryFile".equals(errors.get(0).getErrcode())) {
				// 备品文件导入
				errors.clear();
				readList = uService.readSparesFile(tempfilename, conn, errors, infoes, req.getSession());
			}

			if (errors.size() == 1 && "notSparesFile".equals(errors.get(0).getErrcode())) {
				// 还是旧文件
				errors.clear();
				readList = uService.readFile(tempfilename, conn, errors);
			}

			if (readMap == null && readList.size() == 0 && errors.size() == 0) {
				MsgInfo error = new MsgInfo();
				error.setErrcode("file.invalidFormat");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.invalidFormat"));
				errors.add(error);
				listResponse.put("errors", errors);
			} else {
			}
			// 查询结果放入Ajax响应对象
			listResponse.put("list", readList);
			listResponse.put("status", readMap);
		}

		listResponse.put("errors", errors);
		listResponse.put("infoes", infoes);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		logger.info("UploadAction.accept end");
	}

	/**
	 * 受理文档上传 execute
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 */
	public void doagree(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		logger.info("UploadAction.agree start");
		// Json响应信息
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		// 实际上是文件名字
		UploadService uService = new UploadService();
		String tempfilename = uService.getFile2Local(form, errors);

		if (errors.size() == 0) {
			// 获取了将EXCEL表格的内容
			List<MaterialForm> readList = uService.readAgreed(tempfilename, conn, errors);
			// 如果数据是空的话
			if (readList == null) {
				MsgInfo error = new MsgInfo();
				error.setErrcode("file.invalidFormat");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.invalidFormat"));
				errors.add(error);
				listResponse.put("error", errors);
			} else {
				// 查询结果放入Ajax响应对象
				listResponse.put("list", readList);

				// 触发检测
				HttpAsyncClient httpclient = new DefaultHttpAsyncClient();
				httpclient.start();
				try {
					HttpGet request = new HttpGet("http://localhost:8080/rvspush/trigger/lateinline/" + readList.size()
							+ "/" + new Random().nextInt());
					logger.info("finger:" + request.getURI());
					httpclient.execute(request, null);
				} catch (Exception e) {
				} finally {
					Thread.sleep(100);
					httpclient.shutdown();
				}
			}
		}

		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		logger.info("UploadAction.agree end");
	}

	/**
	 * 入库预定日文档上传 execute
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 */
	public void doreach(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		logger.info("UploadAction.doreach start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		UploadService uService = new UploadService();
		String tempfilename = uService.getFile2Local(form, errors);

		if (errors.size() == 0) {
			List<MaterialForm> readList = uService.readPatrialReach(tempfilename, conn, errors);

			if (readList == null) {
				MsgInfo error = new MsgInfo();
				error.setErrcode("file.invalidFormat");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.invalidFormat"));
				errors.add(error);
				listResponse.put("error", errors);
			} else {
				// 查询结果放入Ajax响应对象
				listResponse.put("list", readList);
			}
		}

		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		logger.info("UploadAction.doreach end");
	}

	/**
	 * 受理文档上传 execute
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 */
	public void setInlineStatus(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		logger.info("UploadAction.setInlineStatus start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		UploadService uService = new UploadService();
		String tempfilename = uService.getFile2Local(form, errors);

		// 转换2003格式
		if (tempfilename.endsWith(".xlsx")) {
			tempfilename = toXls2003(tempfilename);
		}
		if (errors.size() == 0) {
			
			List<MaterialForm> readList = new ArrayList<MaterialForm>();
			Map<String, Integer> readMap = uService.readSetInlineStatus(readList, tempfilename, conn, errors, errors);

			if (readMap == null && readList.size() == 0 && errors.size() == 0) {
				MsgInfo error = new MsgInfo();
				error.setErrcode("file.invalidFormat");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.invalidFormat"));
				errors.add(error);
			}

			// 查询结果放入Ajax响应对象
			listResponse.put("status", readMap);
			// 查询结果放入Ajax响应对象
			listResponse.put("list", readList);
		}

		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		logger.info("UploadAction.setInlineStatus end");
	}

	/**
	 * 归档文件上传
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void confirm(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {
		logger.info("UploadAction.confirmInline start");
		UploadForm upfileForm = (UploadForm) form;
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 取得上传的文件
		FormFile file = upfileForm.getFile();
		// 上传文件名字
		String filename = file.getFileName();
		// 文件输出流
		FileOutputStream fileOutput;
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		if (file == null || CommonStringUtil.isEmpty(filename)) {
			MsgInfo error = new MsgInfo();
			error.setErrcode("file.notExist");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.notExist"));
			errors.add(error);
		}

		String uploadDate = req.getParameter("date");
		UploadService uservice = new UploadService();
		String nameParam = req.getParameter("filepath").replaceAll("report_", "");
		String filepath = uservice.readFileName(uploadDate.substring(0, 4) + uploadDate.substring(5, 7), nameParam);

		try {
			boolean isSpare = filename.indexOf("备品") >= 0;

			if (nameParam.equals("inline")) {
				filepath += "内镜投线记录表-";
			} else if (nameParam.equals("accept")) {
				if (isSpare) {
					filepath += "QR-B25002-2 备品受理记录表-";
				} else {
					filepath += "QR-B31002-59 内镜受理记录表-";
				}
			} else if (nameParam.equals("accept-spare")) {
				filepath += "QR-B25002-2 备品受理记录表-";
			} else if (nameParam.equals("sterilize")) {
				if (isSpare) {
					filepath += "QR-B25002-4 备品灭菌记录表-";
				} else {
					filepath += "QR-B31002-62 内镜EOG灭菌记录表-";
				}
			} else if (nameParam.equals("sterilize-spare")) {
				filepath += "QR-B25002-4 备品灭菌记录表-";
			} else if (nameParam.equals("disinfect")) {
				if (isSpare) {
					filepath += "QR-B25002-3 备品消毒记录表-";
				} else {
					filepath += "QR-B31002-60 内镜清洗消毒记录表-";
				}
			} else if (nameParam.equals("disinfect-spare")) {
				filepath += "QR-B25002-3 备品消毒记录表-";
			} else if (nameParam.equals("schedule")) {
				filepath += "计划报告书-";
			} else if (nameParam.equals("shipping")) {
				filepath += "QR-B31002-63 内镜出货记录表-";
			}
			fileOutput = new FileOutputStream(filepath + uploadDate + ".xls");
			fileOutput.write(file.getFileData());
			fileOutput.flush();
			fileOutput.close();
		} catch (FileNotFoundException e) {
			logger.error("FileNotFound:" + e.getMessage());
		} catch (IOException e) {
			logger.error("IO:" + e.getMessage());
		}

		listResponse.put("errors", errors);
		returnJsonResponse(res, listResponse);
		logger.info("UploadAction.confirmInline end");
	}
	

	/**
	 * 导入零件定位信息
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void docodeposition(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		logger.info("UploadAction.docodeposition start");
		// Json响应信息
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		// 实际文件名字
		UploadService uService = new UploadService();
		String tempfilename = uService.getFile2Local(form, errors);

		// 转换2003格式
		if (tempfilename.endsWith(".xlsx")) {
			tempfilename = toXls2003(tempfilename);
		}

		if (errors.size() == 0) {
			uService.readCodePosition(tempfilename, conn, errors);
			if(errors.size()>0){
				MsgInfo error = new MsgInfo();
				error.setErrcode("file.invalidFormat");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.invalidFormat"));
				errors.add(error);
				listResponse.put("error", errors);
			}
		}
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		logger.info("UploadAction.docodeposition end");
	}

	/**
	 * 导入BOM表,并插入数据
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doUploadBom(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		logger.info("UploadAction.uploadBom start");
		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		// 文件路径
		UploadService uService = new UploadService();
		String tempfilename = uService.getFile2Local(form, errors);

		if (tempfilename.endsWith(".xlsx")) {
			tempfilename = toXls2003(tempfilename);
		}

		if (!tempfilename.endsWith(".xls")) {
			MsgInfo error = new MsgInfo();
			error.setErrcode("file.invalidType");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.invalidType"));
			errors.add(error);
		}

		if (errors.size() == 0) {
			uService.readBomExcel(tempfilename, conn, errors);
		}

		listResponse.put("errors", errors);
		returnJsonResponse(res, listResponse);

		logger.info("UploadAction.uploadBom end");
	}

	/**
	 * 零件发放
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void uploadPartialAssign(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response, SqlSession conn) throws Exception {
		logger.info("UploadAction.uploadPartialAssign start");

		// 对Ajax的响应
		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		// 文件路径
		UploadService uService = new UploadService();
		String tempfilename = uService.getFile2Local(form, errors);
		if (errors.size() == 0) {

			// Add by Gonglm 2014/1/9 start
			// 重新取得零件订购信息
			PartialAssignService paService = new PartialAssignService();
			Map<String, List<MaterialPartialDetailEntity>> responseMap = paService.searchMaterialPartialDetailMap(conn);
			request.getSession().setAttribute("partialMap", responseMap);
			// Add by Gonglm 2014/1/9 end

			Map<String, Map<String, Integer>> partialReceptMap = new HashMap<String, Map<String, Integer>>();

			if (tempfilename.endsWith(".zip")) {
				// 提前领取补单，无需确认
				String extPath = tempfilename.replaceAll(".zip", "");
				ZipUtility.unzipper6(tempfilename, extPath, "UTF-8");
				File extFolder = new File(extPath);

				for (File updFile : extFolder.listFiles()) {
					// 读取文件维修对象列表
					uService.readFileByLines(updFile.getPath(), partialReceptMap, false, conn, errors);
				}

			} else {

				// 读取文件维修对象列表
				uService.readFileByLines(tempfilename, partialReceptMap, false, conn, errors);
			}

			// 处理读取完成的内容
			List<MaterialPartialForm> sList = uService.readPartialAssignFile(request, partialReceptMap, conn, errors);

			if (sList!=null && tempfilename.endsWith(".zip")) {
				for (MaterialPartialForm pline : sList) {
					pline.setIsHistory("1");
				}
			}

			listResponse.put("finished", sList);
		}

		listResponse.put("errors", errors);
		returnJsonResponse(response, listResponse);

		logger.info("UploadAction.uploadPartialAssign end");
	}
	
	
	/**
	 * 零件订购
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doorderdetail(ActionMapping mapping,ActionForm form,HttpServletRequest req,HttpServletResponse res,
			SqlSessionManager conn)	throws Exception{
		logger.info("UploadAction.doorderdetail start");
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		// 实际上是文件名字
		UploadService uService = new UploadService();
		String tempfilename = uService.getFile2Local(form, errors);

		// 转换2003格式
		if (tempfilename.endsWith(".xlsx")) {
			tempfilename = toXls2003(tempfilename);
		}

		if (errors.size() == 0) {
		
			List<MaterialPartialForm> materialDetailList = uService.readMaterialDetail(form,req, tempfilename, conn, errors);
			if(errors.size()>0){
//				MsgInfo error = new MsgInfo();
//				error.setErrcode("file.invalidFormat");
//				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.invalidFormat"));
//				errors.add(error);
//				listResponse.put("error", errors);
			} else {
				listResponse.put("materialDetailList", materialDetailList);
			}
		}		
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		logger.info("UploadAction.doorderdetail end");		
	}
	
	
	/**
	 * 载入零件入库预定日 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doUploadArrivePlanDate(ActionMapping mapping,ActionForm form,HttpServletRequest req,HttpServletResponse res,SqlSessionManager conn)throws Exception{
		logger.info("UploadAction.doUploadArrivePlanDate start");
		
		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		// 实际上是文件名字
		UploadService uService = new UploadService();
		String tempfilename = uService.getFile2Local(form, errors);

		// 转换2003格式
		if (tempfilename.endsWith(".xlsx")) {
			tempfilename = toXls2003(tempfilename);
		}

		if(errors.size()==0){
			if(!tempfilename.endsWith(".xls")){
				MsgInfo info=new MsgInfo();
				info.setErrmsg("请保存成2003格式!");
				errors.add(info);
			}
			if (errors.size() == 0) {
				uService.updateArrivePlanDate(tempfilename, conn, errors);
			}
		}
		
		
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		logger.info("UploadAction.doUploadArrivePlanDate end");		
		
		
	}

	/**
	 * 上传拉动台数设定
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doUploadForecastSetting(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		logger.info("UploadAction.doUploadForecastSetting start");
		
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
		List<MsgInfo> infos = new ArrayList<MsgInfo>();
		
		if(errors.size()==0){
			uService.uploadForecastSetting(tempfilename, conn, infos);
			listResponse.put("info", infos);
		}
		
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		
		logger.info("UploadAction.doUploadForecastSetting end");
	}
	
	/**
	 * 上传基准值设定
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 * @throws Exception
	 */
	public void doUploadTotalForeboardCount(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn)throws Exception{
		logger.info("UploadAction.doUploadTotalForeboardCount start");
		
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
		List<MsgInfo> infos = new ArrayList<MsgInfo>();
		
		String strDate=request.getParameter("end_date");
		Date date=DateUtil.toDate(strDate, DateUtil.DATE_PATTERN);
		List<PartialBaseLineValueEntity> list=new ArrayList<PartialBaseLineValueEntity>();
		
		if(errors.size()==0){
			uService.uploadTotalForeboardCount(tempfilename,request,date, conn, errors,infos,list);
			if(errors.size()==0){
				PartialBaseLineValueService service=new PartialBaseLineValueService();
				service.updateBaseValue(list, conn);
				listResponse.put("infos", infos);
			} else {
				conn.rollback();
			}
		}
		
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		
		logger.info("UploadAction.doUploadTotalForeboardCount end");
	}

	/**
	 * 上传配置文件
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doProp(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {
		logger.info("UploadAction.doProp start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		UploadService uService = new UploadService();
		String tempfilename = uService.getFile2Local(form, errors);

		if (errors.size() == 0) {
			String propName = req.getParameter("prop_name");
			// 取得上传的文件
			String fileName = PathConsts.BASE_PATH + PathConsts.PROPERTIES + "\\" 
					+ propName + ".properties";

			try {
				// 备份
				FileUtils.copyFile(fileName, fileName.replaceAll(".properties", ".setting"));
				FileUtils.copyFile(tempfilename, fileName);

				PathConsts.loadWithCheck();

				PauseFeatureService.resetPauseReason();
				AcceptFactService.resetMap();
				RvsUtils.initAll(conn);

				List<String> triggerList = new ArrayList<String>();
				triggerList.add("http://localhost:8080/rvspush/trigger/prop/" + propName + "/" + new Date().getTime());
				triggerList.add("http://localhost:8080/rvsscan/trigger/prop/" + propName + "/" + new Date().getTime());
				// 控制其他工程
				RvsUtils.sendTrigger(triggerList);
			} catch (Exception e) {
				// 取回备份重新上传
				FileUtils.copyFile(fileName.replaceAll(".properties", ".setting"), fileName);
				PathConsts.load();

				if (errors.size() == 0) {
					MsgInfo error = new MsgInfo();
					error.setErrcode("file.invalidFormat");
					error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.invalidFormat"));
					errors.add(error);
					listResponse.put("errors", errors);
				}
			}

		}

		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		logger.info("UploadAction.doProp end");
	}
}
