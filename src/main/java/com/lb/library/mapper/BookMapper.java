package com.lb.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lb.library.entity.Book;

/**
 * 图书 Mapper（继承 BaseMapper 即可拥有 CRUD 能力，无需 XML）
 */
public interface BookMapper extends BaseMapper<Book> {
}
