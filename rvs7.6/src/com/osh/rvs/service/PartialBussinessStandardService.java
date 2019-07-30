package com.osh.rvs.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;

import com.osh.rvs.bean.master.PartialBussinessStandardEntity;
import com.osh.rvs.form.master.PartialBussinessStandardForm;
import com.osh.rvs.mapper.master.PartialBussinessStandardMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.CodeListUtils;
import framework.huiqing.common.util.CommonStringUtil;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;
import framework.huiqing.common.util.validator.Validators;

public class PartialBussinessStandardService {
	/** 零件规格种别 **/
	private static final String PARTIAL_SPEC_KIND = "partial_spec_kind";

	/**
	 * 查询零件出入库工时标准
	 * 
	 * @param conn
	 * @return
	 */
	public List<PartialBussinessStandardForm> search(SqlSession conn) {
		PartialBussinessStandardMapper dao = conn.getMapper(PartialBussinessStandardMapper.class);

		List<PartialBussinessStandardForm> respList = new ArrayList<PartialBussinessStandardForm>();

		List<PartialBussinessStandardEntity> list = dao.search();
		if (list != null && list.size() > 0) {
			// 拷贝数据到表单对象
			BeanUtil.copyToFormList(list, respList, CopyOptions.COPYOPTIONS_NOEMPTY, PartialBussinessStandardForm.class);
		}

		// DB中保存的规格种别
		Map<String, String> dbKinds = new TreeMap<String, String>();

		for (int i = 0; i < respList.size(); i++) {
			// 规格种别
			String spec_kind = respList.get(i).getSpec_kind();

			String value = CodeListUtils.getValue(PARTIAL_SPEC_KIND, spec_kind);

			// 规格种别名称
			respList.get(i).setSpec_kind_name(value);

			dbKinds.put(spec_kind, spec_kind);
		}

		// 所有规格种别
		Map<String, String> specKindMap = CodeListUtils.getList(PARTIAL_SPEC_KIND);
		Iterator<String> keys = specKindMap.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();

			if (!dbKinds.containsKey(key)) {
				PartialBussinessStandardForm form = new PartialBussinessStandardForm();
				form.setSpec_kind(key);
				form.setSpec_kind_name(specKindMap.get(key));
				form.setBox_count("");
				form.setRecept("");
				form.setCollect_case("");
				form.setCollation_on_shelf("");
				form.setOff_shelf("");
				form.setUnpack("");

				respList.add(form);
			}
		}

		return respList;
	}

	/**
	 * 更新零件出入库工时标准
	 * 
	 * @param conn
	 * @param errors
	 */
	public void update(SqlSessionManager conn, HttpServletRequest req, List<MsgInfo> errors) {
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");

		List<PartialBussinessStandardForm> formList = new AutofillArrayList<PartialBussinessStandardForm>(PartialBussinessStandardForm.class);

		Map<String, String[]> parameters = req.getParameterMap();

		// 整理提交数据
		for (String parameterKey : parameters.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("partial_bussiness_standard".equals(entity)) {
					String column = m.group(2);
					int icounts = Integer.parseInt(m.group(3));
					String[] value = parameters.get(parameterKey);
					if ("spec_kind".equals(column)) {
						formList.get(icounts).setSpec_kind(value[0]);
					} else if ("box_count".equals(column)) {
						formList.get(icounts).setBox_count(value[0]);
					} else if ("recept".equals(column)) {
						formList.get(icounts).setRecept(value[0]);
					} else if ("collect_case".equals(column)) {
						formList.get(icounts).setCollect_case(value[0]);
					} else if ("collation_on_shelf".equals(column)) {
						formList.get(icounts).setCollation_on_shelf(value[0]);
					} else if ("off_shelf".equals(column)) {
						formList.get(icounts).setOff_shelf(value[0]);
					} else if ("unpack".equals(column)) {
						formList.get(icounts).setUnpack(value[0]);
					} else if ("box_count_flg".equals(column)) {
						formList.get(icounts).setBox_count_flg(value[0]);
					}
				}
			}
		}

		// 验证
		for (PartialBussinessStandardForm form : formList) {
			Validators v = BeanUtil.createBeanValidators(form, BeanUtil.CHECK_TYPE_ALL);

			List<MsgInfo> errs = v.validate();
			for (int i = 0; i < errs.size(); i++) {
				errs.get(i).setLineno(CodeListUtils.getValue(PARTIAL_SPEC_KIND, form.getSpec_kind()));
			}
			errors.addAll(errs);
		}

		if (errors.size() == 0) {
			String key = "validator.invalidParam.invalidMoreThanZero";
			
			// 验证负数
			for (PartialBussinessStandardForm form : formList) {
				// 装箱数量
				String boxCount = form.getBox_count();
				String boxCountFlg = form.getBox_count_flg();

				// 收货
				String recept = form.getRecept();

				// 拆盒
				String collectCase = form.getCollect_case();

				// 核对上架
				String collationOnShelf = form.getCollation_on_shelf();

				// 下架
				String offShelf = form.getOff_shelf();

				// 分装
				String unpack = form.getUnpack();

				if ("0".equals(boxCountFlg) && Double.valueOf(boxCount) <= 0) {
					MsgInfo error = new MsgInfo();
					error.setErrcode(key);
					error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage(key, CodeListUtils.getValue(PARTIAL_SPEC_KIND, form.getSpec_kind())
							+ "装箱数量"));
					errors.add(error);
				}

				if (Double.valueOf(recept) <= 0) {
					MsgInfo error = new MsgInfo();
					error.setErrcode(key);
					error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage(key, CodeListUtils.getValue(PARTIAL_SPEC_KIND, form.getSpec_kind()) + "收货"));
					errors.add(error);
				}

				if (Double.valueOf(collectCase) < 0) {
					MsgInfo error = new MsgInfo();
					error.setErrcode(key);
					error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage(key, CodeListUtils.getValue(PARTIAL_SPEC_KIND, form.getSpec_kind()) + "拆盒"));
					errors.add(error);
				}

				if (Double.valueOf(collationOnShelf) <= 0) {
					MsgInfo error = new MsgInfo();
					error.setErrcode(key);
					error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage(key, CodeListUtils.getValue(PARTIAL_SPEC_KIND, form.getSpec_kind())	+ "核对上架"));
					errors.add(error);
				}

				if (Double.valueOf(offShelf) <= 0) {
					MsgInfo error = new MsgInfo();
					error.setErrcode(key);
					error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage(key, CodeListUtils.getValue(PARTIAL_SPEC_KIND, form.getSpec_kind()) + "下架"));
					errors.add(error);
				}

				if (!CommonStringUtil.isEmpty(unpack) && Double.valueOf(unpack) <= 0) {
					MsgInfo error = new MsgInfo();
					error.setErrcode(key);
					error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage(key, CodeListUtils.getValue(PARTIAL_SPEC_KIND, form.getSpec_kind()) + "分装"));
					errors.add(error);
				}

			}
		}

		// 验证通过
		if (errors.size() == 0) {
			// 更新零件出入库工时标准
			PartialBussinessStandardMapper dao = conn.getMapper(PartialBussinessStandardMapper.class);

			for (PartialBussinessStandardForm form : formList) {
				PartialBussinessStandardEntity entity = new PartialBussinessStandardEntity();
				// 拷贝表单数据到对象
				BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
				dao.update(entity);
			}
		}
	}

	public PartialBussinessStandardForm getPartialBussinessStandardBySpecKind(String spec_kind, SqlSession conn) {
		PartialBussinessStandardEntity entity = getStandardTime(conn).get(spec_kind);
		PartialBussinessStandardForm respForm = null;

		if (entity != null) {
			respForm = new PartialBussinessStandardForm();
			BeanUtil.copyToForm(entity, respForm, CopyOptions.COPYOPTIONS_NOEMPTY);
		}
		return respForm;
	}

	public Map<String, PartialBussinessStandardEntity> getStandardTime(SqlSession conn) {
		Map<String, PartialBussinessStandardEntity> map = new TreeMap<String, PartialBussinessStandardEntity>();

		PartialBussinessStandardMapper dao = conn.getMapper(PartialBussinessStandardMapper.class);
		List<PartialBussinessStandardEntity> list = dao.search();

		if (list != null) {
			for (PartialBussinessStandardEntity entity : list) {
				map.put(entity.getSpec_kind().toString(), entity);
			}
		}

		return map;
	}
}
