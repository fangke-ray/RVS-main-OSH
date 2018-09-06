package com.osh.rvs.form.qf;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 通箱库位
 * 
 * @author gonglm
 */
public class TurnoverCaseForm extends ActionForm implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -3025677849786124066L;

	@BeanField(title = "KEY", name = "key", length = 5, notNull = true)
	private String key;
	@BeanField(title = "货架", name = "shelf", length = 3, notNull = true)
	private String shelf;
	@BeanField(title = "位置", name = "location", length = 6, notNull = true)
	private String location;
	@BeanField(title = "层", name = "layer", length = 2)
	private String layer;
	@BeanField(title = "维修对象ID", name = "material_id", length = 11)
	private String material_id;// 维修对象ID
	@BeanField(title = "存入时间", name = "storage_time", type = FieldType.DateTime)
	private String storage_time;
	@BeanField(title = "存入时间起始", name = "storage_time_start", type = FieldType.Date)
	private String storage_time_start;
	@BeanField(title = "存入时间截止", name = "storage_time_end", type = FieldType.Date)
	private String storage_time_end;
	@BeanField(title = "存入执行", name = "execute", type = FieldType.Integer, length = 1)
	private String execute;

	@BeanField(title = "修理单号", name = "omr_notifi_no")
	private String omr_notifi_no;
	@BeanField(title = "型号ID", name = "model_id")
	private String model_id;
	@BeanField(title = "型号名", name = "model_name")
	private String model_name;
	@BeanField(title = "机身号", name = "serial_no")
	private String serial_no;
	@BeanField(title = "维修等级", name = "level", type = FieldType.Integer, length = 2)
	private String level;
	@BeanField(title = "发送地", name = "bound_out_ocm", type = FieldType.Integer, length = 2)
	private String bound_out_ocm;
	@BeanField(title = "直送标记", name = "direct_flg", type = FieldType.Integer, length = 1)
	private String direct_flg;
	@BeanField(title = "维修状态", name = "break_back_flg", type = FieldType.Integer, length = 1)
	private String break_back_flg;

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getShelf() {
		return shelf;
	}

	public void setShelf(String shelf) {
		this.shelf = shelf;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getStorage_time() {
		return storage_time;
	}

	public void setStorage_time(String storage_time) {
		this.storage_time = storage_time;
	}

	public String getModel_id() {
		return model_id;
	}

	public void setModel_id(String model_id) {
		this.model_id = model_id;
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

	public String getBound_out_ocm() {
		return bound_out_ocm;
	}

	public void setBound_out_ocm(String bound_out_ocm) {
		this.bound_out_ocm = bound_out_ocm;
	}

	public String getStorage_time_start() {
		return storage_time_start;
	}

	public void setStorage_time_start(String storage_time_start) {
		this.storage_time_start = storage_time_start;
	}

	public String getStorage_time_end() {
		return storage_time_end;
	}

	public void setStorage_time_end(String storage_time_end) {
		this.storage_time_end = storage_time_end;
	}

	public String getExecute() {
		return execute;
	}

	public void setExecute(String execute) {
		this.execute = execute;
	}

	public String getDirect_flg() {
		return direct_flg;
	}

	public void setDirect_flg(String direct_flg) {
		this.direct_flg = direct_flg;
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

	public String getLayer() {
		return layer;
	}

	public void setLayer(String layer) {
		this.layer = layer;
	}

	public String getBreak_back_flg() {
		return break_back_flg;
	}

	public void setBreak_back_flg(String break_back_flg) {
		this.break_back_flg = break_back_flg;
	}

}
