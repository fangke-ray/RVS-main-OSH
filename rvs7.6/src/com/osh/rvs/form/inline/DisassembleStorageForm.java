package com.osh.rvs.form.inline;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class DisassembleStorageForm extends ActionForm implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7000342492066022673L;

	@BeanField(title = "所属工位", name = "position_id", length = 11, notNull = true)
	private String position_id;
	@BeanField(title = "分解库位编号", name = "case_code", length = 4, notNull = true)
	private String case_code;
	@BeanField(title = "存放对象", name = "material_id", length = 11)
	private String material_id;
	@BeanField(title = "放入时间", name = "refresh_time", type=FieldType.DateTime)
	private String refresh_time;
	@BeanField(title = "自动分配", name = "auto_arrange", type=FieldType.Integer, length = 1, notNull = true)
	private String auto_arrange;
	@BeanField(title = "层数", name = "layer", type=FieldType.Integer, length = 2)
	private String layer;
	@BeanField(title = "货架", name = "shelf", type=FieldType.String, length = 1)
	private String shelf;

	@BeanField(title = "修理单号", name = "omr_notifi_no", type=FieldType.String)
	private String omr_notifi_no;
	
	@BeanField(title = "修理等级", name = "level", type=FieldType.Integer)
	private String level;

	@BeanField(title = "维修对象机种ID", name = "category_id")
	private String category_id;// 维修对象机种ID

	@BeanField(title = "维修对象机种Name", name = "category_name")
	private String category_name;// 维修对象机种Name

	@BeanField(title = "机身号", name = "serial_no")
	private String serial_no;// 机身号

	/** 进度代码 */
	@BeanField(title = "进度代码", name = "process_code", length = 3)
	private String process_code;


	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public String getCase_code() {
		return case_code;
	}

	public void setCase_code(String case_code) {
		this.case_code = case_code;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public String getAuto_arrange() {
		return auto_arrange;
	}

	public void setAuto_arrange(String auto_arrange) {
		this.auto_arrange = auto_arrange;
	}

	public String getRefresh_time() {
		return refresh_time;
	}

	public void setRefresh_time(String refresh_time) {
		this.refresh_time = refresh_time;
	}

	public String getLayer() {
		return layer;
	}

	public void setLayer(String layer) {
		this.layer = layer;
	}

	public String getShelf() {
		return shelf;
	}

	public void setShelf(String shelf) {
		this.shelf = shelf;
	}

	public String getOmr_notifi_no() {
		return omr_notifi_no;
	}

	public void setOmr_notifi_no(String omr_notifi_no) {
		this.omr_notifi_no = omr_notifi_no;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public String getSerial_no() {
		return serial_no;
	}

	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}
}
