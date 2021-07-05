/**
 * 系统名：OSH-RVS<br>
 * 模块名：系统管理<br>
 * 机能名：消耗品在线一览Service<br>
 * @author 龚镭敏
 * @version 0.01
 */
package com.osh.rvs.service.partial;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;

import com.osh.rvs.bean.partial.ConsumableOnlineEntity;
import com.osh.rvs.form.partial.ConsumableOnlineForm;
import com.osh.rvs.mapper.partial.ConsumableOnlineMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.AutofillArrayList;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.validator.Validators;

public class ConsumableOnlineService {

	/**
	 * 消耗品在线一览查询
	 * 
	 * @param entity
	 * @param conn
	 * @throws Exception
	 */
	public List<ConsumableOnlineForm> searchOnlineList(ConsumableOnlineEntity entity, SqlSession conn) throws Exception {

		// 从数据库中查询记录
		ConsumableOnlineMapper dao = conn.getMapper(ConsumableOnlineMapper.class);
		List<ConsumableOnlineEntity> onlineList = dao.searchOnlineList(entity);

		// 建立页面返回表单
		List<ConsumableOnlineForm> lResultForm = new ArrayList<ConsumableOnlineForm>();

		// 数据对象复制到表单
		BeanUtil.copyToFormList(onlineList, lResultForm, null, ConsumableOnlineForm.class);

		return lResultForm;
	}

	/**
	 * 消耗品在线一览清点
	 * @param parameterMap
	 * @param session
	 * @param conn
	 * @param errors
	 * @throws Exception
	 */
	public void updateOnlineList(Map<String, String[]> parameterMap, HttpSession session, SqlSessionManager conn, List<MsgInfo> errors) {

		// 消耗品在线一览Mapper定义
		ConsumableOnlineMapper dao = conn.getMapper(ConsumableOnlineMapper.class);

		// 消耗品在线一览Entity定义
		ConsumableOnlineEntity consumableOnlineEntity = new ConsumableOnlineEntity();

		List<ConsumableOnlineForm> consumableOnlineForms = new AutofillArrayList<ConsumableOnlineForm>(ConsumableOnlineForm.class);
		Pattern p = Pattern.compile("(\\w+).(\\w+)\\[(\\d+)\\]");

		// 整理提交数据
		for (String parameterKey : parameterMap.keySet()) {
			Matcher m = p.matcher(parameterKey);
			if (m.find()) {
				String entity = m.group(1);
				if ("consumable_online".equals(entity)) {
					String column = m.group(2);
					int icounts = Integer.parseInt(m.group(3));
					String[] value = parameterMap.get(parameterKey);

					// 检索前台数据
					if ("section_id".equals(column)) {
						consumableOnlineForms.get(icounts).setCourse(value[0]);
					} else if ("line_id".equals(column)) {
						consumableOnlineForms.get(icounts).setProject(value[0]);
					} else if ("partial_id".equals(column)) {
						consumableOnlineForms.get(icounts).setPartial_id(value[0]);
					} else if ("quantity_modify".equals(column)) {
						consumableOnlineForms.get(icounts).setQuantity_modify(value[0]);
					}
				}
			}
		}

		// 检查每个Form
		for (ConsumableOnlineForm consumableOnlineForm : consumableOnlineForms) {
			Validators v = BeanUtil.createBeanValidators(consumableOnlineForm, BeanUtil.CHECK_TYPE_PASSEMPTY);
			List<MsgInfo> thisErrors = v.validate();
			if (thisErrors.size() > 0) {
				for (MsgInfo thisError : thisErrors) {
					errors.add(thisError);
				}
			// 放入数据库
			} else if (thisErrors.size() == 0) {
				BeanUtil.copyToBean(consumableOnlineForm, consumableOnlineEntity, CopyOptions.COPYOPTIONS_NOEMPTY);
				// 更新
				dao.updateOnlineList(consumableOnlineEntity);
			}
		}
	}
}
