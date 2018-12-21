package com.osh.rvs.bean.inline;

import java.io.Serializable;
import java.util.Date;

import framework.huiqing.common.util.CodeListUtils;

public class MaterialFactEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4293728084051532091L;

	private String material_id;
	private String sorc_no;
	private String model_id;
	private Integer level;
	private String serial_no;
	
	private String levelName;
	private String model_name;
	private String section_name;
	
	private Integer direct_flg;
	private Integer fix_type;
	private String esas_no;
	private String wip_location;
	
	private String agreed_date_start;
	private String agreed_date_end;
	private Date inline_time;
	
	private String img_check; //画面检查
	private String ccd_change;//CCD盖玻璃更换
	private String img_operate_result;//存在画面检查
	private String ccd_operate_result;//存在CCD盖玻璃更换
	private Date agreed_date;
	private Date scheduled_date;
	private String section_id;
	private String pat_id;

	private Integer category_kind;
	private String category_name;
	private Integer unrepair_flg;
	private Integer scheduled_expedited;
	private Integer service_repair_flg;

	private Integer quotation_first;

	public String getSection_id() {
		return section_id;
	}
	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}
	public Date getAgreed_date() {
		return agreed_date;
	}
	public void setAgreed_date(Date agreed_date) {
		this.agreed_date = agreed_date;
	}
	public String getImg_check() {
		return img_check;
	}
	public void setImg_check(String img_check) {
		this.img_check = img_check;
	}
	public String getCcd_change() {
		return ccd_change;
	}
	public void setCcd_change(String ccd_change) {
		this.ccd_change = ccd_change;
	}
	public String getImg_operate_result() {
		return CodeListUtils.getValue("material_operate_result", img_operate_result);
	}
	public void setImg_operate_result(String img_operate_result) {
		this.img_operate_result = img_operate_result;
	}
	public String getCcd_operate_result() {
		return this.ccd_operate_result;
	}
	public void setCcd_operate_result(String ccd_operate_result) {
		this.ccd_operate_result = ccd_operate_result;
	}
	public Date getInline_time() {
		return inline_time;
	}
	public void setInline_time(Date inline_time) {
		this.inline_time = inline_time;
	}
	public String getSorc_no() {
		return sorc_no;
	}
	public void setSorc_no(String sorc_no) {
		this.sorc_no = sorc_no;
	}
	public String getMaterial_id() {
		return material_id;
	}
	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}
	public String getModel_id() {
		return model_id;
	}
	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public String getSerial_no() {
		return serial_no;
	}
	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}
	public String getLevelName() {
		return levelName;
	}
	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}
	public String getModel_name() {
		return model_name;
	}
	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}
	public Integer getDirect_flg() {
		return direct_flg;
	}
	public void setDirect_flg(Integer direct_flg) {
		this.direct_flg = direct_flg;
	}
	public Integer getFix_type() {
		return fix_type;
	}
	public void setFix_type(Integer fix_type) {
		this.fix_type = fix_type;
	}
	public String getEsas_no() {
		return esas_no;
	}
	public void setEsas_no(String esas_no) {
		this.esas_no = esas_no;
	}
	public String getWip_location() {
		return wip_location;
	}
	public void setWip_location(String wip_location) {
		this.wip_location = wip_location;
	}
	public String getAgreed_date_start() {
		return agreed_date_start;
	}
	public void setAgreed_date_start(String agreed_date_start) {
		this.agreed_date_start = agreed_date_start;
	}
	public String getAgreed_date_end() {
		return agreed_date_end;
	}
	public void setAgreed_date_end(String agreed_date_end) {
		this.agreed_date_end = agreed_date_end;
	}
	/**
	 * @return the pat_id
	 */
	public String getPat_id() {
		return pat_id;
	}
	/**
	 * @param pat_id the pat_id to set
	 */
	public void setPat_id(String pat_id) {
		this.pat_id = pat_id;
	}
	/**
	 * @return the category_kind
	 */
	public Integer getCategory_kind() {
		return category_kind;
	}
	/**
	 * @param category_kind the category_kind to set
	 */
	public void setCategory_kind(Integer category_kind) {
		this.category_kind = category_kind;
	}
	/**
	 * @return the unrepair_flg
	 */
	public Integer getUnrepair_flg() {
		return unrepair_flg;
	}
	/**
	 * @param unrepair_flg the unrepair_flg to set
	 */
	public void setUnrepair_flg(Integer unrepair_flg) {
		this.unrepair_flg = unrepair_flg;
	}
	/**
	 * @return the scheduled_expedited
	 */
	public Integer getScheduled_expedited() {
		return scheduled_expedited;
	}
	/**
	 * @param scheduled_expedited the scheduled_expedited to set
	 */
	public void setScheduled_expedited(Integer scheduled_expedited) {
		this.scheduled_expedited = scheduled_expedited;
	}
	/**
	 * @return the scheduled_date
	 */
	public Date getScheduled_date() {
		return scheduled_date;
	}
	/**
	 * @param scheduled_date the scheduled_date to set
	 */
	public void setScheduled_date(Date scheduled_date) {
		this.scheduled_date = scheduled_date;
	}
	public String getSection_name() {
		return section_name;
	}
	public void setSection_name(String section_name) {
		this.section_name = section_name;
	}
	public Integer getQuotation_first() {
		return quotation_first;
	}
	public void setQuotation_first(Integer quotation_first) {
		this.quotation_first = quotation_first;
	}
	public Integer getService_repair_flg() {
		return service_repair_flg;
	}
	public void setService_repair_flg(Integer service_repair_flg) {
		this.service_repair_flg = service_repair_flg;
	}
	public String getCategory_name() {
		return category_name;
	}
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}
	
	
}
