package com.osh.rvs.action.inline;

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
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.inline.SteelWireContainerWashProcessForm;
import com.osh.rvs.service.OperatorService;
import com.osh.rvs.service.PartialService;
import com.osh.rvs.service.inline.SteelWireContainerWashProcessService;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.validator.Validators;

/**
 * @Description: 钢丝固定件清洗记录
 * @author liuxb
 * @date 2018-5-14 下午1:17:15
 */
public class SteelWireContainerWashProcessAction extends BaseAction {
	private Logger log = Logger.getLogger(getClass());
	
	/**
	 * 初始化
	 * @param mapping ActionMapping
	 * @param form 表单
	 * @param request 请求
	 * @param response 响应
	 * @param conn 数据库会话
	 * @throws Exception
	 */
	public void init(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response,SqlSession conn)throws Exception{
		log.info("SteelWireContainerWashProcessAction.init start");
		
		OperatorService operatorService = new OperatorService();
		
		// 责任人
		String oReferChooser = operatorService.getAllOperatorName(conn);
		request.setAttribute("oReferChooser", oReferChooser);
		
		// 当前日期
		request.setAttribute("curDate",DateUtil.toString(Calendar.getInstance().getTime(), DateUtil.DATE_PATTERN));
		
		
		// 取得用户信息
		HttpSession session = request.getSession();
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		
		if(user.getPrivacies().contains(RvsConsts.PRIVACY_PROCESSING)){
			request.setAttribute("privacy", "processing");
		}
		
		actionForward=mapping.findForward(FW_INIT);

		log.info("SteelWireContainerWashProcessAction.init end");
	}
	
	/**
	 * 钢丝固定件清洗记录条件查询
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{
		log.info("SteelWireContainerWashProcessAction.search start");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 检索条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_PASSEMPTY);

		List<MsgInfo> errors = v.validate();
		if(errors.size() == 0){
			SteelWireContainerWashProcessService service = new SteelWireContainerWashProcessService();
			List<SteelWireContainerWashProcessForm> list = service.search(form, conn);
			
			if(req.getParameter("first")!=null){
				PartialService partialService = new PartialService();
				List<Map<String, String>> paetialList = partialService.getPartialAutoCompletes(null, conn);
				listResponse.put("sPartialCode", paetialList);// 零件集合
			}
			
			listResponse.put("finished", list);
		}
		
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("SteelWireContainerWashProcessAction.search end");
	}
	
	/**
	 * 新建钢丝固定件清洗记录
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doInsert(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn)throws Exception{
		log.info("SteelWireContainerWashProcessAction.doInsert start");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		v.delete("process_time");
		v.delete("operator_id");

		List<MsgInfo> errors = v.validate();
		if(errors.size() == 0){
			SteelWireContainerWashProcessService service = new SteelWireContainerWashProcessService();
			service.insert(form, req, conn,errors);
		}
		
		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("SteelWireContainerWashProcessAction.doInsert end");
	}
	
	/**
	 * 更新钢丝固定件清洗记录
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void doUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSessionManager conn)throws Exception{
		log.info("SteelWireContainerWashProcessAction.doUpdate start");
		// Ajax回馈对象	
		Map<String, Object> listResponse = new HashMap<String, Object>();

		// 更新条件表单合法性检查
		Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);
		v.delete("operator_id");
		
		List<MsgInfo> errors = v.validate();
		if(errors.size() == 0){
			SteelWireContainerWashProcessService service = new SteelWireContainerWashProcessService();
			service.update(form, conn,errors);
		}

		// 检查发生错误时报告错误信息
		listResponse.put("errors", errors);

		// 返回Json格式响应信息
		returnJsonResponse(res, listResponse);

		log.info("SteelWireContainerWashProcessAction.doUpdate end");
	}
	
}
