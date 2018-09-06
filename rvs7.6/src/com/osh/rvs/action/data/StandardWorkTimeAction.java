package com.osh.rvs.action.data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.service.ModelService;
import com.osh.rvs.service.StandardWorkTimeService;
import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;

public class StandardWorkTimeAction extends BaseAction {

	private Logger log = Logger.getLogger(getClass());	
	

	/**
	 * 标准工时画面显示
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res, SqlSession conn) throws Exception{

		log.info("StandardWorkTimeAction.init start");		
		
		actionForward = mapping.findForward(FW_INIT);	
		ModelService service = new ModelService();
		String mReferChooser = service.getOptions(conn);
		/*型号*/
		req.setAttribute("mReferChooser", mReferChooser);	
		
		/*等级*/
		req.setAttribute("sMaterial_level_inline", CodeListUtils.getSelectOptions("material_level_inline",null,""));

		log.info("StandardWorkTimeAction.init end");
	}
	/**
	 * 标准工时参考
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @param conn
	 * @throws Exception
	 */
	public void search(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,SqlSession conn) throws Exception {
		log.info("StandardWorkTimeAction.search start");
	
		Map<String, Object> callbackResponse = new HashMap<String, Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>(); 
		StandardWorkTimeService service = new StandardWorkTimeService();			;

		List<PositionEntity> retData = null;
		//检查前台的型号和等级是否为空
		service.checkInput(req, errors);

		if (errors.size() == 0) {
			retData = service.getData(req.getParameter("model_id"),req.getParameter("level"),conn);	
		}

		callbackResponse.put("retData",retData);
		callbackResponse.put("errors", errors);

		returnJsonResponse(res, callbackResponse);
		log.info("StandardWorkTimeAction.search end");
	}
	public void changeRank(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res,SqlSession conn) throws Exception {
		log.info("StandardWorkTimeAction.changeRank.start");
        
		Map<String,Object> ajaxResponse = new HashMap<String,Object>();
		List<MsgInfo> errors = new ArrayList<MsgInfo>();   

		// 根据前台model_id获取等级的 options
		StandardWorkTimeService service = new StandardWorkTimeService();
		//传递前台参数到service（前台的model_id,isLine(是否是流水线) ajaxResponse(ajax)(写在这里是为了在service中给resInfo值)）
    	service.getLevelOptionsByCategoryId(req.getParameter("model_id"),"true".equalsIgnoreCase(req.getParameter("isLine")),conn, ajaxResponse);
       	ajaxResponse.put("errors", errors);
	    returnJsonResponse(res, ajaxResponse);
		log.info("StandardWorkTimeAction.changeRank.end");
	}
}
