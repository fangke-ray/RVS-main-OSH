package com.osh.rvs.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.infect.CheckedFileStorageEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.service.CheckResultService;
import com.osh.rvs.service.DownloadService;
import com.osh.rvs.service.FilingDownloadService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.common.util.CommonStringUtil;

public class FilingDownloadAction extends BaseAction {

	private static Logger logger = Logger.getLogger("Download");
	FilingDownloadService service = new FilingDownloadService();

	private static String EXT_PDF = ".pdf";
	private static String EXT_ZIP = ".zip";
	private static String EXT_EXCEL = ".xls";
	private static String EXT_GIF = ".gif";

	/**
	 * 点检结果归档文件下载
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public ActionForward output(ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse res, SqlSession conn) throws Exception {
		logger.info("FilingDownloadAction.download start");
		
		//下载的文件路径
		String filePath =req.getParameter("filePath");
		
		//下载的文件名字
		String strFileName=req.getParameter("fileName");
		String fileName =new String(strFileName.getBytes("iso-8859-1"),"UTF-8");

		String contentType = "";
		if (CommonStringUtil.isEmpty(fileName)) {
			logger.warn("没有指定文件名");
			return null;
		}
		if(fileName.endsWith(EXT_EXCEL)) {
			contentType = DownloadService.CONTENT_TYPE_EXCEL;
		} else if (fileName.endsWith(EXT_GIF)) {
			contentType = DownloadService.CONTENT_TYPE_GIF;
		} else if (fileName.endsWith(EXT_PDF)) {
			contentType = DownloadService.CONTENT_TYPE_PDF;
		} else if (fileName.endsWith(EXT_ZIP)) {
			contentType = DownloadService.CONTENT_TYPE_ZIP;
		}

		filePath = PathConsts.BASE_PATH + PathConsts.INFECTIONS + filePath+ fileName;

		service.writeFile(res, contentType, strFileName, filePath);

		logger.info("FilingDownloadAction.download end");
		return null;
	}

	public ActionForward make(ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse res, SqlSession conn) throws Exception {
		String sEntity = req.getParameter("entity");
		sEntity = new String(sEntity.getBytes("ISO-8859-1"), "utf-8");

		CheckResultService service = new CheckResultService();

		CheckedFileStorageEntity cfsEntity = 
				JSON.decode(sEntity, CheckedFileStorageEntity.class);

		String sEncodedDeviceList = req.getParameter("encodedDeviceList");
		if (sEncodedDeviceList != null) {
			@SuppressWarnings({ "unchecked" })
			List<String> lEncodedDeviceList = 
					JSON.decode(sEncodedDeviceList, ArrayList.class);

			String sJigOperaterId = req.getParameter("sJigOperaterId");
			if (sJigOperaterId != null) {
				service.makeFileJigs(cfsEntity, lEncodedDeviceList, sJigOperaterId, conn);
			} else {
				service.makeFileGroup(cfsEntity, lEncodedDeviceList, conn);
			}
		}

		String sDeviceId = req.getParameter("sDeviceId");
		if (sDeviceId != null) {
			List<String> lEncodedDeviceList = new ArrayList<String>();
			lEncodedDeviceList.add(sDeviceId);
			service.makeFileGroup(cfsEntity, lEncodedDeviceList, conn);
//			service.makeFileSingle(cfsEntity, sDeviceId, conn);
		}

		return null;
	}

}
