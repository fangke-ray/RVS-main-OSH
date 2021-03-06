package com.osh.rvs.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.data.MaterialTagEntity;
import com.osh.rvs.mapper.data.MaterialTagMapper;

import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

/**
 * 
 * @Description 维修对象属性标签
 * @author dell
 * @date 2020-9-2 上午11:18:15
 */
public class MaterialTagService {

	public static final int TAG_ANIMAL_EXPR = 1;
	public static final int TAG_DISINFECT = 4;
	public static final int TAG_STERIZE = 5;

	private static Set<String> anml_materials = null;

	/**
	 * 新建
	 * 
	 * @param form
	 * @param conn
	 */
	public void insert(ActionForm form, SqlSessionManager conn) {
		MaterialTagMapper dao = conn.getMapper(MaterialTagMapper.class);

		MaterialTagEntity entity = new MaterialTagEntity();
		BeanUtil.copyToBean(form, entity, CopyOptions.COPYOPTIONS_NOEMPTY);

		dao.insert(entity);

		if (TAG_ANIMAL_EXPR == entity.getTag_type()) {
			resetAnmlMaterials(conn);
		}
	}

	/**
	 * 根据维修对象ID删除 维修对象属性标签
	 * 
	 * @param materialId 维修对象ID
	 * @param conn
	 */
	public void deleteByMaterialId(String materialId, SqlSessionManager conn) {
		MaterialTagMapper dao = conn.getMapper(MaterialTagMapper.class);
		dao.deleteByMaterialId(materialId);
	}

	/**
	 * 确认后删除/新增维修对象标签
	 * 
	 * @param material_id 维修品 ID
	 * @param tag_type 标签
	 * @param checked 提交勾选
	 * @param conn
	 */
	public void updataTagByMaterialId(String material_id, Integer tag_type, boolean checked, SqlSessionManager conn) {
		MaterialTagMapper mapper = conn.getMapper(MaterialTagMapper.class);

		List<Integer> result = mapper.checkTagByMaterialId(material_id, tag_type + "");

		if (checked) {
			// 不存在则插入
			if (result == null || result.size() == 0) {
				MaterialTagEntity entity = new MaterialTagEntity();
				entity.setMaterial_id(material_id);
				entity.setTag_type(tag_type);
				mapper.insert(entity);
			}
		} else {
			// 存在则删除
			if (result != null && result.size() > 0) {
				mapper.deleteTagByMaterialId(material_id, tag_type + "");
			}
		}

		if (TAG_ANIMAL_EXPR == tag_type) {
			resetAnmlMaterials(conn);
		}
	}

	public List<Integer> checkTagByMaterialId(String material_id, Integer tag_type, SqlSession conn) {
		MaterialTagMapper mapper = conn.getMapper(MaterialTagMapper.class);
		return mapper.checkTagByMaterialId(material_id, tag_type + "");
	}

	/**
	 * 取得维修品设定的消毒灭菌流程
	 * 
	 * @param material_id
	 * @param conn
	 * @return
	 */
	public String getDisinectFlow(String material_id, SqlSession conn) {
		MaterialTagMapper mapper = conn.getMapper(MaterialTagMapper.class);
		List<Integer> tagsByMaterialId = mapper.checkTagByMaterialId(material_id, null);
		for (Integer tag : tagsByMaterialId) {
			switch(tag) {
			case TAG_DISINFECT : return "00000000010";
			case TAG_STERIZE : return "00000000011";
			}
		}

		return null;
	}

	/**
	 * @return the anml_materials
	 */
	public static Set<String> getAnmlMaterials(SqlSession conn) {
		if (anml_materials == null) {
			if (conn == null) {
				return new HashSet<String>();
			} else {
				resetAnmlMaterials(conn);
			}
		}
		return anml_materials;
	}
	public static void clearAnmlMaterials() {
		anml_materials = null;
	}
	public static void resetAnmlMaterials(SqlSession conn) {
		MaterialTagMapper mapper = conn.getMapper(MaterialTagMapper.class);
		List<String> lAnmlWipMaterials = mapper.getAnmlWipMaterials();
		anml_materials = new HashSet<String>();
		for (String material_id : lAnmlWipMaterials) {
			anml_materials.add(material_id);
		}
	}
}
