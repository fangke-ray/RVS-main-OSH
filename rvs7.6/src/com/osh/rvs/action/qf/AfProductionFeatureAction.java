/**
 * 系统名：OSH-RVS<br>
 * 模块名：受理现品管理<br>
 * 机能名：间接作业记录<br>
 * @author 龚镭敏
 * @version 8.01
 */
package com.osh.rvs.action.qf;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.partial.WastePartialArrangementForm;
import com.osh.rvs.form.partial.WastePartialRecycleCaseForm;
import com.osh.rvs.form.qf.AfProductionFeatureForm;
import com.osh.rvs.service.AcceptFactService;
import com.osh.rvs.service.PauseFeatureService;
import com.osh.rvs.service.partial.WastePartialArrangementService;
import com.osh.rvs.service.partial.WastePartialRecycleCaseService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;

public class AfProductionFeatureAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	/**
	 * 受理画面初始表示处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void getByOperator(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("AfProductionFeatureAction.getByOperator start");

		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		// 取得当前进行中作业
		AcceptFactService service = new AcceptFactService();
		AfProductionFeatureForm processForm = service.getProcessOfOperator(user, conn);

		if (processForm != null) {
			// 如果是直接作业，取得标准工时
			if ("1".equals(processForm.getIs_working())) {
				Integer standard_minutes = service.getStandardMinutes(processForm, conn);
				if (standard_minutes != null) {
					processForm.setStandard_minutes("" + standard_minutes);
				}
			}

			callbackResponse.put("processForm", processForm);
		}

		String init = req.getParameter("init");

		if (init != null) {
			// 可做工位
			List<PositionEntity> afAbilities = user.getAfAbilities();
			callbackResponse.put("afAbilities", afAbilities);

			callbackResponse.put("pauseReasonGroup", PauseFeatureService.getPauseReasonIndirectGroupMap());
		}

		boolean isManager = user.getPrivacies().contains(RvsConsts.PRIVACY_PROCESSING)
				|| user.getPrivacies().contains(RvsConsts.PRIVACY_LINE)
				|| user.getPrivacies().contains(RvsConsts.PRIVACY_RECEPT_EDIT);
		callbackResponse.put("isManager", isManager);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", new ArrayList<MsgInfo>());
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);

		log.info("AfProductionFeatureAction.getByOperator end");
	}

	/**
	 * 受理添加记录实行处理
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void doSwitch(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("AfProductionFeatureAction.doSwitch start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		AcceptFactService service = new AcceptFactService();

		AfProductionFeatureForm processForm = service.switchTo(form, user, conn);

		callbackResponse.put("processForm", processForm);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", new ArrayList<MsgInfo>());
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("AfProductionFeatureAction.doSwitch end");
	}

	/**
	 * 作业记录停止（管理员：停止记录/操作人员：下班）
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void doEnd(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("AfProductionFeatureAction.doEnd start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		AcceptFactService service = new AcceptFactService();

		service.end(form, user, conn);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", new ArrayList<MsgInfo>());
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("AfProductionFeatureAction.doEnd end");
	}

	/**
	 * 取得本人零件订购单编辑记录+SAP零件单修改记录
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void getPartialOrderList(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{
		log.info("AfProductionFeatureAction.getPartialOrderList start");

		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		AcceptFactService service = new AcceptFactService();

		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		callbackResponse.put("today_partial_order_edit_by_self", service.getTodayPartialOrderEditBySelf(user.getOperator_id(), conn));

		callbackResponse.put("today_partial_order_edit_from_sap", service.getTodayPartialOrderEditFromSap(conn));

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", new ArrayList<MsgInfo>());
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("AfProductionFeatureAction.getPartialOrderList end");
	}

	/**
	 * 提交维修对象零件订单编辑记录
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doPartialOrder(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("AfProductionFeatureAction.doPartialOrder start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();

		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		AcceptFactService service = new AcceptFactService();

		boolean edited = service.editPartialOrder(req.getParameter("omr_notifi_no"), user, errors, conn);

		if(edited) {
			conn.commit();

			service.fingerOperatorRefresh(user.getOperator_id());
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);
		
		log.info("AfProductionFeatureAction.doPartialOrder end");
	}

	public void getWastePartial(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("AfProductionFeatureAction.getWastePartial start");
		// Ajax响应对象
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String goMaterialServiceRepaire = CodeListUtils.getGridOptions("material_service_repair");
		callbackResponse.put("goMaterialServiceRepaire", goMaterialServiceRepaire);

		WastePartialArrangementService wastePartialArrangementService = new WastePartialArrangementService();

		// 收集开始时候
		Calendar start = Calendar.getInstance();
		start.set(Calendar.HOUR_OF_DAY, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		start.set(Calendar.MILLISECOND, 0);

		// 收集结束时间
		Calendar end = Calendar.getInstance();
		end.add(Calendar.DATE, 1);
		end.set(Calendar.HOUR_OF_DAY, 0);
		end.set(Calendar.MINUTE, 0);
		end.set(Calendar.SECOND, 0);
		end.set(Calendar.MILLISECOND, 0);

		WastePartialArrangementForm wastePartialArrangementForm = new WastePartialArrangementForm();
		wastePartialArrangementForm.setCollect_time_start(DateUtil.toString(start.getTime(), DateUtil.DATE_PATTERN));
		wastePartialArrangementForm.setCollect_time_end(DateUtil.toString(end.getTime(), DateUtil.DATE_PATTERN));

		// 当日完成废弃零件整理维修对象
		List<WastePartialArrangementForm> materialList = wastePartialArrangementService.search(wastePartialArrangementForm, conn);
		callbackResponse.put("complate", materialList);

		// 未打包回收箱
		WastePartialRecycleCaseService wastePartialRecycleCaseService = new WastePartialRecycleCaseService();
		WastePartialRecycleCaseForm wastePartialRecycleCaseForm = new WastePartialRecycleCaseForm();
		wastePartialRecycleCaseForm.setPackage_flg("1");
		wastePartialRecycleCaseForm.setWaste_flg("1");

		List<WastePartialRecycleCaseForm> caseList = wastePartialRecycleCaseService.search(wastePartialRecycleCaseForm, conn);
		callbackResponse.put("caseList", caseList);

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);

		log.info("AfProductionFeatureAction.getWastePartial end");
	}

	public void wastePartialScan(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception {
		log.info("AfProductionFeatureAction.wastePartialScan start");

		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		// 维修对象ID
		String material_id = req.getParameter("material_id");

		AcceptFactService acceptFactService = new AcceptFactService();
		WastePartialArrangementService wastePartialArrangementService = new WastePartialArrangementService();
		WastePartialRecycleCaseService wastePartialRecycleCaseService = new WastePartialRecycleCaseService();

		// 检查扫描维修对象合法性
		MaterialEntity materialEntity = acceptFactService.checkMaterialId(material_id, errors, conn);
		if (errors.size() == 0) {
			// 机种
			String kind = materialEntity.getKind();
			// 等级
			Integer level = materialEntity.getLevel();

			// 回收箱用途种类
			String collectKind = null;
			// 能否收集标记
			Boolean collectFlg = true;

			WastePartialArrangementForm wastePartialArrangementForm = new WastePartialArrangementForm();
			wastePartialArrangementForm.setMaterial_id(material_id);
			// 废弃零件整理记录
			List<WastePartialArrangementForm> arrangementList = wastePartialArrangementService.search(wastePartialArrangementForm, conn);
			
			// 周边设备
			if ("07".equals(kind)) {
				// 设置回收箱用途种类 “2”表示周边设备
				collectKind = "2";
				
				MaterialForm materialForm = new MaterialForm();
				BeanUtil.copyToForm(materialEntity, materialForm, CopyOptions.COPYOPTIONS_NOEMPTY);
				callbackResponse.put("materialForm", materialForm);
			} else {// 内窥镜
				// 设置回收箱用途种类 “1”表示内窥镜
				collectKind = "1";

				int size = arrangementList.size();

				// S2/S3(可以收集2次)
				if (level!=null && (level == 2 || level == 3)) {
					if (size >= 2) {
						// 不能再次收集
						collectFlg = false;
					}
				} else {// 其他等级(只能收集一次)
					if (size >= 1) {
						// 不能再次收集
						collectFlg = false;
					}
				}
			}
			callbackResponse.put("collectKind", collectKind);
			callbackResponse.put("collectFlg", collectFlg);
			callbackResponse.put("arrangementList", arrangementList);

			// 能收集
			if (collectFlg) {
				// 查询回收箱
				WastePartialRecycleCaseForm wastePartialRecycleCaseForm = new WastePartialRecycleCaseForm();
				wastePartialRecycleCaseForm.setCollect_kind(collectKind);
				wastePartialRecycleCaseForm.setPackage_flg("1");
				wastePartialRecycleCaseForm.setWaste_flg("1");
				
				List<WastePartialRecycleCaseForm> caseList = wastePartialRecycleCaseService.search(wastePartialRecycleCaseForm, conn);
				callbackResponse.put("caseList", caseList);
			}
		}

		// 检查发生错误时报告错误信息
		callbackResponse.put("errors", errors);
		// 返回Json格式回馈信息
		returnJsonResponse(res, callbackResponse);

		log.info("AfProductionFeatureAction.wastePartialScan end");
	}

}
