package com.osh.rvs.form.equipment;

import java.io.Serializable;

import com.osh.rvs.form.UploadForm;

/**
 * 设备工具治具维修记录
 * 
 * @author gonglm
 * 
 */
import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class DeviceJigRepairRecordForm extends UploadForm implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -1748094733407066719L;

	@BeanField(title = "维修记录Key", name = "device_jig_repair_record_key", type = FieldType.String, length = 11, primaryKey = true)
	private String device_jig_repair_record_key;
	@BeanField(title = "点检不合格记录key", name = "check_unqualified_record_key", type = FieldType.String, length = 11)
	private String check_unqualified_record_key;
	@BeanField(title = "报修时间", name = "submit_time", type = FieldType.DateTime)
	private String submit_time;
	@BeanField(title = "报修时间起", name = "submit_time_start", type = FieldType.Date)
	private String submit_time_start;
	@BeanField(title = "报修时间止", name = "submit_time_end", type = FieldType.Date)
	private String submit_time_end;
	@BeanField(title = "故障现象", name = "phenomenon", type = FieldType.String, length = 250, notNull = true)
	private String phenomenon;
	@BeanField(title = "修理完毕时间", name = "repair_complete_time", type = FieldType.DateTime)
	private String repair_complete_time;
	@BeanField(title = "修理完毕时间起", name = "repair_complete_time_start", type = FieldType.Date)
	private String repair_complete_time_start;
	@BeanField(title = "修理完毕时间止", name = "repair_complete_time_end", type = FieldType.Date)
	private String repair_complete_time_end;
	@BeanField(title = "故障和原因", name = "fault_causes", type = FieldType.String, length = 250)
	private String fault_causes;
	@BeanField(title = "修理对策和方向", name = "countermeasure", type = FieldType.String, length = 250)
	private String countermeasure;
	@BeanField(title = "相关照片", name = "photo_flg", type = FieldType.Integer, length = 1)
	private String photo_flg;

	@BeanField(title = "维修担当者 ID", name = "maintainer_id", type = FieldType.String)
	private String maintainer_id;
	@BeanField(title = "维修担当者", name = "maintainer_name", type = FieldType.String)
	private String maintainer_name;

	@BeanField(title = "品名 ID", name = "device_type_id", type = FieldType.String, length = 11)
	private String device_type_id;
	@BeanField(title = "品名", name = "device_type_name", type = FieldType.String, length = 32)
	private String device_type_name;
	@BeanField(title = "型号", name = "model_name", type = FieldType.String, length = 32)
	private String model_name;
	@BeanField(title = "单价", name = "price", type = FieldType.UDouble, length = 8, scale = 2)
	private String price;
	@BeanField(title = "数量", name = "quantity", type = FieldType.UInteger, length = 2)
	private String quantity;
	@BeanField(title = "委外报价", name = "outsourcing_price", type = FieldType.UDouble, length = 8, scale = 2)
	private String outsourcing_price;

	@BeanField(title = "发生工程 ID", name = "line_id", type = FieldType.String, length = 11, notNull = true)
	private String line_id;
	@BeanField(title = "发生工程", name = "line_name", type = FieldType.String)
	private String line_name;
	@BeanField(title = "修理依赖者 ID", name = "submitter_id", type = FieldType.String, length = 11)
	private String submitter_id;
	@BeanField(title = "修理依赖者", name = "submitter_name", type = FieldType.String)
	private String submitter_name;
	@BeanField(title = "对象类型", name = "object_type", type = FieldType.Integer, length = 1, notNull = true)
	private String object_type;// 对象类型
	@BeanField(title = "管理编号", name = "manage_code", type = FieldType.String, length = 11)
	private String manage_code;
	@BeanField(title = "管理ID", name = "manage_id", type = FieldType.String, length = 11)
	private String manage_id;// 管理ID
	@BeanField(title = "维修故障", name = "object_name", type = FieldType.String, length = 32, notNull = true)
	private String object_name;

	@BeanField(title = "停机时间", name = "device_halt", type = FieldType.UInteger)
	private String device_halt;
	private String total_price;
	private String saving_price;

	private String confirmer_id;
	private String confirmer_name;

	@BeanField(title = "备注", name = "comment", type = FieldType.String, length = 64)
	private String comment;
	@BeanField(title = "设备更换部件", name = "consumable", type = FieldType.String, length = 256)
	private String consumable;

	public String getDevice_jig_repair_record_key() {
		return device_jig_repair_record_key;
	}
	public void setDevice_jig_repair_record_key(String device_jig_repair_record_key) {
		this.device_jig_repair_record_key = device_jig_repair_record_key;
	}
	public String getCheck_unqualified_record_key() {
		return check_unqualified_record_key;
	}
	public void setCheck_unqualified_record_key(String check_unqualified_record_key) {
		this.check_unqualified_record_key = check_unqualified_record_key;
	}
	public String getSubmit_time() {
		return submit_time;
	}
	public void setSubmit_time(String submit_time) {
		this.submit_time = submit_time;
	}
	public String getPhenomenon() {
		return phenomenon;
	}
	public void setPhenomenon(String phenomenon) {
		this.phenomenon = phenomenon;
	}
	public String getRepair_complete_time() {
		return repair_complete_time;
	}
	public void setRepair_complete_time(String repair_complete_time) {
		this.repair_complete_time = repair_complete_time;
	}
	public String getFault_causes() {
		return fault_causes;
	}
	public void setFault_causes(String fault_causes) {
		this.fault_causes = fault_causes;
	}
	public String getCountermeasure() {
		return countermeasure;
	}
	public void setCountermeasure(String countermeasure) {
		this.countermeasure = countermeasure;
	}
	public String getPhoto_flg() {
		return photo_flg;
	}
	public void setPhoto_flg(String photo_flg) {
		this.photo_flg = photo_flg;
	}
	public String getMaintainer_id() {
		return maintainer_id;
	}
	public void setMaintainer_id(String maintainer_id) {
		this.maintainer_id = maintainer_id;
	}
	public String getMaintainer_name() {
		return maintainer_name;
	}
	public void setMaintainer_name(String maintainer_name) {
		this.maintainer_name = maintainer_name;
	}
	public String getDevice_type_id() {
		return device_type_id;
	}
	public void setDevice_type_id(String device_type_id) {
		this.device_type_id = device_type_id;
	}
	public String getDevice_type_name() {
		return device_type_name;
	}
	public void setDevice_type_name(String device_type_name) {
		this.device_type_name = device_type_name;
	}
	public String getModel_name() {
		return model_name;
	}
	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getOutsourcing_price() {
		return outsourcing_price;
	}
	public void setOutsourcing_price(String outsourcing_price) {
		this.outsourcing_price = outsourcing_price;
	}
	public String getLine_id() {
		return line_id;
	}
	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}
	public String getLine_name() {
		return line_name;
	}
	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}
	public String getSubmitter_id() {
		return submitter_id;
	}
	public void setSubmitter_id(String submitter_id) {
		this.submitter_id = submitter_id;
	}
	public String getSubmitter_name() {
		return submitter_name;
	}
	public void setSubmitter_name(String submitter_name) {
		this.submitter_name = submitter_name;
	}
	public String getObject_type() {
		return object_type;
	}
	public void setObject_type(String object_type) {
		this.object_type = object_type;
	}
	public String getManage_code() {
		return manage_code;
	}
	public void setManage_code(String manage_code) {
		this.manage_code = manage_code;
	}
	public String getManage_id() {
		return manage_id;
	}
	public void setManage_id(String manage_id) {
		this.manage_id = manage_id;
	}
	public String getObject_name() {
		return object_name;
	}
	public void setObject_name(String object_name) {
		this.object_name = object_name;
	}
	public String getSubmit_time_start() {
		return submit_time_start;
	}
	public void setSubmit_time_start(String submit_time_start) {
		this.submit_time_start = submit_time_start;
	}
	public String getSubmit_time_end() {
		return submit_time_end;
	}
	public void setSubmit_time_end(String submit_time_end) {
		this.submit_time_end = submit_time_end;
	}
	public String getRepair_complete_time_start() {
		return repair_complete_time_start;
	}
	public void setRepair_complete_time_start(String repair_complete_time_start) {
		this.repair_complete_time_start = repair_complete_time_start;
	}
	public String getRepair_complete_time_end() {
		return repair_complete_time_end;
	}
	public void setRepair_complete_time_end(String repair_complete_time_end) {
		this.repair_complete_time_end = repair_complete_time_end;
	}
	public String getDevice_halt() {
		return device_halt;
	}
	public void setDevice_halt(String halt_minute) {
		this.device_halt = halt_minute;
	}
	public String getSaving_price() {
		return saving_price;
	}
	public void setSaving_price(String saving_price) {
		this.saving_price = saving_price;
	}
	public String getTotal_price() {
		return total_price;
	}
	public void setTotal_price(String total_price) {
		this.total_price = total_price;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getConfirmer_id() {
		return confirmer_id;
	}
	public void setConfirmer_id(String confirmer_id) {
		this.confirmer_id = confirmer_id;
	}
	public String getConfirmer_name() {
		return confirmer_name;
	}
	public void setConfirmer_name(String confirmer_name) {
		this.confirmer_name = confirmer_name;
	}
	public String getConsumable() {
		return consumable;
	}
	public void setConsumable(String consumable) {
		this.consumable = consumable;
	}
	

}
