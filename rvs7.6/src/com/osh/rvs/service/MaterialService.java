package com.osh.rvs.service;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import net.arnx.jsonic.JSON;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.data.MaterialTimeNodeEntity;
import com.osh.rvs.bean.data.PostMessageEntity;
import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.inline.MaterialProcessEntity;
import com.osh.rvs.bean.inline.SoloProductionFeatureEntity;
import com.osh.rvs.bean.master.LineEntity;
import com.osh.rvs.bean.master.PcsFixOrderEntity;
import com.osh.rvs.bean.partial.MaterialPartialDetailEntity;
import com.osh.rvs.common.CopyByPoi;
import com.osh.rvs.common.FseBridgeUtil;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.PcsUtils;
import com.osh.rvs.common.RvsUtils;
import com.osh.rvs.common.ZipUtility;
import com.osh.rvs.form.data.MaterialForm;
import com.osh.rvs.form.data.MonthFilesDownloadForm;
import com.osh.rvs.form.master.LineForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.data.MaterialMapper;
import com.osh.rvs.mapper.data.PostMessageMapper;
import com.osh.rvs.mapper.inline.LeaderPcsInputMapper;
import com.osh.rvs.mapper.inline.MaterialCommentMapper;
import com.osh.rvs.mapper.inline.MaterialProcessMapper;
import com.osh.rvs.mapper.inline.ProductionFeatureMapper;
import com.osh.rvs.mapper.manage.PcsFixOrderMapper;
import com.osh.rvs.mapper.master.LineMapper;
import com.osh.rvs.mapper.master.ModelMapper;
import com.osh.rvs.mapper.partial.MaterialPartialMapper;
import com.osh.rvs.mapper.qf.AcceptanceMapper;
import com.osh.rvs.service.inline.PositionPanelService;
import com.osh.rvs.service.inline.SoloSnoutService;
import com.osh.rvs.service.partial.ComponentManageService;
import com.osh.rvs.service.partial.ComponentSettingService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.FileUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;
import framework.huiqing.common.util.message.ApplicationMessage;
import framework.huiqing.common.util.validator.Validators;

public class MaterialService {

	private static final Integer RESOLVED = 2;

	private static Logger logger = Logger.getLogger("ProcessCheckSheet");

	/**
	 * 检索工位当日成果
	 * @param form 提交表单
	 * @param conn 数据库连接
	 * @param errors 错误内容列表
	 * @return List<LineForm> 查询结果表单
	 */
	public List<LineForm> search(ActionForm form, SqlSession conn, List<MsgInfo> errors) {

		// 表单复制到数据对象
		LineEntity conditionBean = new LineEntity();
		BeanUtil.copyToBean(form, conditionBean, null);

		// 从数据库中查询记录
		LineMapper dao = conn.getMapper(LineMapper.class);
		List<LineEntity> lResultBean = dao.searchLine(conditionBean);

		// 建立页面返回表单
		List<LineForm> lResultForm = new ArrayList<LineForm>();

		// 数据对象复制到表单
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, LineForm.class);

		return lResultForm;
	}

	/**
	 * 维修对象条件查询
	 * @param form
	 * @param completed
	 * @param conn
	 * @param errors
	 * @return
	 */
	public List<MaterialForm> searchMaterial(ActionForm form, String completed, SqlSession conn, List<MsgInfo> errors) {
		MaterialEntity conditionBean = new MaterialEntity();
		BeanUtil.copyToBean(form, conditionBean, null);

		conditionBean.setFind_history(completed);

		List<MaterialEntity> lResultBean = new ArrayList<MaterialEntity>();
		MaterialMapper dao = conn.getMapper(MaterialMapper.class);
//		if (!"".equals(conditionBean.getScheduled_date_start()) || !"".equals(conditionBean.getScheduled_date_end())) {
//			List<String> materialIds = dao.searchMaterialIds(conditionBean);
//			List<String> processIds = dao.searchMaterialProcessIds(conditionBean);
//			materialIds.retainAll(processIds);
//			if (materialIds.size() != 0) {
//				lResultBean = dao.getMaterialDetail(materialIds);
//			}
//		} else {
			lResultBean = dao.searchMaterial(conditionBean);
//		}

		List<MaterialForm> lResultForm = new ArrayList<MaterialForm>();
		
		CopyOptions cos = new CopyOptions();
		cos.excludeEmptyString();
		cos.excludeNull();
		cos.dateConverter(DateUtil.ISO_DATE_PATTERN, "reception_time", "scheduled_date", "scheduled_date_end", "outline_time", "partial_order_date", "arrival_plan_date");
		cos.dateConverter(DateUtil.ISO_DATE_TIME_PATTERN, "agreed_date");
		BeanUtil.copyToFormList(lResultBean, lResultForm, cos, MaterialForm.class);
		
		return lResultForm;
	}

	/**
	 * 维修对象条件查询（已完成）
	 * @param form
	 * @param completed
	 * @param conn
	 * @param errors
	 * @return
	 */
	public List<MaterialForm> searchMaterialFiling(ActionForm form, String completed, SqlSession conn, List<MsgInfo> errors) {
		MaterialEntity conditionBean = new MaterialEntity();
		BeanUtil.copyToBean(form, conditionBean, null);

		conditionBean.setFind_history(completed);
		List<MaterialEntity> lResultBean = new ArrayList<MaterialEntity>();
		MaterialMapper dao = conn.getMapper(MaterialMapper.class);
//		if (!"".equals(conditionBean.getScheduled_date_start()) || !"".equals(conditionBean.getScheduled_date_end())) {
//			List<String> materialIds = dao.searchMaterialIds(conditionBean);
//			List<String> processIds = dao.searchMaterialProcessIds(conditionBean);
//			materialIds.retainAll(processIds);
//			if (materialIds.size() != 0) {
//				lResultBean = dao.getMaterialDetail(materialIds);
//			}
//		} else {
			lResultBean = dao.searchMaterialFiling(conditionBean);
//		}

		return checkFileExist(lResultBean);
	}

	private List<MaterialForm> checkFileExist(List<MaterialEntity> lResultBean) {
		List<MaterialForm> lResultForm = new ArrayList<MaterialForm>();
		
		for (MaterialEntity resultBean : lResultBean) {
			MaterialForm result = new MaterialForm();
			// 判断归档文件是否真的存在
			BeanUtil.copyToForm(resultBean, result, CopyOptions.COPYOPTIONS_NOEMPTY);
			String sorcNo = result.getSorc_no();
			String subPath = "";
			if (sorcNo== null || sorcNo.length() < 8) // If EndoEye
				subPath = "SAPD-" + sorcNo + "________";
			else if (sorcNo.length() == 8)
				subPath = "OMRN-" + sorcNo + "________";
			else 
				subPath = sorcNo;
			String sub8 = subPath.substring(0, 8);
			String folderPath = PathConsts.BASE_PATH + PathConsts.PCS + "\\" + sub8 + "\\" + sorcNo + ".zip";
			if (new File(folderPath).exists()) {
				result.setIsHistory("true");
			} else if (subPath.startsWith("SAPD-")) {
				subPath = "CELL-" + sorcNo + "________";
				sub8 = subPath.substring(0, 8);
				folderPath = PathConsts.BASE_PATH + PathConsts.PCS + "\\" + sub8 + "\\" + sorcNo + ".zip";
				if (new File(folderPath).exists()) {
					result.setIsHistory("true");
				}
			}
			lResultForm.add(result);
		}

		return lResultForm;
	}

	/**
	 * 读取维修对象详细信息
	 * @param conn
	 * @param id 维修对象ID
	 * @return
	 */
	public MaterialForm loadMaterialDetail(SqlSession conn, String id) {
		
		MaterialForm form = new MaterialForm();
		
		MaterialMapper dao = conn.getMapper(MaterialMapper.class);
		MaterialEntity entity = dao.loadMaterialDetail(id);
		if (entity != null) {
			CopyOptions cos = new CopyOptions();
			cos.excludeEmptyString();
			cos.excludeNull();
			cos.dateConverter(DateUtil.DATE_PATTERN, "reception_time");
			BeanUtil.copyToForm(entity, form, cos);
		}
		
		return form;
	}

	/**
	 * 读取维修对象详细信息/基本信息
	 * @param conn
	 * @param id 维修对象ID
	 * @return
	 */
	public MaterialForm loadSimpleMaterialDetail(SqlSession conn, String id) {
		MaterialForm form = new MaterialForm();
		
		MaterialEntity entity = loadSimpleMaterialDetailEntity(conn, id);
		if (entity != null) {
			CopyOptions cos = new CopyOptions();
			cos.excludeEmptyString();
			cos.excludeNull();
			cos.dateConverter(DateUtil.DATE_PATTERN, "reception_time");
			BeanUtil.copyToForm(entity, form, cos);
		}
		
		return form;
	}
	public MaterialEntity loadSimpleMaterialDetailEntity(SqlSession conn, String id) {
		MaterialMapper dao = conn.getMapper(MaterialMapper.class);
		MaterialEntity entity = dao.getMaterialNamedEntityByKey(id);
		return entity;
	}

	/**
	 * 读取维修对象详细信息
	 * @param conn
	 * @param id 维修对象ID
	 * @return
	 */
	public MaterialEntity loadMaterialDetailBean(SqlSession conn, String id) {
		
		MaterialMapper dao = conn.getMapper(MaterialMapper.class);
		MaterialEntity entity = dao.loadMaterialDetailAccpetance(id);
		
		return entity;
	}

	/**
	 * 读取维修对象详细信息
	 * @param conn
	 * @param ids 维修对象ID(复数)
	 * @return
	 */
	public List<MaterialEntity> loadMaterialDetailBeans(List<String> ids, SqlSession conn) {
		MaterialMapper dao = conn.getMapper(MaterialMapper.class);
		return dao.getMaterialDetailTicket(ids);
	}

	/**
	 * 更新维修对象信息
	 * @param form 维修对象信息
	 * @param conn
	 * @return
	 */
	public void update(ActionForm form, SqlSession conn) {
		MaterialEntity conditionBean = new MaterialEntity();
		BeanUtil.copyToBean(form, conditionBean, null);
		
		MaterialMapper dao = conn.getMapper(MaterialMapper.class);
		dao.updateMaterial(conditionBean);
		// FSE 数据同步
		try{
			FseBridgeUtil.toUpdateMaterial(conditionBean.getMaterial_id(), "plan");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 新建维修对象信息
	 * @param form 维修对象信息
	 * @param conn
	 */
	public void insert(ActionForm form, SqlSession conn) {
		MaterialEntity insertBean = new MaterialEntity();
		BeanUtil.copyToBean(form, insertBean, null);
		
		MaterialMapper dao = conn.getMapper(MaterialMapper.class);
		dao.insertMaterial(insertBean);
	}

	/**
	 * 检查修理单号是否重复
	 * @param form
	 * @param conn
	 * @return
	 */
	public String checkSorcNo(ActionForm form, SqlSession conn) {
		MaterialEntity insertBean = new MaterialEntity();
		BeanUtil.copyToBean(form, insertBean, CopyOptions.COPYOPTIONS_NOEMPTY);
		if (insertBean.getSorc_no() != null) {
			AcceptanceMapper dao = conn.getMapper(AcceptanceMapper.class);
			String id = dao.checkSorcNo(insertBean);
			
			return id;
		}
		return null;
	}

	/**
	 * 检查ESAS No.是否重复
	 * @param form
	 * @param conn
	 * @return
	 */
	public String checkEsasNo(ActionForm form, SqlSession conn) {
		MaterialEntity insertBean = new MaterialEntity();
		BeanUtil.copyToBean(form, insertBean, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		AcceptanceMapper dao = conn.getMapper(AcceptanceMapper.class);
		String id = dao.checkEsasNo(insertBean);
		
		return id;
	}

	/**
	 * 检查机身号是否重复
	 * @param form
	 * @param conn
	 * @return
	 */
	public String checkModelSerialNo(ActionForm form, SqlSession conn) {
		MaterialEntity insertBean = new MaterialEntity();
		BeanUtil.copyToBean(form, insertBean, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		AcceptanceMapper dao = conn.getMapper(AcceptanceMapper.class);
		String id = dao.checkModelSerialNo(insertBean);
		
		return id;
	}

	/**
	 * 检查维修对象编号重复
	 * @param id
	 * @param form
	 * @param conn
	 * @param errors
	 */
	public void checkRepeatNo(String id, ActionForm form, SqlSessionManager conn, List<MsgInfo> errors) {
		MaterialForm mForm = (MaterialForm)form;

		if ("".equals(id) || id == null) { //新增时判断已存在ID
			String existId1 = null;
			String existId2 = null;
			String existId3 = null;
			if (!isEmpty(mForm.getSorc_no())) {
				existId1 = checkSorcNo(form, conn);
			}
			if (!isEmpty(mForm.getEsas_no())) {
				existId1 = checkEsasNo(form, conn);
			}
			existId2 = checkEsasNo(form, conn);
			existId3 = checkModelSerialNo(form, conn);
			
			if (existId1 != null) {
				MsgInfo info = new MsgInfo();
				info.setErrcode("dbaccess.columnNotUnique");
				info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.columnNotUnique", "修理单号", mForm.getSorc_no(), "维修对象"));
				errors.add(info);
			} else if (existId2 != null) {
				MsgInfo info = new MsgInfo();
				info.setErrcode("dbaccess.columnNotUnique");
				info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.columnNotUnique", "ESAS No.", mForm.getEsas_no(), "维修对象"));
				errors.add(info);
			}  else if (existId3 != null) {
				MsgInfo info = new MsgInfo();
				info.setErrcode("dbaccess.columnNotUnique");
				info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.columnNotUnique", "型号 + 机身号", mForm.getModel_id() +","+ ((MaterialForm)form).getSerial_no(), "维修对象"));
				errors.add(info);
			}
		} else { //更新时判断ID不相等
			String existId1 = checkSorcNo(form, conn);
			// String existId2 = checkEsasNo(form, conn);

			if (existId1 != null && !id.equals(existId1)) {
				MsgInfo info = new MsgInfo();
				info.setErrcode("dbaccess.columnNotUnique");
				info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.columnNotUnique", "修理单号", mForm.getSorc_no(), "维修对象"));
				errors.add(info);
//			} else if (existId2 != null && !id.equals(existId2)) {
//				MsgInfo info = new MsgInfo();
//				info.setErrcode("dbaccess.columnNotUnique");
//				info.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.columnNotUnique", "ESAS No.", mForm.getEsas_no(), "维修对象"));
//				errors.add(info);
			}
		}
	}

	/***
	 * 取得提交维修对象ID(复数)
	 * @param parameterMap
	 * @return
	 */
	public List<String> getIds(Map<String, String[]> parameterMap) {
		List<String> rets = new ArrayList<String>();
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");

		// 整理提交数据
		for (String parameterKey : parameterMap.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("materials".equals(entity)) {
					String column = m.group(2);

					String[] value = parameterMap.get(parameterKey);

					// TODO 全
					if ("material_id".equals(column)) {
						rets.add(value[0]);
					}
				}
			}
		}
		return rets;
	}

	/**
	 * 取得维修对象工程检查票
	 * @param listResponse 相应信息
	 * @param mform 维修对象信息
	 * @param material_id 维修对象ID
	 * @param sline_id 查看工程
	 * @param isLeader 线长身份
	 * @param conn
	 */
	public void getPcses(Map<String, Object> listResponse, MaterialForm mform, String material_id, String sline_id, boolean isLeader, 
			String getHistory, SqlSession conn) {

		List<Map<String, String>> pcses = new ArrayList<Map<String, String>>();

		int ext = 0;

		boolean enterCom = false;
		String[] showLines = {};

		if (mform.getLevel() != null && mform.getLevel().startsWith("5")) {
			showLines = new String[1];
			showLines[0] = "检查卡";
		} else {
			if (!isEmpty(mform.getOutline_time())) {
				showLines = new String[5];
				showLines[0] = "报价";
				showLines[1] = "分解工程";
				showLines[2] = "NS 工程";
				showLines[3] = "总组工程";
				showLines[4] = "最终检验";
				ext = 3;
				if ("00000000011".equals(sline_id)) {
					ext = 0;
				} else if ("00000000012".equals(sline_id)) {
					ext = 1;
				} else if ("00000000013".equals(sline_id)) {
					ext = 2;
				} else if ("00000000014".equals(sline_id)) {
					ext = 3;
					enterCom =true;
				} else if ("00000000015".equals(sline_id)) {
					ext = 4;
					enterCom =true;
				}
			} else if ("00000000011".equals(sline_id)) {
				showLines = new String[1];
				showLines[0] = "报价";
			} else if ("00000000012".equals(sline_id)) {
				if (getHistory != null || MaterialTagService.getAnmlMaterials(conn).contains(material_id)) {
					if ("1".equals(mform.getLevel())) {
						showLines = new String[2];
						showLines[0] = "总组工程";
						showLines[1] = "分解工程";
					} else {
						showLines = new String[3];
						showLines[0] = "总组工程";
						showLines[1] = "分解工程";
						showLines[2] = "NS 工程";
					}
				} else {
					if ("1".equals(mform.getLevel())) {
						showLines = new String[2];
						showLines[0] = "分解工程";
						showLines[1] = "总组工程";
					} else {
						showLines = new String[3];
						showLines[0] = "分解工程";
						showLines[1] = "NS 工程";
						showLines[2] = "总组工程";
					}
				}
			} else if ("00000000013".equals(sline_id)) {
				showLines = new String[1];
				showLines[0] = "NS 工程";
			} else if ("00000000014".equals(sline_id)) {
				showLines = new String[4];
				showLines[0] = "总组工程";
				showLines[1] = "报价";
				showLines[2] = "分解工程";
				showLines[3] = "NS 工程";
				enterCom =true;
			} else if ("00000000015".equals(sline_id)) {
				showLines = new String[5];
				showLines[0] = "最终检验";
				showLines[1] = "报价";
				showLines[2] = "分解工程";
				showLines[3] = "NS 工程";
				showLines[4] = "总组工程";
				enterCom =true;
			}
		}

		boolean isAnmlExp = MaterialTagService.getAnmlMaterials(conn).contains(material_id);
		if (!isAnmlExp && getHistory != null) {
			if ("forAnml".equals(getHistory)) {
				isAnmlExp = true;
			} else {
				MaterialTagService mtService = new MaterialTagService();
				if (mtService.checkTagByMaterialId(material_id, 1, conn).size() > 0) {
					isAnmlExp = true;
				}
			}
		}

		for (int i=0 ; i < showLines.length ; i++) {
			String showLine = showLines[i]; 
			logger.info(showLine);
			Map<String, String> fileTempl = PcsUtils.getXmlContents(showLine, mform.getModel_name(), 
					null, getHistory != null, mform.getMaterial_id(), RvsUtils.isLightFix(mform.getLevel()),   
					conn);

			if ("NS 工程".equals(showLine)) {
				SoloSnoutService ssService = new SoloSnoutService();
				Map<String, String> recoverFileHtml = ssService.getRecoverFileHtml(fileTempl, material_id, mform, conn);
				if (recoverFileHtml != null) {
					pcses.add(recoverFileHtml);
				}

				filterSolo(fileTempl, material_id, mform.getLevel(), conn);
			}

			if (!fileTempl.isEmpty()) {
				Map<String, String> fileHtml = PcsUtils.toHtml(fileTempl, material_id, mform.getSorc_no(),
						mform.getModel_name(), mform.getSerial_no(), mform.getLevel(), "xyz", (i == ext && isLeader ? sline_id : null), 
						isAnmlExp, conn);
				fileHtml = RvsUtils.reverseLinkedMap(fileHtml);
				pcses.add(fileHtml);
			}
		}

		// 采用NS 组件，得到工程检查票
		if (enterCom && "1".equals(mform.getLevel())) {
			// 判断是否NS组件组装对象
			Set<String> nsCompModels = ComponentSettingService.getNsCompModels(conn);
			boolean isNsCompModel = nsCompModels.contains(mform.getModel_id());

			if (isNsCompModel) {
				ComponentManageService cmService = new ComponentManageService();
				String serialNos = cmService.getSerialNosForTargetMaterial(material_id, conn);
				if (!isEmpty(serialNos)) {
					// 有分配

					// 判断组件签收
					MaterialPartialMapper mpMapper = conn.getMapper(MaterialPartialMapper.class);
					List<MaterialPartialDetailEntity> mpdEntities = mpMapper.getMpdForComponent(material_id);

					String[] serialNoArray = serialNos.split(",");
					for (int i = 0;i < serialNoArray.length; i++) {
						if (i >= mpdEntities.size()) { // 没有签收组件信息
							break;
						}
						if (mpdEntities.get(i).getRecent_receive_time() == null) { // 没有签收操作
							break;
						}

						Map<String, Object> partResponse = new HashMap<String, Object>();
						SoloProductionFeatureEntity spf = new SoloProductionFeatureEntity();
						spf.setModel_name(mform.getModel_name());
						spf.setSerial_no(serialNoArray[i]);
						spf.setProcess_code("xyz");
						PositionPanelService.getSoloPcses(partResponse, spf, sline_id, conn);

						List<Map<String, String>> partPcses = (List<Map<String, String>>) partResponse.get("pcses");
						if (partPcses.size() > 0) {
							Map<String, String> nsLine = null;

							for (int isl = 0 ; isl < pcses.size(); isl++) {
								Map<String, String> pcsLine = pcses.get(isl);
								boolean hit = false;
								for (String key : pcsLine.keySet()) {
									if (key.startsWith("NS 工程")) {
										hit = true;
										break;
									}
								}
								if (hit) {
									nsLine = pcsLine;
									break;
								}
							}

							if (nsLine == null) {
								for (int isl = 0 ; isl < showLines.length; isl++) {
									if ("NS 工程".equals(showLines[isl])) {
										nsLine = new HashMap<String, String>();
										if (isl == 0) {
											pcses.add(isl, nsLine);
										} else {
											pcses.add(isl - 1, nsLine);
										}
										break;
									}
								}
							}
							if (nsLine != null) {
								for (String pcsName : partPcses.get(0).keySet()) {
									if (i == 0) {
										nsLine.put(pcsName, partPcses.get(0).get(pcsName));
									} else {
										nsLine.put(pcsName + "(" + i + ")", partPcses.get(0).get(pcsName));
									}
								}
							}
						}
					}
				}
			}
		}

		// 取得选择修理检查票
		OptionalFixService ofService = new OptionalFixService();
		List<String> selectedItems = ofService.getMaterialOptionalFixItems(material_id, conn);
		if (selectedItems != null) {
			Map<String, String> optionalPcs = ofService.getOptionalFixPcses(selectedItems, mform, getHistory, conn);
			if (optionalPcs != null) {
				pcses.add(optionalPcs);
			}
		}

		listResponse.put("pcses", pcses);
		
	}

	/**
	 * 取得维修对象工程检查票
	 * @param listResponse 相应信息
	 * @param mform 维修对象信息
	 * @param material_id 维修对象ID
	 * @param sline_id 查看工程
	 * @param isLeader 线长身份
	 * @param conn
	 */
	public void getPcsesBlank(Map<String, Object> callbackResponse, String modelName, SqlSession conn) {

		List<Map<String, String>> pcses = new ArrayList<Map<String, String>>();

		String[] showLines =  new String[6];
		showLines[0] = "检查卡";
		showLines[1] = "最终检验";
		showLines[2] = "报价";
		showLines[3] = "分解工程";
		showLines[4] = "NS 工程";
		showLines[5] = "总组工程";

		for (int i=0 ; i < showLines.length ; i++) {
			String showLine = showLines[i]; 
			logger.info(showLine);
			Map<String, String> fileTempl = PcsUtils.getXmlContents(showLine, modelName, null, conn);

			Map<String, String> fileHtml = PcsUtils.toHtmlBlank(fileTempl, modelName);
			fileHtml = RvsUtils.reverseLinkedMap(fileHtml);
			pcses.add(fileHtml);
		}

		callbackResponse.put("pcses", pcses);
		
	}

	public void filterSolo(Map<String, String> fileTempl, String material_id, String level, SqlSession conn) {

		if (fileTempl == null) return;

		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);

		boolean isAnmlWithNs = MaterialTagService.getAnmlMaterials(conn).contains(material_id) && !"1".equals(level);

		if (!dao.checkLineDid(material_id, "00000000013")
				&& !isAnmlWithNs) {
			fileTempl.clear();
			return;
		}

		List<String> snouts = new ArrayList<String>(); 
		List<String> snoutHeads = new ArrayList<String>();
		List<String> ccds = new ArrayList<String>(); 
		List<String> lgs = new ArrayList<String>(); 
		List<String> ccdls = new ArrayList<String>(); 
		List<String> nscomps = new ArrayList<String>(); 

		for (String key : fileTempl.keySet()) {
			if (key.contains("先端预制") || key.contains("D／E组装")) {
				snouts.add(key);
			}
			else if (key.contains("回收")) {
				snoutHeads.add(key);
			}
			else if (key.contains("CCD盖玻璃")) {
				ccds.add(key);
			}
			else if (key.contains("LG")) {
				lgs.add(key);
			}
			else if (key.contains("CCD线")) { // TODO
				ccdls.add(key);
			}
			else if (key.contains("NS组件组装")) {
				nscomps.add(key);
			}
		}

		// 如果有先端预制工程检查票
		if (snouts.size() > 0) {
			// 检查是否做过301工位
			if (!dao.checkPositionDid(material_id, "00000000024", null, null)) {
				for (String snout : snouts) {
					fileTempl.remove(snout);
				}
			}
		}

		// 本身C本体回收的不显示
		if (snoutHeads.size() > 0) {
			for (String snout : snoutHeads) {
				fileTempl.remove(snout);
			}
		}

		// 如果有CCD盖玻璃工程检查票
		if (ccds.size() > 0) {
			// 检查是否做过302工位
			if (!dao.checkPositionDid(material_id, "00000000025", null, null)) {
				for (String ccd : ccds) {
					fileTempl.remove(ccd);
				}
			}
		}

		// 如果有LG玻璃工程检查票
		if (lgs.size() > 0) {
			// 检查是否做过303工位
			if (!dao.checkPositionDid(material_id, "00000000060", null, null)) {
				for (String lg : lgs) {
					fileTempl.remove(lg);
				}
			}
		}

		// 如果有CCD线工程检查票
		if (ccdls.size() > 0) {
			// 检查是否做过304工位
			if (!dao.checkPositionDid(material_id, "00000000066", null, null)) {
				for (String ccdl : ccdls) {
					fileTempl.remove(ccdl);
				}
			}
		}

		// 如果有NS组件组装工程检查票
		if (nscomps.size() > 0) {
			
			// 检查是否使用过组件
			ComponentManageService cmService = new ComponentManageService();
			String serialNos = cmService.getSerialNosForTargetMaterial(material_id, conn);
			if (isEmpty(serialNos)) {
				for (String nscomp : nscomps) {
					fileTempl.remove(nscomp);
				}
			}
		}

	}

	public void saveLeaderInput(HttpServletRequest req, LoginData user, SqlSessionManager conn) throws Exception {
		saveLeaderInput(req, req.getParameter("material_id"), user, conn);
	}

	public void saveLeaderInput(HttpServletRequest req, String material_id, LoginData user, SqlSessionManager conn) throws Exception {
		String line_id = req.getParameter("line_id");
		if (line_id == null) {
			line_id = user.getLine_id();
		}
		saveLeaderInput(req.getParameter("pcs_inputs"), req.getParameter("pcs_comments"), material_id, user.getOperator_id(), line_id, conn);
	}
	public void saveLeaderInput(String pcs_inputs, String pcs_comments, String material_id, String operator_id, String line_id, SqlSessionManager conn) throws Exception {
		// 取得最新rework在line_id
		ProductionFeatureMapper pfMapper = conn.getMapper(ProductionFeatureMapper.class);
		int lineMaxRework = pfMapper.getReworkCountWithLine(material_id, line_id);

		// 保存到线长工检票记录
		LeaderPcsInputMapper dao = conn.getMapper(LeaderPcsInputMapper.class);

		ProductionFeatureEntity pfBean = new ProductionFeatureEntity();
		pfBean.setMaterial_id(material_id);
		pfBean.setPcs_inputs(pcs_inputs);
		pfBean.setPcs_comments(pcs_comments);
		pfBean.setOperator_id(operator_id);
		pfBean.setLine_id(line_id);
		pfBean.setRework(lineMaxRework);

		dao.insert(pfBean);
	}

	public void saveManagerInput(HttpServletRequest req, LoginData user, SqlSessionManager conn) throws Exception {
		String line_id = req.getParameter("line_id");
		if (line_id == null || line_id.equals("")) {
			line_id = "00000000014";
		}
		// 取得最新rework在line_id
		ProductionFeatureMapper pfMapper = conn.getMapper(ProductionFeatureMapper.class);
		int lineMaxRework = pfMapper.getReworkCountWithLine(req.getParameter("material_id"), line_id);

		// 保存到线长工检票记录
		LeaderPcsInputMapper dao = conn.getMapper(LeaderPcsInputMapper.class);

		ProductionFeatureEntity pfBean = new ProductionFeatureEntity();
		pfBean.setMaterial_id(req.getParameter("material_id"));
		pfBean.setPcs_inputs("{}");
		pfBean.setPcs_comments(req.getParameter("pcs_comments"));
		pfBean.setOperator_id(user.getOperator_id());
		pfBean.setLine_id(line_id);
		pfBean.setRework(lineMaxRework);

		dao.insert(pfBean);
	}

	public List<Map<String, String>> getLastPositionAndStatus(String material_id, SqlSession conn) {
		// 取得
		ProductionFeatureMapper dao = conn.getMapper(ProductionFeatureMapper.class);
		List<Map<String, String>> ret = dao.getLastPositionAndStatus(material_id);
		return ret;
	}


	public List<MsgInfo> checkQuotation(MaterialForm materialForm, SqlSessionManager conn) {
		Validators v = BeanUtil.createBeanValidators(materialForm, BeanUtil.CHECK_TYPE_ALL);
		v.delete("model_id");
		v.delete("serial_no");

		if (isEmpty(materialForm.getAgreed_date())) {
			v.delete("level");
		} else {
			v.add("ocm_rank", v.required("OCM 修理等级"));
		}


		v.add("sorc_no", v.required("修理单号"));
		// v.add("esas_no", v.required("ESAS NO."));
		// v3 Add Start
		v.add("ocm_deliver_date", v.required("OCM 配送日"));
		v.add("customer_name", v.required("顾客名"));

		// v3 Add End
//		if (CommonStringUtil.isEmpty(materialForm.getWip_location())) {
//			v.add("agreed_date", v.required("同意日"));
//		}
		if (materialForm.getDirect_flg() != null && "1".equals(materialForm.getDirect_flg())) {
			v.add("bound_out_ocm", v.required("直送区域"));
		}
		v.delete("scheduled_expedited");
		return v.validate();
	}

	public void createReport(String fileFullPath, List<MaterialForm> lResultForm, String searchAddition, 
			int fromSupport, boolean additionOptional, SqlSession conn) {
		FileUtils.copyFile(PathConsts.BASE_PATH + PathConsts.REPORT_TEMPLATE + "\\维修对象一览报表模板.xls", fileFullPath);
		
		POIFSFileSystem fs;
		HSSFWorkbook book = null;

		try {
			fs = new POIFSFileSystem(new FileInputStream(fileFullPath));
			book = new HSSFWorkbook(fs);

			Map<String, String> ocms = new HashMap<String, String>();
			Map<String, String> levels = new HashMap<String, String>();
			// 一览列表生成
			HSSFSheet listSheet = book.getSheetAt(0);
			HSSFRow row = null;

			HSSFFont fontYH = book.createFont();
			fontYH.setFontName("Microsoft YaHei");
			fontYH.setFontHeightInPoints((short) 10);

			HSSFCellStyle defaultCell = book.createCellStyle();
			defaultCell.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
			defaultCell.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
			defaultCell.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
			defaultCell.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
            HSSFDataFormat thdf= book.createDataFormat();
            defaultCell.setDataFormat(thdf.getFormat("@"));
			defaultCell.setFont(fontYH);

			HSSFCellStyle grayCell = book.createCellStyle(); // 暗色
			grayCell.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
			grayCell.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
			grayCell.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
			grayCell.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
			grayCell.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
			grayCell.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			grayCell.setFont(fontYH);

			HSSFCellStyle highlightCell = book.createCellStyle(); // 亮色
			highlightCell.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
			highlightCell.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
			highlightCell.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
			highlightCell.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
			highlightCell.setFillForegroundColor(HSSFColor.AQUA.index);
			highlightCell.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			highlightCell.setFont(fontYH);

			HSSFCellStyle dateCell = book.createCellStyle(); // 居中
			dateCell.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
			dateCell.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
			dateCell.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
			dateCell.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
			dateCell.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			dateCell.setDataFormat(book.createDataFormat().getFormat("yy-mm-dd")); 
			dateCell.setFont(fontYH);

			HSSFCellStyle fullTimeCell = book.createCellStyle(); // 居中
			fullTimeCell.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
			fullTimeCell.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
			fullTimeCell.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
			fullTimeCell.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
			fullTimeCell.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			fullTimeCell.setDataFormat(book.createDataFormat().getFormat("yyyy-MM-dd h:mm")); // HSSFDataFormat.getBuiltinFormat("MM/dd h:mm")
			fullTimeCell.setFillForegroundColor((short) 1);
			fullTimeCell.setFont(fontYH);

			HSSFCellStyle timeCell = book.createCellStyle(); // 居中
			timeCell.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
			timeCell.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
			timeCell.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
			timeCell.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
			timeCell.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			timeCell.setDataFormat(book.createDataFormat().getFormat("MM/dd h:mm")); // HSSFDataFormat.getBuiltinFormat("MM/dd h:mm")
			timeCell.setFillForegroundColor((short) 1);
			timeCell.setFont(fontYH);

			HSSFCellStyle centerCell = book.createCellStyle(); // 居中
			centerCell.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
			centerCell.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
			centerCell.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
			centerCell.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
			centerCell.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			centerCell.setFont(fontYH);

			int intSearchAddition = 0;
			if (searchAddition != null) {
				intSearchAddition = Integer.parseInt(searchAddition);
			}
			boolean showProcess = (intSearchAddition & 1) == 1;
			boolean showQuote = (intSearchAddition & 2) == 2;
			boolean showPartorder = (intSearchAddition & 4) == 4;
			boolean showOutshore = (intSearchAddition & 8) == 8;
			boolean showLogitics = (intSearchAddition & 16) == 16;
			HSSFRow rowHeader = listSheet.getRow(0);
			int colCur = -1;

			listSheet.setDefaultColumnStyle(++colCur, highlightCell);
			listSheet.setDefaultColumnStyle(++colCur, defaultCell);
			listSheet.setDefaultColumnStyle(++colCur, defaultCell);
			listSheet.setDefaultColumnStyle(++colCur, defaultCell);
			listSheet.setDefaultColumnStyle(++colCur, defaultCell);
			listSheet.setDefaultColumnStyle(++colCur, defaultCell);
			listSheet.setDefaultColumnStyle(++colCur, defaultCell);

			// 维修课室 ～ NS当前位置
			if (showProcess) {
				listSheet.setDefaultColumnStyle(++colCur, centerCell);
				listSheet.setDefaultColumnStyle(++colCur, centerCell);
				listSheet.setDefaultColumnStyle(++colCur, centerCell);
			} else {
				removeHeaderColumn(rowHeader, colCur + 1);
				removeHeaderColumn(rowHeader, colCur + 1);
				removeHeaderColumn(rowHeader, colCur + 1);
			}

			// 物流到货
			if (showLogitics) {
				listSheet.setDefaultColumnStyle(++colCur, fullTimeCell);
				listSheet.setColumnWidth(colCur, 4160);
			} else {
				removeHeaderColumn(rowHeader, colCur + 1);
			}

			listSheet.setDefaultColumnStyle(++colCur, dateCell);

			// 消毒灭菌完成	报价日期
			if (showQuote) {
				listSheet.setDefaultColumnStyle(++colCur, dateCell);
				listSheet.setDefaultColumnStyle(++colCur, dateCell);
			} else {
				removeHeaderColumn(rowHeader, colCur + 1);
				removeHeaderColumn(rowHeader, colCur + 1);
			}

			listSheet.setDefaultColumnStyle(++colCur, dateCell);
			listSheet.setColumnWidth(colCur, 4160);
			listSheet.setDefaultColumnStyle(++colCur, dateCell);

			MaterialProcessMapper mapper = null;
			// 投线时间 ～ NS 完成
			if (fromSupport == 1) {
				mapper = conn.getMapper(MaterialProcessMapper.class);

				listSheet.setDefaultColumnStyle(++colCur, dateCell);
				listSheet.setDefaultColumnStyle(++colCur, dateCell);
				listSheet.setDefaultColumnStyle(++colCur, dateCell);
			} else if (fromSupport == 2) {
				listSheet.setDefaultColumnStyle(++colCur, dateCell);
				removeHeaderColumn(rowHeader, colCur + 1);
				removeHeaderColumn(rowHeader, colCur + 1);
			} else {
				removeHeaderColumn(rowHeader, colCur + 1);
				removeHeaderColumn(rowHeader, colCur + 1);
				removeHeaderColumn(rowHeader, colCur + 1);
			}

			listSheet.setDefaultColumnStyle(++colCur, dateCell);
			listSheet.setDefaultColumnStyle(++colCur, dateCell);

			// 品保通过 出货时间
			if (showOutshore) {
				listSheet.setDefaultColumnStyle(++colCur, dateCell);
				listSheet.setDefaultColumnStyle(++colCur, timeCell);
				listSheet.setColumnWidth(colCur, 3290);
			} else {
				removeHeaderColumn(rowHeader, colCur + 1);
				removeHeaderColumn(rowHeader, colCur + 1);
			}

			// 物流出货
			if (showLogitics) {
				listSheet.setDefaultColumnStyle(++colCur, fullTimeCell);
				listSheet.setColumnWidth(colCur, 4160);
			} else {
				removeHeaderColumn(rowHeader, colCur + 1);
			}

			// 零件订购日	入库预定日
			if (showPartorder) {
				listSheet.setDefaultColumnStyle(++colCur, dateCell);
				listSheet.setDefaultColumnStyle(++colCur, dateCell);
			} else {
				removeHeaderColumn(rowHeader, colCur + 1);
				removeHeaderColumn(rowHeader, colCur + 1);
			}

			listSheet.setDefaultColumnStyle(++colCur, defaultCell);

			// 备注-选择修理
			OptionalFixService ofService = null;
			if (additionOptional) {
				listSheet.setDefaultColumnStyle(++colCur, defaultCell);
				listSheet.setColumnWidth(colCur, 6000);
				row = listSheet.getRow(0);
				HSSFCell cell = row.createCell(colCur);
				cell.setCellValue("选择修理项");
				ofService = new OptionalFixService();
			}

			for (int i=0; i < lResultForm.size(); i++) {
				colCur = -1;
				MaterialForm resultForm = lResultForm.get(i);
				row = listSheet.createRow(i+1);
				// int excelRowNum = row.getRowNum();
				HSSFCell cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(i+1);

				cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
				if (!"0".equals(resultForm.getBreak_back_flg())) {
					cell.setCellStyle(grayCell);
				}
				cell.setCellValue(resultForm.getSorc_no());

				cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(resultForm.getCategory_name());

				cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(resultForm.getModel_name());

				cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
				String sLevel = resultForm.getLevel();
				if (!levels.containsKey(sLevel)) {
					levels.put(sLevel, CodeListUtils.getValue("material_level", sLevel, " - "));
				}
				sLevel = levels.get(sLevel);
				cell.setCellValue(sLevel);

				cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(resultForm.getSerial_no());

				cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
				String sOcm = resultForm.getOcm();
				if (!ocms.containsKey(sOcm)) {
					ocms.put(sOcm, CodeListUtils.getValue("material_ocm", sOcm, " - "));
				}
				sOcm = ocms.get(sOcm);
				cell.setCellValue(sOcm);

				if (showProcess) {
					cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue(resultForm.getSection_name());

					cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue(resultForm.getProcessing_position());

					cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue(resultForm.getProcessing_position2());
				}

				// 物流到货
				if (showLogitics) {
					cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
					String sSorcReception = resultForm.getSorc_reception();
					if (isEmpty(sSorcReception)) {
						cell.setCellValue("-");
					} else if (sSorcReception.endsWith("00:00:00")) {
						cell.setCellStyle(dateCell);
						cell.setCellValue(sSorcReception.substring(0, 10));
					} else {
						cell.setCellValue(sSorcReception.substring(0, 10) + " " + sSorcReception.substring(11, 16));
					}
				}

				cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
				String sReceptionTime = resultForm.getReception_time();
				if (sReceptionTime != null && sReceptionTime.length() > 10)
					sReceptionTime = sReceptionTime.substring(0, 10);
				cell.setCellValue(sReceptionTime);

				if (showQuote) {
					cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue(resultForm.getQuotation_date_start());

					cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue(resultForm.getQuotation_time());
				}

				cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
				String sAgreedDate = resultForm.getAgreed_date();
				if (isEmpty(sAgreedDate)) {
					cell.setCellValue("-");
				} else if (sAgreedDate.endsWith("00:00:00")) {
					cell.setCellValue(sAgreedDate.substring(0, 10));
				} else {
					cell.setCellStyle(fullTimeCell);
					cell.setCellValue(sAgreedDate.substring(0, 10) + " " + sAgreedDate.substring(11, 16));
				}

				cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(resultForm.getScheduled_date());

				if (fromSupport > 0) {
					cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
					if (resultForm.getInline_time() != null) {
						cell.setCellValue(resultForm.getInline_time().replace('/', '-'));
					} else {
						cell.setCellValue("-");
					}

					if (fromSupport == 1) {
						cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
						MaterialProcessEntity mpEntity = mapper.loadMaterialProcessOfLine(resultForm.getMaterial_id(), "00000000012");
						if (mpEntity == null || mpEntity.getFinish_date() == null) {
							cell.setCellValue("-");
						} else {
							cell.setCellValue(DateUtil.toString(mpEntity.getFinish_date(), DateUtil.ISO_DATE_PATTERN));
						}

						cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
						mpEntity = mapper.loadMaterialProcessOfLine(resultForm.getMaterial_id(), "00000000013");
						if (mpEntity == null || mpEntity.getFinish_date() == null) {
							cell.setCellValue("-");
						} else {
							cell.setCellValue(DateUtil.toString(mpEntity.getFinish_date(), DateUtil.ISO_DATE_PATTERN));
						}
					}
				}

				cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
				if("9999-12-31".equals(resultForm.getScheduled_date_end())){
					cell.setCellValue("另行通知");
				}else{
					cell.setCellValue(resultForm.getScheduled_date_end());
				}
				
				cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
				if (resultForm.getFinish_time_end() != null) {
					cell.setCellValue(resultForm.getFinish_time_end().replace('/', '-'));
				} else {
					cell.setCellValue("-");
				}

				if (showOutshore) {
					cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue(resultForm.getOutline_time());

					cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
					if (!isEmpty(resultForm.getFinish_time())) {
						String outshoreTime = resultForm.getFinish_time();
						if (outshoreTime.length() > 16) {
							outshoreTime = outshoreTime.substring(5, 16);
							outshoreTime = outshoreTime.replace('/', '-');
						}
						cell.setCellValue(outshoreTime);
					} else {
						cell.setCellValue("");
					}
				}

				// 物流出货
				if (showLogitics) {
					cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
					String sSorcShipment = resultForm.getSorc_shipment();
					if (isEmpty(sSorcShipment)) {
						cell.setCellValue("-");
					} else 	if (sSorcShipment.endsWith("00:00:00")) {
						cell.setCellStyle(dateCell);
						cell.setCellValue(sSorcShipment.substring(0, 10));
					} else {
						cell.setCellValue(sSorcShipment.substring(0, 10) + " " + sSorcShipment.substring(11, 16));
					}
				}

				if (showPartorder) {
					cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue(resultForm.getPartial_order_date());

					cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
					if("9999-12-31".equals(resultForm.getArrival_plan_date())){
						cell.setCellValue("未定");
					}else{
						cell.setCellValue(resultForm.getArrival_plan_date());
					}
				}

				cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(resultForm.getIs_late());

				if (additionOptional) {
					cell = row.createCell(++colCur, HSSFCell.CELL_TYPE_STRING);
					List<String> optional_fix_items =ofService.getMaterialOptionalFixItems(resultForm.getMaterial_id(), conn);

					if (optional_fix_items != null && optional_fix_items.size() > 0)
						cell.setCellValue(CommonStringUtil.joinBy("；", optional_fix_items.toArray()));
				}
			}
			// 保存文件
			FileOutputStream fileOut = new FileOutputStream(fileFullPath);
			book.write(fileOut);
			fileOut.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void removeHeaderColumn(HSSFRow rowHeader, int colCur) {
		HSSFCell shiftCell = rowHeader.getCell(colCur);
		if (shiftCell != null) {
			rowHeader.removeCell(shiftCell);
		}
		shiftCell = rowHeader.getCell(++colCur);
		do {
			if (shiftCell == null) break;
			HSSFCell distCell = rowHeader.createCell(colCur - 1);
			CopyByPoi.copyCell(shiftCell, distCell, true);
			shiftCell = rowHeader.getCell(++colCur);
		} while(shiftCell != null);

		rowHeader.removeCell(rowHeader.getCell(colCur - 1));
	}

	public void getPcses4Fix(Map<String, Object> listResponse, MaterialForm mform, String material_id, SqlSession conn) {
		List<Map<String, String>> pcses = new ArrayList<Map<String, String>>();

		String[] showLines = {};

		if (mform.getLevel() != null && mform.getLevel().startsWith("5")) {
			showLines = new String[1];
			showLines[0] = "检查卡";
		} else {
			showLines = new String[5];
			showLines[0] = "最终检验";
			showLines[1] = "报价";
			showLines[2] = "分解工程";
			showLines[3] = "NS 工程";
			showLines[4] = "总组工程";
		}

		for (int i=0 ; i < showLines.length ; i++) {
			String showLine = showLines[i]; 
			logger.info(showLine);
			Map<String, String> fileTempl = PcsUtils.getXmlContents(showLine, mform.getModel_name(), null, conn);

			if ("NS 工程".equals(showLine)) {
				// 检查是否做过D/E组装
				SoloSnoutService ssService = new SoloSnoutService();
				Map<String, String> recoverFileHtml = ssService.getRecoverFileHtml(fileTempl, material_id, mform, conn);
				if (recoverFileHtml != null) {
					pcses.add(recoverFileHtml);
				}
				filterSolo(fileTempl, material_id, mform.getLevel(), conn);
			}

			Map<String, String> fileHtml = PcsUtils.toHtml4Fix(fileTempl, material_id, mform.getSorc_no(),
					mform.getModel_name(), mform.getSerial_no(), conn);
			fileHtml = RvsUtils.reverseLinkedMap(fileHtml);
			pcses.add(fileHtml);
		}

		listResponse.put("pcses", pcses);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void fixInput(HttpServletRequest req, LoginData user, SqlSessionManager conn) throws Exception {

		String material_id = req.getParameter("material_id");

		Map<String, String[]> parameterMap = req.getParameterMap();

		List<HashMap> fixBeans = new AutofillArrayList<HashMap>(HashMap.class);
		List<HashMap> fixCommentsBeans = new AutofillArrayList<HashMap>(HashMap.class);
		Pattern p = Pattern.compile("([\\w\\_]+)\\[(\\d+)\\]\\[(\\w+)\\]");

		// 整理提交数据
		for (String parameterKey : parameterMap.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("pcs_values".equals(entity)) {
					String column = m.group(3);
					int icounts = Integer.parseInt(m.group(2));
					String[] value = parameterMap.get(parameterKey);

					// TODO 全
					if ("name".equals(column)) {
						fixBeans.get(icounts).put("name", value[0]);
					} else if ("value".equals(column)) {
						fixBeans.get(icounts).put("value", value[0]);
					} else if ("jam_code".equals(column)) {
						fixBeans.get(icounts).put("jam_code", value[0]);
					}
				}
				else if ("pcs_comments".equals(entity)) {
					String column = m.group(3);
					int icounts = Integer.parseInt(m.group(2));
					String[] value = parameterMap.get(parameterKey);

					// TODO 全
					if ("name".equals(column)) {
						fixCommentsBeans.get(icounts).put("name", value[0]);
					} else if ("value".equals(column)) {
						fixCommentsBeans.get(icounts).put("value", value[0]);
					} else if ("jam_code".equals(column)) {
						fixCommentsBeans.get(icounts).put("jam_code", value[0]);
					}
				}
			}
		}

		ProductionFeatureMapper pfMapper = conn.getMapper(ProductionFeatureMapper.class);
		PcsFixOrderMapper pfoMapper = conn.getMapper(PcsFixOrderMapper.class);
		LeaderPcsInputMapper lpiMapper = conn.getMapper(LeaderPcsInputMapper.class);

		for (Map fixBean : fixBeans) {
			String jam_code = (String) fixBean.get("jam_code");
			if (jam_code == null) {
			} else if (jam_code.indexOf("_") >= 0) {
				// 操作人员输入
				ProductionFeatureEntity condition = PcsUtils.putJam_codeToPf(jam_code);
				condition.setMaterial_id(material_id);

				List<ProductionFeatureEntity> retEntities = pfMapper.searchProductionFeature(condition);
				if (retEntities.size() > 0) {
					ProductionFeatureEntity target = retEntities.get(0);
					String oldPcsInputs = target.getPcs_inputs();
					Map<String, String> jsonPcsInputs = JSON.decode(oldPcsInputs, Map.class);
					jsonPcsInputs.put(fixBean.get("name").toString(), fixBean.get("value").toString());

					target.setPcs_inputs(JSON.encode(jsonPcsInputs));

					pfoMapper.updateInputs(target);
				}
			} else {
				// 线长输入
				ProductionFeatureEntity leaderPcsInput = lpiMapper.getLeaderPcsInputByKey(jam_code);
				if (leaderPcsInput != null) {
					String oldPcsInputs = leaderPcsInput.getPcs_inputs();
					Map<String, String> jsonPcsInputs = JSON.decode(oldPcsInputs, Map.class);
					jsonPcsInputs.put(fixBean.get("name").toString(), fixBean.get("value").toString());

					pfoMapper.updateInputsLeader(jam_code, JSON.encode(jsonPcsInputs));
				}
			}
		}

		for (Map fixBean : fixCommentsBeans) {
			String jam_code = (String) fixBean.get("jam_code");
			if (jam_code == null) {
			} else if (jam_code.indexOf("_") >= 0) {
				// 操作人员输入
				ProductionFeatureEntity condition = PcsUtils.putJam_codeToPf(jam_code);
				condition.setMaterial_id(material_id);

				List<ProductionFeatureEntity> retEntities = pfMapper.searchProductionFeature(condition);
				if (retEntities.size() > 0) {
					ProductionFeatureEntity target = retEntities.get(0);
					String oldPcsComments = target.getPcs_comments();
					Map<String, String> jsonPcsComments = JSON.decode(oldPcsComments, Map.class);
					jsonPcsComments.put(fixBean.get("name").toString(), fixBean.get("value").toString());

					target.setPcs_comments(JSON.encode(jsonPcsComments));

					pfoMapper.updateComments(target);
				}
			} else {
				// 线长输入
				ProductionFeatureEntity leaderPcsInput = lpiMapper.getLeaderPcsInputByKey(jam_code);
				if (leaderPcsInput != null) {
					String oldPcsComments = leaderPcsInput.getPcs_comments();
					Map<String, String> jsonPcsComments = JSON.decode(oldPcsComments, Map.class);
					jsonPcsComments.put(fixBean.get("name").toString(), fixBean.get("value").toString());

					pfoMapper.updateCommentsLeader(jam_code, JSON.encode(jsonPcsComments));
				}
			}
		}

		fixResolver(req, user, conn);
	}

	/**
	 * 通过主键，仅取得维修品表信息
	 * @param material_id
	 * @param conn
	 * @return
	 */
	public MaterialEntity getMaterialEntityByKey(String material_id, SqlSession conn) {
		MaterialMapper mapper = conn.getMapper(MaterialMapper.class);
		return mapper.getMaterialEntityByKey(material_id);
	}

	public void fixResolver(HttpServletRequest req, LoginData user, SqlSessionManager conn) throws Exception {

		String material_id = req.getParameter("material_id");
		String pcs_fix_order_key = req.getParameter("pcs_fix_order_key");
		PcsFixOrderMapper pfoMapper = conn.getMapper(PcsFixOrderMapper.class);

		// 处理完毕
		PcsFixOrderEntity resolveBean = new PcsFixOrderEntity();
		resolveBean.setStatus(RESOLVED);
		resolveBean.setPcs_fix_order_key(pcs_fix_order_key);
		pfoMapper.resolvePcsFixOrder(resolveBean);

		// 如果维修对象已经完成，则重新生成工程检查票
		MaterialMapper dao = conn.getMapper(MaterialMapper.class);
		MaterialEntity mBean = dao.getMaterialEntityByKey(material_id);
		if (mBean.getOutline_time() != null) {
			try {
				URL url = new URL("http://localhost:8080/rvs/download.do?method=file&material_id=" + material_id);
				url.getQuery();
				URLConnection urlconn = url.openConnection();
				urlconn.setReadTimeout(1); // 不等返回
				urlconn.connect();
				urlconn.getContentType(); // 这个就能触发
			} catch (Exception e) {
				logger.error("Failed", e);
			}
		}

		// 信息推送
		PostMessageMapper pmMapper = conn.getMapper(PostMessageMapper.class);
		PostMessageEntity pmEntity = new PostMessageEntity();
		pmEntity.setSender_id(user.getOperator_id());
		pmEntity.setContent("你的工程检查票修改已处理，请确认！");
		pmEntity.setLevel(1);
		pmEntity.setReason(PostMessageService.PCS_FIX_COMPLETE);

		pmMapper.createPostMessage(pmEntity);

		CommonMapper commonMapper = conn.getMapper(CommonMapper.class);
		String lastInsertID = commonMapper.getLastInsertID();
		pmEntity.setPost_message_id(lastInsertID);

		// 回信给申请者
		PcsFixOrderEntity pcsFixOrder = pfoMapper.getPcsFixOrder(pcs_fix_order_key);
		if (pcsFixOrder != null && !isEmpty(pcsFixOrder.getSender_id())) {
			pmEntity.setReceiver_id(pcsFixOrder.getSender_id());
			pmMapper.createPostMessageSendation(pmEntity);
		}

	}

	public void calcOverTime(List<MaterialForm> lResultForm) {
		for (MaterialForm retForm: lResultForm) {
			// 如果纳期已经超过，标注
			String sOutline_time = retForm.getOutline_time();
			String sScheduled_date = retForm.getScheduled_date();

			if (!isEmpty(sOutline_time) && !isEmpty(sScheduled_date) && sOutline_time.compareTo(sScheduled_date) > 0 ) {
				retForm.setIs_late("纳期延误");
			}
		}
	}

	public String getPcsesBlankXls(String modelName, SqlSession conn, boolean setBlank) {
		String uuid = UUID.randomUUID().toString();
		Date today = new Date();
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(today, "yyyyMM") + "\\" + uuid + "\\";

		String[] showLines = new String[7];
		showLines[0] = "检查卡";
		showLines[1] = "最终检验";
		showLines[2] = "报价";
		showLines[3] = "分解工程";
		showLines[4] = "NS 工程";
		showLines[5] = "总组工程";
		showLines[6] = "外科硬镜修理工程";

		for (int i=0 ; i < showLines.length ; i++) {
			String showLine = showLines[i]; 
			logger.info(showLine);
			Map<String, String> fileTempl = PcsUtils.getXlsContents(showLine, modelName, null, null, false, false, conn);

			try {
				PcsUtils.toTemplatesXls(fileTempl, modelName, cachePath, setBlank, conn);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}

		// 打包
		ZipUtility.zipper(cachePath, cachePath + "\\..\\" + uuid + ".zip", "UTF-8");
		return uuid + ".zip";
	}

	private static Set<String> levelSet = new HashSet<String>();
	static {
		levelSet.add("1");//S1
		levelSet.add("2");//S2
		levelSet.add("3");//S3
		levelSet.add("56");//E1
		levelSet.add("57");//E2
		levelSet.add("59");//EW
	}

	public void checkModelDepacy(MaterialForm materialForm,
			SqlSession conn, List<MsgInfo> errors) {
		ModelMapper mapper = conn.getMapper(ModelMapper.class);

		String level = materialForm.getLevel();
		if (level == null) {
			level = materialForm.getOcm_rank();
		}

		String end_date = null;
		if (levelSet.contains(level)) {
			end_date = mapper.checkModelDepacy(materialForm.getModel_id(), materialForm.getLevel());
		} else {
			end_date = mapper.checkModelDepacy(materialForm.getModel_id(), null);
		}

		if (end_date != null) {
			MsgInfo error = new MsgInfo();
			error.setComponentid("model_id");
			error.setErrcode("model.notDepacy");
			
			if (levelSet.contains(level)) {
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("model.notDepacy", materialForm.getModel_name(), 
						materialForm.getLevelName(), end_date));
			} else {
				error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("model.notDepacy", materialForm.getModel_name(), 
						"全部等", end_date));
			}
			errors.add(error);
		}
		
	}

	/**
	 * 个个
	 * @param entity
	 * @param conn
	 */
	public void updateComment(MaterialEntity entity, SqlSessionManager conn) {
		MaterialMapper mapper = conn.getMapper(MaterialMapper.class);
	
		mapper.updateMaterialComment(entity);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public List<MonthFilesDownloadForm> getMonthFiles(){
		List<MonthFilesDownloadForm> monthFilesDownloadForms = new ArrayList<MonthFilesDownloadForm>();
		
		String filePath = PathConsts.BASE_PATH+PathConsts.PCS+"\\_monthly";
		File file = new File(filePath);
		
		MonthFilesDownloadForm monthFilesDownloadForm;
		//如果该文件是个目录的话
		if(file.isDirectory()){
			File[] files = file.listFiles();
			for(File f:files){
				monthFilesDownloadForm = new MonthFilesDownloadForm();
				if(f.isDirectory()){
					continue;
				}
				
				String fileName = f.getName();
				
				long fileSize = f.length();//文件的大小(字节)
				double ds = (double)fileSize/1024;//文件字节大小/1024所得的就是以kb为单位的大小
				BigDecimal bd = new BigDecimal(ds);
				double resultSize = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");//文件最后修改时间
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(f.lastModified());
				String fileTime = sdf.format(cal.getTime());
				
				monthFilesDownloadForm.setFile_name(fileName);
				monthFilesDownloadForm.setFile_size(resultSize+"kb");
				monthFilesDownloadForm.setFile_time(fileTime);
				
				monthFilesDownloadForms.add(monthFilesDownloadForm);
			}
		}
		
		//按照文件的最后编辑时间进行倒序排列
		Collections.sort(monthFilesDownloadForms, new Comparator<MonthFilesDownloadForm>() {
			@Override
			public int compare(MonthFilesDownloadForm o1, MonthFilesDownloadForm o2) {
				
				return o2.getFile_time().compareTo(o1.getFile_time());
			}
		});

		return monthFilesDownloadForms;
	}

	public void removeComment(String material_id, String operator_id, SqlSessionManager conn){
		MaterialCommentMapper mapper = conn.getMapper(MaterialCommentMapper.class);

		//查询维修对象备注是否存在
		String dbComment = mapper.getMyMaterialComment(material_id, operator_id);
		
		//如果原来存在备注，则删除修对象备注
		if(!CommonStringUtil.isEmpty(dbComment)){
			mapper.deleteMaterialComment(material_id, operator_id);
		}
	}

	public void updateMaterialComment(String material_id, String operator_id,
			String comment, SqlSessionManager conn) {
		MaterialCommentMapper mapper = conn.getMapper(MaterialCommentMapper.class);
		
		Map<String, Object> materialComment = new HashMap<String, Object>();
		materialComment.put("material_id", material_id);
		materialComment.put("operator_id", operator_id);
		materialComment.put("comment", comment);
		
		String dbComment = mapper.getMyMaterialComment(material_id, operator_id);
		if(dbComment == null){
			materialComment.put("create_datetime", new Date());
			mapper.inputMaterialComment(materialComment);
		}else{
			mapper.updateMaterialComment(materialComment);
		}
	}

	public void getMaterialCommentEdit(String material_id, String operator_id,
			Map<String, Object> callbackResponse, boolean writable, SqlSession conn) {
		MaterialCommentMapper mapper = conn.getMapper(MaterialCommentMapper.class);
		if (writable) {
			callbackResponse.put("material_comment_other", mapper.getMaterialComments(material_id, operator_id));
			callbackResponse.put("material_comment", mapper.getMyMaterialComment(material_id, operator_id));
		} else {
			callbackResponse.put("material_comment_other", mapper.getMaterialComments(material_id, null));
		}
	}

	public void getMaterialComment(String material_id,
			Map<String, Object> callbackResponse, SqlSession conn) {
		MaterialCommentMapper mapper = conn.getMapper(MaterialCommentMapper.class);
		callbackResponse.put("material_comment", mapper.getMaterialComments(material_id, null));
	}

	/**
	 * 为维修品归档画面-提供已经报价完成时的周边设备临时检查票
	 * 
	 * @param conn
	 * @return
	 */
	public List<MaterialForm> searchPerlTempFiling(SqlSession conn) {
		MaterialMapper mapper = conn.getMapper(MaterialMapper.class);
		List<MaterialEntity> retEntites = mapper.searchMaterialPerlTempFiling();

		return checkFileExist(retEntites);
	}

	public void setLocation(MaterialForm materialForm, SqlSession conn) {
		String location = materialForm.getWip_location();
		if (location != null && !"".equals(location)) { //location不为空，设置当前状态WIP

			List<Map<String, String>> positions = getLastPositionAndStatus(materialForm.getMaterial_id(), conn);
			int positionSize = positions.size();
			String[] locations = new String[positionSize];
			String[] statuses = new String[positionSize];

			for (int i =0 ; i < positionSize ; i++) {
				Map<String, String> positionMap = positions.get(i);
				if ("2".equals(positionMap.get("operate_result"))) continue;
				locations[i] = "" + positionMap.get("process_code") + " " + positionMap.get("position_name");
				statuses[i] = CodeListUtils.getValue("material_operate_result", positionMap.get("operate_result")) + " (" + positionMap.get("process_code") + " " + positionMap.get("position_name") + ")";
			}

			String sLocations = CommonStringUtil.joinBy("\n", locations);
			String sStatus = CommonStringUtil.joinBy("\n", statuses);
			if (isEmpty(sStatus)) {
				materialForm.setStatus("WIP");
				materialForm.setProcessing_position(location);
			} else {
				materialForm.setStatus("WIP\n" + sStatus);
				materialForm.setProcessing_position(location + "\n" + sLocations);
			}
		} else { //否则，根据Operate_result查找状态
			String position = materialForm.getProcessing_position();
			materialForm.setWip_location(position);

			List<Map<String, String>> positions = getLastPositionAndStatus(materialForm.getMaterial_id(), conn);
			int positionSize = positions.size();
			String[] locations = new String[positionSize];
			String[] statuses = new String[positionSize];
			for (int i =0 ; i < positionSize ; i++) {
				Map<String, String> positionMap = positions.get(i);
				locations[i] = "" + positionMap.get("process_code") + " " + positionMap.get("position_name");
				statuses[i] = CodeListUtils.getValue("material_operate_result", positionMap.get("operate_result")) + " (" + positionMap.get("process_code") + " " + positionMap.get("position_name") + ")";
			}
			materialForm.setProcessing_position(CommonStringUtil.joinBy("\n", locations));
			materialForm.setStatus(CommonStringUtil.joinBy("\n", statuses));
		}
	}

	/**
	 * 取得修理品时间节点
	 * 
	 * @param material_id
	 * @param conn
	 * @return
	 */
	public MaterialTimeNodeEntity getMaterialTimeNode(String material_id, SqlSession conn) {
		MaterialMapper mMapper = conn.getMapper(MaterialMapper.class);

		MaterialTimeNodeEntity entity = mMapper.getMaterialTimeNode(material_id);
		return entity;
	}
}