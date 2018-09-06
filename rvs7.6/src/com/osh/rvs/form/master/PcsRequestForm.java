package com.osh.rvs.form.master;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class PcsRequestForm extends ActionForm {

	private static final long serialVersionUID = -6098051724767304203L;

	@BeanField(title = "Key", name = "pcs_request_key", primaryKey = true, length = 11)
	private String pcs_request_key;
	@BeanField(title = "依赖发布日期", name = "request_date", type = FieldType.Date)
	private String request_date;
	@BeanField(title = "依赖发布日期S", name = "request_date_start", type = FieldType.Date)
	private String request_date_start;
	@BeanField(title = "依赖发布日期E", name = "request_date_end", type = FieldType.Date)
	private String request_date_end;
	@BeanField(title = "依赖 DB 号", name = "request_db_no", notNull = true)
	private String request_db_no;
	@BeanField(title = "依赖描述", name = "description")
	private String description;
	@BeanField(title = "工程检查票类型", name = "line_type", type = FieldType.Integer, length = 10, notNull = true)
	private String line_type;
	@BeanField(title = "工程 ID", name = "line_id", notNull = true, length = 11)
	private String line_id;
	@BeanField(title = "文件名称", name = "file_name", notNull = true)
	private String file_name;
	@BeanField(title = "原文件名称", name = "org_file_name")
	private String org_file_name;
	@BeanField(title = "针对型号", name = "target_model_name")
	private String target_model;
	private String target_model_id;
	@BeanField(title = "影响型号", name = "reacted_model")
	private String reacted_model;
	@BeanField(title = "修改方式", name = "change_means", type = FieldType.Integer, length = 1, notNull = true)
	private String change_means;
	@BeanField(title = "导入者 ID", name = "importer_id", length = 11)
	private String importer_id;
	@BeanField(title = "导入者", name = "importer_name")
	private String importer_name;
	@BeanField(title = "导入系统时间", name = "import_time", type = FieldType.DateTime)
	private String import_time;
	@BeanField(title = "导入系统时间S", name = "import_time_start", type = FieldType.Date)
	private String import_time_start;
	@BeanField(title = "导入系统时间E", name = "import_time_end", type = FieldType.Date)
	private String import_time_end;
	@BeanField(title = "更改描述", name = "change_detail")
	private String change_detail;

	/** file property */
    private FormFile file;

    public FormFile getFile() {
        return file;
    }

    public void setFile(FormFile file) {
        this.file = file;
    }

	public String getPcs_request_key() {
		return pcs_request_key;
	}
	public void setPcs_request_key(String pcs_request_key) {
		this.pcs_request_key = pcs_request_key;
	}
	public String getRequest_date() {
		return request_date;
	}
	public void setRequest_date(String request_date) {
		this.request_date = request_date;
	}
	public String getRequest_db_no() {
		return request_db_no;
	}
	public void setRequest_db_no(String request_db_no) {
		this.request_db_no = request_db_no;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLine_type() {
		return line_type;
	}
	public void setLine_type(String line_type) {
		this.line_type = line_type;
	}
	public String getLine_id() {
		return line_id;
	}
	public void setLine_id(String line_id) {
		this.line_id = line_id;
	}
	public String getFile_name() {
		return file_name;
	}
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	public String getOrg_file_name() {
		return org_file_name;
	}
	public void setOrg_file_name(String org_file_name) {
		this.org_file_name = org_file_name;
	}
	public String getTarget_model() {
		return target_model;
	}
	public void setTarget_model(String target_model) {
		this.target_model = target_model;
	}
	public String getReacted_model() {
		return reacted_model;
	}
	public void setReacted_model(String reacted_model) {
		this.reacted_model = reacted_model;
	}
	public String getChange_means() {
		return change_means;
	}
	public void setChange_means(String change_means) {
		this.change_means = change_means;
	}
	public String getImporter_id() {
		return importer_id;
	}
	public void setImporter_id(String importer_id) {
		this.importer_id = importer_id;
	}
	public String getImporter_name() {
		return importer_name;
	}
	public void setImporter_name(String importer_name) {
		this.importer_name = importer_name;
	}
	public String getImport_time() {
		return import_time;
	}
	public void setImport_time(String import_time) {
		this.import_time = import_time;
	}
	public String getChange_detail() {
		return change_detail;
	}
	public void setChange_detail(String change_detail) {
		this.change_detail = change_detail;
	}
	public String getRequest_date_start() {
		return request_date_start;
	}
	public void setRequest_date_start(String request_date_start) {
		this.request_date_start = request_date_start;
	}
	public String getRequest_date_end() {
		return request_date_end;
	}
	public void setRequest_date_end(String request_date_end) {
		this.request_date_end = request_date_end;
	}
	public String getImport_time_start() {
		return import_time_start;
	}
	public void setImport_time_start(String import_time_start) {
		this.import_time_start = import_time_start;
	}
	public String getImport_time_end() {
		return import_time_end;
	}
	public void setImport_time_end(String import_time_end) {
		this.import_time_end = import_time_end;
	}
	public String getTarget_model_id() {
		return target_model_id;
	}
	public void setTarget_model_id(String target_model_id) {
		this.target_model_id = target_model_id;
	}
}
