package com.osh.rvs.form.qf;

import static framework.huiqing.common.util.CommonStringUtil.isEmpty;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;

public class MaterialFactForm extends ActionForm {

	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private static final SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
	/**
	 * 
	 */
	private static final long serialVersionUID = 4609121699995164795L;

	private String sorc_no;
	
	@BeanField(title = "维修对象ID", name = "material_id", type = FieldType.String)
	private String material_id;
	@BeanField(title = "维修对象型号 ID", name = "model_id", type = FieldType.String)
	private String model_id;
	@BeanField(title = "等级", name = "level", type = FieldType.Integer)
	private String level;
	private String serial_no;
	
	private String levelName;
	private String model_name;
	private String section_name;

	@BeanField(title = "直送", name = "direct_flg", type = FieldType.Integer)
	private String direct_flg;
	@BeanField(title = "流水线分类", name = "fix_type", type = FieldType.Integer)
	private String fix_type;
	private String esas_no;
	private String wip_location;
	
	private String agreed_date_start;
	private String agreed_date_end;
	
	@BeanField(title = "同意时间", name = "agreed_date", type = FieldType.Date)
	private String agreed_date;
	
	private String remark;
	@BeanField(title = "投线时间", name = "inline_time", type = FieldType.DateTime)
	private String inline_time;
	
	private String img_check; //画面检查
	private String ccd_change;//CCD盖玻璃更换
	private String img_operate_result;//存在画面检查
	private String ccd_operate_result;//存在CCD盖玻璃更换
	
	@BeanField(title="课室", name="section_id", type = FieldType.String)
	private String section_id;
	@BeanField(title="维修流程模板ID", name="pat_id")
	private String pat_id;

	@BeanField(title = "机种类别", name = "category_kind", type = FieldType.Integer)
	private String category_kind;

	@BeanField(title = "未修理返还标记", name = "unrepair_flg", type = FieldType.Integer)
	private String unrepair_flg;

	@BeanField(title = "加急", name = "scheduled_expedited", type = FieldType.Integer)
	private String scheduled_expedited; 

	private String ccd_model;

	@BeanField(title = "返修标记", name = "service_repair_flg", type = FieldType.Integer)
	private String service_repair_flg;

	public String getSection_id() {
		return section_id;
	}
	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}
	public String getAgreed_date() {
		return agreed_date;
	}
	public void setAgreed_date(String agreed_date) {
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
		return img_operate_result;
	}
	public void setImg_operate_result(String img_operate_result) {
		this.img_operate_result = img_operate_result;
	}
	public String getCcd_operate_result() {
		return ccd_operate_result;
	}
	public void setCcd_operate_result(String ccd_operate_result) {
		this.ccd_operate_result = ccd_operate_result;
	}
	public String getInline_time() {
		return inline_time;
	}
	public void setInline_time(String inline_time) {
		this.inline_time = inline_time;
	}
	public String getRemark() {
		remark = "";
		if (!isEmpty(direct_flg) && !"0".equals(direct_flg)) {
			remark += " " + CodeListUtils.getValue("material_direct", direct_flg);
		}
		if (!isEmpty(service_repair_flg) && !"0".equals(service_repair_flg)) {
			remark += " " + CodeListUtils.getValue("material_service_repair", service_repair_flg);
		}
		if (!isEmpty(fix_type) && !"0".equals(fix_type)) {
			remark += " " + CodeListUtils.getValue("material_fix_type", fix_type);
		}
		if (remark.length() > 0) {
			remark = remark.substring(1);
		}
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
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
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getSerial_no() {
		return serial_no;
	}
	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
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
	public String getModel_name() {
		return model_name;
	}
	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}
	public String getDirect_flg() {
		return direct_flg;
	}
	public void setDirect_flg(String direct_flg) {
		this.direct_flg = direct_flg;
	}
	public String getFix_type() {
		return fix_type;
	}
	public void setFix_type(String fix_type) {
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
	
	public String filterSpecialValue(String value, String preName) {
		if ("inline_time".equals(preName)) {
			if (!CommonStringUtil.isEmpty(value)) {
				try {
					Date date = format.parse(value);
					return format2.format(date);
				}catch (Exception e) {
				}
			}
		}
		return value;
	}
	public String getPat_id() {
		return pat_id;
	}
	public void setPat_id(String pat_id) {
		this.pat_id = pat_id;
	}
	/**
	 * @return the category_kind
	 */
	public String getCategory_kind() {
		return category_kind;
	}
	/**
	 * @param category_kind the category_kind to set
	 */
	public void setCategory_kind(String category_kind) {
		this.category_kind = category_kind;
	}
	public String getUnrepair_flg() {
		return unrepair_flg;
	}
	public void setUnrepair_flg(String unrepair_flg) {
		this.unrepair_flg = unrepair_flg;
	}
	/**
	 * @return the scheduled_expedited
	 */
	public String getScheduled_expedited() {
		return scheduled_expedited;
	}
	/**
	 * @param scheduled_expedited the scheduled_expedited to set
	 */
	public void setScheduled_expedited(String scheduled_expedited) {
		this.scheduled_expedited = scheduled_expedited;
	}
	/**
	 * @return the ccd_model
	 */
	public String getCcd_model() {
		return ccd_model;
	}
	/**
	 * @param ccd_model the ccd_model to set
	 */
	public void setCcd_model(String ccd_model) {
		this.ccd_model = ccd_model;
	}
	public String getSection_name() {
		return section_name;
	}
	public void setSection_name(String section_name) {
		this.section_name = section_name;
	}
	public String getService_repair_flg() {
		return service_repair_flg;
	}
	public void setService_repair_flg(String service_repair_flg) {
		this.service_repair_flg = service_repair_flg;
	}
}
