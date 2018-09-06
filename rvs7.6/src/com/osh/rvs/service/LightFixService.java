package com.osh.rvs.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;

import com.osh.rvs.bean.LoginData;
import com.osh.rvs.bean.master.LightFixEntity;
import com.osh.rvs.common.RvsConsts;
import com.osh.rvs.form.master.LightFixForm;
import com.osh.rvs.mapper.CommonMapper;
import com.osh.rvs.mapper.master.LightFixMapper;

import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;
import framework.huiqing.common.util.message.ApplicationMessage;

public class LightFixService {

	/**
	 * 检索记录列表
	 * 
	 * @param form 提交表单
	 * @param conn 数据库连接
	 * @return List<LightFixForm> 查询结果表单
	 */
	public List<LightFixForm> search(LightFixForm form, SqlSession conn) {

		// 表单复制到数据对象
		LightFixEntity conditionBean = new LightFixEntity();
		BeanUtil.copyToBean(form, conditionBean, null);

		// 从数据库中查询记录
		LightFixMapper dao = conn.getMapper(LightFixMapper.class);
		List<LightFixEntity> lResultBean = dao.searchLightFix(conditionBean);

		// 建立页面返回表单
		List<LightFixForm> lResultForm = new ArrayList<LightFixForm>();

		// 数据对象复制到表单
		BeanUtil.copyToFormList(lResultBean, lResultForm, null, LightFixForm.class);

		return lResultForm;
	}

	/**
	 * 按照主键检索单条记录
	 * 
	 * @param light_fix_id
	 * @param conn 数据库连接
	 * @return LightFixForm 查询结果表单
	 */
	public LightFixForm getDetail(String light_fix_id, SqlSession conn) {
		// 从数据库中查询记录
		LightFixMapper dao = conn.getMapper(LightFixMapper.class);
		LightFixEntity resultBean = dao.getLightFix(light_fix_id);

		// 建立页面返回表单
		LightFixForm resultForm = new LightFixForm();
		BeanUtil.copyToForm(resultBean, resultForm, null);

		List<String> kind_list = dao.getKinds(light_fix_id);
		List<String> position_list = dao.getPositions(light_fix_id);
		resultForm.setKind_list(kind_list);
		resultForm.setPosition_list(position_list);

		return resultForm;
	}

	/**
	 * 执行插入
	 * 
	 * @param errors
	 * @param form 提交表单
	 * @param session 当前用户会话
	 * @param conn 数据库连接
	 * @throws Exception
	 */
	public void insert(List<MsgInfo> errors, LightFixForm form, HttpSession session, SqlSessionManager conn)
			throws Exception {
		// 表单复制到数据对象
		LightFixEntity insertBean = new LightFixEntity();
		BeanUtil.copyToBean(form, insertBean, CopyOptions.COPYOPTIONS_NOEMPTY);

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		insertBean.setUpdated_by(user.getOperator_id());

		// 新建记录插入到数据库中
		LightFixMapper dao = conn.getMapper(LightFixMapper.class);
		
		// 检查修理代码和机种是否存在
		List<LightFixEntity> lKinds = dao.checkCodeAndCategoryIsExist(insertBean.getActivity_code());
		
		if(lKinds!=null && lKinds.size()>0){
			// 判断机种是否重复
			for(LightFixEntity entity:lKinds){
				//机种
				String kind = entity.getKind().toString();
				
				if(form.getKind_list().contains(kind)){
					MsgInfo error = new MsgInfo();
					error.setErrcode("dbaccess.recordDuplicated");
					error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordDuplicated", "修理代码"));
					errors.add(error);
					return;
				}
			}
		}
		
		dao.insertLightFix(insertBean);

		// 取得刚才插入的主键
		CommonMapper commonMapper = conn.getMapper(CommonMapper.class);
		String lastInsertId = commonMapper.getLastInsertID();
		insertBean.setLight_fix_id(lastInsertId);

		// 小修理对应机种插入到数据库中
		List<String> kind_list = form.getKind_list();
		for (String kind : kind_list) {
			insertBean.setKind(Integer.parseInt(kind));
			dao.insertKind(insertBean);
		}

		// 小修理流程工位插入到数据库中
		List<String> position_list = form.getPosition_list();
		for (String position_id : position_list) {
			insertBean.setPosition_id(position_id);
			dao.insertPosition(insertBean);
		}
	}

	/**
	 * 执行更新
	 * 
	 * @param errors
	 * @param form 提交表单
	 * @param session 当前用户会话
	 * @param conn 数据库连接
	 * @throws Exception
	 */
	public void update(List<MsgInfo> errors, LightFixForm form, HttpSession session, SqlSessionManager conn)
			throws Exception {
		// 表单复制到数据对象
		LightFixEntity updateBean = new LightFixEntity();
		BeanUtil.copyToBean(form, updateBean, CopyOptions.COPYOPTIONS_NOEMPTY);

		LoginData user = (LoginData) session.getAttribute(RvsConsts.SESSION_USER);
		updateBean.setUpdated_by(user.getOperator_id());

		// 更新数据库中记录
		LightFixMapper dao = conn.getMapper(LightFixMapper.class);
		
		// 检查修理代码和机种是否存在
		List<LightFixEntity> lKinds = dao.checkCodeAndCategoryIsExist(updateBean.getActivity_code());
		
		if(lKinds!=null && lKinds.size()>0){
			// 判断机种是否重复
			for(LightFixEntity entity:lKinds){
				// 小修理标准编制ID
				String dbLightFixId = entity.getLight_fix_id();
				
				// 与其他标准编制比较机种是否重复
				if(!dbLightFixId.equals(updateBean.getLight_fix_id())){
					//机种
					String kind = entity.getKind().toString();
					 
					if(form.getKind_list().contains(kind)){
						MsgInfo error = new MsgInfo();
						error.setErrcode("dbaccess.recordDuplicated");
						error.setErrmsg(ApplicationMessage.WARNING_MESSAGES.getMessage("dbaccess.recordDuplicated", "修理代码"));
						errors.add(error);
						return;
					}
				}
			}
		}
		
		// 小修理对应机种更新到数据库中
		dao.deleteKind(updateBean.getLight_fix_id());
		List<String> kind_list = form.getKind_list();
		for (String kind : kind_list) {
			updateBean.setKind(Integer.parseInt(kind));
			dao.insertKind(updateBean);
		}

		// 小修理流程工位更新到数据库中
		dao.deletePosition(updateBean.getLight_fix_id());
		List<String> position_list = form.getPosition_list();
		for (String position_id : position_list) {
			updateBean.setPosition_id(position_id);
			dao.insertPosition(updateBean);
		}

		dao.updateLightFix(updateBean);
	}

	/**
	 * 执行逻辑删除
	 * 
	 * @param light_fix_id
	 * @param conn 数据库连接
	 * @throws Exception
	 */
	public void delete(String light_fix_id, SqlSessionManager conn)
			throws Exception {
		// 在数据库中逻辑删除记录
		LightFixMapper dao = conn.getMapper(LightFixMapper.class);
		dao.deleteLightFix(light_fix_id);
		dao.deleteKind(light_fix_id);
		dao.deletePosition(light_fix_id);
	}
}
