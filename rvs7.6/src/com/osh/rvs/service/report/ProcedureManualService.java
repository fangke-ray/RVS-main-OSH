package com.osh.rvs.service.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.report.ProcedureManualEntity;
import com.osh.rvs.common.PathConsts;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.report.ProcedureManualForm;
import com.osh.rvs.mapper.report.ProcedureManualMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class ProcedureManualService {

	private static final String FILE_PATH = PathConsts.BASE_PATH + PathConsts.DOCS + "\\manual\\";

	static Logger _log = Logger.getLogger(ProcedureManualService.class);

	public List<ProcedureManualForm> search(ActionForm form, LoginData user, SqlSession conn,
			List<MsgInfo> errors) {
		ProcedureManualEntity cond = new ProcedureManualEntity();

		BeanUtil.copyToBean(form, cond, CopyOptions.COPYOPTIONS_NOEMPTY);
		cond.setUpdate_by(user.getOperator_id());

		ProcedureManualMapper pmMapper = conn.getMapper(ProcedureManualMapper.class);

		List<ProcedureManualEntity> result = pmMapper.searchWithPersonalList(cond);

		List<ProcedureManualForm> ret = new ArrayList<ProcedureManualForm>();

		for (ProcedureManualEntity bean : result) {
			ProcedureManualForm retForm = new ProcedureManualForm();
			BeanUtil.copyToForm(bean, retForm, CopyOptions.COPYOPTIONS_NOEMPTY);

			// 检查文件是否存在
			File file = new File(FILE_PATH + bean.getProcedure_manual_id() + ".pdf");
			if (!file.exists()) {
				retForm.setFile_name("(不存在) " + retForm.getFile_name());
			}

			ret.add(retForm);
		}

		return ret;
	}

	/**
	 * 新建/编辑作业要领书
	 * @param procedureManualForm
	 * @param session
	 * @param conn
	 */
	public String updateProcedureManual(ProcedureManualForm procedureManualForm,
			HttpSession session, SqlSessionManager conn) {

		ProcedureManualMapper pmMapper = conn.getMapper(ProcedureManualMapper.class);

		if (procedureManualForm.getProcedure_manual_id() == null) {
			// 新建
			// id生成
			int leadcode = 0;
			String fileName = procedureManualForm.getFile_name();
			if (fileName.length() >=  2) {
				leadcode = (fileName.charAt(0) % 10) * 10 + (fileName.charAt(1) % 10);
			}
			int rnd = (int) (Math.random() * 10000);

			int id = leadcode * 10000 + rnd;

			ProcedureManualEntity idCheckEntity = new ProcedureManualEntity();
			idCheckEntity.setProcedure_manual_id("" + id);
			do {
				List<ProcedureManualEntity> hit = pmMapper.searchWithPersonalList(idCheckEntity);
				if (hit.size() > 0) {
					idCheckEntity.setProcedure_manual_id("" + (++id));
				} else {
					break;
				}
			} while(true);

			LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

			ProcedureManualEntity inst = new ProcedureManualEntity();

			BeanUtil.copyToBean(procedureManualForm, inst, CopyOptions.COPYOPTIONS_NOEMPTY);
			String procedure_manual_id = CommonStringUtil.fillChar("" + id, '0', 11, true);
			inst.setProcedure_manual_id(procedure_manual_id);
			inst.setUpdate_by(user.getOperator_id());
			pmMapper.insertProcedureManual(inst);

			return procedure_manual_id;
		} else {
			// 编辑
			ProcedureManualEntity upd = new ProcedureManualEntity();

			BeanUtil.copyToBean(procedureManualForm, upd, CopyOptions.COPYOPTIONS_NOEMPTY);

			FormFile file = procedureManualForm.getFile();
			if (file != null) {
				// 文件上传
				LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
				upd.setUpdate_by(user.getOperator_id());

				pmMapper.updateProcedureManual(upd);

				return upd.getProcedure_manual_id();
			} else {
				// 文件未上传
				pmMapper.updateProcedureManualName(upd);
				
				return null;
			}
		}
	}

	/**
	 * 删除作业要领书
	 * @param procedure_manual_id
	 * @param conn
	 */
	public void removeProcedureManual(String procedure_manual_id,
			SqlSessionManager conn) {

		ProcedureManualMapper pmMapper = conn.getMapper(ProcedureManualMapper.class);
		pmMapper.deleteProcedureManual(procedure_manual_id);

		pmMapper.deleteBooklist(procedure_manual_id, null);

		// 删除文件
		File file = new File(FILE_PATH + procedure_manual_id + ".pdf");
		if (file.exists()) {
			file.delete();
		}
	}

	public void setBooklist(ActionForm form,
			HttpSession session, SqlSessionManager conn) {
		ProcedureManualMapper pmMapper = conn.getMapper(ProcedureManualMapper.class);
		
		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);

		ProcedureManualEntity entity = new ProcedureManualEntity();

		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		if (entity.getBooklist() == 1) {
			// 增加
			entity.setUpdate_by(user.getOperator_id());

			pmMapper.insertBooklist(entity);

			List<ProcedureManualEntity> l = pmMapper.searchWithPersonalList(entity);
			if (l.size() > 0) {
				user.getBooks().add(l.get(0));
			}
		} else {
			// 删除
			pmMapper.deleteBooklist(entity.getProcedure_manual_id(), user.getOperator_id());

			for (ProcedureManualEntity book : user.getBooks()) {
				if (book.getProcedure_manual_id().equals(entity.getProcedure_manual_id())) {
					user.getBooks().remove(book);
					break;
				}
			}
		}

	}

	public void fileStorage(String procedure_manual_id, FormFile file) {
		OutputStream fileOutput = null;
		try {
			fileOutput = new FileOutputStream(FILE_PATH + procedure_manual_id + ".pdf");
			fileOutput.write(file.getFileData());
			fileOutput.flush();
			fileOutput.close();
		} catch (FileNotFoundException e) {
			_log.error("FileNotFound:" + e.getMessage());
		} catch (IOException e) {
			_log.error("IO:" + e.getMessage());
		}
	}

	public List<ProcedureManualEntity> getBooks(String operator_id,
			SqlSession conn) {
		ProcedureManualMapper pmMapper = conn.getMapper(ProcedureManualMapper.class);

		return pmMapper.getPersonalList(operator_id);
	}

	public void writeHeaderMenuResponse(HttpServletResponse res,
			List<ProcedureManualEntity> books) throws IOException {
		ServletOutputStream output = res.getOutputStream();
		StringBuffer sv = new StringBuffer();

		if (books == null || books.size() == 0) {
			sv.append("您的书单中没有文档。");
			sv.append("<hr>");
			sv.append("请到<br><a href='procedureManual.do'>【文档管理】->【作业要领书】</a><br>画面中设定。");
			output.write(sv.toString().getBytes("utf8"));

			return;
		}
		sv.append("您的书单中共有 " + books.size() + " 个文档。");
		sv.append("<hr><ul>");
		for (ProcedureManualEntity book : books) {
			String id = book.getProcedure_manual_id();
			sv.append("<li><a target=\"_" + id + "\" href=\"/docs/manual/" + id + ".pdf\">" + book.getFile_name() + "</a></li>");
		}

		sv.append("</ul>");
		output.write(sv.toString().getBytes("utf8"));
	}

}
