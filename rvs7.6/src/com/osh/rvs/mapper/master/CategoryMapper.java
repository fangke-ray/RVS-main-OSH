package com.osh.rvs.mapper.master;

import java.util.List;

import com.osh.rvs.bean.master.CategoryEntity;

public interface CategoryMapper {

	/** insert single */
	public int insertCategory(CategoryEntity category) throws Exception;

	public int updateCategory(CategoryEntity category) throws Exception;

	/** search all */
	public List<CategoryEntity> getAllCategory();

	public CategoryEntity getCategoryByID(String category_id);

	/** search*/
	public List<CategoryEntity> searchCategory(CategoryEntity category);

	public void deleteCategory(CategoryEntity updateBean) throws Exception;
}
