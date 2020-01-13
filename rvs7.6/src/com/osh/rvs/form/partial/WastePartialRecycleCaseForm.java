package com.osh.rvs.form.partial;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 废弃零件回收箱
 * 
 * @Description
 * @author dell
 * @date 2019-12-26 下午4:33:28
 */
public class WastePartialRecycleCaseForm extends ActionForm implements Serializable {
	private static final long serialVersionUID = 6824033703261392181L;

	@BeanField(title = "回收箱 ID", name = "case_id", type = FieldType.String, length = 11, notNull = true)
	private String case_id;

	@BeanField(title = "装箱编号", name = "case_code", type = FieldType.String, length = 6, notNull = true)
	private String case_code;

	@BeanField(title = "回收箱用途种类", name = "collect_kind", type = FieldType.Integer, length = 1, notNull = true)
	private String collect_kind;

	@BeanField(title = "备注说明", name = "comment", type = FieldType.String, length = 45)
	private String comment;

	@BeanField(title = "打包日期", name = "package_date", type = FieldType.Date)
	private String package_date;

	@BeanField(title = "重量", name = "weight", type = FieldType.UDouble, length = 5, scale = 1)
	private String weight;

	@BeanField(title = "废弃申请日期", name = "waste_apply_date", type = FieldType.Date)
	private String waste_apply_date;

	@BeanField(title = "废弃标记", name = "waste_flg", type = FieldType.Integer)
	private String waste_flg;

	@BeanField(title = "打包标记", name = "package_flg", type = FieldType.Integer)
	private String package_flg;

	@BeanField(title = "打包开始日期", name = "package_date_start", type = FieldType.Date)
	private String package_date_start;

	@BeanField(title = "打包结束日期", name = "package_date_end", type = FieldType.Date)
	private String package_date_end;

	@BeanField(title = "废弃申请日期开始", name = "waste_apply_date_start", type = FieldType.Date)
	private String waste_apply_date_start;

	@BeanField(title = "废弃申请日期结束", name = "waste_apply_date_end", type = FieldType.Date)
	private String waste_apply_date_end;

	public String getCase_id() {
		return case_id;
	}

	public void setCase_id(String case_id) {
		this.case_id = case_id;
	}

	public String getCase_code() {
		return case_code;
	}

	public void setCase_code(String case_code) {
		this.case_code = case_code;
	}

	public String getCollect_kind() {
		return collect_kind;
	}

	public void setCollect_kind(String collect_kind) {
		this.collect_kind = collect_kind;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getPackage_date() {
		return package_date;
	}

	public void setPackage_date(String package_date) {
		this.package_date = package_date;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getWaste_apply_date() {
		return waste_apply_date;
	}

	public void setWaste_apply_date(String waste_apply_date) {
		this.waste_apply_date = waste_apply_date;
	}

	public String getPackage_flg() {
		return package_flg;
	}

	public void setPackage_flg(String package_flg) {
		this.package_flg = package_flg;
	}

	public String getWaste_flg() {
		return waste_flg;
	}

	public void setWaste_flg(String waste_flg) {
		this.waste_flg = waste_flg;
	}

	public String getPackage_date_start() {
		return package_date_start;
	}

	public void setPackage_date_start(String package_date_start) {
		this.package_date_start = package_date_start;
	}

	public String getPackage_date_end() {
		return package_date_end;
	}

	public void setPackage_date_end(String package_date_end) {
		this.package_date_end = package_date_end;
	}

	public String getWaste_apply_date_start() {
		return waste_apply_date_start;
	}

	public void setWaste_apply_date_start(String waste_apply_date_start) {
		this.waste_apply_date_start = waste_apply_date_start;
	}

	public String getWaste_apply_date_end() {
		return waste_apply_date_end;
	}

	public void setWaste_apply_date_end(String waste_apply_date_end) {
		this.waste_apply_date_end = waste_apply_date_end;
	}

}
