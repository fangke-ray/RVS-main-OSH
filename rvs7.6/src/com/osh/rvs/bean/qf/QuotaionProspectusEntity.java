package com.osh.rvs.bean.qf;

import java.io.Serializable;
import java.util.Date;

/**
 * 报价说明书
 * 
 * @author lxb
 * 
 */
public class QuotaionProspectusEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -719209790244411563L;
	private String material_id;// 维修对象ID
	private String external_no;// 修理外部号
	private String repair_plans;// 修理等级
	private String customer_point_outs;// 用户所指出的不良事项
	private String olympus_comfirm_results;// OLYMPUS检查确认结果
	private String other_requirements;// 其它方面用户的宝贵意见及要求
	private String damage_part_a_points;// 故障部位的说明A
	private String damage_part_b_points;// 故障部位的说明B
	private String damage_part_c_points;// 故障部位的说明C
	private String damage_part_d_points;// 故障部位的说明D
	private String damage_part_e_points;// 故障部位的说明E
	private String damage_part_f_points;// 故障部位的说明F
	private String damage_part_f2_points;// 故障部位的说明F
	private String selectable_damage_points;// 不更换故障点
	private String selectable_damage_retains;// 不更换故障项目
	private String selectable_damage_parts;// 不更换故障部件

	private String customer_name;// 医院名称
	private String model_name;// 型号名称
	private String serial_no;// 机身号
	private Integer service_repair_flg;// 保修类别
	private Date reception_time;// 受理日期
	private Integer service_free_flg;// 有无尝
	private Date finish_time;// 报价时间
	private String job_no;// 报价单制作者
	private Integer ocm;// 委托出
	private Integer selectable;// 选择式报价
	private String category_name;//维修对象机种名称
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

	public Integer getService_repair_flg() {
		return service_repair_flg;
	}

	public void setService_repair_flg(Integer service_repair_flg) {
		this.service_repair_flg = service_repair_flg;
	}

	public Date getReception_time() {
		return reception_time;
	}

	public void setReception_time(Date reception_time) {
		this.reception_time = reception_time;
	}

	public Integer getService_free_flg() {
		return service_free_flg;
	}

	public void setService_free_flg(Integer service_free_flg) {
		this.service_free_flg = service_free_flg;
	}

	public Date getFinish_time() {
		return finish_time;
	}

	public void setFinish_time(Date finish_time) {
		this.finish_time = finish_time;
	}

	public String getJob_no() {
		return job_no;
	}

	public void setJob_no(String job_no) {
		this.job_no = job_no;
	}

	public Integer getOcm() {
		return ocm;
	}

	public void setOcm(Integer ocm) {
		this.ocm = ocm;
	}

	public Integer getSelectable() {
		return selectable;
	}

	public void setSelectable(Integer selectable) {
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
