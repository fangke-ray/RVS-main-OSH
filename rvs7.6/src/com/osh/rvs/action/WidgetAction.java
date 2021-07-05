/**
 * 系统名：OSH-RVS<br>
 * 模块名：系统管理<br>
 * 机能名：维修对象机种系统管理事件<br>
 * @author 龚镭敏
 * @version 0.01
 */
package com.osh.rvs.action;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.mapper.data.MaterialMapper;
import com.osh.rvs.service.MaterialTagService;
import com.osh.rvs.service.PauseFeatureService;
import com.osh.rvs.service.ProcessAssignService;
import com.osh.rvs.service.ProductionFeatureService;
import com.osh.rvs.service.SectionService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.common.util.BaseConst;
import framework.huiqing.common.util.CodeListUtils;

public class WidgetAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());

	/**
	 * 无指定初始表示处理
	 * 
	 * @param mapping
	 *            ActionMapping
	 * @param form
	 *            表单
	 * @param req
	 *            页面请求
	 * @param res
	 *            页面响应
	 * @param conn
	 *            数据库会话
	 * @throws Exception
	 */
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("WidgetAction.init start");

		// 迁移到页面
		actionForward = mapping.findForward(FW_INIT);

		log.info("WidgetAction.init end");
	}

	/**
	 * 暂停画面初始表示处理
	 * 
	 * @param mapping
	 *            ActionMapping
	 * @param form
	 *            表单
	 * @param req
	 *            页面请求
	 * @param res
	 *            页面响应
	 * @param conn
	 *            数据库会话
	 * @throws Exception
	 */
	public void pauseoperator(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("WidgetAction.pauseoperator start");

		// 可用链接设定到画面
		req.setAttribute("linkto", req.getParameter("linkto"));

		// 迁移到页面
		actionForward = mapping.findForward(req.getParameter(BaseConst.METHOD));

		log.info("WidgetAction.pauseoperator end");
	}

	/**
	 * 暂停画面初始表示处理
	 * 
	 * @param mapping
	 *            ActionMapping
	 * @param form
	 *            表单
	 * @param req
	 *            页面请求
	 * @param res
	 *            页面响应
	 * @param conn
	 *            数据库会话
	 * @throws Exception
	 */
	public void breakoperator(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("WidgetAction.breakoperator start");

		// 可用链接设定到画面
		req.setAttribute("linkto", req.getParameter("linkto"));

		// 迁移到页面
		actionForward = mapping.findForward(req.getParameter(BaseConst.METHOD));

		log.info("WidgetAction.breakoperator end");
	}

	/**
	 * 步骤画面初始表示处理
	 * 
	 * @param mapping
	 *            ActionMapping
	 * @param form
	 *            表单
	 * @param req
	 *            页面请求
	 * @param res
	 *            页面响应
	 * @param conn
	 *            数据库会话
	 * @throws Exception
	 */
	public void stepoperator(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("WidgetAction.stepoperator start");

		// 可用链接设定到画面
		req.setAttribute("linkto", req.getParameter("linkto"));

		// 迁移到页面
		actionForward = mapping.findForward(req.getParameter(BaseConst.METHOD));

		log.info("WidgetAction.stepoperator end");
	}

	/**
	 * 不良对处初始表示处理
	 * 
	 * @param mapping
	 *            ActionMapping
	 * @param form
	 *            表单
	 * @param req
	 *            页面请求
	 * @param res
	 *            页面响应
	 * @param conn
	 *            数据库会话
	 * @throws Exception
	 */
	public void nogoodedit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("WidgetAction.nogoodedit start");

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		req.setAttribute("interfaced", true);

		List<Integer> privacies = user.getPrivacies();
		if (privacies.contains(RvsConsts.PRIVACY_SCHEDULE)) {
			req.setAttribute("level", "2");
		} else if (privacies.contains(RvsConsts.PRIVACY_PROCESSING)) {
			req.setAttribute("level", "1");
		} else if (privacies.contains(RvsConsts.PRIVACY_LINE)) {
			req.setAttribute("level", "0");
		}

		// 迁移到页面
		actionForward = mapping.findForward(req.getParameter(BaseConst.METHOD));

		log.info("WidgetAction.nogoodedit end");
	}

	/**
	 * 工序再指派画面初始表示处理
	 * 
	 * @param mapping
	 *            ActionMapping
	 * @param form
	 *            表单
	 * @param req
	 *            页面请求
	 * @param res
	 *            页面响应
	 * @param conn
	 *            数据库会话
	 * @throws Exception
	 */
	public void rework(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("WidgetAction.rework start");

		// 可用链接设定到画面
		req.setAttribute("linkto", req.getParameter("linkto"));

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		List<Integer> privacies = user.getPrivacies();
		if (privacies.contains(RvsConsts.PRIVACY_SCHEDULE)) {
			req.setAttribute("level", "2");
		} else if (privacies.contains(RvsConsts.PRIVACY_PROCESSING)) {
			ProcessAssignService paService = new ProcessAssignService();
			String patOptions = paService.getGroupOptions(null, conn);
			req.setAttribute("patOptions", patOptions);
			req.setAttribute("level", "1");
		} else if (privacies.contains(RvsConsts.PRIVACY_LINE)) {
			req.setAttribute("level", "0");
		}

		// 迁移到页面
		actionForward = mapping.findForward(req.getParameter(BaseConst.METHOD));

		log.info("WidgetAction.rework end");
	}

	/**
	 * 维修对象详细画面
	 * 
	 * @param mapping
	 *            ActionMapping
	 * @param form
	 *            表单
	 * @param req
	 *            页面请求
	 * @param res
	 *            页面响应
	 * @param conn
	 *            数据库会话
	 * @throws Exception
	 */
	public void materialDetail(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("WidgetAction.materialDetail start");

		// 取得用户信息
		HttpSession session = req.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		List<Integer> privacies = user.getPrivacies();
		String editLevel = null;
		if (privacies.contains(RvsConsts.PRIVACY_SA) 
				|| privacies.contains(RvsConsts.PRIVACY_ADMIN)) {
			editLevel = "3";
			req.setAttribute("level", "3");
		} else if (privacies.contains(RvsConsts.PRIVACY_SCHEDULE)) {
			req.setAttribute("level", "2");
		} else if (privacies.contains(RvsConsts.PRIVACY_PROCESSING)
				|| privacies.contains(RvsConsts.PRIVACY_RECEPT_EDIT)
				|| privacies.contains(RvsConsts.PRIVACY_LINE)) {
			req.setAttribute("level", "1");
		} else {
			req.setAttribute("level", "0");
		}

		// 可用链接设定到画面
		req.setAttribute("linkto", req.getParameter("linkto"));
		String material_id = req.getParameter("material_id");
		MaterialMapper mdao = conn.getMapper(MaterialMapper.class);
		MaterialEntity mBean = mdao.getMaterialEntityByKey(material_id);

		req.setAttribute("hasDecom", true);

		if (mBean == null) {

		} else {
			if (mBean.getInline_time() == null) { // 尚未投线
				if (mBean.getLevel() == null) {
					req.setAttribute("lOptions", CodeListUtils.getSelectOptions("material_level", null, "(未定)", false));
				} else if (RvsUtils.isPeripheral(mBean.getLevel())) {
					req.setAttribute("lOptions", CodeListUtils.getSelectOptions("material_level_peripheral", null, "(未定)", false));
					req.setAttribute("hasDecom", false);
				} else {
					req.setAttribute("lOptions", CodeListUtils.getSelectOptions("material_level_endoscope", null, "(未定)", false));
				}
				req.setAttribute("sOptions", "");
			} else {
				if (mBean.getFix_type() == 1) {
					boolean isLightFix = RvsUtils.isLightFix(mBean.getLevel());
//					if (mBean.getLevel()==9 || mBean.getLevel()==91 || mBean.getLevel()==92 || mBean.getLevel()==93) {
					if (isLightFix) {
						req.setAttribute("lOptions", CodeListUtils.getSelectOptions("material_level_light", null, null, false));
					} else if (RvsUtils.isPeripheral(mBean.getLevel())) {
						req.setAttribute("lOptions", CodeListUtils.getSelectOptions("material_level_peripheral", null, null, false));
						req.setAttribute("hasDecom", false);
					} else {
						req.setAttribute("lOptions", CodeListUtils.getSelectOptions("material_level_heavy", null, null, false));
					}
				} else {
					req.setAttribute("lOptions", CodeListUtils.getSelectOptions("material_level_cell", null, null, false));
				}
				SectionService sService = new SectionService();
				req.setAttribute("sOptions", sService.getOptions(conn, null));
				ProcessAssignService paService = new ProcessAssignService();
				String patOptions = paService.getGroupOptions(null, conn);
				req.setAttribute("patOptions", patOptions);
			}
			if ("3".equals(editLevel)) {
				req.setAttribute("lOptions", CodeListUtils.getSelectOptions("material_level", null, "(未定)", false));
			}


			boolean isAnmlExp = false;
			if (mBean.getOutline_time() != null) { // 完成
				isAnmlExp = MaterialTagService.getAnmlMaterials(conn).contains(material_id);
			} else {
				MaterialTagService mtService = new MaterialTagService(); 
				List<Integer> l = mtService.checkTagByMaterialId(material_id, MaterialTagService.TAG_ANIMAL_EXPR, conn);
				isAnmlExp = l.size() > 0;
			}
			if (isAnmlExp) {
				req.setAttribute("showDjLoan", "yes");
			}

		}

		// 迁移到页面
		actionForward = mapping.findForward(req.getParameter(BaseConst.METHOD));

		log.info("WidgetAction.materialDetail end");
	}

	/**
	 * 清除工位画面
	 * 
	 * @param mapping
	 *            ActionMapping
	 * @param form
	 *            表单
	 * @param req
	 *            页面请求
	 * @param res
	 *            页面响应
	 * @param conn
	 *            数据库会话
	 * @throws Exception
	 */
	public void cleanPosition(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("WidgetAction.cleanPosition start");

		// 可用链接设定到画面
		req.setAttribute("linkto", req.getParameter("linkto"));
		String material_id = req.getParameter("material_id");
		ProductionFeatureService pfService = new ProductionFeatureService();

		Map<String, String> codeMap = pfService.getPoistionsOfMaterial(material_id, conn);

		req.setAttribute("pOptions", CodeListUtils.getSelectOptions(codeMap, null, "", false));

		req.setAttribute("fromAdmin", true);

		req.setAttribute("model_name", req.getParameter("model_name"));

		req.setAttribute("serial_no", req.getParameter("serial_no"));

		// 迁移到页面
		actionForward = mapping.findForward(req.getParameter(BaseConst.METHOD));

		log.info("WidgetAction.cleanPosition end");
	}

	/**
	 * 照片编辑初始表示处理
	 * 
	 * @param mapping
	 *            ActionMapping
	 * @param form
	 *            表单
	 * @param req
	 *            页面请求
	 * @param res
	 *            页面响应
	 * @param conn
	 *            数据库会话
	 * @throws Exception
	 */
	public void photoEditor(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("WidgetAction.photoEditor start");

		// 可用链接设定到画面
		req.setAttribute("linkto", req.getParameter("linkto"));

		String photo_uuid = req.getParameter("photo_uuid");

		if (isEmpty(photo_uuid)) {
			req.setAttribute("no_image", "没有图片");
			req.setAttribute("photo_uuid", "");
		} else {
			req.setAttribute("no_image", "");
			req.setAttribute("photo_uuid", photo_uuid);
		}

		// 迁移到页面
		actionForward = mapping.findForward(req.getParameter(BaseConst.METHOD));

		log.info("WidgetAction.photoEditor end");
	}

	/**
	 * 暂停理由画面
	 * 
	 * @param mapping
	 *            ActionMapping
	 * @param form
	 *            表单
	 * @param req
	 *            页面请求
	 * @param res
	 *            页面响应
	 * @param conn
	 *            数据库会话
	 * @throws Exception
	 */
	public void pauseWorkreport(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,
			SqlSession conn) throws Exception {

		log.info("WidgetAction.pauseWorkreport start");

		// 可用链接设定到画面
		req.setAttribute("linkto", req.getParameter("linkto"));

		req.setAttribute("pause_reasons", PauseFeatureService.getPauseReasonSelectOptions());

		req.setAttribute("pause_comments", PauseFeatureService.getPauseReasonSelectComments());

		// 迁移到页面
		actionForward = mapping.findForward(req.getParameter(BaseConst.METHOD));

		log.info("WidgetAction.pauseWorkreport end");
	}
}
