package com.osh.rvs.form.partial;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;
import framework.huiqing.common.util.CodeListUtils;

/**
 * 废弃零件整理
 * 
 * @Description
 * @author dell
 * @date 2019-12-26 下午3:28:45
 */
public class WastePartialArrangementForm extends ActionForm implements Serializable {
	private static final long serialVersionUID = -8438041954146446797L;

	@BeanField(title = "维修品ID", name = "material_id", type = FieldType.String, length = 11, notNull = true)
	private String material_id;

	@BeanField(title = "回收部分", name = "part", type = FieldType.Integer, length = 1, notNull = true)
	private String part;

	@BeanField(title = "收集操作者ID", name = "operator_id", type = FieldType.String, length = 11, notNull = true)
	private String operator_id;

	@BeanField(title = "收集时间", name = "collect_time", type = FieldType.DateTime, notNull = true)
	private String collect_time;

	@BeanField(title = "废弃零件回收箱ID", name = "collect_case_id", type = FieldType.String, length = 11, notNull = true)
	private String collect_case_id;

	@BeanField(title = "回收箱用途种类", name = "collect_kind", type = FieldType.Integer, length = 1, notNull = true)
	private String collect_kind;

	@BeanField(title = "收集开始时间", name = "collect_time_start", type = FieldType.Date)
	private String collect_time_start;

	@BeanField(title = "收集结束时间", name = "collect_time_end", type = FieldType.Date)
	private String collect_time_end;

	@BeanField(title = "修理单号", name = "omr_notifi_no", type = FieldType.String)
	private String omr_notifi_no;

	@BeanField(title = "型号名称", name = "model_name", type = FieldType.String)
	private String model_name;

	@BeanField(title = "机身号", name = "serial_no", type = FieldType.String)
	private String serial_no;

	@BeanField(title = "等级", name = "level", type = FieldType.Integer)
	private String level;

	@BeanField(title = "返修分类", name = "service_repair_flg", type = FieldType.Integer)
	private String service_repair_flg;

	@BeanField(title = "装箱编号", name = "case_code", type = FieldType.String)
	private String case_code;

	@BeanField(title = "收集操作者名称", name = "operator_name", type = FieldType.String)
	private String operator_name;

	@BeanField(title = "型号ID", name = "model_id", type = FieldType.String)
	private String model_id;

	private String levelName;

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public String getPart() {
		return part;
	}

	public void setPart(String part) {
		this.part = part;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public String getCollect_time() {
		return collect_time;
	}

	public void setCollect_time(String collect_time) {
		this.collect_time = collect_time;
	}

	public String getCollect_case_id() {
		return collect_case_id;
	}

	public void setCollect_case_id(String collect_case_id) {
		this.collect_case_id = collect_case_id;
	}

	public String getCollect_kind() {
		return collect_kind;
	}

	public void setCollect_kind(String collect_kind) {
		this.collect_kind = collect_kind;
	}

	public String getCollect_time_start() {
		return collect_time_start;
	}

	public void setCollect_time_start(String collect_time_start) {
		this.collect_time_start = collect_time_start;
	}

	public String getCollect_time_end() {
		return collect_time_end;
	}

	public void setCollect_time_end(String collect_time_end) {
		this.collect_time_end = collect_time_end;
	}

	public String getOmr_notifi_no() {
		return omr_notifi_no;
	}

	public void setOmr_notifi_no(String omr_notifi_no) {
		this.omr_notifi_no = omr_notifi_no;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getSerial_no() {
		return serial_no;
	}

	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getService_repair_flg() {
		return service_repair_flg;
	}

	public void setService_repair_flg(String service_repair_flg) {
		this.service_repair_flg = service_repair_flg;
	}

	public String getLevelName() {
		if (level != null) {
			return CodeListUtils.getValue("material_level", level);
		}
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public String getCase_code() {
		return case_code;
	}

	public void setCase_code(String case_code) {
		this.case_code = case_code;
	}

	public String getOperator_name() {
		return operator_name;
	}

	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}

	public String getModel_id() {
		return model_id;
	}

	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}

}
