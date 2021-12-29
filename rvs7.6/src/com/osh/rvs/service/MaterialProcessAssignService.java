package com.osh.rvs.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.inline.MaterialProcessAssignEntity;
import com.osh.rvs.bean.inline.MaterialProcessEntity;
import com.osh.rvs.bean.master.LightFixEntity;
import com.osh.rvs.bean.master.LineEntity;
import com.osh.rvs.bean.master.ModelEntity;
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.bean.master.ProcessAssignEntity;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.inline.MaterialProcessAssignForm;
import com.osh.rvs.form.master.LightFixForm;
import com.osh.rvs.form.master.ModelForm;
import com.osh.rvs.form.master.ProcessAssignForm;
import com.osh.rvs.mapper.data.MaterialMapper;
import com.osh.rvs.mapper.inline.MaterialProcessAssignMapper;
import com.osh.rvs.mapper.master.LightFixMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

/**
 * 
 * @Title MaterialProcessAssignService.java
 * @Project rvs
 * @Package com.osh.rvs.service
 * @ClassName: MaterialProcessAssignService
 * @Description: 维修对象独有修理流程
 * @author lxb
 * @date 2015-8-19 下午4:02:48
 */
public class MaterialProcessAssignService {
	
	/**
	 * 小修理标准编制
	 * @param form
	 * @param conn
	 * @return
	 */
	public List<LightFixForm> searchLightFixs(ActionForm form,SqlSession conn){
		LightFixEntity lightFixEntity = new LightFixEntity();
		//复制表单数据到对象
		BeanUtil.copyToBean(form, lightFixEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//小修理标准编制
		LightFixMapper lightFixMapper = conn.getMapper(LightFixMapper.class);
		List<LightFixEntity> list = lightFixMapper.getLightFixByMaterialId(lightFixEntity);
		List<LightFixForm> rList = new ArrayList<LightFixForm>();
		//复制数据表单对象
		BeanUtil.copyToFormList(list, rList, CopyOptions.COPYOPTIONS_NOEMPTY, LightFixForm.class);
		
		Map<String,LightFixForm> lightFixMap = new TreeMap<String,LightFixForm>();
		for(int i=0;i<rList.size();i++){
			LightFixForm lightFixForm = rList.get(i);
			String activity_code = lightFixForm.getActivity_code();//code
			
			String position_id = lightFixForm.getPosition_id();//工位
			if(position_id!=null){
				position_id = position_id.replaceAll("^0*", "");
			}

			String key = activity_code;
			if(lightFixMap.containsKey(key)){
				if(position_id!=null) lightFixMap.get(key).getPosition_list().add(position_id);
			}else{
				if(position_id!=null){
					lightFixForm.getPosition_list().add(position_id);
					lightFixMap.put(key, lightFixForm);
				}else{
					lightFixMap.put(key, lightFixForm);
				}
			}
		}
		
		rList = converMapToList(lightFixMap);
		
		return rList;
	}
	
	public List<MaterialProcessAssignForm> searchMaterialProcessAssign(ActionForm form,SqlSession conn){
		MaterialProcessAssignEntity entity = new MaterialProcessAssignEntity();
		//复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		MaterialProcessAssignMapper materialProcessAssignMapper = conn.getMapper(MaterialProcessAssignMapper.class);
		
		List<MaterialProcessAssignEntity> list = materialProcessAssignMapper.searchMaterialProcessAssign(entity);
		
		List<MaterialProcessAssignForm> rList = new ArrayList<MaterialProcessAssignForm>();
		if(list.size()>0){
			BeanUtil.copyToFormList(list, rList, CopyOptions.COPYOPTIONS_NOEMPTY, MaterialProcessAssignForm.class);
			for(int i= 0;i<rList.size();i++){
				MaterialProcessAssignForm connForm = rList.get(i);
				String position_id = connForm.getPosition_id();//工位
				position_id = position_id.replaceAll("^0*", "");
				rList.get(i).setPosition_id(position_id);
			}
		}
		
		return rList;
	}
	
	
	/**
	 * 查询维修对象选用小修理
	 * @param form
	 * @param conn
	 * @return
	 */
	public List<MaterialProcessAssignForm> searchMaterialLightFix(ActionForm form,SqlSession conn){
		MaterialProcessAssignEntity entity = new MaterialProcessAssignEntity();
		//复制表单数据到对象
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		MaterialProcessAssignMapper materialProcessAssignMapper = conn.getMapper(MaterialProcessAssignMapper.class);
		List<MaterialProcessAssignEntity> list = materialProcessAssignMapper.searchMaterialLightFix(entity);
		
		
		List<MaterialProcessAssignForm> rList = new ArrayList<MaterialProcessAssignForm>();
		if(list.size()>0){
			BeanUtil.copyToFormList(list, rList, CopyOptions.COPYOPTIONS_NOEMPTY, MaterialProcessAssignForm.class);
		}
		
		return rList;
	}
	 
	/**
	 * Map集合转换List
	 * @param map
	 * @return
	 */
	private <T> List<T> converMapToList(Map<String,T> map){
		Set<String> set = map.keySet();
		Iterator<String> iter = set.iterator();
		
		List<T> list = new ArrayList<T>();
		
		while(iter.hasNext()){
			list.add(map.get(iter.next()));
		}
		return list;
	}

	/**
	 * 重新设定流程
	 * @param material_id
	 * @param request
	 * @param conn
	 * @param renew 重新建立小修流程
	 * @throws Exception
	 */
	public void updateProcessAssign(String material_id, HttpServletRequest request,SqlSessionManager conn, boolean renew) throws Exception {
		MaterialProcessAssignMapper materialProcessAssignMapper = conn.getMapper(MaterialProcessAssignMapper.class);

		List<MaterialProcessAssignForm> lighFixList = new AutofillArrayList<MaterialProcessAssignForm>(MaterialProcessAssignForm.class);
		List<MaterialProcessAssignForm> processAssignList = new AutofillArrayList<MaterialProcessAssignForm>(MaterialProcessAssignForm.class);
		
		Map<String,String[]> map=(Map<String,String[]>)request.getParameterMap();
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");
		// 整理提交数据
		for (String parameterKey : map.keySet()) {
			 Matcher m = p.matcher(parameterKey);
			 if (m.find()) {
				 String table = m.group(1);
				 if ("material_light_fix".equals(table)) {//维修对象选用小修理
				     String column = m.group(2);
				     int icounts = Integer.parseInt(m.group(3));
					 String[] value = map.get(parameterKey);
					 if ("light_fix_id".equals(column)) {
						 lighFixList.get(icounts).setLight_fix_id(value[0]);
					 }
				 }else if("material_process_assign".equals(table)) {//维修对象独有修理流程
					 String column = m.group(2);
				     int icounts = Integer.parseInt(m.group(3));
					 String[] value = map.get(parameterKey);
					 if ("position_id".equals(column)) {
						 processAssignList.get(icounts).setPosition_id(value[0]);
					 } else if ("next_position_id".equals(column)) {
						 processAssignList.get(icounts).setNext_position_id(value[0]);
					 } else if ("prev_position_id".equals(column)) {
						 processAssignList.get(icounts).setPrev_position_id(value[0]);
					 } else if ("sign_position_id".equals(column)) {
						 processAssignList.get(icounts).setSign_position_id(value[0]);
					 }
				 }
			 }
		}

		List<String> oldHasLines = null;

		MaterialProcessService mpService = new MaterialProcessService();
		if (processAssignList.size() > 0) {
			oldHasLines = mpService.loadMaterialProcessLineIds(material_id, conn); // 取得已存在工程
		}

		if (renew) {
			if (lighFixList.size() > 0) {
				//删除维修对象选用小修理
				materialProcessAssignMapper.deleteMaterialLightFix(material_id);
			}
			if (processAssignList.size() > 0) {
				//删除维修对象独有修理流程
				materialProcessAssignMapper.deleteMaterialProcessAssign(material_id);
			}
		} else {
			//删除维修对象选用小修理
			materialProcessAssignMapper.deleteMaterialLightFix(material_id);
			//删除维修对象独有修理流程
			materialProcessAssignMapper.deleteMaterialProcessAssign(material_id);
		}

		//新建维修对象选用小修理
		for(MaterialProcessAssignForm connForm:lighFixList){
			MaterialProcessAssignEntity entity = new MaterialProcessAssignEntity();
			BeanUtil.copyToBean(connForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
			entity.setMaterial_id(material_id);
			materialProcessAssignMapper.insertMaterialLightFix(entity);
		}
		
		//新建维修对象独有修理流程
		if (processAssignList.size() > 0) {

			for(MaterialProcessAssignForm processAssignForm:processAssignList){
				MaterialProcessAssignEntity entity = new MaterialProcessAssignEntity();
				BeanUtil.copyToBean(processAssignForm, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
				String position_id = entity.getPosition_id();
				if (position_id == null) continue;
				entity.setMaterial_id(material_id);
				entity.setLine_id("9000000");
				if (entity.getSign_position_id() == null) 
					entity.setSign_position_id(position_id);
				if (entity.getPrev_position_id() == null)
					entity.setPrev_position_id("0");
				if (entity.getNext_position_id() == null)
					entity.setNext_position_id("9999999");
				materialProcessAssignMapper.insertMaterialProcessAssign(entity);
			}

			// 工程存在与否判断
			int decToogle = 0; 
			int nsToogle = 0; 
			int comToogle = 0; 

			if (renew && oldHasLines != null && oldHasLines.size() > 0) { // 未投线不需要
				List<String> newHasLines = this.checkPatHasLine(material_id, conn); // 新流程对应工程

				if (newHasLines.contains("00000000012") && !oldHasLines.contains("00000000012")) {
					decToogle = 1;
				} else if (!newHasLines.contains("00000000012") && oldHasLines.contains("00000000012")) {
					decToogle = -1;
				}

				if (newHasLines.contains("00000000013") && !oldHasLines.contains("00000000013")) {
					nsToogle = 1;
				} else if (!newHasLines.contains("00000000013") && oldHasLines.contains("00000000013")) {
					nsToogle = -1;
				}

				if (newHasLines.contains("00000000014") && !oldHasLines.contains("00000000014")) {
					comToogle = 1;
				} else if (!newHasLines.contains("00000000014") && oldHasLines.contains("00000000014")) {
					comToogle = -1;
				}
			}

			int px = 1; // 暂时先全当成B线 TODO evalPx
			Date scheduledDate = null;
			Date beforeScheduledDate = null;

			if (decToogle == 1 || nsToogle == 1 || comToogle == 1) { // 新建时计算纳期
				MaterialService mService = new MaterialService();
				MaterialEntity mBean = mService.getMaterialEntityByKey(material_id, conn);
				ModelService mdlService = new ModelService();
				ModelEntity model = mdlService.getDetailEntity(mBean.getModel_id(), conn);
				if (decToogle == 1 || nsToogle == 1) {
					Date[] dSchedulePlans = RvsUtils.getTimeLimit(mBean.getAgreed_date(), 
							mBean.getLevel(), mBean.getFix_type(), mBean.getScheduled_expedited(), model.getSeries(), conn, true);
					scheduledDate = dSchedulePlans[0];
					beforeScheduledDate = dSchedulePlans[1];
				}
				if (comToogle == 1 && scheduledDate == null) {
					Date[] dSchedulePlans = RvsUtils.getTimeLimit(mBean.getAgreed_date(), 
							mBean.getLevel(), mBean.getFix_type(), mBean.getScheduled_expedited(), model.getSeries(), conn, false);
					scheduledDate = dSchedulePlans[0];
				}
			}

			if (decToogle == 1) { // 新增分解工程
				MaterialProcessEntity insertBean = new MaterialProcessEntity();
				insertBean.setMaterial_id(material_id);
				insertBean.setPx(px);
				insertBean.setScheduled_date(beforeScheduledDate);
				insertBean.setLine_id("00000000012");
				mpService.insertMaterialProcess(insertBean, conn);
			}
			if (decToogle == -1) { // 结束分解工程
				mpService.finishMaterialProcess(material_id, "00000000012", null, conn);
			}
			if (nsToogle == 1) { // 新增NS工程
				MaterialProcessEntity insertBean = new MaterialProcessEntity();
				insertBean.setMaterial_id(material_id);
				insertBean.setPx(px);
				insertBean.setScheduled_date(beforeScheduledDate);
				insertBean.setLine_id("00000000013");
				mpService.insertMaterialProcess(insertBean, conn);
			}
			if (nsToogle == -1) { // 结束NS工程
				mpService.finishMaterialProcess(material_id, "00000000013", null, conn);
			}

			MaterialService materialService = new MaterialService();

			if (comToogle == 1) { // 新增总组工程
				MaterialEntity mEntity = materialService.loadMaterialDetailBean(conn, material_id);
				px = mpService.evalPx(mEntity.getModel_id(), "00000000014", true, conn);

				MaterialProcessEntity insertBean = new MaterialProcessEntity();
				insertBean.setMaterial_id(material_id);
				insertBean.setPx(px);
				insertBean.setScheduled_date(scheduledDate);
				insertBean.setLine_id("00000000014");
				mpService.insertMaterialProcess(insertBean, conn);
			}
			if (comToogle == -1) { // 结束总组工程
				mpService.finishMaterialProcess(material_id, "00000000014", null, conn);
			}

			// 设定为系统
			String operator_id = "00000000001";
			// 得到小修理信息
			MaterialProcessAssignService mpas = new MaterialProcessAssignService();
			String lightFixStr = mpas.getLightFixesByMaterial(material_id, conn);

			String lightFlowStr = mpas.getLightFixFlowByMaterial(material_id, null, conn);
			String comment = getLightStr(lightFixStr, lightFlowStr);
			materialService.updateMaterialComment(material_id, operator_id, comment, conn);
		}
	}

	public String getLightStr(String lightFixStr, String lightFlowStr) {
		String comment = (lightFixStr == null ? "" : "小修理内容为：" + lightFixStr + "\n")
				+ "小修理的工位流程为：" + lightFlowStr;
		if (comment.length() > 500) {
			lightFixStr = lightFixStr.substring(0, 500 - "小修理内容为：\n小修理的工位流程为：".length()
					- lightFlowStr.length() - 2) + "…";
			comment = (lightFixStr == null ? "" : "小修理内容为：" + lightFixStr + "\n")
					+ "小修理的工位流程为：" + lightFlowStr;
		}
		return comment;
	}
	/**
	 * 查询流程包含工程
	 * @param material_id
	 * @param conn
	 * @return
	 */
	private List<String> checkPatHasLine(String materialId, SqlSession conn) {
		List<String> ret = new ArrayList<String>();

		MaterialProcessAssignMapper mapper = conn.getMapper(MaterialProcessAssignMapper.class);
		List<LineEntity> lines = mapper.getWorkedLines(materialId);

		for (LineEntity line : lines) {
			ret.add(line.getLine_id());
		}
		return ret;
	}

	public void update(ActionForm form,HttpServletRequest request,SqlSessionManager conn)throws Exception{
		MaterialProcessAssignEntity entity = new MaterialProcessAssignEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		String material_id = entity.getMaterial_id();//维修对象 ID
		String pat_id = entity.getPad_id();

		updateProcessAssign(material_id, request, conn, false);

		MaterialMapper materialMapper = conn.getMapper(MaterialMapper.class);
		
		materialMapper.updateMaterialPat(material_id, pat_id);
		
	}
	
	/**
	 * check D级流水线materialForm
	 * @param material_id
	 * @param conn
	 */
	public void checkDAndInline(ActionForm form,HttpServletRequest request,SqlSession conn,List<MsgInfo> errors){
		MaterialProcessAssignForm materialProcessAssignForm = (MaterialProcessAssignForm)form;
		String level = materialProcessAssignForm.getLevel();//等级
		String fix_type = materialProcessAssignForm.getFix_type();//修理方式
		String pat_id = materialProcessAssignForm.getPad_id();//维修流程
		boolean isLightFix = RvsUtils.isLightFix(level);

//		if(("9".equals(level) || "91".equals(level) || "92".equals(level) || "93".equals(level)) && "1".equals(fix_type)){
		if (isLightFix && "1".equals(fix_type)) {
			if(CommonStringUtil.isEmpty(pat_id)){
				MsgInfo msgInfo = new MsgInfo();
				msgInfo.setErrcode("validator.required");
				msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "维修流程"));
				errors.add(msgInfo);
			}
			
			List<MaterialProcessAssignForm> lighFixList = new AutofillArrayList<MaterialProcessAssignForm>(MaterialProcessAssignForm.class);
			Map<String,String[]> map=(Map<String,String[]>)request.getParameterMap();
			Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");
			for (String parameterKey : map.keySet()) {
				 Matcher m = p.matcher(parameterKey);
				 if (m.find()) {
					 String table = m.group(1);
					 if ("material_light_fix".equals(table)) {//维修对象选用小修理
					     String column = m.group(2);
					     int icounts = Integer.parseInt(m.group(3));
						 String[] value = map.get(parameterKey);
						 if ("light_fix_id".equals(column)) {
							 lighFixList.get(icounts).setLight_fix_id(value[0]);
						 }
					 }
				 }
			}
			
			if(lighFixList.size()==0){
				MsgInfo msgInfo = new MsgInfo();
				msgInfo.setErrcode("validator.required.multidetail");
				msgInfo.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required.multidetail", "小修理流程"));
				errors.add(msgInfo);
			}
		}
	}

	public List<ProcessAssignForm> getAssigns(String material_id, SqlSession conn) {
		// 从数据库中查询记录
		MaterialProcessAssignMapper mapper = conn.getMapper(MaterialProcessAssignMapper.class);
		List<ProcessAssignEntity> entities = mapper.getProcessAssignByMaterialID(material_id);
		List<ProcessAssignForm> result = new ArrayList<ProcessAssignForm>();
		BeanUtil.copyToFormList(entities, result, null, ProcessAssignForm.class);
		return result;
	}

	public String getFirstPositionId(String material_id, SqlSession conn) {
		MaterialProcessAssignMapper mapper = conn.getMapper(MaterialProcessAssignMapper.class);
		ProcessAssignEntity firstPosition = mapper.getFirstPosition(material_id);

//		if ("25".equals(firstPosition.getPosition_id())
//					|| "00000000025".equals(firstPosition.getPosition_id()))
//			return firstPosition.getNext_position_id();
//		else if ("60".equals(firstPosition.getPosition_id())
//				|| "00000000060".equals(firstPosition.getPosition_id()))  {
//			if ("25".equals(firstPosition.getNext_position_id())
//					|| "00000000025".equals(firstPosition.getNext_position_id())) {
//				return firstPosition.getSign_position_id();
//			} else {
//				return firstPosition.getNext_position_id();
//			}
//		}
//		else 
		return firstPosition.getPosition_id();
	}

//	public String getBeforeInlinePositions(String material_id, SqlSession conn) {
//		MaterialProcessAssignMapper mapper = conn.getMapper(MaterialProcessAssignMapper.class);
//		ProcessAssignEntity firstPosition = mapper.getFirstPosition(material_id);
//
//		if ("25".equals(firstPosition.getPosition_id())
//			|| "00000000025".equals(firstPosition.getPosition_id()))
//			return "CCD";
//		else if ("60".equals(firstPosition.getPosition_id())
//			|| "00000000060".equals(firstPosition.getPosition_id()))  {
//			if ("25".equals(firstPosition.getNext_position_id())
//					|| "00000000025".equals(firstPosition.getNext_position_id())) {
//				return "LG+CCD";
//			} else {
//				return "LG";
//			}
//		}
//		return "";
//	}

	public String getLightFixesByMaterial(String material_id,
			SqlSession conn) {
		MaterialProcessAssignMapper mapper = conn.getMapper(MaterialProcessAssignMapper.class);
		return mapper.getLightFixesByMaterial(material_id);
	}

	/**
	 * 取得维修对象已选择修理内容的全工位（包括当前流程用的和不用的），返工时切换参考流程用
	 * 
	 * @param material_id
	 * @param conn
	 * @return
	 */
	public List<String> getLightPositionsByMaterial(String material_id,
			SqlSession conn) {
		MaterialProcessAssignMapper mapper = conn.getMapper(MaterialProcessAssignMapper.class);
		return mapper.getLightPositionsByMaterial(material_id);
	}

	/**
	 * 取得完整小修理流程
	 * @param material_id
	 * @param now_process_code
	 * @param conn
	 * @return
	 */
	public String getLightFixFlowByMaterial(String material_id,
			String now_process_code, SqlSession conn) {
		MaterialProcessAssignMapper mapper = conn.getMapper(MaterialProcessAssignMapper.class);
		ProcessAssignEntity checkedPosition = mapper.getFirstPosition(material_id);
		if (checkedPosition == null)
			return null;

		Map<String, List<String>> positionMappings = null;

		if (MaterialTagService.getAnmlMaterials(conn).contains(material_id)) {
			positionMappings = PositionService.getPositionUnitizeds(conn);
		} else {
			positionMappings = PositionService.getPositionMappings(conn);
		}
		

		String position_id = checkedPosition.getPosition_id();
		String ret = getProcessInterf(new PositionService().getPositionEntityByKey(position_id, conn), now_process_code);
		if (positionMappings.containsKey(CommonStringUtil.fillChar(position_id, '0', 11, true))) {
			List<String> mappedProcessCodes = mapper.getSelectedMappings(material_id, position_id);
			ret += "(" + CommonStringUtil.joinBy(",", 
					mappedProcessCodes.toArray(new String[mappedProcessCodes.size()])) + ")";
		}
		while (position_id != null) {
			List<PositionEntity> nextPositions = mapper.getNextPositions(material_id, position_id);
			if (nextPositions.size() > 0) {
				for (PositionEntity nextPosition : nextPositions) {
					ret += " -> " + getProcessInterf(nextPosition, now_process_code);
					position_id = nextPosition.getPosition_id();
					if (positionMappings.containsKey(CommonStringUtil.fillChar(position_id, '0', 11, true))) {
						List<String> mappedProcessCodes = mapper.getSelectedMappings(material_id, position_id);
						ret += "(" + CommonStringUtil.joinBy(",", 
								mappedProcessCodes.toArray(new String[mappedProcessCodes.size()])) + ")";
					}
				}
			} else {
				position_id = null;
			}
		}
		return ret;
	}

	/**
	 * 取得工位表现格式
	 * 
	 * @param position
	 * @param now_process_code 当前工位
	 * @return
	 */
	private String getProcessInterf(PositionEntity position,
			String now_process_code) {
		if (position == null)
			return "";
		if (now_process_code == null || !now_process_code.equals(position.getProcess_code())) {
			if (position.getLight_division_flg() == 1) {
				return position.getProcess_code() + "B";
			} else {
				return position.getProcess_code();
			}
		}
		else {
			// 当前工位显示下划线
			if (position.getLight_division_flg() != null && position.getLight_division_flg() == 1) {
				return "<span style='font-weight:bold; text-decoration: underline;'>" + position.getProcess_code() + "B</span>";
			} else {
				return "<span style='font-weight:bold; text-decoration: underline;'>" + position.getProcess_code() + "</span>";
			}
		}
	}

	/**
	 * 取得中小修理对应工位
	 * 
	 * @param conn
	 * @return
	 */
	public Map<String, String[]> getPositionMappingEntities(SqlSession conn) {
		PositionService pService = new PositionService();
		Map<String, String[]> positionMappingEntities = new HashMap<String, String[]>();
		Map<String, String> positionMapping = PositionService.getPositionMappingRevers(conn);
		for (String pId : positionMapping.keySet()) {
			String[] positionMappingEntity = new String[2];
			positionMappingEntity[0] = positionMapping.get(pId);
			positionMappingEntity[1] = pService.getPositionEntityByKey(positionMappingEntity[0], conn).getProcess_code();
			positionMappingEntities.put(pId.replaceAll("^0+", ""), positionMappingEntity);
		}
		return positionMappingEntities;
	}

	/**
	 * 取得动物内镜对应工位
	 * 
	 * @param conn
	 * @return
	 */
	public Map<String, String[]> getPositionUnitizedEntities(SqlSession conn) {
		PositionService pService = new PositionService();
		Map<String, String[]> positionMappingEntities = new HashMap<String, String[]>();
		Map<String, String> positionMapping = PositionService.getPositionUnitizedRevers(conn);
		for (String pId : positionMapping.keySet()) {
			String[] positionMappingEntity = new String[2];
			positionMappingEntity[0] = positionMapping.get(pId);
			positionMappingEntity[1] = pService.getPositionEntityByKey(positionMappingEntity[0], conn).getProcess_code();
			positionMappingEntities.put(pId.replaceAll("^0+", ""), positionMappingEntity);
		}
		return positionMappingEntities;
	}

}
