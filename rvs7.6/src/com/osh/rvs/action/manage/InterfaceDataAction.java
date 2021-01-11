package com.osh.rvs.action.manage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.manage.InterfaceDataForm;
import com.osh.rvs.service.manage.InterfaceDataService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CommonStringUtil;

/**
 * 处理接口数据
 * 
 * @author Gong
 * 
 */
public class InterfaceDataAction extends BaseAction {
	private static final Class<Void> String = null;
	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * 初始化
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void init(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res, SqlSession conn)
			throws Exception {

		logger.info("InterfaceDataAction.init start");

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		logger.info("InterfaceDataAction.init end");
	}

	/**
	 * 查询
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void searchAll(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res, SqlSession conn)
			throws Exception {

		logger.info("InterfaceDataAction.search start");

		Map<String, Object> listResponse = new HashMap<String, Object>();

		InterfaceDataService service = new InterfaceDataService();
		List<InterfaceDataForm> finishedForms = service.searchAllContent(conn);
		listResponse.put("finished", finishedForms);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		logger.info("InterfaceDataAction.search end");
	}

	/**
	 * 详细查询
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void getDetial(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res, SqlSession conn)
			throws Exception {

		logger.info("InterfaceDataAction.getDetial start");

		InterfaceDataService service = new InterfaceDataService();
		InterfaceDataForm detailForm = new InterfaceDataForm();
		Map<String, Object> listResponse = new HashMap<String, Object>();

		String if_sap_message_key = req.getParameter("if_sap_message_key");
		String seq = req.getParameter("seq");
		if (!CommonStringUtil.isEmpty(if_sap_message_key)
				&& !CommonStringUtil.isEmpty(seq)) {
			detailForm = service.searchContentByKey(if_sap_message_key, seq, conn);
		}
		listResponse.put("detailForm", detailForm);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		logger.info("InterfaceDataAction.getDetial end");
	}

	/**
	 * 确定
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doSave(ActionMapping mapping, ActionForm form,	HttpServletRequest req, HttpServletResponse res,SqlSessionManager conn) throws Exception {
		logger.info("InterfaceDataAction.doSave start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		InterfaceDataService service = new InterfaceDataService();
		HashMap<String, Object> content = new HashMap<String, Object>();
		
		
		String if_sap_message_key = req.getParameter("if_sap_message_key");
		String seq = req.getParameter("seq");
		String kind = req.getParameter("kind");
		if (!CommonStringUtil.isEmpty(if_sap_message_key)&& !CommonStringUtil.isEmpty(seq)) {
			
			if("part_order".equals(kind)){
				
				List<HashMap> partialList = new AutofillArrayList<HashMap>(HashMap.class);//零件
				List<HashMap> orderList = new AutofillArrayList<HashMap>(HashMap.class);//订购单
				Map<String,String[]> map=(Map<String,String[]>)req.getParameterMap();
				Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");
				// 整理提交数据
				for (String parameterKey : map.keySet()) {
					 Matcher m = p.matcher(parameterKey);
					 if (m.find()) {
						 String table = m.group(1);
						 if ("if_sap_message_content_two".equals(table)) {
						     String column = m.group(2);
						     int icounts = Integer.parseInt(m.group(3));
							 String[] value = map.get(parameterKey);
							 if ("id".equals(column)) {
								 partialList.get(icounts).put("id", value[0]);
							 }else if("SMONo".equals(column)){
								 partialList.get(icounts).put("SMONo", value[0]);
							 }else if("SMOItemNo".equals(column)){
								 partialList.get(icounts).put("SMOItemNo", value[0]);
							 }else if("partsMaterialNo".equals(column)){
								 partialList.get(icounts).put("partsMaterialNo", value[0]);
							 }else if("times".equals(column)){
								 partialList.get(icounts).put("times", value[0]);
							 }else if("appendReason".equals(column)){
								 partialList.get(icounts).put("appendReason", value[0]);
							 }else if("requirementQty".equals(column)){
								 partialList.get(icounts).put("requirementQty", value[0]);
							 }else if("unitPrice".equals(column)){
								 partialList.get(icounts).put("unitPrice", value[0]);
							 }else if("currency".equals(column)){
								 partialList.get(icounts).put("currency", value[0]);
							 }								
						 }else if("if_sap_message_content_three".equals(table)){
							 String column = m.group(2);
						     int icounts = Integer.parseInt(m.group(3));
							 String[] value = map.get(parameterKey);
							 if ("id".equals(column)) {
								 orderList.get(icounts).put("id", value[0]);
							 }else if("STONo".equals(column)){
								 orderList.get(icounts).put("STONo", value[0]);
							 }else if("STOItemNo".equals(column)){
								 orderList.get(icounts).put("STOItemNo", value[0]);
							 }else if("orderQty".equals(column)){
								 orderList.get(icounts).put("orderQty", value[0]);
							 }else if("deliveryDate".equals(column)){
								 orderList.get(icounts).put("deliveryDate", value[0]);
							 }
						 }
					 }
				}
				
				
				content.put("OMRNotifiNo", req.getParameter("OMRNotifiNo"));
				content.put("SORCCode", req.getParameter("SORCCode"));
				content.put("partsConfirmDate", req.getParameter("partsConfirmDate"));
				content.put("partsConfirmTime", req.getParameter("partsConfirmTime"));
				
				List<Object> plist = new ArrayList<Object>();
				for(int i=0;i < partialList.size();i++){
					HashMap partialMap = partialList.get(i);
					String id = (String) partialMap.get("id");
					
					List<Object> olist = new ArrayList<Object>();
					for(int j=0;j < orderList.size();j++){
						HashMap orderMap = orderList.get(j);
						if(id.equals(orderMap.get("id"))){
							orderMap.remove("id");
							olist.add(orderMap);
						}
					}
					partialMap.put("STOItem", olist);
					partialMap.remove("id");
					plist.add(partialMap);
				}
				
				content.put("SMOItem", plist);
			}else if("approve".equals(kind)){
				content.put("itemReceiverFlag", req.getParameter("edit_item_receiver_flag"));
				content.put("OMRNotifiNo", req.getParameter("edit_omr_notifi_no"));
				content.put("SORCCode", req.getParameter("edit_sorc_code"));
				content.put("itemInputLineDate", req.getParameter("itemInputLineDate"));
				content.put("itemInputLineTime", req.getParameter("itemInputLineTime"));
			}else if("recept".equals(kind)){
				content.put("itemReceiverFlag", req.getParameter("edit_item_receiver_flag"));
				content.put("OMRNotifiNo", req.getParameter("edit_omr_notifi_no"));
				content.put("SORCCode", req.getParameter("edit_sorc_code"));
				content.put("RCCode", req.getParameter("edit_rc_code"));
				content.put("itemCode", req.getParameter("edit_item_code"));
				content.put("serialNo", req.getParameter("edit_series_no"));
				content.put("repairMethod", req.getParameter("edit_repair_method"));
				content.put("OCMRank", req.getParameter("edit_ocm_rank"));
				content.put("FSEOROCMShipDate", req.getParameter("edit_ocm_ship_date"));
				content.put("OSHRank", req.getParameter("edit_osh_rank"));
				content.put("agreeTime", req.getParameter("edit_agree_time"));
				content.put("hospital", req.getParameter("edit_hospital"));
				content.put("directlyToSORC", req.getParameter("edit_directly_to_sorc"));
				content.put("itemReceiverDate", req.getParameter("edit_item_receiver_date"));
				content.put("itemReceiverTime", req.getParameter("edit_item_receiver_time"));
				content.put("itemReceiverPerson", req.getParameter("edit_item_receiver_person"));
				content.put("itemFailureDescrip", req.getParameter("edit_item_failure_descrip"));
			}

			service.updateContent(if_sap_message_key, seq, content, conn);
			conn.commit();
			
			// 调用rvsIf
			HttpClient httpclient = new DefaultHttpClient();
			try {
				HttpGet request = new HttpGet("http://localhost:8080/rvsIf/rehandle/" + if_sap_message_key + "/" + seq);
				logger.info("finger:" + request.getURI());
				HttpResponse response = httpclient.execute(request);
				if (response != null) {
					logger.info("Response: " + response.getStatusLine());
					HttpEntity entity = response.getEntity();
					if (entity != null) {						
						String errMsg = convertStreamToString(entity);
						if (!"".equals(errMsg)) {
							MsgInfo msgInfo = new MsgInfo();
							msgInfo.setErrmsg(errMsg);
							errors.add(msgInfo);
						}
					}
				}

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		logger.info("InterfaceDataAction.doSave end");
	}

	/**
	 * 删除
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doDelete(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {

		logger.info("InterfaceDataAction.doDelete start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		InterfaceDataService service = new InterfaceDataService();

		String if_sap_message_key = req.getParameter("if_sap_message_key");
		String seq = req.getParameter("seq");
		if (!CommonStringUtil.isEmpty(if_sap_message_key)
				&& !CommonStringUtil.isEmpty(seq)) {
			service.deleteContent(if_sap_message_key, seq, conn);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		
		logger.info("InterfaceDataAction.doDelete end");
	}

	/**
	 * 忽略
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doIgnore(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res,
			SqlSessionManager conn) throws Exception {

		logger.info("InterfaceDataAction.doIgnore start");

		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

		Map<String, Object> listResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		InterfaceDataService service = new InterfaceDataService();

		String if_sap_message_key = req.getParameter("if_sap_message_key");
		String seq = req.getParameter("seq");
		if (!CommonStringUtil.isEmpty(if_sap_message_key)
				&& !CommonStringUtil.isEmpty(seq)) {
			service.ignoreContent(if_sap_message_key, seq, user, conn);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);
		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);
		
		logger.info("InterfaceDataAction.doIgnore end");
	}

	public static String convertStreamToString(HttpEntity entity) throws Exception {
		InputStream is = entity.getContent();
		InputStreamReader isr = new InputStreamReader(is,"UTF-8");
		BufferedReader reader = new BufferedReader(isr);
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
