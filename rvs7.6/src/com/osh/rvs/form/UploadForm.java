package com.osh.rvs.form;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

public class UploadForm extends ActionForm {

	private static final long serialVersionUID = 2546637555687213767L;

	/** file property */
    private FormFile file;


    public FormFile getFile() {
        return file;
    }

    public void setFile(FormFile file) {
        this.file = file;
    }
}
