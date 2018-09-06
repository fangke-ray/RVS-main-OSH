package com.osh.rvs.form.pda;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts.action.ActionForm;

/**
 * 
 * @Title PdaApplyDetailForm.java
 * @Project rvs
 * @Package com.osh.rvs.form.pda
 * @ClassName: PdaApplyDetailForm
 * @Description: 消耗品入库
 * @author lxb
 * @date 2015-5-29 上午10:38:46
 */
public class PdaApplyDetailForm extends ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2673618876075994740L;


	/** 消耗品申请单Key **/
	private String consumable_application_key;

	/** 零件编码 **/
	private String code;

	/** 申请单编号**/
	private String application_no;

	/** 课室ID **/
	private String section_id;

	/** 工程ID **/
	private String line_id;

	/** SAP修理通知单No**/
	private String omr_notifi_no;

	/** 理由 **/
	private String apply_reason;

	/** 发放完成标记 **/
	private String supplied_flg;

	/** btn标记 **/
	private String btn_flg;

	/** 明细待处理 件数 **/
	private String count;

	/** 消耗品申请单明细 **/
	private List<PdaApplyElementForm> detail_list = new ArrayList<PdaApplyElementForm>();
	
	private String scrollTop;

	public String getConsumable_application_key() {
		return consumable_application_key;
	}

	public void setConsumable_application_key(String consumable_application_key) {
		this.consumable_application_key = consumable_application_key;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getApplication_no() {
		return application_no;
	}

	public void setApplication_no(String application_no) {
		this.application_no = application_no;
	}

	public String getSection_id() {
		return section_id;
	}

	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}

	public String getLine_id() {
		return line_id;
	}

	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}

	public String getOmr_notifi_no() {
		return omr_notifi_no;
	}

	public void setOmr_notifi_no(String omr_notifi_no) {
		this.omr_notifi_no = omr_notifi_no;
	}

	public String getApply_reason() {
		return apply_reason;
	}

	public void setApply_reason(String apply_reason) {
		this.apply_reason = apply_reason;
	}

	public String getSupplied_flg() {
		return supplied_flg;
	}

	public void setSupplied_flg(String supplied_flg) {
		this.supplied_flg = supplied_flg;
	}

	public String getBtn_flg() {
		return btn_flg;
	}

	public void setBtn_flg(String btn_flg) {
		this.btn_flg = btn_flg;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public List<PdaApplyElementForm> getDetail_list() {
		return detail_list;
	}

	public void addApply_list(PdaApplyElementForm detailForm) {
		this.detail_list.add(detailForm);
	}

	public void setDetail_list(List<PdaApplyElementForm> detail_list) {
		this.detail_list = detail_list;
	}

	public String getScrollTop() {
		return scrollTop;
	}

	public void setScrollTop(String scrollTop) {
		this.scrollTop = scrollTop;
	}
	
	
}
