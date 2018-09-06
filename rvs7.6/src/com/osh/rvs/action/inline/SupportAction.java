package com.osh.rvs.action.inline;

import java.util.ArrayList;
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
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.mapper.inline.ProductionFeatureMapper;
import com.osh.rvs.mapper.inline.SupportMapper;
import com.osh.rvs.service.PauseFeatureService;
import com.osh.rvs.service.inline.PositionPanelService;
import com.osh.rvs.service.inline.SupportService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;

public class SupportAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());
	private SupportService service = new SupportService();
	private PositionPanelService ppService = new PositionPanelService();
	private PauseFeatureService bfService = new PauseFeatureService();

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
	@Privacies(permit = { 1, 0 })
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("SupportAction.init start");

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("SupportAction.init end");
	}

	/**
	 * 辅助工位画面初始取值处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void jsinit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("SupportAction.jsinit start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		String section_id = user.getSection_id();

		List<MsgInfo> infoes = new ArrayList<MsgInfo>();

		// 设定暂停选项
		String pauseOptions = "";

		pauseOptions += PauseFeatureService.getPauseReasonSelectOptions();
		listResponse.put("pauseOptions", pauseOptions);

		// 判断是否有在辅助中的维修对象
		ProductionFeatureEntity workingPf = ppService.getWorkingOrSupportingPf(user, conn);
		// 进行中的话
		if (workingPf != null) {
			if (RvsConsts.OPERATE_RESULT_SUPPORT == workingPf.getOperate_result()) {
				// 取得作业信息
				service.getProccessingData(listResponse, workingPf.getMaterial_id(), workingPf, user, conn);

				//spent_mins
				listResponse.put("action_time", DateUtil.toString(workingPf.getAction_time(), "HH:mm:ss"));

				listResponse.put("section_name", user.getSection_name());
				listResponse.put("main_line", workingPf.getLine_name());
				listResponse.put("main_position", workingPf.getPosition_name());
				listResponse.put("main_operator", workingPf.getOperator_name());

				String process_code = workingPf.getProcess_code();

				// 判断是否有特殊页面效果
				String special_forward = PathConsts.POSITION_SETTINGS.getProperty("page." + process_code);

				// 取得工程检查票
				if (!"simple".equals(special_forward) && !"result".equals(special_forward)) {
					workingPf.setProcess_code(null);
					PositionPanelService.getPcses(listResponse, workingPf, workingPf.getLine_id(), conn);
				}

			} else {
				MsgInfo msginfo = new MsgInfo();
				msginfo.setErrcode("info.linework.workingRemain");
				msginfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.workingRemain",
						(workingPf.getSection_name() == null ? "" : workingPf.getSection_name()) + workingPf.getProcess_code() + workingPf.getPosition_name()));
				infoes.add(msginfo);
				listResponse.put("redirect", "panel.do");
			}

			// 页面设定为编辑模式
			listResponse.put("workstauts", "1");
		} else {
			service.getCanSupport(listResponse, section_id, user.getLine_id(), conn);
			// 准备中
			listResponse.put("workstauts", "0");
		}

		listResponse.put("section_name", user.getSection_name());

		// 检查发生错误时报告错误信息
		listResponse.put("errors", infoes);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("SupportAction.jsinit end");
	}

	@Privacies(permit={0})
	public void dostart(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("SupportAction.dostart start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		String position_id = req.getParameter("position_id");
		String operator_id = req.getParameter("operator_id");

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		String section_id = user.getSection_id();

		// 判断辅助维修对象在等待区，并返回这一条作业信息
		ProductionFeatureEntity supportingPf = service.checkMaterialId(position_id, operator_id, section_id, errors, conn);

		if (supportingPf == null) {
			MsgInfo msginfo = new MsgInfo();
			msginfo.setErrcode("info.linework.supportObjectLost");
			msginfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.linework.supportObjectLost"));
		}

		if (errors.size() == 0) {
			service.getProccessingData(listResponse, supportingPf.getMaterial_id(), supportingPf, user, conn);

			// 新建一条辅助中作业
			SupportMapper sdao = conn.getMapper(SupportMapper.class);
			ProductionFeatureMapper pfdao = conn.getMapper(ProductionFeatureMapper.class);

			supportingPf.setOperator_id(user.getOperator_id());
			supportingPf.setPace(sdao.getSupportPace(supportingPf));
			pfdao.supportProductionFeature(supportingPf);

			// 只要开始做，就结束掉本人所有的暂停信息。
			bfService.finishPauseFeature(null, section_id, user.getPosition_id(), user.getOperator_id(), conn);

			String process_code = supportingPf.getProcess_code();

			// 判断是否有特殊页面效果
			String special_forward = PathConsts.POSITION_SETTINGS.getProperty("page." + process_code);

			// 取得工程检查票
			if (!"simple".equals(special_forward) && !"result".equals(special_forward)) {
				supportingPf.setProcess_code(null);
				PositionPanelService.getPcses(listResponse, supportingPf, supportingPf.getLine_id(), conn);
			}

			listResponse.put("main_line", supportingPf.getLine_name());
			listResponse.put("main_position", supportingPf.getPosition_name());
			listResponse.put("main_operator", supportingPf.getOperator_name());
		}

		// user.setSection_id(section_id);
		
		listResponse.put("section_name", user.getSection_name());

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("SupportAction.dostart end");
	}

	@Privacies(permit={0})
	public void dofinish(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("SupportAction.dofinish start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		List<MsgInfo> errors = new ArrayList<MsgInfo>();

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		// 判断辅助维修对象在等待区，并返回这一条作业信息
		ProductionFeatureEntity supportingPf = service.getSupportingPf(user, conn);

		// 完成本次辅助
		ProductionFeatureMapper pfdao = conn.getMapper(ProductionFeatureMapper.class);
		pfdao.finishProductionFeature(supportingPf);

		service.getCanSupport(listResponse, user.getSection_id(), user.getLine_id(), conn);

		// 准备中
		listResponse.put("workstauts", "0");
		listResponse.put("section_name", user.getSection_name());

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("SupportAction.dofinish end");
	}
}
