package com.osh.rvs.service.partial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.pda.PdaApplyElementEntity;
import com.osh.rvs.bean.pda.PdaApplyEntity;
import com.osh.rvs.common.ReverseResolution;
import com.osh.rvs.form.pda.PdaApplyDetailForm;
import com.osh.rvs.form.pda.PdaApplyElementForm;
import com.osh.rvs.form.pda.PdaApplyForm;
import com.osh.rvs.mapper.pda.PdaApplyMapper;

import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class PdaApplyService {

	private static final String DISP_FLG_HIDDEN_ELEM = "H1";
	private static final String DISP_FLG_HIDDEN_PART = "1";
	private static final String DISP_FLG_HIDDEN_GROUP = "G1";
	private static final String DISP_FLG_SHOW_GROUP = "G";

	/**
	 * 消耗品申请单一览
	 * 
	 * @param applyForm
	 * @param conn
	 * @throws Exception
	 */
	public void searchApplyList(PdaApplyForm applyForm, SqlSession conn) throws Exception {

		PdaApplyMapper dao = conn.getMapper(PdaApplyMapper.class);

		List<PdaApplyEntity> applyList = dao.searchApplyList();

		List<PdaApplyElementForm> lResultForm = new ArrayList<PdaApplyElementForm>();
		BeanUtil.copyToFormList(applyList, lResultForm, null, PdaApplyElementForm.class);

		applyForm.setApply_list(lResultForm);
		applyForm.setCount(String.valueOf(lResultForm.size()));
	}

	/**
	 * 消耗品申请单明细
	 * 
	 * @param applyForm
	 * @param conn
	 * @throws Exception
	 */
	public void searchApplyDetailList(PdaApplyDetailForm detailForm, SqlSession conn) throws Exception {

		PdaApplyMapper dao = conn.getMapper(PdaApplyMapper.class);

		List<PdaApplyEntity> detailList = dao.searchApplyDetailList(detailForm.getConsumable_application_key());

		List<PdaApplyElementForm> lResultForm = new ArrayList<PdaApplyElementForm>();
		BeanUtil.copyToFormList(detailList, lResultForm, null, PdaApplyElementForm.class);

		String apply_method_old = "";
		String type_old = "";
		// 控制按钮是否可用
		int disp_cnt = 0;
		// 待处理件数
		int count = 0;

		String currentPartialId = null;
		Map<String, Integer> partialCountMap = new HashMap<String, Integer>(); 
		int groupSupplyQuantity = 0, groupApplyQuantity = 0, groupWaittingQuantity = 0;
		Map<Integer, PdaApplyElementForm> groupFormMap = new TreeMap<Integer, PdaApplyElementForm>(); 

		for (int i = 0; i < lResultForm.size(); i++) {
			PdaApplyElementForm elementForm = lResultForm.get(i);
			elementForm.setType_name(CodeListUtils.getValue("consumable_type", elementForm.getType(), ""));

			String partialId = elementForm.getPartial_id();

			if (!partialId.equals(currentPartialId)) {
				if (currentPartialId != null) {
					if (partialCountMap.get(currentPartialId) > 1) { // 多人申请
						int hit = 0;
						PdaApplyElementForm gForm = new PdaApplyElementForm(); // 集体显示的Form
						gForm.setPartial_id(currentPartialId);
						if (groupWaittingQuantity == 0) {
							gForm.setDisp_flg(DISP_FLG_HIDDEN_GROUP);
						} else {
							gForm.setDisp_flg(DISP_FLG_SHOW_GROUP);
						}
						gForm.setSupply_quantity("" + groupSupplyQuantity);
						gForm.setDisp_supply_quantity("" + groupSupplyQuantity); // TODO V
						gForm.setDb_supply_quantity("" + (groupApplyQuantity - groupWaittingQuantity));
						gForm.setApply_quantity("" + groupApplyQuantity);
						gForm.setWaitting_quantity("" + groupWaittingQuantity);

						for (int ii = 0; ii < lResultForm.size(); ii++) {
							PdaApplyElementForm elementFormIn = lResultForm.get(ii);
							if (elementFormIn.getPartial_id().equals(currentPartialId)) {
								if (hit == 0) {
									gForm.setConsumable_application_key(elementFormIn.getConsumable_application_key());
									gForm.setCode(elementFormIn.getCode());
									gForm.setType(elementFormIn.getType());
									gForm.setType_name(elementFormIn.getType_name());
									gForm.setName(elementFormIn.getName());
									gForm.setStock_code(elementFormIn.getStock_code());
									gForm.setAvailable_inventory(elementFormIn.getAvailable_inventory());
									if ("1".equals(elementFormIn.getContent())) {
										gForm.setSupply_quantity("1");
										gForm.setDisp_supply_quantity("1"); // TODO V
										if (groupApplyQuantity == 0) {
											gForm.setWaitting_quantity("1");
										} else {
											gForm.setApply_quantity("1");
											gForm.setWaitting_quantity("0");
										}
									}
								}
								if (hit > partialCountMap.get(currentPartialId)) break;
								elementFormIn.setDisp_flg(DISP_FLG_HIDDEN_ELEM);
								hit++;
							}
						}
						groupFormMap.put(i, gForm);
					}
				}

				groupSupplyQuantity = 0; groupApplyQuantity = 0; groupWaittingQuantity = 0;
				partialCountMap.put(partialId, 0);
				currentPartialId = partialId;
			}
			partialCountMap.put(partialId, partialCountMap.get(partialId) + 1);

			int supply_quantity = Integer.parseInt(elementForm.getDb_supply_quantity());
			int apply_quantity = Integer.parseInt(elementForm.getApply_quantity());
			groupSupplyQuantity += supply_quantity; groupApplyQuantity += apply_quantity;
			// 待发放数量
			String waitting_quantity = ""; Integer iWaittingQuantity = 0;
			if (apply_quantity - supply_quantity <= 0) {
				waitting_quantity = "0";
			} else {
				iWaittingQuantity = apply_quantity - supply_quantity;
				waitting_quantity = String.valueOf(iWaittingQuantity);
			}
			groupWaittingQuantity += iWaittingQuantity;

			elementForm.setWaitting_quantity(waitting_quantity);
			// 申请单详细画面用已发放数量
			elementForm.setDisp_supply_quantity(elementForm.getDb_supply_quantity());
			elementForm.setSupply_quantity("0");

			// 申请数量 >提供数量的时候，这一行表示灰色
			if (apply_quantity <= supply_quantity) {
				elementForm.setDisp_flg(DISP_FLG_HIDDEN_PART);
				disp_cnt++;
			}
			if (supply_quantity == 0) {
				count++;
			}

			if (i == 0) {
				// 显示表格标题
				elementForm.setHeader_flg("1");
				apply_method_old = elementForm.getApply_method();
				type_old = getTypeGrp(apply_method_old, elementForm.getType());

				detailForm.setConsumable_application_key(elementForm.getConsumable_application_key());
				detailForm.setApplication_no(elementForm.getApplication_no());
				detailForm.setOmr_notifi_no(elementForm.getOmr_notifi_no());
				detailForm.setApply_reason(elementForm.getApply_reason());
				detailForm.setSection_id(elementForm.getSection_id());
				detailForm.setLine_id(elementForm.getLine_id());
				continue;
			}

			String apply_method = elementForm.getApply_method();
			// 补充方式不同的时候，显示表格标题
			if (!apply_method.equals(apply_method_old)) {
				elementForm.setHeader_flg("1");
				apply_method_old = apply_method;
				type_old = getTypeGrp(apply_method, elementForm.getType());
			} else if ("2".equals(apply_method)) {
				// 2:定时补充时，type为1~7的显示为一组，8显示一组，9显示一组
				String type = getTypeGrp(apply_method, elementForm.getType());
				if (!type.equals(type_old)) {
					elementForm.setHeader_flg("1");
					apply_method_old = apply_method;
					type_old = getTypeGrp(apply_method, elementForm.getType());
				}
			}
		}

		if (currentPartialId != null) {
			if (partialCountMap.get(currentPartialId) > 1) { // 多人申请
				int hit = 0;
				PdaApplyElementForm gForm = new PdaApplyElementForm(); // 集体显示的Form
				gForm.setPartial_id(currentPartialId);
				if (groupWaittingQuantity == 0) {
					gForm.setDisp_flg(DISP_FLG_HIDDEN_GROUP);
				} else {
					gForm.setDisp_flg(DISP_FLG_SHOW_GROUP);
				}
				gForm.setSupply_quantity("" + groupSupplyQuantity);
				gForm.setDisp_supply_quantity("" + groupSupplyQuantity);
				gForm.setDb_supply_quantity("" + (groupApplyQuantity - groupWaittingQuantity));
				gForm.setApply_quantity("" + groupApplyQuantity);
				gForm.setWaitting_quantity("" + groupWaittingQuantity);

				for (int ii = 0; ii < lResultForm.size(); ii++) {
					PdaApplyElementForm elementFormIn = lResultForm.get(ii);
					if (elementFormIn.getPartial_id().equals(currentPartialId)) {
						if (hit == 0) {
							gForm.setConsumable_application_key(elementFormIn.getConsumable_application_key());
							gForm.setCode(elementFormIn.getCode());
							gForm.setType(elementFormIn.getType());
							gForm.setType_name(elementFormIn.getType_name());
							gForm.setName(elementFormIn.getName());
							gForm.setStock_code(elementFormIn.getStock_code());
							gForm.setAvailable_inventory(elementFormIn.getAvailable_inventory());

							if ("1".equals(elementFormIn.getContent())) {
								gForm.setSupply_quantity("1");
								gForm.setDisp_supply_quantity("1"); // TODO V
								if (groupApplyQuantity == 0) {
									gForm.setWaitting_quantity("1");
								} else {
									gForm.setApply_quantity("1");
									gForm.setWaitting_quantity("0");
								}
							}
						}
						if (hit > partialCountMap.get(currentPartialId)) break;
						elementFormIn.setDisp_flg(DISP_FLG_HIDDEN_ELEM);
						hit++;
					}
				}
				groupFormMap.put(lResultForm.size(), gForm);
			}
		}

		// 加入整体显示
		int expandIdx = 0;
		for (int orgIdx : groupFormMap.keySet()) {
			lResultForm.add(orgIdx + expandIdx, groupFormMap.get(orgIdx));
			expandIdx ++;
		}

		if (disp_cnt == lResultForm.size()) {
			// all的时候，画面上按钮不可以用
			detailForm.setBtn_flg("all");
		} else {
			detailForm.setBtn_flg("");
		}
		detailForm.setDetail_list(lResultForm);
		detailForm.setCount(String.valueOf(count));
	}

	/**
	 * 补充方式为2:定时补充的时候
	 * type为1~7的显示为一组，8显示一组，9显示一组
	 * 
	 * @param apply_method
	 * @param type
	 * @throws Exception
	 */
	private String getTypeGrp(String apply_method, String type) {
		String typeGrp = type;
		if ("2".equals(apply_method)) {
			if (!"8".equals(type) && !"9".equals(type)) {
				typeGrp = "1";
			}
		}
		return typeGrp;
	}

	/**
	 * 消耗品申请单发放画面
	 * 
	 * @param applyForm
	 * @param conn
	 * @throws Exception
	 */
	public PdaApplyElementForm getApplyElementDetail(ActionForm form,HttpServletRequest req,SqlSession conn){
		PdaApplyElementEntity entity = new PdaApplyElementEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//获取消耗品ID
		entity.setPartial_id(ReverseResolution.getPartialByCode(entity.getCode(), conn));

		PdaApplyMapper pdaApplyMapper = conn.getMapper(PdaApplyMapper.class);
		PdaApplyElementEntity pdaApplyElementEntity  = pdaApplyMapper.getApplyElementDetail(entity);

		PdaApplyElementForm pdaApplyElementForm = new PdaApplyElementForm();

		if(pdaApplyElementEntity!=null){
			BeanUtil.copyToForm(pdaApplyElementEntity, pdaApplyElementForm, CopyOptions.COPYOPTIONS_NOEMPTY);
			
			String type = pdaApplyElementForm.getType();
			pdaApplyElementForm.setType_name(CodeListUtils.getValue("consumable_type", type, ""));
		}

		// 待发放数取得
		PdaApplyDetailForm detailForm  = (PdaApplyDetailForm)req.getSession().getAttribute("pdaApplyDetailForm");

		pdaApplyElementForm.setCount(detailForm.getCount());
		return pdaApplyElementForm;
	}

	/**
	 * 消耗品发放（Session对象内）
	 * @param form
	 * @param request
	 */
	public void updateSupply(ActionForm form,HttpServletRequest request){
		PdaApplyElementForm elementForm = (PdaApplyElementForm) form;
		String key = elementForm.getConsumable_application_key();//key
		String partial_id = elementForm.getPartial_id();//消耗品ID
		String content = elementForm.getContent();//内容量
//		String pack_method = elementForm.getPack_method();//包装
		String supply_quantity = elementForm.getSupply_quantity();
		boolean firstHitForOpenPack = false;
		String open_flg = elementForm.getOpen_flg();
		
		PdaApplyDetailForm detailForm  = (PdaApplyDetailForm)request.getSession().getAttribute("pdaApplyDetailForm");

		// 控制按钮是否可用
		int disp_cnt = 0;
		List<PdaApplyElementForm> lResultForm  = detailForm.getDetail_list();

		for(PdaApplyElementForm pdaApplyElementForm:lResultForm){
			String tempKey = pdaApplyElementForm.getConsumable_application_key();
			String tempPartialId = pdaApplyElementForm.getPartial_id();
			
			String dispFlg = pdaApplyElementForm.getDisp_flg();
			boolean isGroup = isGroupLine(dispFlg);

			if(tempKey.equals(key) && tempPartialId.equals(partial_id)){ // 定位消耗品 ID
				pdaApplyElementForm.setOpen_flg(open_flg);
				if("1".equals(content)){
					if("A".equals(open_flg)){
						pdaApplyElementForm.setSupply_quantity("0");
					}else if("B".equals(open_flg) || "C".equals(open_flg)){
						if (!firstHitForOpenPack || isGroup) {
							pdaApplyElementForm.setSupply_quantity("1");
							firstHitForOpenPack = true;
							if (isGroup) {
								switchDisplay(pdaApplyElementForm);
							}
						} else {
							pdaApplyElementForm.setSupply_quantity("0");
						}
					}
//				}else if("0".equals(pack_method)){
//					pdaApplyElementForm.setSupply_quantity(supply_quantity + "");
				}else{
					Integer iSupplyQuantity = Integer.parseInt(supply_quantity);
					if (isGroup) {
						pdaApplyElementForm.setSupply_quantity(supply_quantity + "");
						pdaApplyElementForm.setDisp_supply_quantity(supply_quantity + ""); // TODO ?
					} else {

						// 申请数量
						int iNeedQuantity = Integer.parseInt(pdaApplyElementForm.getApply_quantity());

						if (iSupplyQuantity < iNeedQuantity) {
							pdaApplyElementForm.setSupply_quantity(iSupplyQuantity + "");
							iSupplyQuantity = 0;
						} else {
							pdaApplyElementForm.setSupply_quantity(iNeedQuantity + "");
							iSupplyQuantity -= iNeedQuantity;
						}
					}
				}
			}

			// 申请单发放画面用发放数量
			int upd_supply_quantity = Integer.parseInt(pdaApplyElementForm.getSupply_quantity());
			// DB已发放数量
			int db_supply_quantity = Integer.parseInt(pdaApplyElementForm.getDb_supply_quantity());
			// 申请单详细画面用已发放数量
			int disp_supply_quantity = db_supply_quantity + upd_supply_quantity;
			pdaApplyElementForm.setDisp_supply_quantity(String.valueOf(disp_supply_quantity));

			// 申请数量 <= 提供数量的时候，这一行表示灰色
			int apply_quantity = Integer.parseInt(pdaApplyElementForm.getApply_quantity()); 
			if (apply_quantity <= disp_supply_quantity) {
				if (DISP_FLG_HIDDEN_ELEM.equals(pdaApplyElementForm.getDisp_flg())) {
				} else {
					pdaApplyElementForm.setDisp_flg(DISP_FLG_HIDDEN_PART);
					if (isGroup) {
						pdaApplyElementForm.setDisp_flg(DISP_FLG_HIDDEN_GROUP);
						continue;
					}
				}
				disp_cnt++;
			}		
		}

		if (disp_cnt == lResultForm.size()) {
			// all的时候，画面上按钮不可以用
			detailForm.setBtn_flg("all");
		} else {
			detailForm.setBtn_flg("");
		}
		// code内容清空
		detailForm.setCode("");

		request.getSession().setAttribute("pdaApplyDetailForm", detailForm);
		request.setAttribute("pdaApplyDetailForm", detailForm);
	}

	public void switchDisplay(PdaApplyElementForm elementForm) {
		String dispFlg = elementForm.getDisp_flg();
		if (DISP_FLG_HIDDEN_PART.equals(dispFlg)) {
			elementForm.setDisp_flg("0");
		} else if (DISP_FLG_SHOW_GROUP.equals(dispFlg)) {
			elementForm.setDisp_flg(DISP_FLG_HIDDEN_GROUP);
		} else if (DISP_FLG_HIDDEN_GROUP.equals(dispFlg)) {
			elementForm.setDisp_flg(DISP_FLG_SHOW_GROUP);
		} else if (!DISP_FLG_HIDDEN_ELEM.equals(dispFlg)){
			elementForm.setDisp_flg(DISP_FLG_HIDDEN_PART);
		}
	}

	public boolean isGroupLine(String dispFlg) {
		return dispFlg != null && (DISP_FLG_HIDDEN_GROUP.equals(dispFlg) || DISP_FLG_SHOW_GROUP.equals(dispFlg));
	}

	public boolean showDisplay(PdaApplyElementForm elementForm) {
		boolean pass = false;
		String dispFlg = elementForm.getDisp_flg();
		if (DISP_FLG_HIDDEN_PART.equals(dispFlg)) {
			elementForm.setDisp_flg("0");
		} else if (DISP_FLG_HIDDEN_GROUP.equals(dispFlg)) {
			elementForm.setDisp_flg(DISP_FLG_SHOW_GROUP);
		} else if (DISP_FLG_HIDDEN_ELEM.equals(dispFlg)){
			pass = true;
		}
		return pass;
	}
}
