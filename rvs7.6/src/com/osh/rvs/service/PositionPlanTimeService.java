package com.osh.rvs.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.arnx.jsonic.JSON;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.PostMessageEntity;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.manage.PositionPlanTimeEntity;
import com.osh.rvs.bean.master.OperatorEntity;
import com.osh.rvs.bean.master.OperatorNamedEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.master.PositionPlanTimeForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.data.AlarmMesssageMapper;
import com.osh.rvs.mapper.data.PostMessageMapper;
import com.osh.rvs.mapper.manage.PositionPlanTimeMapper;
import com.osh.rvs.mapper.master.OperatorMapper;
import com.osh.rvs.mapper.master.PositionMapper;
import com.osh.rvs.service.inline.PositionPanelService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

public class PositionPlanTimeService {

	private Logger log = Logger.getLogger(getClass());

	public List<PositionPlanTimeForm> searchMaterialPositionPlanTime(String material_id,String line_id,SqlSession conn){
		//复制表单到对象
		PositionPlanTimeEntity entity = new PositionPlanTimeEntity();
		entity.setMaterial_id(material_id);
		entity.setLine_id(line_id);
		
		PositionPlanTimeMapper dao = conn.getMapper(PositionPlanTimeMapper.class);
		
		List<PositionPlanTimeEntity> list = dao.search(entity);
		List<PositionPlanTimeForm> respList = new ArrayList<PositionPlanTimeForm>();
		
		if(list!=null && list.size() > 0){
			BeanUtil.copyToFormList(list, respList, CopyOptions.COPYOPTIONS_NOEMPTY, PositionPlanTimeForm.class);
		}

		return respList;
	}

	/**
	 * 检查倒计时未达成
	 * @param req
	 * @param workingPf
	 * @param infoes
	 * @param user 
	 * @param conn
	 * @param cbResponse 
	 * @throws Exception 
	 */
	public void checkPositionDelay(HttpServletRequest req, ProductionFeatureEntity workingPf, List<MsgInfo> infoes,
			LoginData user, SqlSessionManager conn, Map<String, Object> cbResponse) throws Exception {
		PositionPlanTimeMapper mapper = conn.getMapper(PositionPlanTimeMapper.class);

		String main_cause = req.getParameter("main_cause");

		if (main_cause == null) {
			PositionPlanTimeEntity condition = new PositionPlanTimeEntity();
			condition.setMaterial_id(workingPf.getMaterial_id());
			condition.setPosition_id(workingPf.getPosition_id());
			condition.setLine_id(workingPf.getLine_id());

			PositionPlanTimeEntity result = mapper.checkPositionUnreach(condition);
			// 本工位未达成时
			if (result != null) {
				String causes = result.getComment();
				if (CommonStringUtil.isEmpty(causes)) {
					causes = evalCauses(workingPf, conn);
					condition.setComment(causes);

					mapper.requestCountdownUnreach(condition);
				}
				cbResponse.put("causes", causes);

				MsgInfo info = new MsgInfo();
				info.setComponentid("material_id");
				info.setErrcode("info.countdown.unreach");
				infoes.add(info);
			}
		} else {
			PositionPlanTimeEntity entity = new PositionPlanTimeEntity();

			entity.setMaterial_id(workingPf.getMaterial_id());
			entity.setLine_id(workingPf.getLine_id());
			entity.setPosition_id(workingPf.getPosition_id());
			entity.setOperator_id(workingPf.getOperator_id());
			entity.setMain_cause(Integer.parseInt(main_cause));
			entity.setComment("");

			mapper.setCountdownUnreach(entity);

			// 通知线长
			String dr_need_comment = req.getParameter("dr_need_comment");
			if (dr_need_comment != null && !"null".equals(dr_need_comment)) {
				// 发送给当前线长人员
				OperatorMapper oMapper = conn.getMapper(OperatorMapper.class);
				OperatorEntity operator = new OperatorEntity();
				operator.setLine_id(user.getLine_id());
				operator.setSection_id(user.getSection_id());
				operator.setRole_id(RvsConsts.ROLE_LINELEADER);

				List<OperatorNamedEntity> leaders = oMapper.searchOperator(operator);

				MaterialService mService = new MaterialService();
				MaterialEntity mBean = mService.loadMaterialDetailBean(conn, workingPf.getMaterial_id());

				PostMessageMapper pmMapper = conn.getMapper(PostMessageMapper.class);
				PostMessageEntity pmEntity = new PostMessageEntity();
				pmEntity.setContent("维修对象 " + mBean.getSorc_no() + " 在 " + workingPf.getProcess_code() + 
						" 工位没有达成倒计时计划，请确认原因。");
				pmEntity.setSender_id(user.getOperator_id());
				pmEntity.setLevel(1);
				pmEntity.setReason(PostMessageService.COUNTDOWN_UNREACH);
				pmMapper.createPostMessage(pmEntity);

				CommonMapper commonMapper = conn.getMapper(CommonMapper.class);
				String lastInsertId = commonMapper.getLastInsertID();

				for(OperatorNamedEntity leader: leaders){
					PostMessageEntity postMessageEntity = new PostMessageEntity();
					postMessageEntity.setPost_message_id(lastInsertId);
					postMessageEntity.setReceiver_id(leader.getOperator_id());
					pmMapper.createPostMessageSendation(postMessageEntity);
				}
			}
		}
	}

	private String evalCauses(ProductionFeatureEntity workingPf, SqlSession conn) {
		Map<String, String> causes = new HashMap<String, String>();

		PositionPanelService ppService = new PositionPanelService();

		String material_id = workingPf.getMaterial_id();

		try {
			// 取得维修对象信息。
			MaterialForm mform = ppService.getMaterialInfo(material_id, conn);

			// 取得维修对象的作业标准时间。
			Integer iLeagal_overline = 0;
			try {
				String leagal_overline = RvsUtils.getLevelOverLine(mform.getModel_name(), mform.getCategory_name(), mform.getLevel(), null, workingPf.getProcess_code());
				iLeagal_overline = Integer.parseInt(leagal_overline);
			} catch (Exception e) {
			}

			// 检查工位超时
			Integer use_minutes = ppService.getTotalTimeByRework(workingPf, conn) / 60;
			if (use_minutes > iLeagal_overline) {
				causes.put("1", ApplicationMessage.WARNING_MESSAGES.getMessage("info.countdown.unreach.1", (use_minutes - iLeagal_overline)));
			}

			// 检查零件到达
			PositionPlanTimeMapper mapper = conn.getMapper(PositionPlanTimeMapper.class);
			List<String> l = mapper.checkCausedByPartial(material_id, workingPf.getLine_id());
			if (l.size() > 0) {
				causes.put("2", ApplicationMessage.WARNING_MESSAGES.getMessage("info.countdown.unreach.2"));
			}

			// 检查仕挂量
			int count = mapper.checkCausedByHeap(material_id, workingPf.getSection_id(), workingPf.getLine_id(), workingPf.getPosition_id());
			if (count > 0) {
				causes.put("3", ApplicationMessage.WARNING_MESSAGES.getMessage("info.countdown.unreach.3", count));
			}

			// 检查休假状态
			// causes.put("4", "RR");

			// 检查警报
			AlarmMesssageMapper amMapper = conn.getMapper(AlarmMesssageMapper.class);
			int amCount = amMapper.getOccursByMaterialIdOfPosition(material_id, workingPf.getPosition_id());
			if (amCount > 0) {
				causes.put("5", ApplicationMessage.WARNING_MESSAGES.getMessage("info.countdown.unreach.5", count));
			}
		} catch (Exception e) {
			log.error("试运行期间错误!" + e.getMessage(), e);
		}

		// 返回JSON格式
		return JSON.encode(causes, false);
	}

	/**
	 * 自动设定为倒计时未达成
	 * @param req
	 * @param workingPf
	 * @param infoes
	 * @param conn
	 * @param cbResponse 
	 */
	public void autosetUnreach(HttpServletRequest req, SqlSessionManager conn) {
		PositionPlanTimeMapper mapper = conn.getMapper(PositionPlanTimeMapper.class);
		PositionMapper pMapper = conn.getMapper(PositionMapper.class);

		LoginData user = (LoginData) req.getSession().getAttribute(RvsConsts.SESSION_USER);

		PositionPlanTimeEntity condition = new PositionPlanTimeEntity();
		condition.setMaterial_id(req.getParameter("material_id"));
		String reworkPosition_id = req.getParameter("position_id");
		condition.setLine_id(pMapper.getPositionByID(reworkPosition_id).getLine_id());

		condition.setPosition_id(reworkPosition_id);
		condition.setOperator_id(user.getOperator_id());
		condition.setMain_cause(5); // 
		condition.setComment("返工");

		PositionPlanTimeEntity result = mapper.checkLineUnreach(condition);
		if (result != null) {
			if (result.getContrast_time() == null) {
				mapper.requestCountdownUnreach(condition);
			}
			if (result.getMain_cause() == null) {
				mapper.setCountdownUnreach(condition);
			}
		}
	}
}
