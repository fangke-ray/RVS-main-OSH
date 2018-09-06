package com.osh.rvs.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.osh.rvs.bean.master.ModelEntity;
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.form.master.ModelForm;
import com.osh.rvs.mapper.master.ModelMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.message.ApplicationMessage;

public class StandardWorkTimeService {
	private Logger logger = Logger.getLogger(getClass());

	/**
	 * 标准工时的型号选择改变等级选项
	 * 
	 * @param ajaxResponse
	 * @param args
	 */
	public void getLevelOptionsByCategoryId(String parameter, boolean isLine, SqlSession conn,
			Map<String, Object> ajaxResponse) {
		// 查询出category_id
		ModelService mservice = new ModelService();
		// 初始设置前台传递的值是流水线的值
		// isLine=true;
		//
		boolean thisIsLine = true;
		String category_id = mservice.getDetail(parameter, conn).getCategory_id();
		String options = "";
		// 如果前台传递的单元的值是true加上category_id的 值是满足条件
		if (isLine && (category_id.equals("00000000013") || category_id.equals("00000000016"))) {
			thisIsLine = false;
			options = CodeListUtils.getSelectOptions("material_level_cell", null, "");
		} else if (!isLine && (!category_id.equals("00000000013") && !category_id.equals("00000000016"))) {
			thisIsLine = true;
			options = CodeListUtils.getSelectOptions("material_level_inline", null, "");
		} else {
			return;
		}
		// true or false
		ajaxResponse.put("isLine", thisIsLine);
		// options返回到前台
		ajaxResponse.put("level_options", options);
	}

	public List<PositionEntity> getData(String model_id, String level, SqlSession conn) throws Exception {
		ModelMapper dao = conn.getMapper(ModelMapper.class);
		// 取出name和category_name
		ModelEntity nameCategoryNameByVmodel = dao.getModelByID(model_id);
		// 获取所有相关工位的processCode和name
		List<PositionEntity> lines = dao.getPositionsOfModel(model_id);

		// 取出S1跳过工位
		if ("1".equals(level)) {
			for (int i = lines.size() - 1; i >= 0; i--) {
				PositionEntity position = lines.get(i);
				for (int j = 0; j < ProcessAssignService.S1PASSES.length; j++) {
					if (ProcessAssignService.S1PASSES[j] == Integer.parseInt(position.getPosition_id())) {
						lines.remove(i);
						break;
					}
				}
			}
		} else {
			// 先端预制对象
			if (RvsUtils.getSnoutModels(conn).containsKey(model_id)) {
				PositionEntity element = new PositionEntity();
				element.setProcess_code("301");
				element.setName("先端预制"); // TODO
				lines.add(element);
			}

			// CCD盖玻璃对象
			if (RvsUtils.getCcdModels(conn).contains(model_id)) {
				PositionEntity element = new PositionEntity();
				element.setProcess_code("302");
				element.setName("CCD 盖玻璃更换"); // TODO
				lines.add(element);
			}

			if ("9".equals(level)) {
				// LG 目镜对应机型
				ModelService ms = new ModelService();
				ModelForm model = ms.getDetail(model_id, conn);
				if (model.getKind().equals("01")) {
					PositionEntity element = new PositionEntity();
					element.setProcess_code("303");
					element.setName("LG 玻璃更换"); // TODO
					lines.add(element);
				} else if (model.getKind().equals("03")) {
					PositionEntity element = new PositionEntity();
					element.setProcess_code("361");
					element.setName("A 橡皮涂胶"); // TODO
					lines.add(element);
				}
			}
		}

		for (int i = 0; i < lines.size(); i++) {
			// 获取行数据
			PositionEntity entity = lines.get(i);
			// 获取process_code
			String positionByCode = entity.getProcess_code();
			// 将标准工时进行设值（借用Line_name）
			entity.setLine_name(formatMinute(RvsUtils.getLevelOverLine(nameCategoryNameByVmodel.getName(),
					nameCategoryNameByVmodel.getCategory_name(), level, null, positionByCode)));
		}
		return lines;

	}

	private String formatMinute(String levelOverLine) {
		if ("-1".equals(levelOverLine)) {
			return "未设定";
		}
		return levelOverLine + " 分钟";
	}

	public void checkInput(HttpServletRequest req, List<MsgInfo> errors) {
		if(req.getParameter("level").equals("")){
			MsgInfo error = new MsgInfo();
			error.setComponentid("level");
			error.setErrcode("validator.required");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "等级"));
			errors.add(error);
		}
		if("".equals(req.getParameter("model_id"))){
			MsgInfo error = new MsgInfo();
			error.setComponentid("model_id");
			error.setErrcode("validator.required");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "型号"));
			errors.add(error);
		}		
	}
}
