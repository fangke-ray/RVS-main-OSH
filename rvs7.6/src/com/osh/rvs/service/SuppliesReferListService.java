package com.osh.rvs.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.master.SuppliesReferListEntity;
import com.osh.rvs.form.master.SuppliesReferListForm;
import com.osh.rvs.mapper.master.SuppliesReferListMapper;

import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

/**
 * 
 * @Description 常用采购清单
 * @author liuxb
 * @date 2021-11-30 上午10:26:15
 */
public class SuppliesReferListService {
	/**
	 * 查询常用采购清单
	 * 
	 * @param form
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public List<SuppliesReferListForm> search(ActionForm form, SqlSession conn) throws Exception {
		SuppliesReferListMapper dao = conn.getMapper(SuppliesReferListMapper.class);

		SuppliesReferListEntity entity = new SuppliesReferListEntity();
		// 拷贝数据
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		// 检索
		List<SuppliesReferListEntity> list = dao.search(entity);

		List<SuppliesReferListForm> respList = new ArrayList<SuppliesReferListForm>();
		BeanUtil.copyToFormList(list, respList, CopyOptions.COPYOPTIONS_NOEMPTY, SuppliesReferListForm.class);

		return respList;
	}

	/**
	 * 新建常用采购清单
	 * 
	 * @param form
	 * @param conn
	 * @throws Exception
	 */
	public void insert(ActionForm form, SqlSessionManager conn) throws Exception {
		SuppliesReferListMapper dao = conn.getMapper(SuppliesReferListMapper.class);

		SuppliesReferListEntity entity = new SuppliesReferListEntity();
		// 拷贝数据
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		// 新建
		dao.insert(entity);
	}

	/**
	 * 更新常用采购清单
	 * 
	 * @param form
	 * @param conn
	 * @throws Exception
	 */
	public void update(ActionForm form, SqlSessionManager conn) throws Exception {
		SuppliesReferListMapper dao = conn.getMapper(SuppliesReferListMapper.class);

		SuppliesReferListEntity entity = new SuppliesReferListEntity();
		// 拷贝数据
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		// 更新
		dao.update(entity);
	}
	
	/**
	 * 根据采购清单KEY查询采购清单信息
	 * @param form
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public SuppliesReferListForm getSuppliesRefer(ActionForm form,SqlSession conn) throws Exception{
		SuppliesReferListMapper dao = conn.getMapper(SuppliesReferListMapper.class);
		
		SuppliesReferListEntity connd = new SuppliesReferListEntity();
		// 拷贝数据
		BeanUtil.copyToBean(form, connd, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		//查询采购清单
		SuppliesReferListEntity entity = dao.getSuppliesReferByKey(connd.getRefer_key());
		
		SuppliesReferListForm respForm = null;
		if(entity != null){
			respForm = new SuppliesReferListForm();
			BeanUtil.copyToForm(entity, respForm, CopyOptions.COPYOPTIONS_NOEMPTY);
		}
		
		return respForm;
	}
	
	/**
	 * 根据采购清单KEY删除采购清单
	 * @param referKey 采购清单KEY
	 * @param conn
	 * @throws Exception
	 */
	public void delete(String referKey,SqlSessionManager conn) throws Exception{
		SuppliesReferListMapper dao = conn.getMapper(SuppliesReferListMapper.class);
		dao.delete(referKey);
	}

	/**
	 * 查询规格为空的记录
	 * 
	 * @param form
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public List<SuppliesReferListForm> searchEmptyModel(ActionForm form, SqlSession conn) throws Exception {
		SuppliesReferListMapper dao = conn.getMapper(SuppliesReferListMapper.class);

		SuppliesReferListEntity entity = new SuppliesReferListEntity();
		// 拷贝数据
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		// 查询规格为空的记录
		List<SuppliesReferListEntity> list = dao.searchEmptyModel(entity);

		List<SuppliesReferListForm> respList = new ArrayList<SuppliesReferListForm>();
		BeanUtil.copyToFormList(list, respList, CopyOptions.COPYOPTIONS_NOEMPTY, SuppliesReferListForm.class);

		return respList;
	}
	
	/**
	 * 更新图片UUID
	 * @param form
	 * @param conn
	 * @throws Exception
	 */
	public void updatePhotoUUID(ActionForm form,SqlSessionManager conn)throws Exception{
		SuppliesReferListMapper dao = conn.getMapper(SuppliesReferListMapper.class);

		SuppliesReferListEntity entity = new SuppliesReferListEntity();
		// 拷贝数据
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);
		
		dao.updatePhotoUuid(entity);
	}
}
