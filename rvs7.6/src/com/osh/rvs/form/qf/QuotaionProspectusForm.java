package com.osh.rvs.form.qf;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

/**
 * 报价说明书
 * 
 * @author lxb
 * 
 */
public class QuotaionProspectusForm extends ActionForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4639001305681108954L;
	@BeanField(title = "维修对象ID", name = "material_id", type = FieldType.String, length = 11, primaryKey = true, notNull = true)
	private String material_id;// 维修对象ID

	@BeanField(title = "修理外部号", name = "external_no", type = FieldType.String, length = 9)
	private String external_no;// 修理外部号

	@BeanField(title = "修理等级", name = "repair_plans", type = FieldType.String, length = 60)
	private String repair_plans;// 修理等级

	@BeanField(title = "用户所指出的不良事项", name = "customer_point_outs", type = FieldType.String, length = 240)
	private String customer_point_outs;// 用户所指出的不良事项

	@BeanField(title = "OLYMPUS检查确认结果", name = "olympus_comfirm_results", type = FieldType.String, length = 240)
	private String olympus_comfirm_results;// OLYMPUS检查确认结果

	@BeanField(title = "其它方面用户的宝贵意见及要求", name = "other_requirements", type = FieldType.String, length = 120)
	private String other_requirements;// 其它方面用户的宝贵意见及要求

	@BeanField(title = "故障部位的说明A", name = "damage_part_a_points", type = FieldType.String, length = 15)
	private String damage_part_a_points;// 故障部位的说明A

	@BeanField(title = "故障部位的说明B", name = "damage_part_b_points", type = FieldType.String, length = 15)
	private String damage_part_b_points;// 故障部位的说明B

	@BeanField(title = "故障部位的说明C", name = "damage_part_c_points", type = FieldType.String, length = 15)
	private String damage_part_c_points;// 故障部位的说明C

	@BeanField(title = "故障部位的说明D", name = "damage_part_d_points", type = FieldType.String, length = 15)
	private String damage_part_d_points;// 故障部位的说明D

	@BeanField(title = "故障部位的说明E", name = "damage_part_e_points", type = FieldType.String, length = 15)
	private String damage_part_e_points;// 故障部位的说明E

	@BeanField(title = "故障部位的说明F", name = "damage_part_f_points", type = FieldType.String, length = 15)
	private String damage_part_f_points;// 故障部位的说明F

	@BeanField(title = "故障部位的说明F2", name = "damage_part_f2_points", type = FieldType.String, length = 15)
	private String damage_part_f2_points;// 故障部位的说明F2

	@BeanField(title = "不更换故障点", name = "selectable_damage_points", type = FieldType.String, length = 20)
	private String selectable_damage_points;// 不更换故障点

	@BeanField(title = "不更换故障项目", name = "selectable_damage_retains", type = FieldType.String, length = 20)
	private String selectable_damage_retains;// 不更换故障项目

	@BeanField(title = "不更换故障部件", name = "selectable_damage_parts", type = FieldType.String, length = 60)
	private String selectable_damage_parts;// 不更换故障部件

	@BeanField(title = "医院名称", name = "customer_name", type = FieldType.String, notNull = true)
	private String customer_name;// 医院名称

	@BeanField(title = "型号名称", name = "model_name", type = FieldType.String, length = 50, notNull = true)
	private String model_name;// 型号名称

	@BeanField(title = "机身号", name = "serial_no", type = FieldType.String, length = 8, notNull = true)
	private String serial_no;// 机身号

	@BeanField(title = "保修类别", name = "service_repair_flg", type = FieldType.Integer, length = 1)
	private String service_repair_flg;// 保修类别

	@BeanField(title = "受理日期", name = "reception_time", type = FieldType.Date)
	private String reception_time;// 受理日期

	@BeanField(title = "有无尝", name = "service_free_flg", type = FieldType.Integer, length = 1)
	private String service_free_flg;// 有无尝

	@BeanField(title = "报价时间", name = "finish_time", type = FieldType.Date)
	private String finish_time;// 报价时间

	@BeanField(title = "报价单制作者", name = "job_no", type = FieldType.String, length = 8, notNull = true)
	private String job_no;// 报价单制作者

	@BeanField(title = "委托出", name = "ocm", type = FieldType.Integer, length = 2, notNull = true)
	private String ocm;// 委托出

	@BeanField(title = "选择式报价", name = "selectable", type = FieldType.Integer, length = 1)
	private String selectable;// 选择式报价
	
	@BeanField(title="维修对象机种名称",name="category_name",type = FieldType.String,length=50)
	private String category_name;//维修对象机种名称
	
	@BeanField(title="机种",name="kind",type = FieldType.String)
	private String kind;//机种

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public String getExternal_no() {
		return external_no;
	}

	public void setExternal_no(String external_no) {
		this.external_no = external_no;
	}

	public String getRepair_plans() {
		return repair_plans;
	}

	public void setRepair_plans(String repair_plans) {
		this.repair_plans = repair_plans;
	}

	public String getCustomer_point_outs() {
		return customer_point_outs;
	}

	public void setCustomer_point_outs(String customer_point_outs) {
		this.customer_point_outs = customer_point_outs;
	}

	public String getOlympus_comfirm_results() {
		return olympus_comfirm_results;
	}

	public void setOlympus_comfirm_results(String olympus_comfirm_results) {
		this.olympus_comfirm_results = olympus_comfirm_results;
	}

	public String getOther_requirements() {
		return other_requirements;
	}

	public void setOther_requirements(String other_requirements) {
		this.other_requirements = other_requirements;
	}

	public String getDamage_part_a_points() {
		return damage_part_a_points;
	}

	public void setDamage_part_a_points(String damage_part_a_points) {
		this.damage_part_a_points = damage_part_a_points;
	}

	public String getDamage_part_b_points() {
		return damage_part_b_points;
	}

	public void setDamage_part_b_points(String damage_part_b_points) {
		this.damage_part_b_points = damage_part_b_points;
	}

	public String getDamage_part_c_points() {
		return damage_part_c_points;
	}

	public void setDamage_part_c_points(String damage_part_c_points) {
		this.damage_part_c_points = damage_part_c_points;
	}

	public String getDamage_part_d_points() {
		return damage_part_d_points;
	}

	public void setDamage_part_d_points(String damage_part_d_points) {
		this.damage_part_d_points = damage_part_d_points;
	}

	public String getDamage_part_e_points() {
		return damage_part_e_points;
	}

	public void setDamage_part_e_points(String damage_part_e_points) {
		this.damage_part_e_points = damage_part_e_points;
	}

	public String getDamage_part_f_points() {
		return damage_part_f_points;
	}

	public void setDamage_part_f_points(String damage_part_f_points) {
		this.damage_part_f_points = damage_part_f_points;
	}

	public String getDamage_part_f2_points() {
		return damage_part_f2_points;
	}

	public void setDamage_part_f2_points(String damage_part_f2_points) {
		this.damage_part_f2_points = damage_part_f2_points;
	}

	public String getSelectable_damage_points() {
		return selectable_damage_points;
	}

	public void setSelectable_damage_points(String selectable_damage_points) {
		this.selectable_damage_points = selectable_damage_points;
	}

	public String getSelectable_damage_retains() {
		return selectable_damage_retains;
	}

	public void setSelectable_damage_retains(String selectable_damage_retains) {
		this.selectable_damage_retains = selectable_damage_retains;
	}

	public String getSelectable_damage_parts() {
		return selectable_damage_parts;
	}

	public void setSelectable_damage_parts(String selectable_damage_parts) {
		this.selectable_damage_parts = selectable_damage_parts;
	}

	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
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

	public String getService_repair_flg() {
		return service_repair_flg;
	}

	public void setService_repair_flg(String service_repair_flg) {
		this.service_repair_flg = service_repair_flg;
	}

	public String getReception_time() {
		return reception_time;
	}

	public void setReception_time(String reception_time) {
		this.reception_time = reception_time;
	}

	public String getService_free_flg() {
		return service_free_flg;
	}

	public void setService_free_flg(String service_free_flg) {
		this.service_free_flg = service_free_flg;
	}

	public String getFinish_time() {
		return finish_time;
	}

	public void setFinish_time(String finish_time) {
		this.finish_time = finish_time;
	}

	public String getJob_no() {
		return job_no;
	}

	public void setJob_no(String job_no) {
		this.job_no = job_no;
	}

	public String getOcm() {
		return ocm;
	}

	public void setOcm(String ocm) {
		this.ocm = ocm;
	}

	public String getSelectable() {
		return selectable;
	}

	public void setSelectable(String selectable) {
		this.selectable = selectable;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

}
