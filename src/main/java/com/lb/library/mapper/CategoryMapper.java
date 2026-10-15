package com.lb.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lb.library.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 图书分类 Mapper（继承 BaseMapper 自带 17 个通用方法）
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
