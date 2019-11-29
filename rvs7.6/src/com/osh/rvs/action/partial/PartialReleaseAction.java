package com.osh.rvs.action.partial;

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
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.partial.MaterialPartialEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.partial.FactPartialReleaseForm;
import com.osh.rvs.form.partial.MaterialPartialDetailForm;
import com.osh.rvs.form.partial.MaterialPartialForm;
import com.osh.rvs.form.qf.AfProductionFeatureForm;
import com.osh.rvs.mapper.partial.MaterialPartialMapper;
import com.osh.rvs.service.AcceptFactService;
import com.osh.rvs.service.MaterialPartialService;
import com.osh.rvs.service.MaterialService;
import com.osh.rvs.service.inline.ForSolutionAreaService;
import com.osh.rvs.service.partial.FactPartialReleaseService;
import com.osh.rvs.service.partial.PartialReleaseService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.validator.Validators;

/**
 * 零件发放
 * 
 * @author lxb
 * 
 */
public class PartialReleaseAction extends BaseAction {
	private Logger log=Logger.getLogger(getClass());
	public void  init(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn ){
		log.info("PartialReleaseAction.init start");
		
		request.setAttribute("goMaterial_level_inline",CodeListUtils.getGridOptions("material_level"));
		// 取得登录用户权限
		LoginData user = (LoginData) request.getSession().getAttribute(RvsConsts.SESSION_USER);
		List<Integer> privacies = user.getPrivacies();
		
		// 零件管理员
		if (privacies.contains(RvsConsts.PRIVACY_PARTIAL_MANAGER)) {
			request.setAttribute("privacy", "pm");
		}
		
		actionForward=mapping.findForward(FW_INIT);
		
		
		log.info("PartialReleaseAction.init end");
	}

	public void search(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn){
		log.info("PartialReleaseAction.search start");
		
		// 对Ajax的响应
		Map<String,Object> listResponse=new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		
		PartialReleaseService service=new PartialReleaseService();

		List<MaterialPartialForm> responseFormList= service.searchMaterialPartialRelease(form,request,conn);
		listResponse.put("responseFormList", responseFormList);
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		
		log.info("PartialReleaseAction.search end");
	}
	
	/**
	 * 维修对象发放详细信息
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param conn
	 */
	public void searchMaterialPartialDetail(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn){
		log.info("PartialReleaseAction.searchMaterialPartialDetail start");
		
		// 对Ajax的响应
		Map<String,Object> listResponse=new HashMap<String, Object>();
		// 错误信息集合
		List<MsgInfo> errors = new ArrayList<MsgInfo>();
		PartialReleaseService service=new PartialReleaseService();
		
		String occur_times=request.getParameter("occur_times");
		MaterialForm responseForm = service.searchMaterialPartialDetail(form, occur_times, conn);
		
		List<MaterialPartialDetailForm>  responseList=service.secrchPartialOfRelease(form, conn);
		
		listResponse.put("responseForm", responseForm);
		listResponse.put("responseList", responseList);
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);
		
		log.info("PartialReleaseAction.searchMaterialPartialDetail end");
	}
	
	public void doUpdateWaitingQuantityAndStatus(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSessionManager conn) throws Exception{
		log.info("PartialReleaseAction.doUpdateWaitingQuantity start");
		
		// 对Ajax的响应
		Map<String,Object> listResponse=new HashMap<String, Object>();
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);
		List<MsgInfo> errors = v != null ? v.validate(): new ArrayList<MsgInfo>();

		PartialReleaseService service=new PartialReleaseService();
		
		String flag=request.getParameter("flag");

		if(errors.size()==0){
			// 取得用户信息
			HttpSession session = request.getSession();
			LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

			if (!"check".equals(flag)) { // 正式发放
				service.updateWaitingQuantityAndStatus(form, request.getParameterMap(), user.getOperator_id(), conn ,errors);

				if(errors.size()==0){
					MaterialPartialService mpService = new MaterialPartialService();
					MaterialPartialEntity materialPartialEntity = new MaterialPartialEntity();
					BeanUtil.copyToBean(form, materialPartialEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
					mpService.updateBoFlgWithDetail(materialPartialEntity, conn);
					// service.updateBoFlg(form,conn,flag);

					ForSolutionAreaService fsoService = new ForSolutionAreaService();
					if ("big".equals(flag)) {
						boolean isNoBo = checkOfCN(materialPartialEntity.getMaterial_id(), conn);
						// 追加大单也拿待解决区域
						if (materialPartialEntity.getOccur_times() > 1) {
							// 检查工位上BO零件为解除
							fsoService.solveBo(materialPartialEntity.getMaterial_id(), materialPartialEntity.getOccur_times(), user.getOperator_id(), conn);
						} else if (materialPartialEntity.getOccur_times() == 1 && isNoBo) {
//							List<String> triggerList = new ArrayList<String>();
//							// 计算预估完成日
//							triggerList.add("http://localhost:8080/rvspush/trigger/expected_finish_time/"
//									+ materialPartialEntity.getMaterial_id() + "/1");
	//
//							conn.commit();
//							RvsUtils.sendTrigger(triggerList);
						}

						if (isNoBo) {
							/**零件出库**/
							AcceptFactService acceptFactService = new AcceptFactService();
							FactPartialReleaseService factPartialReleaseService = new FactPartialReleaseService();

							AfProductionFeatureForm afPfForm = acceptFactService.getUnFinish(user.getOperator_id(), conn);
							if (afPfForm != null) {
								String afPfKey = afPfForm.getAf_pf_key();
								List<MaterialPartialDetailForm> list = service.countQuantityOfKind(form, conn);

								for (MaterialPartialDetailForm item : list) {
									FactPartialReleaseForm factPartialReleaseForm = new FactPartialReleaseForm();
									factPartialReleaseForm.setAf_pf_key(afPfKey);
									factPartialReleaseForm.setMaterial_id(item.getMaterial_id());
									factPartialReleaseForm.setSpec_kind(item.getSpec_kind());
									factPartialReleaseForm.setQuantity(item.getQuantity());
									
									//判断当前维修对象零件出库作业数是否存在
									FactPartialReleaseForm dbForm = factPartialReleaseService.getPartialRelease(factPartialReleaseForm, conn);
									if(dbForm!=null){
										Integer existQuantity = Integer.valueOf(dbForm.getQuantity());
										Integer quantity = Integer.valueOf(factPartialReleaseForm.getQuantity());
										
										Integer totalQuantity = existQuantity + quantity;
										factPartialReleaseForm.setQuantity(totalQuantity.toString());
										//更新零件出库作业数
										factPartialReleaseService.update(factPartialReleaseForm, conn);
									}else{
										//新建零件出库作业数
										factPartialReleaseService.insert(factPartialReleaseForm, conn);
									}
								}
							}

							// 零件发放者完成321工位
							service.finishNsPartialRelease(materialPartialEntity.getMaterial_id(), user, conn);
						}
					} else {
						// 检查工位上BO零件为解除
						fsoService.solveBo(materialPartialEntity.getMaterial_id(), materialPartialEntity.getOccur_times(), user.getOperator_id(), conn);
					}

					// TODO check negative 防止同时发放
				}
			} else { // 判定是否BO
				service.checkBoFlg(form, request.getParameterMap(), conn);
			}
		}

		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(response, listResponse);

		log.info("PartialReleaseAction.doUpdateWaitingQuantity end");
	}

	/**
	 * 建立工程BO进入PA
	 * 内窥镜判断分解 和 NS非别是否有BO
	 * @param material_id
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	private boolean checkOfCN(String material_id, SqlSessionManager conn) throws Exception {
		// 检查工位上BO零件
		ForSolutionAreaService fsoService = new ForSolutionAreaService();
		MaterialService mService = new MaterialService();
		String bo_partials = "";
		MaterialPartialMapper mpMapper = conn.getMapper(MaterialPartialMapper.class);

		// TODO
		MaterialEntity me = mService.loadSimpleMaterialDetailEntity(conn, material_id);
		if (!"06".equals(me.getKind()) && !"07".equals(me.getKind())) {

			// 252工位则判断全工程内是否有BO
			bo_partials = mpMapper.getBoPartialOfLineOfPosition(material_id, "00000000021");
			if (!CommonStringUtil.isEmpty(bo_partials)) {
				// 判断是否现有该工程的BO待解决记录 TODO
				fsoService.create(material_id, bo_partials, 1, "00000000021", conn, true);
				return false;
			}

			// 321工位则判断全工程内是否有BO
			bo_partials = mpMapper.getBoPartialOfLineOfPosition(material_id, "00000000027");
			if (!CommonStringUtil.isEmpty(bo_partials)) {
				fsoService.create(material_id, bo_partials, 1, "00000000027", conn, true);
				return false;
			}
		} else {
			// 400工位则判断全工程内是否有BO
			bo_partials = mpMapper.getBoPartialOfLineOfPosition(material_id, "00000000032");
			if (!CommonStringUtil.isEmpty(bo_partials)) {
				fsoService.create(material_id, bo_partials, 1, "00000000032", conn, true);
				return false;
			}
		}
		return true;

	}

	/**
	 * 删除零件
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param req 页面请求
	 * @param res 页面响应
	 * @param conn 数据库连接
	 * @throws Exception
	 */
	public void doDelete(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn) throws Exception{
		log.info("PartialReleaseAction.doDelete start");
		
		/* Ajax回馈对象 */
		Map<String, Object> callResponse = new HashMap<String, Object>();
		List<MsgInfo> infos = new ArrayList<MsgInfo>();

		PartialReleaseService service=new PartialReleaseService();
		String flag=req.getParameter("flag");
		service.deletePartial(form, req.getParameterMap(), conn,flag);
		
		/* errors */
		callResponse.put("errors", infos);
		returnJsonResponse(res, callResponse);

		log.info("PartialReleaseAction.doDelete end");
	}
	
}
