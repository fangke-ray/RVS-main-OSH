package com.osh.rvs.form.data;

import org.apache.struts.action.ActionForm;

import framework.huiqing.bean.annotation.BeanField;
import framework.huiqing.bean.annotation.FieldType;

public class MonthFilesDownloadForm extends ActionForm {


	/**
	 * 月档案下载
	 */
	private static final long serialVersionUID = 8362721062364998369L;

	//月档案文件名
	@BeanField(title="文件名字",name="file_name",type=FieldType.String)
	private String file_name;
	
	//月档案文件大小
	@BeanField(title="文件大小",name="file_size",type=FieldType.String)
	private String file_size;
	
	//月档案文件最后修改时间
	@BeanField(title="文件大小",name="file_time",type=FieldType.String)
	private String file_time;

	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	public String getFile_size() {
		return file_size;
	}

	public void setFile_size(String file_size) {
		this.file_size = file_size;
	}

	public String getFile_time() {
		return file_time;
	}

	public void setFile_time(String file_time) {
		this.file_time = file_time;
	}
}
