package com.osh.rvs.bean.master;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class PcsRequestEntity implements Serializable {

	private static final long serialVersionUID = -836826319876933720L;
	private String pcs_request_key;
	private Date request_date;
	private Date request_date_start;
	private Date request_date_end;
	private String request_db_no;
	private String description;
	private Integer line_type;
	private String line_id;
	private String file_name;
	private String org_file_name;
	private String target_model_name;
	private String target_model_id;
	private String reacted_model;
	private Integer change_means;
	private String importer_id;
	private String importer_name;
	private Date import_time;
	private Date import_time_start;
	private Date import_time_end;
	private String change_detail;
	private List<ModelEntity> modelList;

	public String getPcs_request_key() {
		return pcs_request_key;
	}
	public void setPcs_request_key(String pcs_request_key) {
		this.pcs_request_key = pcs_request_key;
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
	public String getTarget_model_id() {
		return target_model_id;
	}
	public void setTarget_model_id(String target_model_id) {
		this.target_model_id = target_model_id;
	}
	public String getReacted_model() {
		return reacted_model;
	}
	public void setReacted_model(String reacted_model) {
		this.reacted_model = reacted_model;
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
	public String getChange_detail() {
		return change_detail;
	}
	public void setChange_detail(String change_detail) {
		this.change_detail = change_detail;
	}
	public Date getRequest_date() {
		return request_date;
	}
	public void setRequest_date(Date request_date) {
		this.request_date = request_date;
	}
	public Integer getLine_type() {
		return line_type;
	}
	public void setLine_type(Integer line_type) {
		this.line_type = line_type;
	}
	public Integer getChange_means() {
		return change_means;
	}
	public void setChange_means(Integer change_means) {
		this.change_means = change_means;
	}
	public Date getImport_time() {
		return import_time;
	}
	public void setImport_time(Date import_time) {
		this.import_time = import_time;
	}
	public Date getRequest_date_start() {
		return request_date_start;
	}
	public void setRequest_date_start(Date request_date_start) {
		this.request_date_start = request_date_start;
	}
	public Date getRequest_date_end() {
		return request_date_end;
	}
	public void setRequest_date_end(Date request_date_end) {
		this.request_date_end = request_date_end;
	}
	public Date getImport_time_start() {
		return import_time_start;
	}
	public void setImport_time_start(Date import_time_start) {
		this.import_time_start = import_time_start;
	}
	public Date getImport_time_end() {
		return import_time_end;
	}
	public void setImport_time_end(Date import_time_end) {
		this.import_time_end = import_time_end;
	}
	public String getTarget_model_name() {
		return target_model_name;
	}
	public void setTarget_model_name(String target_model_name) {
		this.target_model_name = target_model_name;
	}

	public void setModelList(List<ModelEntity> models) {
		this.modelList = models;
	}

	public List<ModelEntity> getModelList() {
		return modelList;
	}

}
