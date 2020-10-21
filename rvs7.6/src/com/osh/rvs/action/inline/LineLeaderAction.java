/**
 * 系统名：OSH-RVS<br>
 * 模块名：系统管理<br>
 * 机能名：维修对象线长线上管理事件<br>
 * @author 龚镭敏
 * @version 0.01
 */
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
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.mapper.data.MaterialMapper;
import com.osh.rvs.mapper.inline.ProductionFeatureMapper;
import com.osh.rvs.service.ProductionFeatureService;
import com.osh.rvs.service.inline.LineLeaderService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.action.Privacies;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;

/**
 * 线长管理
 * @author Gong
 *
 */
public class LineLeaderAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	private LineLeaderService service = new LineLeaderService();

	/**
	 * 工程画面初始表示处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("LineLeaderAction.init start");
		
		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("LineLeaderAction.init end");
	}

	/**
	 * 工程画面初始取值处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void jsinit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("LineLeaderAction.jsinit start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		String section_id = user.getSection_id();
		String line_id = user.getLine_id();
		if (CommonStringUtil.isEmpty(line_id)) {
			line_id = "00000000014";
			user.setLine_id(line_id);
			// 更新会话用户信息
			session.setAttribute(RvsConsts.SESSION_USER, user);
		}

		// 取得今日计划暨作业对象一览
		listResponse.put("performance", service.getPerformanceList(section_id, line_id, null, null, null, conn));

		service.getChartContent(user, conn, listResponse);

		// 文字选项
		listResponse.put("opt_level" ,CodeListUtils.getGridOptions("material_level"));
		listResponse.put("opt_operate_result", CodeListUtils.getGridOptions("material_operate_result"));
		listResponse.put("opt_px" ,CodeListUtils.getGridOptions("material_px"));

		// 线长处理接收工位
		if ("00000000012".equals(line_id)) {
			listResponse.put("receivePos" , "252");
		}
		else if ("00000000013".equals(line_id)) {
			listResponse.put("receivePos" , "321");
		}
		else if ("00000000014".equals(line_id)) {
			listResponse.put("receivePos" , "400");
		}

		// 1课有分线
		if ("00000000001".equals(section_id)) {
			listResponse.put("division_flg" , "1");
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", new ArrayList<MsgInfo>());

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("LineLeaderAction.jsinit end");
	}

	/**
	 * 在线品一览处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void refreshList(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("LineLeaderAction.refreshList start");
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		String position_id = req.getParameter("position_id");
		String today = req.getParameter("today");
		String checked_group = req.getParameter("checked_group");
		String section_id = user.getSection_id();
		String line_id = user.getLine_id();

		// 取得今日计划暨作业对象一览
		listResponse.put("performance", service.getPerformanceList(section_id, line_id, position_id, checked_group, today, conn));

		// service.getChartContent(section_id, line_id, conn, listResponse);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", new ArrayList<MsgInfo>());

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("LineLeaderAction.refreshList end");
	}

	/**
	 * 线长加急处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void doexpedite(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("LineLeaderAction.doexpedite start");

		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检查发生错误时报告错误信息
		listResponse.put("errors", new ArrayList<MsgInfo>());

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		String line_id = user.getLine_id();

		String material_id = req.getParameter("material_id");

		service.switchLeaderExpedite(material_id, line_id, conn);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("LineLeaderAction.doexpedite end");
	}

	/**
	 * 取得不良信息处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void getwarning(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{
		log.info("LineLeaderAction.getwarning start");

		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", new ArrayList<MsgInfo>());

		String material_id = req.getParameter("material_id");
		String position_id = req.getParameter("position_id");

		listResponse.put("warning", service.getWarning(material_id, user.getOperator_id(), position_id, conn));

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("LineLeaderAction.getwarning end");
	}

	/**
	 * 线长零件订购处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void doleaderorder(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("LineLeaderAction.doleaderorder start");

		Map<String, Object> listResponse = new HashMap<String, Object>();
		// 触发列表
		List<String> triggerList = new ArrayList<String>();

		// 检查发生错误时报告错误信息
		listResponse.put("errors", new ArrayList<MsgInfo>());

		String material_id = req.getParameter("material_id");
		String position_id = req.getParameter("position_id");
		String model_name = req.getParameter("model_name");

		LoginData loginData = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

		if ("00000000109".equals(position_id)) { // 新241工位
			// “零件订购”工位线长处理
			service.partialResolve(material_id, model_name, loginData.getSection_id(), position_id, conn, loginData);
			// 触发之后工位
			ProductionFeatureService pfService = new ProductionFeatureService();
			// 取得刚才完成的作业信息（主要需要rework）
			ProductionFeatureEntity workingPf = new ProductionFeatureEntity();
			workingPf.setMaterial_id(material_id);
			workingPf.setPosition_id(position_id);
			workingPf.setSection_id(loginData.getSection_id());
			workingPf.setOperate_result(RvsConsts.OPERATE_RESULT_FINISH);
			workingPf = pfService.searchProductionFeatureOne(workingPf, conn);
			pfService.fingerNextPosition(material_id , workingPf, conn, triggerList);
		} else if("00000000013".equals(loginData.getLine_id())) {
			// NS 线长提交零件订购单
			MaterialMapper mDao = conn.getMapper(MaterialMapper.class);
			mDao.updateMaterialNsPartial(material_id);

			// 试图触发241工位
			ProductionFeatureService pfService = new ProductionFeatureService();
			ProductionFeatureEntity fsPf = new ProductionFeatureEntity();

			ProductionFeatureMapper ppDao = conn.getMapper(ProductionFeatureMapper.class);
			// 本工位作业状态等待的记录中取得Rework
			int rework = 0;
			rework = ppDao.getReworkCount(material_id);

			fsPf.setMaterial_id(material_id);
			fsPf.setPosition_id("00000000109"); // 新241工位
			fsPf.setRework(rework); // 返工号
			fsPf.setSection_id(loginData.getSection_id());
			// 指定251工位开始(关联检查)
			pfService.fingerSpecifyPosition(material_id, false, fsPf, triggerList, conn);
		} else {
			// 零件再订购
		}

		if (triggerList.size() > 0 && errors.size() == 0) {
			conn.commit();
			RvsUtils.sendTrigger(triggerList);
		}

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("LineLeaderAction.doleaderorder end");
	}

	/**
	 * 线长零件接受处理
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	@Privacies(permit={1, 0})
	public void doleaderreceive(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("LineLeaderAction.doleaderreceive start");

		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 触发列表
		List<String> triggerList = new ArrayList<String>();

		// 检查发生错误时报告错误信息
		listResponse.put("errors", new ArrayList<MsgInfo>());

		String material_id = req.getParameter("material_id");
		String position_id = req.getParameter("position_id");
		String model_name = req.getParameter("model_name");

		LoginData loginData = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

		// “零件订购”工位线长处理
		ProductionFeatureEntity workingPf = service.partialResolve(material_id, model_name, loginData.getSection_id(), position_id, conn, loginData);
		// 触发之后工位
		ProductionFeatureService pfService = new ProductionFeatureService();
		// 取得刚才完成的作业信息（主要需要rework）
		workingPf.setOperate_result(RvsConsts.OPERATE_RESULT_FINISH);
		workingPf = pfService.searchProductionFeatureOne(workingPf, conn);
		pfService.fingerNextPosition(material_id, workingPf, conn, triggerList);

		if (triggerList.size() > 0 && errors.size() == 0) {
			conn.commit();
			RvsUtils.sendTrigger(triggerList);
		}

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("LineLeaderAction.doleaderreceive end");
	}

	@Privacies(permit={1, 0})
	public void refreshChart(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{
		log.info("LineLeaderAction.refreshChart start");

		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		// 检查发生错误时报告错误信息
		listResponse.put("errors", new ArrayList<MsgInfo>());

		String line_id = user.getLine_id();
		if (CommonStringUtil.isEmpty(line_id)) {
			line_id = "00000000014";
			user.setLine_id(line_id);
			// 更新会话用户信息
			session.setAttribute(RvsConsts.SESSION_USER, user);
		}

		service.getChartContent(user, conn, listResponse);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("LineLeaderAction.refreshChart end");
	}
	
}
