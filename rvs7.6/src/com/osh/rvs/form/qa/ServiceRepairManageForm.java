package com.osh.rvs.form.qa;

import java.io.Serializable;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class ServiceRepairManageForm extends ActionForm implements Serializable {

	private static final long serialVersionUID = -6875628788728630232L;

	// 图片显示序号
	@BeanField(title = "图片显示序号", name = "seq_no", type = FieldType.Integer, length = 1)
	private String seq_no;

	// 上传图片id
	@BeanField(title = "上传图片ID", name = "image_uuid", type = FieldType.String)
	private String image_uuid;

	// 此次受理日(SORC受理日)
	@BeanField(title = "此次受理日", name = "reception_time", type = FieldType.Date)
	private String reception_time;

	public String getReception_time() {
		return reception_time;
	}

	public void setReception_time(String reception_time) {
		this.reception_time = reception_time;
	}

	/** 保内QIS管理-分析表详细数据-start **/
	// 分析对应建议
	@BeanField(title = "分析对应建议", name = "analysis_correspond_suggestion", length = 500)
	private String analysis_correspond_suggestion;
	// 分析表编号
	@BeanField(title = "分析表编号", name = "analysis_no", length = 32)
	private String analysis_no;
	// 客户编号
	@BeanField(title = "客户编号", name = "customer_id", type = FieldType.Integer, length = 11)
	private String customer_id;
	// 客户名
	@BeanField(title = "客户名", name = "customer_name", length = 60)
	private String customer_name;
	// 不良内容
	@BeanField(title = "不良内容", name = "fix_demand", length = 500)
	private String fix_demand;
	// 不良内容
	@BeanField(title = "故障描述", name = "trouble_discribe", length = 768)
	private String trouble_discribe;
	// 故障原因
	@BeanField(title = "故障原因", name = "trouble_cause", length = 768)
	private String trouble_cause;
	// 分析结果
	@BeanField(title = "分析结果", name = "analysis_result", type = FieldType.Integer, length = 2)
	private String analysis_result;
	// 责任区分
	@BeanField(title = "责任区分", name = "liability_flg", type = FieldType.Integer, length = 1)
	private String liability_flg;
	// 维修场所标记
	@BeanField(title = "维修场所标记", name = "manufactory_flg", type = FieldType.Integer, length = 1)
	private String manufactory_flg;
	// 追加部件
	@BeanField(title = "追加部件", name = "append_component", length = 20)
	private String append_component;
	// 数量
	@BeanField(title = "数量", name = "quantity", type = FieldType.Integer, length = 2)
	private String quantity;
	// 损金数额
	@BeanField(title = "损金数额", name = "loss_amount", type = FieldType.UDouble)
	private String loss_amount;
	// 上次修理编号
	@BeanField(title = "上次修理编号", name = "last_sorc_no", length = 18)
	private String last_sorc_no;
	// 上次出货日期
	@BeanField(title = "上次出货日期", name = "last_shipping_date", type = FieldType.Date, length = 20)
	private String last_shipping_date;
	// 上次等级
	@BeanField(title = "上次等级", name = "last_rank", length = 6)
	private String last_rank;
	// 上次ocm等级
	@BeanField(title = "上次ocm等级", name = "last_ocm_rank", length = 6)
	private String last_ocm_rank;
	// 上次故障内容
	@BeanField(title = "上次故障内容", name = "last_trouble_feature", length = 768)
	private String last_trouble_feature;
	// 清洗
	@BeanField(title = "清洗", name = "wash_feature", length = 20)
	private String wash_feature;
	// 消毒
	@BeanField(title = "消毒", name = "disinfect_feature", length = 20)
	private String disinfect_feature;
	// 灭菌
	@BeanField(title = "灭菌", name = "steriliza_feature", length = 20)
	private String steriliza_feature;
	// 使用频率
	@BeanField(title = "使用频率", name = "usage_frequency", length = 20)
	private String usage_frequency;
	/** 保内QIS管理-分析表详细数据-end **/

	/** 保内QIS管理表数据-start **/
	@BeanField(title = "型号", name = "model_name", length = 30, notNull = true, primaryKey = true)
	private String model_name;// 型号

	@BeanField(title = "QA受理时间", name = "qa_reception_time", type = FieldType.DateTime)
	private String qa_reception_time;// QA受理时间

	@BeanField(title = "QA受理时间开始", name = "qa_reception_time_start", type = FieldType.Date)
	private String qa_reception_time_start;// QA受理时间开始 -------

	@BeanField(title = "QA受理时间结束", name = "qa_reception_time_end", type = FieldType.Date)
	private String qa_reception_time_end;// QA受理时间结束

	@BeanField(title = "机身号", name = "serial_no", length = 20, notNull = true, primaryKey = true)
	private String serial_no;// 机身号

	@BeanField(title = "修理编号", name = "sorc_no", length = 15)
	private String sorc_no;// 修理编号

	@BeanField(title = "修理类别", name = "service_repair_flg", type = FieldType.Integer, length = 1, notNull = true)
	private String service_repair_flg;// 修理类别

	@BeanField(title = "QA判定日期开始", name = "qa_referee_time_start", type = FieldType.Date)
	private String qa_referee_time_start;// QA判定日期开始 ----

	@BeanField(title = "QA判定日期", name = "qa_referee_time", type = FieldType.DateTime)
	private String qa_referee_time;// QA判定日期

	@BeanField(title = "QA判定日期结束", name = "qa_referee_time_end", type = FieldType.Date)
	private String qa_referee_time_end;// QA判定日期结束

	@BeanField(title = "答复时限", name = "answer_in_deadline", type = FieldType.Integer, length = 1)
	private String answer_in_deadline;// 答复时限

	@BeanField(title = "有无偿", name = "service_free_flg", type = FieldType.Integer, length = 1)
	private String service_free_flg;// 有无偿

	@BeanField(title = "未修理返还", name = "unfix_back_flg", type = FieldType.Integer)
	private String unfix_back_flg;// 未修理返还

	@BeanField(title = "维修对象编号", name = "material_id", length = 11)
	private String material_id;// 维修对象编号

	@BeanField(title = "RC邮件发送日", name = "rc_mailsend_date", type = FieldType.Date, notNull = true, primaryKey = true)
	private String rc_mailsend_date;// RC邮件发送日

	@BeanField(title = "实物收到日", name = "rc_ship_assign_date", type = FieldType.Date)
	private String rc_ship_assign_date;// RC出货指示日

	@BeanField(title = "SORC受理日", name = "reception_date", type = FieldType.Date)
	private String reception_date;// SORC受理日

	@BeanField(title = "QA二次判定日", name = "qa_secondary_referee_date", type = FieldType.Date)
	private String qa_secondary_referee_date;// QA二次判定日

	@BeanField(title = "等级", name = "rank", length = 6)
	private String rank;// 等级

	@BeanField(title = "维修站", name = "workshop", type = FieldType.Integer, length = 2)
	private String workshop;// 维修站

	@BeanField(title = "处理对策", name = "countermeasures", length = 120)
	private String countermeasures;// 处理对策

	@BeanField(title = "实发故障", name = "actual_fault", length = 45)
	private String actual_fault;

	@BeanField(title = "SORC报价日", name = "quotation_date", type = FieldType.Date)
	private String quotation_date;// SORC报价日

	@BeanField(title = "修理同意日", name = "agreed_date", type = FieldType.Date)
	private String agreed_date;// 修理同意日

	@BeanField(title = "投线日", name = "inline_date", type = FieldType.Date)
	private String inline_date;// 投线日

	@BeanField(title = "修理完了日", name = "outline_date", type = FieldType.Date)
	private String outline_date;// 修理完了日

	@BeanField(title = "备注", name = "comment", length = 120)
	private String comment;// 备注

	@BeanField(title = "提要", name = "mention", length = 45)
	private String mention;// 提要

	/* 后期添加 */
	@BeanField(title = "质量信息单号", name = "quality_info_no", length = 25)
	private String quality_info_no;

	@BeanField(title = "QIS发送单号", name = "qis_invoice_no", length = 32)
	private String qis_invoice_no;

	@BeanField(title = "QIS发送日期", name = "qis_invoice_date", type = FieldType.Date)
	private String qis_invoice_date;

	@BeanField(title = "请款月", name = "include_month", length = 6)
	private String include_month;

	@BeanField(title = "请款金额", name = "charge_amount", length = 11, type = FieldType.Double)
	private String charge_amount;

	@BeanField(title = "作业状态", name = "operate_result", type = FieldType.Integer)
	private String operate_result;

	@BeanField(title = "处理方式", name = "corresponse_flg", type = FieldType.Integer, length = 1)
	private String corresponse_flg;// 处理方式

	@BeanField(title = "实物处理", name = "entity_send_flg", type = FieldType.Integer, length = 2)
	private String entity_send_flg;// 实物处理

	@BeanField(title = "故障品接收日期", name = "trouble_item_reception_date", type = FieldType.Date)
	private String trouble_item_reception_date;// 故障品接收

	@BeanField(title = "故障品提交给业务日期", name = "trouble_item_in_bussiness_date", type = FieldType.Date)
	private String trouble_item_in_bussiness_date;// 故障品提交给业务

	@BeanField(title = "故障品发送（业务）日期", name = "trouble_item_out_bussiness_date", type = FieldType.Date)
	private String trouble_item_out_bussiness_date;// 故障品发送（业务）

	@BeanField(title = "QIS2", name = "qis2_date", type = FieldType.Date)
	private String qis2_date;// QIS2

	@BeanField(title = "QIS3", name = "qis3_date", type = FieldType.Date)
	private String qis3_date;// QIS3

	@BeanField(title = "废弃证明", name = "waste_certificated_date", type = FieldType.Date)
	private String waste_certificated_date;// 废弃证明

	@BeanField(title = "质量判定", name = "quality_judgment", type = FieldType.Integer, length = 1)
	private String quality_judgment;// 质量判定

	@BeanField(title = "发行QIS", name = "qis_isuse", type = FieldType.Integer, length = 1)
	private String qis_isuse;// 发行QIS

	@BeanField(title = "产品分类", name = "kind", type = FieldType.Integer, length = 2, notNull = true)
	private String kind;

	@BeanField(title = "故障现象确认(工厂)", name = "m_trouble_phenomenon_confirm", type = FieldType.String, length = 32)
	private String m_trouble_phenomenon_confirm;

	@BeanField(title = "判定结果(工厂)", name = "m_judgment_result", type = FieldType.String, length = 32)
	private String m_judgment_result;

	@BeanField(title = "分析结果简述(工厂)", name = "m_analysis_result_brief", type = FieldType.String, length = 1023)
	private String m_analysis_result_brief;

	@BeanField(title = "对应方法(工厂)", name = "m_correspond_method", type = FieldType.String, length = 32)
	private String m_correspond_method;

	@BeanField(title = "对策(工厂)", name = "m_solutions", type = FieldType.String, length = 32)
	private String m_solutions;

	@BeanField(title = "ETQ 单号", name = "etq_no", type = FieldType.String, length = 9)
	private String etq_no;

	@BeanField(title = "PAE 编号", name = "pae_no", type = FieldType.String, length = 20)
	private String pae_no;

	@BeanField(title = "购买/安装日期", name = "setup_date", type = FieldType.Date)
	private String setup_date;

	@BeanField(title = "故障发生日期", name = "trouble_happen_date", type = FieldType.Date)
	private String trouble_happen_date;

	@BeanField(title = "使用累计次数", name = "use_count", type = FieldType.String, length = 32)
	private String use_count;

	@BeanField(title = "使用累计时间", name = "use_elapse", type = FieldType.String, length = 32)
	private String use_elapse;

	@BeanField(title = "INVOICE_NO.", name = "invoice_no", type = FieldType.String, length = 32)
	private String invoice_no;

	@BeanField(title = "故障零件号码", name = "trouble_parts", type = FieldType.String, length = 45)
	private String trouble_parts;

	@BeanField(title = "故障零件废弃日期", name = "trouble_parts_waste_date", type = FieldType.Date)
	private String trouble_parts_waste_date;

	@BeanField(title = "寄送区外单号", name = "deliver_external_no", type = FieldType.String, length = 32)
	private String deliver_external_no;

	@BeanField(title = "寄送区外日期", name = "deliver_external_date", type = FieldType.Date)
	private String deliver_external_date;

	public String getAnalysis_correspond_suggestion() {
		return analysis_correspond_suggestion;
	}

	public void setAnalysis_correspond_suggestion(
			String analysis_correspond_suggestion) {
		this.analysis_correspond_suggestion = analysis_correspond_suggestion;
	}

	public String getLast_ocm_rank() {
		return last_ocm_rank;
	}

	public void setLast_ocm_rank(String last_ocm_rank) {
		this.last_ocm_rank = last_ocm_rank;
	}

	public String getFix_demand() {
		return fix_demand;
	}

	public void setFix_demand(String fix_demand) {
		this.fix_demand = fix_demand;
	}

	public String getAnalysis_no() {
		return analysis_no;
	}

	public void setAnalysis_no(String analysis_no) {
		this.analysis_no = analysis_no;
	}

	public String getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}

	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	public String getTrouble_cause() {
		return trouble_cause;
	}

	public void setTrouble_cause(String trouble_cause) {
		this.trouble_cause = trouble_cause;
	}

	public String getAnalysis_result() {
		return analysis_result;
	}

	public void setAnalysis_result(String analysis_result) {
		this.analysis_result = analysis_result;
	}

	public String getLiability_flg() {
		return liability_flg;
	}

	public void setLiability_flg(String liability_flg) {
		this.liability_flg = liability_flg;
	}

	public String getManufactory_flg() {
		return manufactory_flg;
	}

	public void setManufactory_flg(String manufactory_flg) {
		this.manufactory_flg = manufactory_flg;
	}

	public String getAppend_component() {
		return append_component;
	}

	public void setAppend_component(String append_component) {
		this.append_component = append_component;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getLoss_amount() {
		return loss_amount;
	}

	public void setLoss_amount(String loss_amount) {
		this.loss_amount = loss_amount;
	}

	public String getLast_sorc_no() {
		return last_sorc_no;
	}

	public void setLast_sorc_no(String last_sorc_no) {
		this.last_sorc_no = last_sorc_no;
	}

	public String getLast_shipping_date() {
		return last_shipping_date;
	}

	public void setLast_shipping_date(String last_shipping_date) {
		this.last_shipping_date = last_shipping_date;
	}

	public String getLast_rank() {
		return last_rank;
	}

	public void setLast_rank(String last_rank) {
		this.last_rank = last_rank;
	}

	public String getLast_trouble_feature() {
		return last_trouble_feature;
	}

	public void setLast_trouble_feature(String last_trouble_feature) {
		this.last_trouble_feature = last_trouble_feature;
	}

	public String getWash_feature() {
		return wash_feature;
	}

	public void setWash_feature(String wash_feature) {
		this.wash_feature = wash_feature;
	}

	public String getDisinfect_feature() {
		return disinfect_feature;
	}

	public void setDisinfect_feature(String disinfect_feature) {
		this.disinfect_feature = disinfect_feature;
	}

	public String getSteriliza_feature() {
		return steriliza_feature;
	}

	public void setSteriliza_feature(String steriliza_feature) {
		this.steriliza_feature = steriliza_feature;
	}

	public String getUsage_frequency() {
		return usage_frequency;
	}

	public void setUsage_frequency(String usage_frequency) {
		this.usage_frequency = usage_frequency;
	}

	/** 保内QIS管理表数据-end **/
	public String getQuality_info_no() {
		return quality_info_no;
	}

	public void setQuality_info_no(String quality_info_no) {
		this.quality_info_no = quality_info_no;
	}

	public String getQis_invoice_no() {
		return qis_invoice_no;
	}

	public void setQis_invoice_no(String qis_invoice_no) {
		this.qis_invoice_no = qis_invoice_no;
	}

	public String getQis_invoice_date() {
		return qis_invoice_date;
	}

	public void setQis_invoice_date(String qis_invoice_date) {
		this.qis_invoice_date = qis_invoice_date;
	}

	public String getInclude_month() {
		return include_month;
	}

	public void setInclude_month(String include_month) {
		this.include_month = include_month;
	}

	public String getCharge_amount() {
		return charge_amount;
	}

	public void setCharge_amount(String charge_amount) {
		this.charge_amount = charge_amount;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getQa_reception_time() {
		return qa_reception_time;
	}

	public void setQa_reception_time(String qa_reception_time) {
		this.qa_reception_time = qa_reception_time;
	}

	public String getSerial_no() {
		return serial_no;
	}

	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}

	public String getSorc_no() {
		return sorc_no;
	}

	public void setSorc_no(String sorc_no) {
		this.sorc_no = sorc_no;
	}

	public String getService_repair_flg() {
		return service_repair_flg;
	}

	public void setService_repair_flg(String service_repair_flg) {
		this.service_repair_flg = service_repair_flg;
	}

	public String getQa_referee_time() {
		return qa_referee_time;
	}

	public void setQa_referee_time(String qa_referee_time) {
		this.qa_referee_time = qa_referee_time;
	}

	public String getAnswer_in_deadline() {
		return answer_in_deadline;
	}

	public void setAnswer_in_deadline(String answer_in_deadline) {
		this.answer_in_deadline = answer_in_deadline;
	}

	public String getService_free_flg() {
		return service_free_flg;
	}

	public void setService_free_flg(String service_free_flg) {
		this.service_free_flg = service_free_flg;
	}

	public String getMaterial_id() {
		return material_id;
	}

	public void setMaterial_id(String material_id) {
		this.material_id = material_id;
	}

	public String getRc_mailsend_date() {
		return rc_mailsend_date;
	}

	public void setRc_mailsend_date(String rc_mailsend_date) {
		this.rc_mailsend_date = rc_mailsend_date;
	}

	public String getRc_ship_assign_date() {
		return rc_ship_assign_date;
	}

	public void setRc_ship_assign_date(String rc_ship_assign_date) {
		this.rc_ship_assign_date = rc_ship_assign_date;
	}

	public String getReception_date() {
		return reception_date;
	}

	public void setReception_date(String reception_date) {
		this.reception_date = reception_date;
	}

	public String getQa_secondary_referee_date() {
		return qa_secondary_referee_date;
	}

	public void setQa_secondary_referee_date(String qa_secondary_referee_date) {
		this.qa_secondary_referee_date = qa_secondary_referee_date;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getWorkshop() {
		return workshop;
	}

	public void setWorkshop(String workshop) {
		this.workshop = workshop;
	}

	public String getCountermeasures() {
		return countermeasures;
	}

	public void setCountermeasures(String countermeasures) {
		this.countermeasures = countermeasures;
	}

	public String getQuotation_date() {
		return quotation_date;
	}

	public void setQuotation_date(String quotation_date) {
		this.quotation_date = quotation_date;
	}

	public String getAgreed_date() {
		return agreed_date;
	}

	public void setAgreed_date(String agreed_date) {
		this.agreed_date = agreed_date;
	}

	public String getInline_date() {
		return inline_date;
	}

	public void setInline_date(String inline_date) {
		this.inline_date = inline_date;
	}

	public String getOutline_date() {
		return outline_date;
	}

	public void setOutline_date(String outline_date) {
		this.outline_date = outline_date;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getMention() {
		return mention;
	}

	public void setMention(String mention) {
		this.mention = mention;
	}

	public String getQa_reception_time_end() {
		return qa_reception_time_end;
	}

	public void setQa_reception_time_end(String qa_reception_time_end) {
		this.qa_reception_time_end = qa_reception_time_end;
	}

	public String getQa_referee_time_end() {
		return qa_referee_time_end;
	}

	public void setQa_referee_time_end(String qa_referee_time_end) {
		this.qa_referee_time_end = qa_referee_time_end;
	}

	public String getQa_reception_time_start() {
		return qa_reception_time_start;
	}

	public void setQa_reception_time_start(String qa_reception_time_start) {
		this.qa_reception_time_start = qa_reception_time_start;
	}

	public String getQa_referee_time_start() {
		return qa_referee_time_start;
	}

	public void setQa_referee_time_start(String qa_referee_time_start) {
		this.qa_referee_time_start = qa_referee_time_start;
	}

	public String getUnfix_back_flg() {
		return unfix_back_flg;
	}

	public void setUnfix_back_flg(String unfix_back_flg) {
		this.unfix_back_flg = unfix_back_flg;
	}

	public String getOperate_result() {
		return operate_result;
	}

	public void setOperate_result(String operate_result) {
		this.operate_result = operate_result;
	}

	public String getTrouble_discribe() {
		return trouble_discribe;
	}

	public void setTrouble_discribe(String trouble_discribe) {
		this.trouble_discribe = trouble_discribe;
	}

	public String getImage_uuid() {
		return image_uuid;
	}

	public void setImage_uuid(String image_uuid) {
		this.image_uuid = image_uuid;
	}

	public String getSeq_no() {
		return seq_no;
	}

	public void setSeq_no(String seq_no) {
		this.seq_no = seq_no;
	}

	public String getCorresponse_flg() {
		return corresponse_flg;
	}

	public void setCorresponse_flg(String corresponse_flg) {
		this.corresponse_flg = corresponse_flg;
	}

	public String getEntity_send_flg() {
		return entity_send_flg;
	}

	public void setEntity_send_flg(String entity_send_flg) {
		this.entity_send_flg = entity_send_flg;
	}

	public String getTrouble_item_reception_date() {
		return trouble_item_reception_date;
	}

	public void setTrouble_item_reception_date(
			String trouble_item_reception_date) {
		this.trouble_item_reception_date = trouble_item_reception_date;
	}

	public String getTrouble_item_in_bussiness_date() {
		return trouble_item_in_bussiness_date;
	}

	public void setTrouble_item_in_bussiness_date(
			String trouble_item_in_bussiness_date) {
		this.trouble_item_in_bussiness_date = trouble_item_in_bussiness_date;
	}

	public String getTrouble_item_out_bussiness_date() {
		return trouble_item_out_bussiness_date;
	}

	public void setTrouble_item_out_bussiness_date(
			String trouble_item_out_bussiness_date) {
		this.trouble_item_out_bussiness_date = trouble_item_out_bussiness_date;
	}

	public String getQis2_date() {
		return qis2_date;
	}

	public void setQis2_date(String qis2_date) {
		this.qis2_date = qis2_date;
	}

	public String getQis3_date() {
		return qis3_date;
	}

	public void setQis3_date(String qis3_date) {
		this.qis3_date = qis3_date;
	}

	public String getWaste_certificated_date() {
		return waste_certificated_date;
	}

	public void setWaste_certificated_date(String waste_certificated_date) {
		this.waste_certificated_date = waste_certificated_date;
	}

	public String getQuality_judgment() {
		return quality_judgment;
	}

	public void setQuality_judgment(String quality_judgment) {
		this.quality_judgment = quality_judgment;
	}

	public String getQis_isuse() {
		return qis_isuse;
	}

	public void setQis_isuse(String qis_isuse) {
		this.qis_isuse = qis_isuse;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getM_trouble_phenomenon_confirm() {
		return m_trouble_phenomenon_confirm;
	}

	public void setM_trouble_phenomenon_confirm(
			String m_trouble_phenomenon_confirm) {
		this.m_trouble_phenomenon_confirm = m_trouble_phenomenon_confirm;
	}

	public String getM_judgment_result() {
		return m_judgment_result;
	}

	public void setM_judgment_result(String m_judgment_result) {
		this.m_judgment_result = m_judgment_result;
	}

	public String getM_analysis_result_brief() {
		return m_analysis_result_brief;
	}

	public void setM_analysis_result_brief(String m_analysis_result_brief) {
		this.m_analysis_result_brief = m_analysis_result_brief;
	}

	public String getM_correspond_method() {
		return m_correspond_method;
	}

	public void setM_correspond_method(String m_correspond_method) {
		this.m_correspond_method = m_correspond_method;
	}

	public String getM_solutions() {
		return m_solutions;
	}

	public void setM_solutions(String m_solutions) {
		this.m_solutions = m_solutions;
	}

	public String getEtq_no() {
		return etq_no;
	}

	public void setEtq_no(String etq_no) {
		this.etq_no = etq_no;
	}

	public String getSetup_date() {
		return setup_date;
	}

	public void setSetup_date(String setup_date) {
		this.setup_date = setup_date;
	}

	public String getTrouble_happen_date() {
		return trouble_happen_date;
	}

	public void setTrouble_happen_date(String trouble_happen_date) {
		this.trouble_happen_date = trouble_happen_date;
	}

	public String getUse_count() {
		return use_count;
	}

	public void setUse_count(String use_count) {
		this.use_count = use_count;
	}

	public String getUse_elapse() {
		return use_elapse;
	}

	public void setUse_elapse(String use_elapse) {
		this.use_elapse = use_elapse;
	}

	public String getPae_no() {
		return pae_no;
	}

	public void setPae_no(String pae_no) {
		this.pae_no = pae_no;
	}

	public String getActual_fault() {
		return actual_fault;
	}

	public void setActual_fault(String actual_fault) {
		this.actual_fault = actual_fault;
	}

	/**
	 * @return the invoice_no
	 */
	public String getInvoice_no() {
		return invoice_no;
	}

	/**
	 * @param invoice_no the invoice_no to set
	 */
	public void setInvoice_no(String invoice_no) {
		this.invoice_no = invoice_no;
	}

	/**
	 * @return the trouble_parts
	 */
	public String getTrouble_parts() {
		return trouble_parts;
	}

	/**
	 * @param trouble_parts the trouble_parts to set
	 */
	public void setTrouble_parts(String trouble_parts) {
		this.trouble_parts = trouble_parts;
	}

	/**
	 * @return the trouble_parts_waste_date
	 */
	public String getTrouble_parts_waste_date() {
		return trouble_parts_waste_date;
	}

	/**
	 * @param trouble_parts_waste_date the trouble_parts_waste_date to set
	 */
	public void setTrouble_parts_waste_date(String trouble_parts_waste_date) {
		this.trouble_parts_waste_date = trouble_parts_waste_date;
	}

	/**
	 * @return the deliver_external_no
	 */
	public String getDeliver_external_no() {
		return deliver_external_no;
	}

	/**
	 * @param deliver_external_no the deliver_external_no to set
	 */
	public void setDeliver_external_no(String deliver_external_no) {
		this.deliver_external_no = deliver_external_no;
	}

	/**
	 * @return the deliver_external_date
	 */
	public String getDeliver_external_date() {
		return deliver_external_date;
	}

	/**
	 * @param deliver_external_date the deliver_external_date to set
	 */
	public void setDeliver_external_date(String deliver_external_date) {
		this.deliver_external_date = deliver_external_date;
	}

}
