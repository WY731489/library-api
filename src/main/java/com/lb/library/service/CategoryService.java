package com.lb.library.service;

import com.lb.library.entity.Category;

import java.util.List;

/**
 * 图书分类 Service
 */
public interface CategoryService {

    /** 新增分类 */
    boolean addCategory(Category category);

    /** 修改分类 */
    boolean updateCategory(Category category);

    /** 删除分类 */
    boolean deleteCategory(Integer id);

    /** 查询全部分类 */
    List<Category> listCategories();

    /** 按 id 查分类 */
    Category getById(Integer id);
}
