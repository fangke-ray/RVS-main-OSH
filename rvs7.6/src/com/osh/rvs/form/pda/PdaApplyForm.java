package com.osh.rvs.form.pda;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts.action.ActionForm;

/**
 * 
 * @Title PdaApplyForm.java
 * @Project rvs
 * @Package com.osh.rvs.form.pda
 * @ClassName: PdaApplyForm
 * @Description: 消耗品入库
 * @author lxb
 * @date 2015-5-29 上午10:38:46
 */
public class PdaApplyForm extends ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3235278009914806011L;

	/** 消耗品申请单Key **/
	private String consumable_application_key;

	/** 一览待处理 件数 **/
	private String count;

	/** 消耗品申请单一览 **/
	private List<PdaApplyElementForm> apply_list = new ArrayList<PdaApplyElementForm>();

	public String getConsumable_application_key() {
		return consumable_application_key;
	}

	public void setConsumable_application_key(String consumable_application_key) {
		this.consumable_application_key = consumable_application_key;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public List<PdaApplyElementForm> getApply_list() {
		return apply_list;
	}

	public void addApply_list(PdaApplyElementForm applyForm) {
		this.apply_list.add(applyForm);
	}
	
	public void setApply_list(List<PdaApplyElementForm> apply_list) {
		this.apply_list = apply_list;
	}
}
