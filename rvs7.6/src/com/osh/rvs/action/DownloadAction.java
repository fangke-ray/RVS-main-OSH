package com.osh.rvs.action;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.common.XlsUtil;
import com.osh.rvs.common.ZipUtility;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.service.DownloadService;
import com.osh.rvs.service.MaterialPartialService;
import com.osh.rvs.service.qa.QualityAssuranceService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.DateUtil;

public class DownloadAction extends BaseAction {

	private static Logger logger = Logger.getLogger("Download");
	DownloadService service = new DownloadService();

	private static String EXT_PDF = ".pdf";
	private static String EXT_ZIP = ".zip";
	private static String EXT_EXCEL = ".xls";
	private static String EXT_EXCEL_OPENXML = ".xlsx";
	private static String EXT_GIF = ".gif";

	/**
	 * Method execute
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 */
	public ActionForward output(ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse res, SqlSession conn) throws Exception {
		logger.info("DownloadAction.download start");

		String filePath = req.getParameter("filePath");
		String from = req.getParameter("from");
		String fileName = req.getParameter("fileName");

		String contentType = "";
		if (CommonStringUtil.isEmpty(fileName)) {
			logger.warn("没有指定文件名");
			return null;
		}
		if(fileName.endsWith(EXT_EXCEL)) {
			contentType = DownloadService.CONTENT_TYPE_EXCEL;
		} else if (fileName.endsWith(EXT_EXCEL_OPENXML)) {
			contentType = DownloadService.CONTENT_TYPE_EXCEL_OPENXML;
		} else if (fileName.endsWith(EXT_GIF)) {
			contentType = DownloadService.CONTENT_TYPE_GIF;
		} else if (fileName.endsWith(EXT_PDF)) {
			contentType = DownloadService.CONTENT_TYPE_PDF;
		} else if (fileName.endsWith(EXT_ZIP)) {
			contentType = DownloadService.CONTENT_TYPE_ZIP;
		}
		String strFileName = "";
		if (CommonStringUtil.isEmpty(filePath)) {
			filePath = RvsUtils.charRecorgnize(fileName);//  new String(fileName.getBytes("iso-8859-1"),"UTF-8");
		} else {
			filePath = RvsUtils.charRecorgnize(filePath);
		}

		strFileName = filePath.replaceAll(".*-(\\d{4}\\-\\d{2})\\-\\d{2}.*", "$1").replaceAll("-", "");

		if (CommonStringUtil.isEmpty(from) || ("cache".equals(from))) {
			Date today = new Date();
			filePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(today, "yyyyMM") + "\\" + filePath;
		} else if ("pcs".equals(from)) {
			int idxdDot = filePath.indexOf(".");
			String subPath = "";
			if (idxdDot >= 0) {
				subPath = filePath.substring(0, idxdDot);
			}
			if (subPath== null || subPath.length() < 8)
				subPath = "SAPD-" + subPath + "________";
			else if (subPath.length() == 8)
				subPath = "OMRN-" + subPath + "________";
			else 
				subPath = filePath;
			filePath = PathConsts.BASE_PATH + PathConsts.PCS + "\\" + subPath.substring(0, 8) + "\\" + filePath;
		} else if ("report".equals(from)) {
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\" + filePath;
		} else if ("reportm".equals(from)) {
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\works\\" + filePath;
		} else if ("report_accept".equals(from)) {
		
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\accept\\" + strFileName + "\\"  + filePath;
		}  else if ("report_sterilize".equals(from)) {
		
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\sterilize\\" + strFileName + "\\"  + filePath;
		} else if ("report_disinfect".equals(from)) {
		
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\disinfect\\" + strFileName + "\\"  + filePath;
		} else if ("report_inline".equals(from)) {
			
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\inline\\" + strFileName + "\\"  + filePath;
		} else if ("report_schedule".equals(from)) {
			
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\schedule\\" + strFileName + "\\"  + filePath;
		} else if ("report_shipping".equals(from)) {
			
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\shipping\\" + strFileName + "\\"  + filePath;
		} else if ("report_accept_confirm".equals(from)) {
			
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\accept\\" + strFileName + "\\confirm\\"  + filePath;
		} else if ("report_inline_confirm".equals(from)) {
			
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\inline\\" + strFileName + "\\confirm\\"  + filePath;
		}  else if ("report_sterilize_confirm".equals(from)) {
			
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\sterilize\\" + strFileName + "\\confirm\\" + filePath;
		} else if ("report_disinfect_confirm".equals(from)) {
		
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\disinfect\\" + strFileName + "\\confirm\\" + filePath;
		} else if ("report_schedule_confirm".equals(from)) {
			
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\schedule\\" + strFileName + "\\confirm\\" + filePath;
		} else if ("report_shipping_confirm".equals(from)) {
			
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\shipping\\" + strFileName + "\\confirm\\" + filePath;
		}else if ("partial".equals(from)) {
			
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\partial\\" + strFileName + "\\"  + filePath;
		}else if ("report_unrepaire".equals(from)) {
			
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\wip\\" + strFileName + "\\"  + filePath;
		}else if ("report_snout".equals(from)) {
			
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\snout\\" + filePath;
		}else if ("report_partial_bo".equals(from)) {
			
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\partail_bo\\" + filePath;
		}else if ("report_wash".equals(from)) {
			
			filePath = PathConsts.BASE_PATH + PathConsts.REPORT + "\\steel_wire_container_wash\\" + strFileName + "\\" + filePath;
		}else if ("prop".equals(from)) {
			filePath = PathConsts.BASE_PATH + PathConsts.PROPERTIES + "\\" + filePath;
		}

		service.writeFile(res, contentType, fileName, filePath);

		logger.info("DownloadAction.download end");
		return null;
	}

	/**
	 * 提供文档转PDF接口
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 */
	public ActionForward savePdf(ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse res, SqlSession conn) throws Exception {
		logger.info("DownloadAction.savePdf start");

		String filename = req.getParameter("filename");

		XlsUtil xls = null;
		try {
			xls = new XlsUtil(PathConsts.BASE_PATH + PathConsts.REPORT + "\\weeks\\" + filename + ".xls");

			xls.SaveAsPdf(PathConsts.BASE_PATH + PathConsts.REPORT + "\\weeks\\" + filename + ".pdf");
		} catch(Exception e) {
			if (xls != null) {
				xls.CloseExcel(false);
			}
		}
		logger.info("DownloadAction.savePdf end");
		return null;
	}

	/**
	 * 提供文档转PDF接口
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 */
	public ActionForward saveRPdf(ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse res, SqlSession conn) throws Exception {
		logger.info("DownloadAction.saveRPdf start");

		String path = req.getParameter("path");
		String filename = RvsUtils.charRecorgnize(req.getParameter("filename"));

		if (path == null || path.length() == 0) {
			if (filename == null || filename.length() < 10) {
				return null;
			}
//			if (filename.indexOf('月') < 0) {
//				filename = new String(req.getParameter("filename").getBytes("iso-8859-1"), req.getCharacterEncoding());
//			}
			// filename = filename.substring(0, 9) + "月SORC维修运营月报.xlsx";
			XlsUtil xls = null;
			try {
				xls = new XlsUtil(PathConsts.BASE_PATH + PathConsts.REPORT + "\\works\\mail\\" + filename);
				xls.SaveAsPdf(PathConsts.BASE_PATH + PathConsts.REPORT + "\\works\\" + filename.replaceAll("\\.xlsx", ".pdf"));
			} catch(Exception e) {
				if (xls != null) {
					xls.CloseExcel(false);
				}
			}
		} else {
//			filename = new String(filename.getBytes("iso-8859-1"),"UTF-8");
			filename = "每日KPI指标达成情况-" + filename + ".xls";
			XlsUtil xls = null;
			try {
				xls = new XlsUtil(PathConsts.BASE_PATH + PathConsts.REPORT + "\\kpi_process\\" + path + "\\" + filename);
				xls.SaveAsPdf(PathConsts.BASE_PATH + PathConsts.REPORT + "\\kpi_process\\" + path + "\\" + filename.replaceAll("\\.xls", ".pdf"));
			} catch(Exception e) {
				if (xls != null) {
					xls.CloseExcel(false);
				}
			}
		}
		logger.info("DownloadAction.saveRPdf end");
		return null;
	}

	/**
	 * 作业完成
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={0})
	public void file(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{
		log.info("QualityAssuranceAction.file start");

		String material_id = req.getParameter("material_id");
		String getHistory = req.getParameter("get_history");

		QualityAssuranceService service = new QualityAssuranceService();
		if (!CommonStringUtil.isEmpty(material_id)) {
			MaterialForm mform = service.getMaterialInfo(material_id, conn);
			String sorcNo = mform.getSorc_no();
			String subPath = "";
			if (sorcNo== null || sorcNo.length() < 8) // If EndoEye
				subPath = "SAPD-" + sorcNo + "________";
			else if (sorcNo.length() == 8)
				subPath = "OMRN-" + sorcNo + "________";
			else 
				subPath = sorcNo;
			String sub8 = subPath.substring(0, 8);
			String folderPath = PathConsts.BASE_PATH + PathConsts.PCS + "\\" + sub8 + "\\" + sorcNo;

			// 工程检查票Pdf生成
			service.makePdf(mform, folderPath, (getHistory != null), conn);

			MaterialPartialService mpService = new MaterialPartialService();
			try {
				mpService.createArchireOfPartialRecept(mform, folderPath, conn);
			}catch(Exception e) {
				// 防错
				logger.error(e.getMessage(), e);
			}
			// 打包
			ZipUtility.zipper(folderPath, folderPath + ".zip", "UTF-8");

			// TODO  FileUtils.removeDir();

		}

		Map<String, Object> callback = new HashMap<String, Object>();
		// 返回Json格式响应信息
		returnJsonResponse(res, callback);

		log.info("QualityAssuranceAction.file end");
	}
}
