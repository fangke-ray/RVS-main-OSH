package com.osh.rvs.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.CellUtil;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.master.ModelEntity;
import com.osh.rvs.bean.master.PartialEntity;
import com.osh.rvs.bean.master.PartialPositionEntity;
import com.osh.rvs.bean.partial.PartialWasteModifyHistoryEntity;
import com.osh.rvs.common.CopyByPoi;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.ReverseResolution;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.master.PartialForm;
import com.osh.rvs.form.master.PartialPositionForm;
import com.osh.rvs.mapper.master.PartialMapper;
import com.osh.rvs.mapper.master.PartialPositionMapper;
import com.osh.rvs.mapper.partial.PartialWasteModifyHistoryMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;

public class PartialService {

	public List<PartialForm> searchPartial(PartialEntity partialEntity, SqlSession conn) {
		PartialMapper dao = conn.getMapper(PartialMapper.class);
		List<PartialForm> resultForm = new ArrayList<PartialForm>();
		/* 判断数据为null的情况 */
		if (partialEntity != null) {
			List<PartialEntity> resultBean = dao.searchPartial(partialEntity);
			BeanUtil.copyToFormList(resultBean, resultForm, null, PartialForm.class);
			return resultForm;
		} else {
			return null;
		}
	}

	/* 验证零件编码和零件 */
	public void customValidate(ActionForm partialForm, SqlSession conn, List<MsgInfo> errors) {
		PartialMapper dao = conn.getMapper(PartialMapper.class);
		PartialEntity conditionBean = new PartialEntity();
		/* 数据复制 */
		BeanUtil.copyToBean(partialForm, conditionBean, (new CopyOptions()).include("partial_id", "code"));

		List<String> resultBean = dao.checkPartial(conditionBean);
		if (resultBean != null && resultBean.size() > 0) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("code");
			error.setErrcode("dbaccess.columnNotUnique");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.columnNotUnique", "零件编码",
					conditionBean.getCode(), "零件"));
			errors.add(error);
		}
	}

	public void insert(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors)
			throws Exception {
		PartialEntity insertBean = new PartialEntity();
		BeanUtil.copyToBean(form, insertBean, null);
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		insertBean.setUpdated_by(user.getOperator_id());

		/* partail表插入数据 */
		PartialMapper dao = conn.getMapper(PartialMapper.class);
		dao.insertPartial(insertBean);

		/*
		 * CommonMapper cDao = conn.getMapper(CommonMapper.class); String partial_id =
		 * cDao.getLastInsertID();//取得本连接最后取得的自增ID
		 * 
		 * insertBean.setPartial_id(partial_id); dao.insertPartialPrice(insertBean);
		 */
	}

	public PartialForm getDetail(PartialEntity partialEntity, SqlSession conn, List<MsgInfo> errors) {
		String partial_id = partialEntity.getPartial_id();

		PartialMapper dao = conn.getMapper(PartialMapper.class);
		PartialEntity partial = dao.getPartialByID(partial_id);
		if (partial == null) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("partial_id");
			error.setErrcode("dbaccess.recordNotExist");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordNotExist", "零件对象型号"));
			errors.add(error);
			return null;
		} else {
			PartialForm pf = new PartialForm();
			BeanUtil.copyToForm(partial, pf, null);
			return pf;
		}
	}

	public void delete(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors)
			throws Exception {
		PartialEntity updateBean = new PartialEntity();
		BeanUtil.copyToBean(form, updateBean, null);
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		updateBean.setUpdated_by(user.getOperator_id());

		PartialMapper dao = conn.getMapper(PartialMapper.class);
		dao.deletePartial(updateBean);
	}

	/* 双击修改页面内容 */

	public void update(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors)
			throws Exception {
		PartialEntity updateBean = new PartialEntity();
		BeanUtil.copyToBean(form, updateBean, null);
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		// 更新人
		updateBean.setUpdated_by(user.getOperator_id());

		PartialMapper dao = conn.getMapper(PartialMapper.class);
		dao.updatePartial(updateBean);

		// 清空反查缓存
		ReverseResolution.partialRever.clear();
		/*
		 * //如果返回的是false则只执行更新PartialPrice表 if ("false".equals(priceNotChanged)) { dao.updatePartialPrice(updateBean); }
		 */
	}

	/* 零件partial的code和name更新 */
	public void updatePartialCodeName(ActionForm form, String judgeHistorylimitdate, HttpSession session,
			SqlSessionManager conn, List<MsgInfo> errors) throws Exception {
		PartialEntity updateBean = new PartialEntity();
		PartialEntity updateBeanForPrice = new PartialEntity();
		BeanUtil.copyToBean(form, updateBean, CopyOptions.COPYOPTIONS_NOEMPTY);
		BeanUtil.copyToBean(form, updateBeanForPrice, CopyOptions.COPYOPTIONS_NOEMPTY);

		/* 获取的操作者的ID */
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		updateBean.setUpdated_by(user.getOperator_id());

		PartialMapper dao = conn.getMapper(PartialMapper.class);

		/* 从页面传递有效截至日期和选择的日期进行比较大小 */
		if ("false".equals(judgeHistorylimitdate)) {
			/* 新建插入partial表的code和name */
			dao.insertPartialCodeName(updateBean);
		}
	}
	/** 零件集合 **/
	public List<Map<String, String>> getPartialAutoCompletes(String code, SqlSession conn) {
		PartialMapper dao = conn.getMapper(PartialMapper.class);
		return dao.getAllPartial(code);
	}

	public List<PartialPositionForm> searchPartialModelNameProcessCodeActiveData(PartialPositionEntity partialPositionEntity, SqlSession conn) {
		PartialMapper dao = conn.getMapper(PartialMapper.class);
		List<PartialPositionForm> resultForm = new ArrayList<PartialPositionForm>();
		List<PartialPositionEntity> resultList = dao.searchPartialModelNameProcessCodeActiveData(partialPositionEntity);
		BeanUtil.copyToFormList(resultList, resultForm, null, PartialPositionForm.class);
		return resultForm;
	}

	/** 取出所有的型号集合 */
	public Map<String, String> getPartialModelNameS(PartialEntity partialEntity, SqlSession conn, List<MsgInfo> errors) {
		Map<String, String> modelMameMap = new HashMap<String, String>();
		PartialMapper dao = conn.getMapper(PartialMapper.class);
		// 所有还有效的型号
		List<ModelEntity> modelList = dao.getModelOfPartialAvaliable(partialEntity);
		if (modelList.size() > 0) {
			for (ModelEntity model : modelList) {
				modelMameMap.put(model.getModel_id(), model.getName());
			}
		} else {
			MsgInfo error = new MsgInfo();
			error.setComponentid("code");
			error.setErrcode("info.partial.noModelsLocated");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.partial.noModelsLocated", "", null, null));
			errors.add(error);
		}
		return modelMameMap;
	}

	/* 更新有效期 */
	public void updatePartialPosition(ActionForm form, HttpSession session, SqlSessionManager conn,
			List<MsgInfo> errors, HttpServletRequest req) throws Exception {
		
		PartialMapper partialDao = conn.getMapper(PartialMapper.class);
		PartialPositionEntity partialPositionEntity = new PartialPositionEntity();
		BeanUtil.copyToBean(form, partialPositionEntity, null);

		String sHistory_limit_date = req.getParameter("history_limit_date");
		String sModel_ids = req.getParameter("model_id");
		if("".equals(sModel_ids)){
			MsgInfo error = new MsgInfo();
			error.setComponentid("model_id");
			error.setErrcode("validator.required.multidetail");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required.multidetail", "", null, null));
			errors.add(error);
		}
			/* 取得零件型号 工位 有效期 */
			List<PartialPositionForm> partialPositionFormList = this.searchPartialModelNameProcessCodeActiveData(partialPositionEntity, conn);
			if (partialPositionFormList.size() > 0) {
				
				Map<String, Object> param = new HashMap<String, Object>();
				/* model_name */
				List<String> modelIds = new ArrayList<String>();

				if (sModel_ids.contains(",")) {
					String[] modelIdArray = sModel_ids.split(",");
					for (int i = 0; i < modelIdArray.length; i++) {
						modelIds.add(modelIdArray[i]);
					}
				} else {
					modelIds.add(sModel_ids);
				}
				
				String position_id = "";
				for(int i=0;i<partialPositionFormList.size();i++){
					PartialPositionForm partialPositionForm = partialPositionFormList.get(i);
					position_id = partialPositionForm.getPosition_id();
					String partial_id = partialPositionEntity.getPartial_id();
			        String new_partial_id= partialPositionEntity.getPartial_id();
			            
						param.put("partial_id",partial_id );
						param.put("position_id", position_id);
						param.put("new_partial_id", new_partial_id);
						param.put("history_limit_date", sHistory_limit_date);
						param.put("model_id", modelIds);

						// 更新定位表
						partialDao.updatePartialPosition(param);
				}           

				LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
				String updated_by = user.getOperator_id();
				
			    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				Date date = sdf.parse(sHistory_limit_date);	
				String modelId="";
				for(int i=0;i<modelIds.size();i++){
					 modelId = modelIds.get(i);				
					  				
						partialPositionEntity.setModel_id(modelId);
						partialPositionEntity.setPosition_id(position_id);
						partialPositionEntity.setNew_partial_id(partialPositionEntity.getPartial_id());
						partialPositionEntity.setHistory_limit_date(date);
						partialPositionEntity.setUpdated_by(updated_by);
						
						//TODO 判断零件废改订履历是否已经插入过有效截止日期
						PartialWasteModifyHistoryMapper partialWasteModifyDao = conn.getMapper(PartialWasteModifyHistoryMapper.class);
						PartialWasteModifyHistoryEntity partialWasteModifyHistoryEntity  = new PartialWasteModifyHistoryEntity();
						partialWasteModifyHistoryEntity.setModel_id(sModel_ids);
						partialWasteModifyHistoryEntity.setOld_partial_id(partialPositionEntity.getPartial_id());
						partialWasteModifyHistoryEntity.setNew_partial_id(partialPositionEntity.getPartial_id());
						
						List<PartialWasteModifyHistoryEntity> partialWasteModifyHistoryEntityList =partialWasteModifyDao.searchPartialWasteModifyHistory(partialWasteModifyHistoryEntity); 
						if(partialWasteModifyHistoryEntityList.size()==0){
							// 插入改废订历史管理表
							partialDao.insertPartialWasteModifyHistory(partialPositionEntity);
							//否则弹出信息显示（该条数据已经进行废该增）
						}else{
							MsgInfo error = new MsgInfo();
							error.setComponentid("code");
							error.setErrcode("info.partial.noModelsLocated");
							error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("info.partial.noModelsLocated", "", null, null));
							errors.add(error);
						}
				}		
			}
	}

	public void updatePartialPositionNewPartial(HttpServletRequest request, ActionForm form, HttpSession session,
			SqlSessionManager conn, List<MsgInfo> errors) throws Exception {
		PartialMapper partialDao = conn.getMapper(PartialMapper.class);

		PartialPositionEntity partialPositionEntity = new PartialPositionEntity();
		Map<String, Object> historyParam = new HashMap<String, Object>();
		Map<String, Object> positionParam = new HashMap<String, Object>();
		BeanUtil.copyToBean(form, partialPositionEntity, null);

		String sHistory_limit_date = request.getParameter("history_limit_date");
		String sModel_ids = request.getParameter("model_id");

		/* 取得零件型号 工位 有效期 */
		List<PartialPositionForm> partialPositionFormList = this.searchPartialModelNameProcessCodeActiveData(partialPositionEntity, conn);
		String position_id = "";

		for (int i = 0; i < partialPositionFormList.size(); i++) {
			PartialPositionForm partialPositionForm = partialPositionFormList.get(i);
			position_id = partialPositionForm.getPosition_id();

			positionParam.put("position_id", position_id);
			partialPositionEntity.setPosition_id(position_id);
		}
		/* model_name */
		List<String> modelIds = new ArrayList<String>();
		if (sModel_ids.contains(",")) {
			String[] modelIdArray = sModel_ids.split(",");
			for (int i = 0; i < modelIdArray.length; i++) {
				modelIds.add(modelIdArray[i]);
			}
		} else {
			modelIds.add(sModel_ids);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		Date date = sdf.parse(sHistory_limit_date);

		String partial_id = partialPositionEntity.getPartial_id();
		String new_partial_id = partialPositionEntity.getNew_partial_id();
		;
		historyParam.put("position_id", position_id);
		historyParam.put("partial_id", partial_id);
		historyParam.put("new_partial_id", new_partial_id);
		historyParam.put("history_limit_date", sdf.format(date));
		historyParam.put("model_id", modelIds);

		// 更新定位表
		partialDao.updatePartialPosition(historyParam);

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		String updated_by = user.getOperator_id();

		String modelId = "";

		for (int i = 0; i < modelIds.size(); i++) {
			modelId = modelIds.get(i);
			partialPositionEntity.setModel_id(modelId);
			partialPositionEntity.setPartial_id(partial_id);
			partialPositionEntity.setNew_partial_id(new_partial_id);
			partialPositionEntity.setHistory_limit_date(date);
			partialPositionEntity.setUpdated_by(updated_by);
			partialPositionEntity.setParent_partial_id(partial_id);

			// 插入改废订历史管理表
			partialDao.insertPartialWasteModifyHistory(partialPositionEntity);

			PartialEntity partialEntity = new PartialEntity();
			partialEntity.setPartial_id(partialPositionEntity.getPartial_id());

			List<String> partialIdList = partialDao.checkPartial(partialEntity);
			if (partialIdList.size() > 0) {
				String codeByPartialID = partialIdList.get(0);
				partialPositionEntity.setPartial_id(codeByPartialID);
			}

			// 判断零件定位表是否已经存在该条记录
			PartialPositionMapper partialPositionDao = conn.getMapper(PartialPositionMapper.class);
			List<PartialPositionEntity> partialPositionEntityList = partialPositionDao
					.searchPartialPosition(partialPositionEntity);
			if (partialPositionEntityList.size() > 0) {
				// 插入定位表
				partialDao.insertPartialPosition(partialPositionEntity);
			}
		}
//
//		for (int i = 0; i < modelIds.size(); i++) {
//			modelId = modelIds.get(i);
//			partialPositionEntity.setModel_id(modelId);
//			partialPositionEntity.setPartial_id(partial_id);
//			partialPositionEntity.setNew_partial_id(new_partial_id);
//			partialPositionEntity.setHistory_limit_date(date);
//			partialPositionEntity.setUpdated_by(updated_by);
//			partialPositionEntity.setParent_partial_id(partial_id);
//
//			// 插入改废订历史管理表
//			partialDao.insertPartialWasteModifyHistory(partialPositionEntity);
//
//			PartialEntity partialEntity = new PartialEntity();
//			partialEntity.setPartial_id(partialPositionEntity.getPartial_id());
//
//			// List<String> partialIdList = partialDao.checkPartial(partialEntity);
//
//			// String codeByPartialID = partialIdList.get(0);
//			// 插入定位表
//			partialPositionEntity.setModel_id(modelId);
//			partialPositionEntity.setPartial_id(new_partial_id);
//			partialPositionEntity.setNew_partial_id(new_partial_id);
//			partialPositionEntity.setHistory_limit_date(date);
//			partialPositionEntity.setUpdated_by(updated_by);
//			partialPositionEntity.setParent_partial_id(partial_id);
//
//			partialDao.insertPartialPosition(partialPositionEntity);
//		}
	}

	/**
	 * 解析上传文件，更新零件价格
	 * @param form
	 * @param conn
	 * @param errors
	 * @throws Exception
	 */
	public void updatePrice(ActionForm form, SqlSessionManager conn, List<MsgInfo> errors) throws Exception{
		UploadService uService = new UploadService();
		String tempfilename = uService.getFile2Local(form, errors);
		
		// 文件不存在
		if(errors.size() > 0){
			return;
		}
		
		// 判断文件格式
		if (!tempfilename.endsWith(".xls")) {
			MsgInfo error = new MsgInfo();
			error.setErrcode("file.invalidType");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.invalidType"));
			errors.add(error);
			return;
		}
		
		InputStream in = null;
		try{
			// 读取文件
			in = new FileInputStream(tempfilename);
			
			// 工作簿对象 
			HSSFWorkbook work = new HSSFWorkbook(in);
			
			// 获取Sheet
			HSSFSheet sheet =  work.getSheetAt(0);
			
			// 行对象
			HSSFRow row = null;
			
			// 单元格对象
			HSSFCell cell = null;
			
			Pattern pricePattern = Pattern.compile("\\d{1,5}(\\.\\d{1,2})?");
			
			Matcher matcher = null;
			
			// 获取第2行
			row = sheet.getRow(1);
			if(row == null){
				MsgInfo error = new MsgInfo();
				error.setErrcode("file.invalidFormat");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.invalidFormat"));
				errors.add(error);
				return;
			}
			
			// 零件名称
			cell = row.getCell(CellReference.convertColStringToIndex("A"));
			if(cell == null){
				MsgInfo error = new MsgInfo();
				error.setErrcode("file.invalidFormat");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.invalidFormat"));
				errors.add(error);
				return;
			}
			
			// 价格
			cell = row.getCell(CellReference.convertColStringToIndex("B"));
			if(cell == null){
				MsgInfo error = new MsgInfo();
				error.setErrcode("file.invalidFormat");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.invalidFormat"));
				errors.add(error);
				return;
			}
			
			String strPrice = UploadService.getCellStringValue(cell);
			matcher = pricePattern.matcher(strPrice);
			if(!matcher.matches()){
				MsgInfo error = new MsgInfo();
				error.setErrcode("file.invalidFormat");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.invalidFormat"));
				errors.add(error);
				return;
			}
			
			// 最后一行索引
			int lastrow = sheet.getLastRowNum();
			
			String code = null;
			BigDecimal price = null;
			
			List<PartialEntity> xlsList = new ArrayList<PartialEntity>();
			
			for(int i = 1;i <= lastrow;i++){
				row = sheet.getRow(i);
				
				// 行不存在继续读取下一行
				if(row == null){
					continue;
				}
				
				// 零件名称
				cell = row.getCell(CellReference.convertColStringToIndex("A"));
				code = UploadService.getCellStringValue(cell);

				// 零件名称单元格不存在或者单元格没有内容继续读取下一行
				if(cell == null || CommonStringUtil.isEmpty(code)){
					continue;
				}
				
				// 价格
				cell = row.getCell(CellReference.convertColStringToIndex("B"));
				strPrice = CopyByPoi.getCellStringValue(cell);
				
				// 价格单元格不存在或者单元格没有内容继续读取下一行
				if(cell == null || CommonStringUtil.isEmpty(strPrice)){
					continue;
				}
				
				// 价格格式不正确继续读取下一行
				matcher = pricePattern.matcher(strPrice);
				if(!matcher.matches()){
					continue;
				}
				
				PartialEntity entity = new PartialEntity();
				entity.setCode(code);
				entity.setPrice(new BigDecimal(strPrice));
				
				xlsList.add(entity);
			}
			
			// 没有数据，不用更新
			if(xlsList.size() == 0){
				return;
			}
			
			// 零件Mapper
			PartialMapper partialMapper = conn.getMapper(PartialMapper.class);
			
			// 获取所有零件
			List<PartialEntity> partialList = partialMapper.searchAllPartial();
			
			Map<String,PartialEntity> partialMap = new HashMap<String,PartialEntity>();
			
			// 将List转变为Map
			for(PartialEntity entity : partialList){
				partialMap.put(entity.getCode(), entity);
			}
			
			// 遍历上传文件数据集
			for(int i = 0; i < xlsList.size();i++){
				PartialEntity xlsEntity  = xlsList.get(i);
				
				// 零件编码
				code = xlsEntity.getCode();
				
				// 价格
				price = xlsEntity.getPrice();
				
				// 零件存在
				if(partialMap.containsKey(code)){
					PartialEntity dbEntity  = partialMap.get(code);
					
					// db价格为空或者价格不相等
					if(dbEntity.getPrice() == null || (price.doubleValue() != dbEntity.getPrice().doubleValue())){
						xlsEntity.setPartial_id(dbEntity.getPartial_id());
						partialMapper.updatePrice(xlsEntity);
					}
				}
			}
		}catch(Exception e){
			throw e;
		}finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 下载零件价格
	 * @param conn
	 * @return
	 */
	public String downloadPrice(SqlSession conn) throws Exception{
		String cacheName = "零件价格" + new Date().getTime() + ".xls";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" +cacheName; 
		
		// 零件Mapper
		PartialMapper partialMapper = conn.getMapper(PartialMapper.class);
		
		OutputStream out = null;
		
		try{
			// 定义文件
			File file = new File(cachePath);
			
			// 判断文件目录是否存在
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			
			// 文件不存在
			if(!file.exists()){
				file.createNewFile();
			}
			
			// 创建Excel
			HSSFWorkbook work = new HSSFWorkbook();
			
			// 创建Sheet
			HSSFSheet sheet = work.createSheet("零件价格");
			
			HSSFRow row = null;
			
			// 字体
			Font font = work.createFont();
			font.setFontHeightInPoints((short) 10);
			font.setFontName("微软雅黑");
			
			// 基本样式
			CellStyle cellStyle = work.createCellStyle();
			cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
			cellStyle.setBorderTop(CellStyle.BORDER_THIN);
			cellStyle.setBorderRight(CellStyle.BORDER_THIN);
			cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
			cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
			cellStyle.setFont(font);
			
			// 居中对齐
			CellStyle centerStyle = work.createCellStyle();
			centerStyle.cloneStyleFrom(cellStyle);
			centerStyle.setAlignment(CellStyle.ALIGN_CENTER);
			
			// 居右对齐
			CellStyle rightStyle = work.createCellStyle();
			rightStyle.cloneStyleFrom(cellStyle);
			rightStyle.setAlignment(CellStyle.ALIGN_RIGHT);
			
			sheet.setColumnWidth(0, 256*12);
			sheet.setColumnWidth(1, 256*10);
			
			row = sheet.createRow(0);
			
			CellUtil.createCell(row, 0, "零件品名", centerStyle);
			CellUtil.createCell(row, 1, "FOB价", centerStyle);
			
			// 获取所有零件
			List<PartialEntity> partialList = partialMapper.searchAllPartial();
			PartialEntity entity = null;
			
			for(int i = 0;i < partialList.size();i++){
				row = sheet.createRow(i+1);
				
				entity = partialList.get(i);
				
				// 零件Code
				CellUtil.createCell(row, 0, entity.getCode(), cellStyle);
				
				// 价格
				if(entity.getPrice() == null){
					CellUtil.createCell(row, 1, null, rightStyle);
				}else{
					CellUtil.createCell(row, 1, String.valueOf(entity.getPrice().doubleValue()), rightStyle);
				}
			}
			
			out = new FileOutputStream(file);
			work.write(out);
		}catch (Exception e) {
			throw e;
		} finally {
			if (out != null) {
				out.close();
				out = null;
			}
		}
		
		return cacheName;
	}
}
