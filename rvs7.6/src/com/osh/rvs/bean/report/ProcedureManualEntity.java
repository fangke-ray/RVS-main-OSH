package com.osh.rvs.bean.report;

import java.io.Serializable;
import java.util.Date;

public class ProcedureManualEntity implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -3104155177382812019L;

	// 作业要领书 ID", name = "model_id", primaryKey = true, length = 11)
	private String procedure_manual_id;
	// 文件名
	private String file_name;
	// 上传者
	private String update_by;
	// 最终上传时间
	private Date update_time;
	// 本人书单
	private Integer booklist;

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
	public Date getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}
	public Integer getBooklist() {
		return booklist;
	}
	public void setBooklist(Integer booklist) {
		this.booklist = booklist;
	}
}
