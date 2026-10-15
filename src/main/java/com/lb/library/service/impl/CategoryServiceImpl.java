package com.lb.library.service.impl;

import com.lb.library.entity.Category;
import com.lb.library.mapper.CategoryMapper;
import com.lb.library.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public boolean addCategory(Category category) {
        return categoryMapper.insert(category) > 0;
    }

    @Override
    public boolean updateCategory(Category category) {
        return categoryMapper.updateById(category) > 0;
    }

    @Override
    public boolean deleteCategory(Integer id) {
        return categoryMapper.deleteById(id) > 0;
    }

    @Override
    public List<Category> listCategories() {
        return categoryMapper.selectList(null);
    }

    @Override
    public Category getById(Integer id) {
        return categoryMapper.selectById(id);
    }
}
