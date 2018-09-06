package com.osh.rvs.service.qa;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.osh.rvs.bean.data.QaResultEntity;
import com.osh.rvs.form.data.QaResultForm;
import com.osh.rvs.mapper.data.QaResultMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;

public class QaResultService {
	Logger logger = Logger.getLogger("QaResultService");


	public List<QaResultForm> searchQaResult(QaResultEntity qaResultEntity,SqlSession conn, List<MsgInfo> errors) {
		List<QaResultForm> listForm = new ArrayList<QaResultForm>();
		QaResultMapper dao = conn.getMapper(QaResultMapper.class);
		if (qaResultEntity != null) {
			List<QaResultEntity> list = dao.searchQaResult(qaResultEntity);
			// 数据复制到表单对象
			BeanUtil.copyToFormList(list, listForm, null, QaResultForm.class);
			return listForm;
		} else {
			return null;
		}
	}
}
