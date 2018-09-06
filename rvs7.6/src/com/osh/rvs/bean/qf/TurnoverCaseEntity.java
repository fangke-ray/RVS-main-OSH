package com.osh.rvs.bean.qf;

import java.io.Serializable;
import java.util.Date;

/**
 * 通箱库位
 * 
 * @author gonglm
 */
public class TurnoverCaseEntity implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -8537604773684950586L;
	private String key;
	private String shelf;
	private String location;
	private String material_id;// 维修对象ID
	private Date storage_time;
	private Date storage_time_start;
	private Date storage_time_end;
	private Integer execute;
	private Integer layer;

	private String model_id;
	private String model_name;
	private String serial_no;
	private Integer level;
	private Integer bound_out_ocm;
	private Integer direct_flg;
	private String omr_notifi_no;
	private Integer break_back_flg;

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

	public Date getStorage_time() {
		return storage_time;
	}

	public void setStorage_time(Date storage_time) {
		this.storage_time = storage_time;
	}

	public Date getStorage_time_start() {
		return storage_time_start;
	}

	public void setStorage_time_start(Date storage_time_start) {
		this.storage_time_start = storage_time_start;
	}

	public Date getStorage_time_end() {
		return storage_time_end;
	}

	public void setStorage_time_end(Date storage_time_end) {
		this.storage_time_end = storage_time_end;
	}

	public Integer getExecute() {
		return execute;
	}

	public void setExecute(Integer execute) {
		this.execute = execute;
	}

	public Integer getBound_out_ocm() {
		return bound_out_ocm;
	}

	public void setBound_out_ocm(Integer bound_out_ocm) {
		this.bound_out_ocm = bound_out_ocm;
	}

	public Integer getDirect_flg() {
		return direct_flg;
	}

	public void setDirect_flg(Integer direct_flg) {
		this.direct_flg = direct_flg;
	}

	public String getOmr_notifi_no() {
		return omr_notifi_no;
	}

	public void setOmr_notifi_no(String omr_notifi_no) {
		this.omr_notifi_no = omr_notifi_no;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getLayer() {
		return layer;
	}

	public void setLayer(Integer layer) {
		this.layer = layer;
	}

	public Integer getBreak_back_flg() {
		return break_back_flg;
	}

	public void setBreak_back_flg(Integer break_back_flg) {
		this.break_back_flg = break_back_flg;
	}

}
