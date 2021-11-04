package com.osh.rvs.service.partial;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.data.MaterialEntity;
import com.osh.rvs.bean.master.PartialBomEntity;
import com.osh.rvs.bean.master.PartialEntity;
import com.osh.rvs.bean.master.PartialPositionEntity;
import com.osh.rvs.bean.partial.MaterialPartInstructEntity;
import com.osh.rvs.bean.partial.MaterialPartPrelistEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.master.PartialPositionForm;
import com.osh.rvs.form.partial.MaterialPartInstructForm;
import com.osh.rvs.mapper.master.PartialMapper;
import com.osh.rvs.mapper.partial.MaterialPartInstructMapper;
import com.osh.rvs.service.MaterialService;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.copy.DateUtil;

public class MaterialPartInstructService {

	Logger _log = Logger.getLogger(MaterialPartInstructService.class);

	private static final String PROCEDURE_NONE = "0";
	public static final String PROCEDURE_QUOTE = "1";
	public static final String PROCEDURE_INLINE = "2";
	private static final String PROCEDURE_CONFIRM = "3";
	private static final String PROCEDURE_TO_SUBMIT = "4";
	private static final String PROCEDURE_ORDERED = "5";

	/**
	 * 查询维修中指示单一览
	 * 
	 * @param form
	 * @param conn
	 * @param errors
	 * @return
	 */
	public List<MaterialPartInstructForm> searchInstruct(ActionForm form,
			SqlSession conn, List<MsgInfo> errors) {
		MaterialPartInstructEntity condition = new MaterialPartInstructEntity();
		MaterialPartInstructForm bussForm = (MaterialPartInstructForm) form;
		CopyOptions cos = new CopyOptions();
		cos.excludeEmptyString(); cos.excludeNull();
		cos.exclude("procedure");
		BeanUtil.copyToBean(bussForm, condition, cos);

		MaterialPartInstructMapper mapper = conn.getMapper(MaterialPartInstructMapper.class);
		List<MaterialPartInstructEntity> list = null;
		if ("1".equals(bussForm.getRange())) {
			if (!isEmpty(bussForm.getNongood_append())) {
				switch(bussForm.getNongood_append()) {
				case "1":
					condition.setOccur_times(-1);
					break;
				case "2":
					condition.setOrder_date(new Date());
					// nobreak;
				case "3":
					condition.setOccur_times(1);
					break;
				case "4":
					condition.setOccur_times(2);
					break;
				}
			}
			if (!isEmpty(bussForm.getProcedure())) {
				String[] search_procedures = bussForm.getProcedure().split(",");
				condition.setSearch_procedures(search_procedures);
			}
			list = mapper.searchMaterialPartInstructInline(condition);
		} else {
			list = mapper.searchMaterialPartInstructOutline(condition);
		}

		List<MaterialPartInstructForm> ret = new ArrayList<MaterialPartInstructForm>();

		for (MaterialPartInstructEntity entity : list) {
			MaterialPartInstructForm dest = new MaterialPartInstructForm();
			BeanUtil.copyToForm(entity, dest, CopyOptions.COPYOPTIONS_NOEMPTY);

			// 进展
			if (entity.getOrder_date() != null) {
				dest.setProcedure("5");
			} else if (entity.getInline_adjust() == null) { 
				dest.setProcedure("0");
			} else if (entity.getInline_time() == null) {
				dest.setProcedure("1");
			} else {
				Integer procedure = entity.getProcedure();
				if (procedure == null || procedure == 0) {
					dest.setProcedure("2");
				} else {
					if (entity.getConfirm() == null || entity.getConfirm() == 1) {
						dest.setProcedure("4");
					} else {
						dest.setProcedure("3");
					}
				}
			}

			// 不良追加
			if (entity.getOccur_times() != null && entity.getOccur_times() > 1) {
				dest.setNongood_append("有");
			} else {
				dest.setNongood_append("");
			}

			ret.add(dest);
		}

		return ret;
	}

	/**
	 * 取得维修品的指示信息
	 * 
	 * @param operator_id
	 * @param conn
	 * @return
	 */
	public List<MaterialPartPrelistEntity> getInstuctListForMaterial(String material_id,
			SqlSession conn) {

		MaterialPartInstructMapper mapper = conn.getMapper(MaterialPartInstructMapper.class);
		List<MaterialPartPrelistEntity> resultList = mapper.getInstuctListForMaterial(material_id);

		return resultList;
	}

	/**
	 * 个人关注取得
	 * 
	 * @param operator_id
	 * @param conn
	 * @return
	 */
	public List<PartialPositionForm> getFocusPartialsByOperator(String operator_id,
			SqlSession conn) {
		List<PartialPositionForm> ret = new ArrayList<PartialPositionForm> ();

		MaterialPartInstructMapper mapper = conn.getMapper(MaterialPartInstructMapper.class);
		List<PartialPositionEntity> resultList = mapper.getFocusPartialsByOperator(operator_id);

		BeanUtil.copyToFormList(resultList, ret, CopyOptions.COPYOPTIONS_NOEMPTY, PartialPositionForm.class);

		return ret;
	}

	/**
	 * 在线追加时，标记作业者本人以外的追加项目
	 * 
	 * @param instuctListForMaterial
	 * @param job_no
	 * @param isLeader
	 * @return !isLeader null/0 self 1 other isLeader 2 self other 1
	 */
	public void signNoPriv(List<MaterialPartPrelistEntity> instuctListForMaterial,
			String job_no, boolean isLeader) {
		for (MaterialPartPrelistEntity entity : instuctListForMaterial) {
			if (isLeader) {
				entity.setNo_priv(2);
			}
			if (entity.getQuote_job_no() != null &&
					!entity.getQuote_job_no().equals(job_no)) {
				entity.setNo_priv(1);
			}
			if (entity.getInline_job_no() != null &&
					!entity.getInline_job_no().equals(job_no)) {
				entity.setNo_priv(1);
			}
		}
	}

	/**
	 * 调取加入关注相关的零件信息
	 * 
	 * @param partial_id
	 * @param conn
	 * @return
	 */
	public PartialPositionForm loadPartialForFocus(String partial_id,
			SqlSession conn) {
		PartialPositionForm ret = new PartialPositionForm ();

		MaterialPartInstructMapper mapper = conn.getMapper(MaterialPartInstructMapper.class);
		PartialPositionEntity result = mapper.getPartialById(partial_id);

		if (result != null) {
			BeanUtil.copyToForm(result, ret, CopyOptions.COPYOPTIONS_NOEMPTY);
			return ret;
		} else {
			return null;
		}
	}

	/**
	 * 个人关注设置
	 * 
	 * @param operator_id
	 * @param conn
	 * @return
	 */
	public void setFocusPartialList(Map<String, String[]> parameterMap, String operator_id,
			SqlSessionManager conn) {
		MaterialPartInstructMapper mapper = conn.getMapper(MaterialPartInstructMapper.class);
		mapper.removeFocusPartialListByOperator(operator_id);
		for (String paramKey : parameterMap.keySet()) {
			if (paramKey.startsWith("partial_id")) {
				mapper.insertFocusPartialListByOperator(operator_id, parameterMap.get(paramKey)[0]);
			}
		}
	}

	public String getProcedure(MaterialEntity mEntity,
			List<MaterialPartPrelistEntity> instuctListForMaterial, SqlSession conn) {
		if (instuctListForMaterial == null || instuctListForMaterial.size() == 0) {
			return PROCEDURE_NONE;
		}
		if (mEntity.getInline_time() == null) {
			return PROCEDURE_QUOTE;
		}
		
		return getProcedureConfirm(mEntity.getMaterial_id(), conn);
	}

	public String getProcedureConfirm(String material_id, SqlSession conn) {
		// 当前进度信息取得
		MaterialPartInstructEntity materialPartProcedure = getMaterialPartProcedure(material_id, conn);

		if (materialPartProcedure == null || materialPartProcedure.getConfirm() == null || materialPartProcedure.getConfirm() == 0) {
			return PROCEDURE_INLINE;
		}

		boolean confirmed = false;
		String confirmOperator = materialPartProcedure.getOperator_id();
		if (confirmOperator == null) return PROCEDURE_INLINE;
		
		switch(materialPartProcedure.getConfirm()) {
		case 1:
			confirmed = !confirmOperator.endsWith("-");
			break;
		case 2:
			confirmed = !confirmOperator.startsWith("-");
			break;
		case 3:
			confirmed = !(confirmOperator.startsWith("-") || confirmOperator.endsWith("-"));
			break;
		}

		return confirmed ? PROCEDURE_CONFIRM : PROCEDURE_TO_SUBMIT;
	}

	/**
	 * 根据进度和登录者权限返回可操作权限
	 * 
	 * @param procedure
	 * @param privates
	 * @param line_id
	 * @return
	 */
	public Integer getEditType(String procedure, List<Integer> privates, String line_id) {
		switch(procedure) {
		case PROCEDURE_NONE : 
		case PROCEDURE_TO_SUBMIT :
		case PROCEDURE_ORDERED :
			return 0;
		case PROCEDURE_QUOTE : {
			if (privates.contains(RvsConsts.PRIVACY_POSITION) && "00000000011".equals(line_id)) {
				return 1;
			}
			return 0;
		}
		case PROCEDURE_INLINE : {
			if (privates.contains(RvsConsts.PRIVACY_POSITION) 
					&& ("00000000012".equals(line_id) || "00000000013".equals(line_id))) {
				return 2;
			}
			return 0;
		}
		case PROCEDURE_CONFIRM : {
			if (privates.contains(RvsConsts.PRIVACY_LINE)) {
				return 3;
			}
			return 0;
		}
		}
		return null;
	}

	/**
	 * 执行更新
	 * @param cbResponse 
	 * @param map 
	 * 
	 */
	public void doUpdateItem(ActionForm form, Map<String, String[]> parameterMap, 
			LoginData user, List<String> triggerList,
			Map<String, Object> cbResponse, SqlSessionManager conn, List<MsgInfo> errors) {
		MaterialPartInstructMapper mapper = conn.getMapper(MaterialPartInstructMapper.class);

		List<MaterialPartPrelistEntity> postList 
			= new AutofillArrayList<MaterialPartPrelistEntity>(MaterialPartPrelistEntity.class);

		Pattern p = Pattern.compile("(\\w+)\\[(\\d+)\\].(\\w+)");

		// 整理提交数据
		for (String parameterKey : parameterMap.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("item".equals(entity)) {
					int icounts = Integer.parseInt(m.group(2));
					String column = m.group(3);
					String[] value = parameterMap.get(parameterKey);
					switch(column) {
					case "partial_id" : postList.get(icounts).setPartial_id(value[0]); break;
					case "bom_code" : postList.get(icounts).setBom_code(value[0]);  break;
					case "quantity" : {
						Integer quantity = null;
						try {
							quantity = Integer.parseInt(value[0]);
						} catch (Exception e) {
						}
						postList.get(icounts).setQuantity(quantity);
						break;
					}
					case "inline_comment" : postList.get(icounts).setInline_comment(value[0]);  break;
					}
				}
			}
		}

		MaterialPartInstructForm bussForm = (MaterialPartInstructForm) form;

		String materialId = bussForm.getMaterial_id();
		// 操作实行
		String action = bussForm.getUpd_action();

		MaterialService mService = new MaterialService();
		MaterialEntity mEntity = mService.getMaterialEntityByKey(materialId, conn);
		boolean inline = mEntity.getInline_time() != null;

		// 本人工号（图章用）
		String curJobNo = null;
		// 更新对象
		String target = inline ? "i" : "q";
		cbResponse.put("target", target);

		if (postList != null && postList.size() > 0) {
			for (MaterialPartPrelistEntity plEntity : postList) {
				boolean updated = true;

				// 确认指示信息，是否零件列表中存在此记录，存在的话是否已经指示/未指示，指示的话是否本人
				plEntity.setMaterial_id(materialId);
				MaterialPartPrelistEntity origin = mapper.checkPartPrelist(plEntity);

				if (inline) {
					if ("append".equals(action)) {
						if (origin == null) { // 零件列表基础上增加
							plEntity.setInline_operator_id(user.getOperator_id());
							plEntity.setRank(9);
							mapper.insertPartPrelist(plEntity);
							// 负责人
							curJobNo = user.getJob_no();
						} else {
							if (origin.getInline_operator_id() != null) {
								if (origin.getQuote_adjust() < 0) {
									// 报价取消的，标记登录在线作业者意味再追加
									origin.setInline_operator_id(user.getOperator_id());
									origin.setInline_comment(plEntity.getInline_comment());
									mapper.updatePartPrelist(origin);
									curJobNo = user.getJob_no();
								} else if (origin.getQuote_adjust() == 0) {
									// 不是报价增加的，取消在线作业者信息复原
									origin.setInline_operator_id(null);
									origin.setInline_comment(null);
									mapper.updatePartPrelist(origin);
								} else {
									MsgInfo error = new MsgInfo();
									error.setErrmsg("此零件" + getPartialCode(plEntity.getPartial_id(), conn) +"已经存在，不能追加。");
									errors.add(error);
									updated = false;
								}
							} else {
								// 登记在线作业者名字，恢复为报价人员没有处理过
								origin.setInline_operator_id(user.getOperator_id());
								origin.setInline_comment(plEntity.getInline_comment());
								mapper.updatePartPrelist(origin);
								curJobNo = user.getJob_no();
							}
						}
					} else if ("cancel".equals(action)) {
						if (origin == null) {
							MsgInfo error = new MsgInfo();
							error.setErrmsg("原列表没有选择此零件" + getPartialCode(plEntity.getPartial_id(), conn) +"。");
							errors.add(error);
							updated = false;
						} else {
							if (origin.getInline_operator_id() != null && 
									!origin.getInline_operator_id().equals(user.getOperator_id())) {
								MsgInfo error = new MsgInfo();
								error.setErrmsg("此零件" + getPartialCode(plEntity.getPartial_id(), conn) +"为他人追加的零件，您不能取消。");
								errors.add(error);
								updated = false;
							} else {
								if (origin.getRank() == 9) {
									mapper.deletePartPrelist(origin);
								} else if (origin.getQuote_adjust() > 0) {
									// 报价增加的，登录在线作业者意味取消
									origin.setInline_operator_id(user.getOperator_id());
									origin.setInline_comment(plEntity.getInline_comment());
									mapper.updatePartPrelist(origin);
									// 负责人
									curJobNo = user.getJob_no();
								} else if (origin.getQuote_adjust() == 0) {
									// 不是报价增加的，登录在线作业者意味取消
									origin.setInline_operator_id(user.getOperator_id());
									origin.setInline_comment(plEntity.getInline_comment());
									mapper.updatePartPrelist(origin);
									// 负责人
									curJobNo = user.getJob_no();
								} else if (origin.getQuote_adjust() < 0) {
									if (origin.getInline_operator_id() != null) {
										// 报价取消的，删除登录在线作业者还是取消
										origin.setInline_operator_id(null);
										origin.setInline_comment(null);
										mapper.updatePartPrelist(origin);
									} else {
										MsgInfo error = new MsgInfo();
										error.setErrmsg("此零件" + getPartialCode(plEntity.getPartial_id(), conn) +"已经被取消，不能再次取消。");
										errors.add(error);
										updated = false;
									}
								}
							}
						}
					}
				} else {
					if ("append".equals(action)) {
						if (origin == null) { // 零件列表基础上增加
							plEntity.setQuote_adjust(plEntity.getQuantity());
							plEntity.setQuote_operator_id(user.getOperator_id());
							plEntity.setRank(0);
							mapper.insertPartPrelist(plEntity);
							// 负责人
							curJobNo = user.getJob_no();
						} else {
							if (origin.getQuote_adjust() >= 0) {
								MsgInfo error = new MsgInfo();
								error.setErrmsg("此零件" + getPartialCode(plEntity.getPartial_id(), conn) +"已经存在，不能追加。");
								errors.add(error);
								updated = false;
							} else if (origin.getQuote_adjust() < 0) {
								// 恢复
								origin.setQuote_adjust(0);
								origin.setQuote_operator_id(null);
								mapper.updatePartPrelist(origin);
							}
						}
					} else if ("cancel".equals(action)) {
						if (origin == null) {
							MsgInfo error = new MsgInfo();
							error.setErrmsg("原列表没有选择此零件" + getPartialCode(plEntity.getPartial_id(), conn) +"。");
							errors.add(error);
							updated = false;
						} else {
							if (origin.getQuote_operator_id() != null && 
									!origin.getQuote_operator_id().equals(user.getOperator_id())) {
								MsgInfo error = new MsgInfo();
								error.setErrmsg("此零件" + getPartialCode(plEntity.getPartial_id(), conn) +"为他人追加的零件，您不能取消。");
								errors.add(error);
								updated = false;
							} else {
								if (origin.getQuote_adjust() > 0) {
									mapper.deletePartPrelist(origin);
								} else if (origin.getQuote_adjust() == 0) {
									// 扣除已选
									origin.setQuote_adjust(-origin.getQuantity());
									origin.setQuote_operator_id(user.getOperator_id());
									mapper.updatePartPrelist(origin);
									// 负责人
									curJobNo = user.getJob_no();
								} else {
									MsgInfo error = new MsgInfo();
									error.setErrmsg("此零件" + getPartialCode(plEntity.getPartial_id(), conn) +"已经被取消，不能再次取消。");
									errors.add(error);
									updated = false;
								}
							}
						}
					}
				}

				if (updated) {
					String triggerText = "http://localhost:8080/rvspush/trigger/instruct_async/" 
							+ materialId + "/" + plEntity.getBom_code() + "/" + target + "/" + curJobNo;
					if (curJobNo != null && CommonStringUtil.isEmpty(plEntity.getInline_comment())) {
						try {
							triggerText += "/" + java.net.URLEncoder.encode(plEntity.getInline_comment(), "UTF-8");
						} catch (UnsupportedEncodingException e) {
						}
					}
					triggerList.add(triggerText); 
				}
			}

			// 负责人章（有章就要加章，否则删除指定位置上的章）
			cbResponse.put("job_no", curJobNo);
		}
	}

	private String getPartialCode(String partial_id, SqlSession conn) {
		PartialMapper pMapper = conn.getMapper(PartialMapper.class);
		PartialEntity partial = pMapper.getPartialByID(partial_id);

		if (partial == null) {
			return "";
		}
		return partial.getCode();
	}

	/**
	 * 判断维修品是否处于“同意前”的状态，
	 * 如是，则增加SORC-RANK数目
	 * 
	 * @param material_id 
	 * @param instuctListForMaterial
	 * @param rankBomList
	 * @param conn
	 */
	public void tryToFill(
			String material_id, List<MaterialPartPrelistEntity> instuctListForMaterial,
			List<PartialBomEntity> rankBomList, SqlSession conn) {
		MaterialPartInstructMapper mapper = conn.getMapper(MaterialPartInstructMapper.class);

		MaterialPartInstructEntity partProcedure = mapper.getPartProcedure(material_id);
		// 是同意后的清单则不处理
		if (partProcedure != null) return;

		List<MaterialPartPrelistEntity> instuctListForMaterialAppendix = new ArrayList<MaterialPartPrelistEntity>();

		for (PartialBomEntity rankBom : rankBomList) {
			boolean matched = false;
			for (MaterialPartPrelistEntity instuct : instuctListForMaterial) {
				if (instuct.getBom_code().equals(rankBom.getCode())) {
					matched = true;
					break;
				}
			}
			if (!matched) {
				MaterialPartPrelistEntity appendix = new MaterialPartPrelistEntity();
				appendix.setBom_code(rankBom.getCode());
				appendix.setPartial_id(rankBom.getPartial_id());
				appendix.setQuantity(rankBom.getQuantity());
				appendix.setQuote_adjust(0);
				appendix.setRank(3);
				appendix.setShip(0);
				instuctListForMaterialAppendix.add(appendix);
			}
		}

		if (!instuctListForMaterialAppendix.isEmpty()) {
			instuctListForMaterial.addAll(instuctListForMaterialAppendix);
		}
	}

	/**
	 * 取得可选理由
	 * 
	 * @param material_id
	 * @param conn
	 * @return
	 */
	public Map<String, Set<String>> getReasons(String material_id, SqlSession conn) {
		MaterialPartInstructMapper mapper = conn.getMapper(MaterialPartInstructMapper.class);
		List<MaterialPartPrelistEntity> listReasons = mapper.getPartCommentsFromLossDetail(material_id);

		Map<String, Set<String>> ret = new HashMap<String, Set<String>>();

		for (MaterialPartPrelistEntity rReason : listReasons) {
			String partial_id = rReason.getPartial_id();
			if (!ret.containsKey(partial_id)) {
				ret.put(partial_id, new TreeSet<String>());
			}
			ret.get(partial_id).add(rReason.getInline_comment());
		}

		return ret;
	}

	/**
	 * 获取追加零件信息（确认用）
	 * 
	 * @param material_id
	 * @param cbResponse
	 * @param conn
	 */
	public void getAdditionalOrder(String material_id,
			Map<String, Object> cbResponse, SqlSession conn) {
		MaterialPartInstructMapper mapper = conn.getMapper(MaterialPartInstructMapper.class);
		List<MaterialPartPrelistEntity> listAdditionalOrder = mapper.getAdditionalOrder(material_id);

		// 报价追加信息
		String quoteAdditions = "";
		// 流水线追加信息
		List<MaterialPartPrelistEntity> inlineAdditions = new ArrayList<MaterialPartPrelistEntity>();

		for (MaterialPartPrelistEntity additionalOrder : listAdditionalOrder) {
			Integer quote_adjust = additionalOrder.getQuote_adjust();
			if (additionalOrder.getQuote_operator_id() != null) {
				if (quote_adjust > 0) {
					quoteAdditions += additionalOrder.getCode() + " x " + additionalOrder.getQuantity() + " ";
				}
			}
			if (additionalOrder.getInline_job_no() != null) {
				if (additionalOrder.getRank() == 9) {
					// 在线追加
				} else {
					if (quote_adjust > 0) {
						// 报价追加
						additionalOrder.setQuantity(- additionalOrder.getQuantity());
					} else if (quote_adjust < 0) {
						// 报价取消
					} else {
						// Rank 或者 RC追加
						additionalOrder.setQuantity(- additionalOrder.getQuantity());
					}
				}
				inlineAdditions.add(additionalOrder);
			}
		}

		cbResponse.put("quoteAdditions", quoteAdditions);
		cbResponse.put("inlineAdditions", inlineAdditions);
	}

	/**
	 * 取得当前确认进度
	 * 
	 * @param material_id
	 * @param conn
	 * @return
	 */
	public MaterialPartInstructEntity getMaterialPartProcedure(String material_id,
			SqlSession conn) {
		MaterialPartInstructMapper mapper = conn.getMapper(MaterialPartInstructMapper.class);
		return mapper.getMaterialPartProcedure(material_id);
	}

	/**
	 * 更新追加进度，以及提示给谁
	 * 
	 * @param material_id
	 * @param materialPartProcedure
	 * @param section_id
	 * @param line_id NULL 线长增加
	 * @param triggerList 
	 * @param conn
	 */
	public int setProcedureConfirm(String material_id,
			String section_id, String line_id,
			List<String> triggerList, SqlSessionManager conn) {

		MaterialPartInstructMapper mapper = conn.getMapper(MaterialPartInstructMapper.class);

		// 当前进度信息取得
		MaterialPartInstructEntity materialPartProcedure = getMaterialPartProcedure(material_id, conn);

		if (materialPartProcedure == null) {
			materialPartProcedure = new MaterialPartInstructEntity();
			materialPartProcedure.setMaterial_id(material_id);
			materialPartProcedure.setProcedure(0);

			mapper.insertMaterialPartProcedure(material_id);
		}

		List<MaterialPartPrelistEntity> needNoticeList = mapper.getPartNeedNotice(material_id);

		Integer procedure = materialPartProcedure.getProcedure();
		if (procedure >= 1) return -1;

		if (line_id != null) {
			procedure = 1;
		}

		Integer confirm = 0;
		Set<String> cc = new HashSet<String>(); 

		if (needNoticeList == null || needNoticeList.size() == 0) {
			// 给物料课
			confirm = 0;
		} else {
			// 固定为分解线长了
			confirm |= 2;
			for (MaterialPartPrelistEntity needNotice : needNoticeList) {
				if (needNotice.getOrder_flg() == 2) {
					confirm |= 1;
				}
				if (needNotice.getInline_comment() != null) {
					String[] ccTo = needNotice.getInline_comment().split(",");
					for (String focusOp : ccTo) {
						cc.add(focusOp);
					}
				}
			}
		}

		materialPartProcedure.setProcedure(procedure);
		materialPartProcedure.setConfirm(confirm);
		mapper.updateMaterialPartProcedure(materialPartProcedure);

		if (triggerList != null) {
			if (confirm == 0) {
				triggerList.add("http://localhost:8080/rvspush/trigger/instruct_notice/" 
						+ material_id + "/" + section_id + "/" + "00000000020"); // 物料组
			} else {
				triggerList.add("http://localhost:8080/rvspush/trigger/instruct_notice/" 
						+ material_id + "/" + section_id + "/" + line_id + "/" + confirm); // 线长
			}
		}

		return confirm;
	}

	/**
	 * 线长以上人员确认
	 * 
	 * @param material_id
	 * @param materialPartProcedure
	 * @param triggerList 
	 * @param user
	 * @param conn
	 * @return
	 */
	public boolean applyProcedureConfirm(String material_id,
			MaterialPartInstructEntity materialPartProcedure, List<String> triggerList, LoginData user, 
			SqlSessionManager conn) {
		MaterialPartInstructMapper mapper = conn.getMapper(MaterialPartInstructMapper.class);

		if (materialPartProcedure == null) {
			materialPartProcedure = new MaterialPartInstructEntity();
			materialPartProcedure.setMaterial_id(material_id);
			materialPartProcedure.setProcedure(0);

			mapper.insertMaterialPartProcedure(material_id);
		}

		boolean updated = false;
		// 判断那部分是需要判定的。
		boolean decNeed = false;
		boolean nsNeed = false;
		Integer confirm = materialPartProcedure.getConfirm();
		if (confirm == null) confirm = 0;

		if (confirm >= 2) { // 10 11
			decNeed = true;
		}
		if (confirm == 1 
				|| confirm == 3) { // 1 11
			nsNeed = true;
		}

		boolean decAbility = false;
		boolean nsAbility = false;
		if (user.getPrivacies().contains(RvsConsts.PRIVACY_PROCESSING)) {
			decAbility = true;
			nsAbility = true;
		} else {
			if (user.getPrivacies().contains(RvsConsts.PRIVACY_LINE)) {
				if ("00000000012".equals(user.getLine_id())) {
					decAbility = true;
				}
			}
		}

		if (decNeed && decAbility) {
			updated = true;
			if (materialPartProcedure.getOperator_id() == null || materialPartProcedure.getOperator_id().startsWith("-")) {
				materialPartProcedure.setOccur_times(12);
				materialPartProcedure.setOperator_id(user.getOperator_id());
				mapper.updateMaterialPartProcedure(materialPartProcedure);
			}
		}

		if (nsNeed && nsAbility) {
			updated = true;
			materialPartProcedure.setOccur_times(13);
			materialPartProcedure.setOperator_id(user.getOperator_id());
			mapper.updateMaterialPartProcedure(materialPartProcedure);
		}

		if (updated) {
			if (decNeed == decAbility && nsNeed == nsAbility) {
				// 物料组
				triggerList.add("http://localhost:8080/rvspush/trigger/instruct_notice/" 
						+ material_id + "/00000000000/00000000020");
			} else if (nsNeed != nsAbility) {
				MaterialService mService = new MaterialService();
				MaterialEntity mEntity = mService.loadSimpleMaterialDetailEntity(conn, material_id);
				if (mEntity != null && mEntity.getSection_id() != null) {
					// 经理
					triggerList.add("http://localhost:8080/rvspush/trigger/instruct_notice/" 
							+ material_id + "/" + mEntity.getSection_id() + "/00000000000"); 
				}
			}
		}

		return updated;
	}

	/**
	 * 生成追加文件
	 * 
	 * @param material_id
	 * @param conn
	 * @return
	 */
	public String makeAdditionalOrderFile(String material_id, SqlSession conn) {
		MaterialPartInstructMapper mapper = conn.getMapper(MaterialPartInstructMapper.class);
		List<MaterialPartPrelistEntity> listAdditionalOrder = mapper.getAdditionalOrder(material_id);

		//Excel临时文件
		String cacheName ="_additionalOrder" + new Date().getTime() + ".xlsx";
		String cachePath = PathConsts.BASE_PATH + PathConsts.LOAD_TEMP + "\\" + DateUtil.toString(new Date(), "yyyyMM") + "\\" +cacheName; 
		
		OutputStream out = null;
		try {
			File file = new File(cachePath);
			if(!file.exists()){
				file.createNewFile();
			}
			
			XSSFWorkbook work=new XSSFWorkbook();
//			XSSFSheet sheetAdd = work.createSheet("追加订购零件");
//			XSSFSheet sheetCancel = work.createSheet("取消订购零件");
			XSSFSheet sheetAdd = work.createSheet("追加");
			XSSFSheet sheetCancel = work.createSheet("取消");

			int idxAdd = 0, idxCancel = 0;
			XSSFRow row = sheetAdd.createRow(0);
			row.createCell(0).setCellValue("零件代码");
			row.createCell(1).setCellValue("数量");
			row.createCell(2).setCellValue("追加理由");

			row = sheetCancel.createRow(0);
			row.createCell(0).setCellValue("零件代码");
			row.createCell(1).setCellValue("数量");
			row.createCell(2).setCellValue("取消分类");

			XSSFCellStyle styleGray = work.createCellStyle();
			styleGray.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
			styleGray.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

			row.getCell(0).setCellStyle(styleGray);
			row.getCell(1).setCellStyle(styleGray);
			row.getCell(2).setCellStyle(styleGray);

			for (MaterialPartPrelistEntity additionalOrder : listAdditionalOrder) {
				Integer quote_adjust = additionalOrder.getQuote_adjust();
				if (additionalOrder.getQuote_operator_id() != null) {
					if (quote_adjust > 0) {
						if (additionalOrder.getInline_job_no() != null) {
							writeLine(sheetAdd, ++idxAdd, additionalOrder.getCode(), quote_adjust, "Q", null);
						}
					} else if (quote_adjust < 0) {
						if (additionalOrder.getInline_job_no() != null) {
							writeLine(sheetCancel, ++idxCancel, additionalOrder.getCode(), quote_adjust, "Q", styleGray);
						}
					}
					continue;
				}
				if (additionalOrder.getInline_job_no() != null) {
					String belongs = "D";
					if (additionalOrder.getOrder_flg() == 2) {
						belongs = "N";
					} else if ("00000000013".equals(additionalOrder)) {
						belongs = "N";
					}
					if (additionalOrder.getRank() == 9) {
						// 在线追加
						writeLine(sheetAdd, ++idxAdd, additionalOrder.getCode(), additionalOrder.getQuantity(), belongs, null);
					} else {
						// 在线取消
						writeLine(sheetCancel, ++idxCancel, additionalOrder.getCode(), additionalOrder.getQuantity(), belongs, styleGray);
					}
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

	private void writeLine(XSSFSheet sheet, int idx, String code,
			Integer quantity, String belongs, XSSFCellStyle style) {
		XSSFRow row = sheet.createRow(idx);

		// code
		row.createCell(0).setCellValue(code);
		if (style != null) row.getCell(0).setCellStyle(style);

		// belongs
		row.createCell(1).setCellValue(quantity);
		if (style != null) row.getCell(1).setCellStyle(style);

		// belongs
		row.createCell(2).setCellValue(belongs);
		if (style != null) row.getCell(2).setCellStyle(style);
	}
}
