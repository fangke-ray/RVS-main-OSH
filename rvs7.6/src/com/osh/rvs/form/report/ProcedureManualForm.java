package com.osh.rvs.form.report;

import com.osh.rvs.form.UploadForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class ProcedureManualForm extends UploadForm {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6428927662397866312L;

	@BeanField(title = "作业要领书 ID", name = "procedure_manual_id", primaryKey = true, length = 11)
	private String procedure_manual_id;
	@BeanField(title = "文件名", name = "file_name", length = 75, notNull = true)
	private String file_name;
	@BeanField(title = "上传者", name = "update_by")
	private String update_by;
	@BeanField(title = "最终上传时间", name = "update_time", type = FieldType.TimeStamp)
	private String update_time;
	@BeanField(title = "本人书单", name = "booklist", type = FieldType.Integer)
	private String booklist;

	public String getProcedure_manual_id() {
		return procedure_manual_id;
	}
	public void setProcedure_manual_id(String procedure_manual_id) {
		this.procedure_manual_id = procedure_manual_id;
	}
	public String getFile_name() {
		return file_name;
	}
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	public String getUpdate_by() {
		return update_by;
	}
	public void setUpdate_by(String update_by) {
		this.update_by = update_by;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	public String getBooklist() {
		return booklist;
	}
	public void setBooklist(String booklist) {
		this.booklist = booklist;
	}
}
