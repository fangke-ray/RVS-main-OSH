package com.osh.rvs.bean.inline;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class DisassembleStorageEntity implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -5668786925850419951L;

	private String position_id;// 所属工位
	private String case_code;// 分解库位编号
	private String material_id;// 存放对象
	private Date refresh_time;// 放入时间
	private Integer auto_arrange;// 自动分配
	private Integer layer;// 层数
	private String shelf;

	private String omr_notifi_no;
	
	private Integer level;

	private String category_id;// 维修对象机种ID

	private String category_name;// 维修对象机种Name

	private String serial_no;// 机身号

	private String process_code;

	private List<String> positions;

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

	public Date getRefresh_time() {
		return refresh_time;
	}

	public void setRefresh_time(Date refresh_time) {
		this.refresh_time = refresh_time;
	}

	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = position_id;
	}

	public Integer getAuto_arrange() {
		return auto_arrange;
	}

	public void setAuto_arrange(Integer auto_arrange) {
		this.auto_arrange = auto_arrange;
	}

	public Integer getLayer() {
		return layer;
	}

	public void setLayer(Integer layer) {
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

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
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

	public List<String> getPositions() {
		return positions;
	}

	public void setPositions(List<String> positions) {
		this.positions = positions;
	}
}
