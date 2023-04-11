package com.osh.rvs.service.partial;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.master.PartialEntity;
import com.osh.rvs.bean.master.PartialPositionEntity;
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.common.CopyByPoi;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.ReverseResolution;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.master.PartialPositionForm;
import com.osh.rvs.mapper.master.PartialMapper;
import com.osh.rvs.mapper.master.PartialPositionMapper;
import com.osh.rvs.service.PartialService;
import com.osh.rvs.service.PositionService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;

public class PartialPositionService {

	private static Logger _log = Logger.getLogger(PartialPositionService.class);

	public List<PartialPositionForm> searchPartialPosition(PartialPositionEntity partialPositionEntity, SqlSession conn) {
		PartialPositionMapper dao = conn.getMapper(PartialPositionMapper.class);
		List<PartialPositionForm> resultForm = new ArrayList<PartialPositionForm>();
		// TODO newNot
		List<PartialPositionEntity> resultList = dao.searchPartialPositionNew(partialPositionEntity);
		BeanUtil.copyToFormList(resultList, resultForm, null, PartialPositionForm.class);
		return resultForm;
	}

	public void updatePartialPosition(ActionForm form, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors)
			throws Exception {
		PartialPositionMapper dao = conn.getMapper(PartialPositionMapper.class);
		PartialPositionEntity partialPositionEntity = new PartialPositionEntity();
		BeanUtil.copyToBean(form, partialPositionEntity, null);
		//partialPositionEntity.setNew_partial_id(partialPositionEntity.getPartial_id());
		
		//设置new_partial_id =0，零件选中进行废止
		String new_partial_id ="0";
		partialPositionEntity.setNew_partial_id(new_partial_id);
		
		// 更新定位表
		dao.updatePartialPosition(partialPositionEntity);

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		partialPositionEntity.setUpdated_by(user.getOperator_id());
		// 插入改废订历史管理表
		dao.insertPartialWasteModifyHistory(partialPositionEntity);
	}

	public void updatePartialPositionNewPartial(ActionForm form, HttpSession session, SqlSessionManager conn,
			List<MsgInfo> errors) throws Exception {
		PartialPositionMapper dao = conn.getMapper(PartialPositionMapper.class);
		PartialPositionEntity partialpositionEntity = new PartialPositionEntity();
		BeanUtil.copyToBean(form, partialpositionEntity, null);
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		partialpositionEntity.setUpdated_by(user.getOperator_id());

		PartialMapper partialDao = conn.getMapper(PartialMapper.class);
		PartialEntity partialEntity = new PartialEntity();
		partialEntity.setCode(partialpositionEntity.getCode());
	
		// 更新数据(更新有效截止时间和)
		dao.updatePartialPositionOldHistoryLimitDate(partialpositionEntity);

		// 插入历史管理表
		List<String> list = partialDao.checkPartial(partialEntity);
		String new_partial_id = list.get(0);
		partialpositionEntity.setNew_partial_id(new_partial_id);
		dao.insertPartialWasteModifyHistory(partialpositionEntity);
		partialpositionEntity.setPartial_id(new_partial_id);
		// 插入定位表
		dao.insertPartialPosition(partialpositionEntity);

	}
	/*验证型号*/
	public void modelNameValidate(PartialPositionEntity partialPositionEntity , List<MsgInfo> errors){
		if(CommonStringUtil.isEmpty(partialPositionEntity.getModel_id())){
			MsgInfo error = new MsgInfo();
			error.setComponentid("model_id");
			error.setErrcode("validator.required");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("validator.required", "零件型号",
					partialPositionEntity.getCode(), "零件"));
			errors.add(error);
		}
	}
	/*验证零件是否已经存在*/
	public void customValidate(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		PartialPositionEntity partialPositionEntity = new PartialPositionEntity();
		PartialPositionMapper dao = conn.getMapper(PartialPositionMapper.class);
        BeanUtil.copyToBean(form, partialPositionEntity,(new CopyOptions()).include("history_limit_date","code","model_id", "position_id"));
        
        PartialMapper partialDao = conn.getMapper(PartialMapper.class);
        PartialEntity partialEntity = new PartialEntity();
        partialEntity.setCode(partialPositionEntity.getCode());
        List<String> partialIDList  =partialDao.checkPartial(partialEntity);
        if(partialIDList.size()>0){
        	String partial_id = partialIDList.get(0);
        	partialPositionEntity.setPartial_id(partial_id);
        }
        List<String> result =  dao.checkPartialPosition(partialPositionEntity);
		if (result != null && result.size() > 0) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("code");
			error.setErrcode("dbaccess.columnNotUnique");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.columnNotUnique", "零件编码",
					partialPositionEntity.getCode(), "零件"));
			errors.add(error);
		}
	}

	/*验证零件是否已经存在*/
	public void customValidateChange(ActionForm form, SqlSession conn, List<MsgInfo> errors) {
		PartialPositionEntity partialPositionEntity = new PartialPositionEntity();
		PartialPositionMapper dao = conn.getMapper(PartialPositionMapper.class);
        BeanUtil.copyToBean(form, partialPositionEntity,(new CopyOptions()).include("history_limit_date","code","model_id", "position_id"));
        
        PartialMapper partialDao = conn.getMapper(PartialMapper.class);
        PartialEntity partialEntity = new PartialEntity();
        partialEntity.setCode(partialPositionEntity.getCode());
        List<String> partialIDList  =partialDao.checkPartial(partialEntity);
        if(partialIDList.size()>0){
        	String partial_id = partialIDList.get(0);
        	partialPositionEntity.setPartial_id(partial_id);
        }
        List<String> result =  dao.checkPartialPosition(partialPositionEntity);
		if (result != null && result.size() > 0) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("code");
			error.setErrcode("dbaccess.columnNotUnique");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.columnNotUnique", "零件编码",
					partialPositionEntity.getCode(), "零件"));
			errors.add(error);
		}
	}

	/**
	 * 检查SAP导入的文件
	 * 
	 * @param tempfilename
	 * @param conn
	 * @param errors
	 * @return
	 */
	public boolean checkReadBomFile(String tempfilename, String targetModelName,
			SqlSessionManager conn, List<MsgInfo> errors) {
		boolean acceptableFormat = false;
		InputStream in = null;

		PartialPositionMapper partialPositionMapper = conn.getMapper(PartialPositionMapper.class);
		PositionService pService = new PositionService();
		Date today = new Date();

		try {
			in = new BufferedInputStream(new FileInputStream(tempfilename));
			XSSFWorkbook work = new XSSFWorkbook(in);

			XSSFSheet sheet 
			= work.getSheet("ZCMMXLBOM");
			if (sheet == null) sheet = work.getSheetAt(0);

			// 读取文件头
			// bomcode
			int idxBomCode = -1;
			// ProductCode
			int idxProductCode = -1;
			// ParentItemCode
			int idxParentItemCode = -1;
			// 子物料号
			int idxItemCode = -1;
			// 数量	
			int idxQuantity = -1;
			// EnabledDat
			int idxActiveDate = -1;
			// DisableDat
			int idxHistoryLimitDate = -1;
			// ProcessCode
			int idxProcessCode = -1;

			XSSFRow titleRow = sheet.getRow(0);
			int rowNumber = 1;
			for (; rowNumber <= sheet.getLastRowNum(); rowNumber++) {
				if (titleRow != null) {
					break;
				}
				titleRow = sheet.getRow(rowNumber);
			}
			if (titleRow == null) {
				_log.error("没有表头。");
				MsgInfo error = new MsgInfo();
				error.setComponentid("code");
				error.setErrcode("file.invalidFormat");
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.invalidFormat") + "没有表头。");
				errors.add(error);
				return false;
			}
			for (int i = 0; i < titleRow.getLastCellNum(); i++) {
				XSSFCell titleCell = titleRow.getCell(i);
				if (titleCell != null && titleCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
					switch(titleCell.getStringCellValue().trim().toLowerCase()) {
					case "productcode" :
					case "pcode" :
						idxProductCode = i; break;
					case "bomcode" :
						idxBomCode = i; break;
					case "parentitemcode" :
					case "parentcode" :
						idxParentItemCode = i; break;
					case "childitemcode" :
					case "子物料号" :
						idxItemCode = i; break;
					case "qty" :
					case "数量" : 
						idxQuantity = i; break;
					case "enableddate" : 
					case "enableddat" : 
						idxActiveDate = i; break;
					case "disableddate" : 
					case "disabledat" : 
						idxHistoryLimitDate = i; break;
					case "定位" :
						idxProcessCode = i; break;
					}
				}
			}

			// 除定位外的列都需要
			if (idxBomCode == -1 || idxProductCode == -1 || idxParentItemCode == -1 || idxItemCode == -1 || idxQuantity == -1 || idxHistoryLimitDate == -1) {
				return false;
			}

			// 零件导入服务
			PartialService partService = new PartialService();

			// 读入型号失败暂存
			Set<String> modelLoseCache = new HashSet<String>(); 

			Map<PartialPositionEntity, String> multiPositionPartsForFile = new HashMap<PartialPositionEntity, String>(); 

			// 读入型号列表
			Map<String, List<PartialPositionEntity>> modelReadIn = new HashMap<String, List<PartialPositionEntity>>();

			for (; rowNumber <= sheet.getLastRowNum(); rowNumber++) {
				PartialPositionEntity partialPositionEntity = new PartialPositionEntity();
				XSSFRow row = sheet.getRow(rowNumber);
				if (row == null) continue;

				// BomCode
				String bomCode = CopyByPoi.getCellStringValue(row.getCell(idxBomCode));
				partialPositionEntity.setBom_code(bomCode);

				// ProductCode
				String productCode = CopyByPoi.getCellStringValue(row.getCell(idxProductCode));
				if (targetModelName != null && !targetModelName.equals(productCode)) continue;

				if (modelLoseCache.contains(productCode)) continue;

				String modelId = ReverseResolution.getModelByName(productCode, conn);
				if (modelId == null) {
					modelLoseCache.add(productCode);
					_log.warn("型号不可识别：" + productCode);
					continue;
				}

				partialPositionEntity.setModel_id(modelId);
				if (!modelReadIn.containsKey(modelId)) {
					modelReadIn.put(modelId, new ArrayList<PartialPositionEntity>());
				}

				// ParentItemCode
				String parentItemCode = CopyByPoi.getCellStringValue(row.getCell(idxParentItemCode));
				String parentPartialId = null;
				if (parentItemCode.equals(productCode)) {
					// 顶层零件
					parentPartialId = "00000000000";
				} else {
					parentPartialId = ReverseResolution.getPartialByCode(parentItemCode, conn);
					if (parentPartialId == null) {
						_log.warn("零件不可识别：" + parentPartialId);
						parentPartialId = partService.insert(parentItemCode, conn);
					}
				}

				partialPositionEntity.setParent_partial_id(parentPartialId);

				// 子物料号
				String itemCode = CopyByPoi.getCellStringValue(row.getCell(idxItemCode));
				String PartialId = ReverseResolution.getPartialByCode(itemCode, conn);
				if (PartialId == null) {
					_log.warn("零件不可识别：" + itemCode);
					PartialId = partService.insert(itemCode, conn);
				}

				partialPositionEntity.setPartial_id(PartialId);

				// 数量	
				Integer quantity = getNumCellValue(row.getCell(idxQuantity));

				partialPositionEntity.setQuantity(quantity);

				// EnabledDat
				if (idxActiveDate >= 0) {
					Date activeDate = CopyByPoi.getCellDateValue(row.getCell(idxActiveDate));
					partialPositionEntity.setActive_date(activeDate);
				}

				// DisableDat
				Date historyLimitDate = CopyByPoi.getCellDateValue(row.getCell(idxHistoryLimitDate));

				partialPositionEntity.setHistory_limit_date(historyLimitDate);

				// ProcessCode
				if (idxProcessCode >=0) {
					String processCode = CopyByPoi.getCellStringValue(row.getCell(idxProcessCode));
					if (!CommonStringUtil.isEmpty(processCode)) {
						if (processCode.indexOf(";") > -1) {
							multiPositionPartsForFile.put(partialPositionEntity, processCode);
						} else {
							if (processCode.indexOf(":") > -1) {
								processCode = processCode.substring(0, 3);
							}
							String positionId = ReverseResolution.getPositionByProcessCode(processCode, conn);
							partialPositionEntity.setPosition_id(positionId);
						}
					}
				}

				modelReadIn.get(modelId).add(partialPositionEntity);
			}

			for (String modelId : modelReadIn.keySet()) {
				List<PartialPositionEntity> listOfModel = modelReadIn.get(modelId);

				Map<PartialPositionEntity, String> multiPositionParts = new HashMap<PartialPositionEntity, String>(); 

				// 没有定位信息时，取得现有定位信息
				if (idxProcessCode == -1) {
					List<PartialPositionEntity> orgList = partialPositionMapper.getPartialPositionOfModel(modelId);
					if (orgList.size() > 0) {
						Map<String, String> toNewMap = new HashMap<String, String>();
						Map<String, HashSet<String>> positionMap = new HashMap<String, HashSet<String>>();

						// 取得原数据
						for (PartialPositionEntity orgBean : orgList) {
							String poskey = orgBean.getParent_partial_id() + "_" + orgBean.getPartial_id();
							if (!orgBean.getPartial_id().equals(orgBean.getNew_partial_id())) {
								toNewMap.put(poskey, orgBean.getNew_partial_id());
							}
							String positionId = orgBean.getPosition_id();
							if (positionId != null && !"00000000000".equals(positionId)) {
								if (!positionMap.containsKey(poskey)) {
									positionMap.put(poskey, new HashSet<String>());
								}
								positionMap.get(poskey).add(positionId);
							}
						}

						// 关联元数据
						for (PartialPositionEntity ppEntity : listOfModel) {
							String poskey = ppEntity.getParent_partial_id() + "_" + ppEntity.getPartial_id();
							// 零件改定更替关系
							if (toNewMap.containsKey(poskey)) {
								ppEntity.setNew_partial_id(toNewMap.get(poskey));
							}
							// 定位信息
							if (positionMap.containsKey(poskey)) {
								HashSet<String> l = positionMap.get(poskey);
								if (l.size() == 1) {
									for (String positionId : l) {
										ppEntity.setPosition_id(positionId);
									}
								} else {
									int thisQuantity = ppEntity.getQuantity();
									int remainder = 0;
									String autoArras = "";
									if (thisQuantity > l.size()) {
										remainder = thisQuantity - l.size() - 1;
									}
									for (String positionId : l) {
										if ("00000000000".equals(positionId)) continue;
										PositionEntity pEntity = pService.getPositionEntityByKey(positionId, conn);
										if (thisQuantity > 0) {
											// 分配1个 + 余数
											autoArras += pEntity.getProcess_code() + ":" + (1 + remainder) + ";";
											remainder = 0;
											thisQuantity -= 1;
										} else {
											autoArras += pEntity.getProcess_code() + ":0;";
										}
									}
									if (autoArras.length() > 0) {
										multiPositionParts.put(ppEntity, autoArras.substring(0, autoArras.length() - 1));
									}
								}
							} else {
								// 不存在的数据，如果已经废止不则需要
								if (ppEntity.getHistory_limit_date().before(today)) {
									ppEntity.setHistory_limit_date(null);
								}
							}
						}						
					}
				} else {
					// 有定位信息时，只要零件改定关系
					List<PartialPositionEntity> orgList = partialPositionMapper.getPartialPositionRevisionOfModel(modelId);
					if (orgList.size() > 0) {
						Map<String, String> toNewMap = new HashMap<String, String>();
						for (PartialPositionEntity orgBean : orgList) {
							toNewMap.put(orgBean.getParent_partial_id() + "_" + orgBean.getPartial_id(),
									orgBean.getNew_partial_id());
						}
						for (PartialPositionEntity ppEntity : listOfModel) {
							if (toNewMap.containsKey(ppEntity.getParent_partial_id() + "_" + ppEntity.getPartial_id())) {
								ppEntity.setNew_partial_id(toNewMap.get(ppEntity.getParent_partial_id() + "_" + ppEntity.getPartial_id()));
							}
						}
					}

					for (PartialPositionEntity e : multiPositionPartsForFile.keySet()) {
						if (e.getModel_id().equals(modelId)) {
							multiPositionParts.put(e, multiPositionPartsForFile.get(e));
						}
					}
				}

				// 删除现有BOM/定位信息
				partialPositionMapper.deletePartialPosition(modelId);

				for (PartialPositionEntity ppEntity : listOfModel) {
					if (ppEntity.getHistory_limit_date() == null) {
						continue;
					}
					if (ppEntity.getNew_partial_id() == null) {
						ppEntity.setNew_partial_id(ppEntity.getPartial_id());
					}
					if (ppEntity.getPosition_id() == null) {
						ppEntity.setPosition_id("0");
					}
					partialPositionMapper.insertPartialPosition(ppEntity);
				}

				// 多工位零件更新
				for (PartialPositionEntity multiPositionPart : multiPositionParts.keySet()) {
					String multiPosition = multiPositionParts.get(multiPositionPart);
					String[] positionAndQuantity = multiPosition.split(";");
					for (int i=0; i < positionAndQuantity.length; i++) {
						String[] splitPQ = positionAndQuantity[i].split(":");
						multiPositionPart.setPosition_id(ReverseResolution.getPositionByProcessCode(splitPQ[0], conn));
						int quatity = 0;
						try {
							quatity = Integer.parseInt(splitPQ[1]);
						} catch(Exception e) {
						}
						multiPositionPart.setQuantity(quatity);
						if (i==0) {
							// 首条记录Update
							partialPositionMapper.updatePartialPositionQuantity(multiPositionPart);
						} else {
							// 其他记录Insert
							partialPositionMapper.insertPartialPosition(multiPositionPart);
						}
					}
				}
			}

			acceptableFormat = true;
		} catch (Exception e) {
			_log.error(e.getMessage(), e);
			MsgInfo error = new MsgInfo();
			error.setComponentid("code");
			error.setErrcode("file.invalidFormat");
			error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("file.invalidFormat") + e.getMessage());
			errors.add(error);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return acceptableFormat;
	}

	private Integer getNumCellValue(XSSFCell cell) {
		if (cell == null) {
			return null;
	    }
		switch (cell.getCellType()) {
		case XSSFCell.CELL_TYPE_NUMERIC:
			return (int) cell.getNumericCellValue();
		default:
			return null;
		}
	}

	public static final String TOP_PAGE = "@";

	/**
	 * 按型号读取工作指示单
	 * 
	 * @param model_id
	 * @param conn
	 * @return map<零件页, 页列表>
	 */
	public Map<String, List<PartialPositionForm>> loadInstruct(
			String model_id, SqlSession conn) {
		PartialPositionMapper mapper = conn.getMapper(PartialPositionMapper.class);

		List<PartialPositionEntity> l = mapper.getInstructOfModel(model_id);

		Map<String, String> parentPage = new HashMap<String, String>();
		Map<String, Integer> subLevel = new HashMap<String, Integer>();

		// 暂未登录的上级零件
		Map<String, List<PartialPositionEntity>> lostParents = new HashMap<String, List<PartialPositionEntity>>();

		Map<String, List<PartialPositionForm>> ret = new TreeMap<String, List<PartialPositionForm>>();

		for (PartialPositionEntity entity : l) {
			String this_id = entity.getPartial_id();
			String parent = entity.getParent_partial_code();
			String parent_id = entity.getParent_partial_id();

			String page = null;
			if (parent == null) {
				parentPage.put(this_id, TOP_PAGE);
				parent = TOP_PAGE;
				page = TOP_PAGE;

				if (!ret.containsKey(page)) {
					ret.put(page, new ArrayList<PartialPositionForm>());
				}

				PartialPositionForm form = new PartialPositionForm();
				BeanUtil.copyToForm(entity, form, CopyOptions.COPYOPTIONS_NOEMPTY);
				ret.get(page).add(form);

			} else {
				// 有上级零件
				if (parentPage.containsKey(parent_id)) {
					// 自身有页面
					String parentOfPage = page = parentPage.get(parent_id);
					if (TOP_PAGE.equals(parentOfPage)) {
						// 上级是顶页的，则属于上级这一页
						page = parent;
					} else {
						// 设置子零件层数
						Integer parentSubLevel = subLevel.get(parent_id);
						if (parentSubLevel == null) parentSubLevel = 1;
						int sendSubLevel = parentSubLevel + 1;
						subLevel.put(this_id, sendSubLevel);
						subLevel.put(entity.getBom_code(), sendSubLevel);
						entity.setLevel("" + sendSubLevel);
					}
					parentPage.put(this_id, page);

					if (!ret.containsKey(page)) {
						ret.put(page, new ArrayList<PartialPositionForm>());
					}

					PartialPositionForm form = new PartialPositionForm();
					BeanUtil.copyToForm(entity, form, CopyOptions.COPYOPTIONS_NOEMPTY);

					int insertIdx = Integer.MAX_VALUE;
					List<PartialPositionForm> pageList = ret.get(page);
					int pageSize = pageList.size();

					if (!TOP_PAGE.equals(parentOfPage)) {
						// 找到上层，加入到上层之后
						for (int i = 0; i < pageSize; i++) {
							PartialPositionForm parentForm = pageList.get(i);
							if (parentForm.getPartial_id().equals(parent_id)) {
								insertIdx = i + 1;
								break;
							}
						}
					}

					if (insertIdx > pageSize) {
						pageList.add(form);
					} else {
						pageList.add(insertIdx, form);
					}

					// 自身是暂未登录的零件
					if (lostParents.containsKey(this_id)) {
						addSubPart(this_id, entity.getBom_code(), insertIdx, pageSize, pageList, lostParents, subLevel);
					}

				} else {
					// 上级零件如果没有定页，一定是3级及以下的。先加入暂未登录
					if (!lostParents.containsKey(parent_id)) {
						lostParents.put(parent_id, new ArrayList<PartialPositionEntity>());
					}
					lostParents.get(parent_id).add(entity);
					continue;
				}
			}
		}

		return ret;
	}

	private int addSubPart(String this_id, String bom_code, int insertIdx, int pageSize, List<PartialPositionForm> pageList,
			Map<String, List<PartialPositionEntity>> lostParents, Map<String, Integer> subLevel) {

		// 将暂时登录的下级加入到自身下方
		// 设置子零件层数
		Integer parentSubLevel = subLevel.get(bom_code);
		if (parentSubLevel == null) parentSubLevel = 1;
		int sendSubLevel = parentSubLevel + 1;

		if (insertIdx > pageSize) {
			for (PartialPositionEntity subEntity : lostParents.get(this_id)) {
				subEntity.setLevel("" + sendSubLevel);
				subLevel.put(subEntity.getPartial_id(), sendSubLevel);
				subLevel.put(subEntity.getBom_code(), sendSubLevel);

				PartialPositionForm subForm = new PartialPositionForm();
				BeanUtil.copyToForm(subEntity, subForm, CopyOptions.COPYOPTIONS_NOEMPTY);
				pageList.add(subForm);

				// 子零件也是未登录上级的时候
				if (lostParents.containsKey(subEntity.getPartial_id())) {
					insertIdx = addSubPart(subEntity.getPartial_id(), subEntity.getBom_code(), insertIdx, pageSize, pageList, lostParents, subLevel);
				}
			}
		} else {
			for (PartialPositionEntity subEntity : lostParents.get(this_id)) {
				subEntity.setLevel("" + sendSubLevel);
				subLevel.put(subEntity.getPartial_id(), sendSubLevel);
				subLevel.put(subEntity.getBom_code(), sendSubLevel);

				PartialPositionForm subForm = new PartialPositionForm();
				BeanUtil.copyToForm(subEntity, subForm, CopyOptions.COPYOPTIONS_NOEMPTY);
				pageList.add(++insertIdx, subForm);
				if (lostParents.containsKey(subEntity.getPartial_id())) {
					insertIdx = addSubPart(subEntity.getPartial_id(), subEntity.getBom_code(), insertIdx, pageSize, pageList, lostParents, subLevel);
				}
			}
		}

		return insertIdx;
	}

	/**
	 * 按型号取得组件
	 * 
	 * @param model_id
	 * @param conn
	 * @return list<组件code>
	 */
	public List<String> getComponentOfModel(String model_id, SqlSession conn) {
		PartialPositionMapper mapper = conn.getMapper(PartialPositionMapper.class);

		return mapper.getComponentOfModel(model_id);
	}

	public String makeKindBomFile(String kind,
			SqlSession conn) {

		PartialPositionMapper mapper = conn.getMapper(PartialPositionMapper.class);

		List<PartialPositionEntity> l = mapper.getInstructOfCategoryKind(kind);

		if (l.size() == 0) return null;

		return makeBomFile(l, conn);
	}
	public String makeModelBomFile(String model_id,
			SqlSession conn) {

		PartialPositionMapper mapper = conn.getMapper(PartialPositionMapper.class);

		List<PartialPositionEntity> l = mapper.getAllInstructOfModel(model_id);

		if (l.size() == 0) return null;

		return makeBomFile(l, conn);
	}
	public String makeBomFile(List<PartialPositionEntity> l,
			SqlSession conn) {

		//Excel临时文件
		String cacheName ="BOM_position" + new Date().getTime() + ".xlsx";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\"; 
		File filePath = new File(cachePath);
		if(!filePath.exists()){
			filePath.mkdirs();
		}
	
		OutputStream out = null;
		try {
			cachePath += cacheName;
			File file = new File(cachePath);
			if(!file.exists()){
				file.createNewFile();
			}
			
			XSSFWorkbook work=new XSSFWorkbook();
			XSSFSheet sheet = work.createSheet("零件BOM及定位");

			int idx = 0;
			XSSFRow row = sheet.createRow(idx++);
			row.createCell(0).setCellValue("ProductCode");
			row.createCell(1).setCellValue("BomCode");
			row.createCell(2).setCellValue("ParentItemCode");
			row.createCell(3).setCellValue("子物料号");
			row.createCell(4).setCellValue("数量");
			row.createCell(5).setCellValue("EnabledDate");
			row.createCell(6).setCellValue("DisabledDate");
			row.createCell(7).setCellValue("定位");

			for (PartialPositionEntity posiEntity : l) {
				row = sheet.createRow(idx++);

				// productcode
				row.createCell(0).setCellValue(posiEntity.getModel_name());

				// bomcode
				row.createCell(1).setCellValue(posiEntity.getBom_code());

				// parentitemcode
				String parentItemCode = posiEntity.getParent_partial_code();
				if (parentItemCode == null) {
					parentItemCode = posiEntity.getModel_name();
				}
				row.createCell(2).setCellValue(parentItemCode);

				// 子物料号
				row.createCell(3).setCellValue(posiEntity.getCode());

				// 数量
				row.createCell(4).setCellValue(posiEntity.getQuantity());

				// enableddate
				Date enabledDate = posiEntity.getActive_date();
				if (enabledDate == null) {
					row.createCell(5).setCellType(XSSFCell.CELL_TYPE_BLANK);
				} else {
					row.createCell(5).setCellValue(DateUtil.toString(enabledDate, DateUtil.ISO_DATE_PATTERN));
				}

				// disableddate
				row.createCell(6).setCellValue(
						DateUtil.toString(posiEntity.getHistory_limit_date(), DateUtil.ISO_DATE_PATTERN));

				// 定位
				String position = posiEntity.getProcess_code();
				if (position == null) {
					row.createCell(7).setCellType(XSSFCell.CELL_TYPE_BLANK);
				} else {
					if (position.indexOf(";") < 0) {
						position = position.substring(0, 3);
					}
					row.createCell(7).setCellValue(position);
				}
			}

			out= new FileOutputStream(file);
			work.write(out);

		} catch (Exception ex){
			_log.error(ex.getMessage(), ex);
		} finally{
			if(out!=null){
				try {
					out.close();
				} catch (IOException e) {
					_log.error(e.getMessage(), e);
				}
			}
		}

		return cacheName;
	}
}
