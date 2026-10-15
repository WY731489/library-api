package com.lb.library.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lb.library.entity.Book;

import java.util.List;

/**
 * 图书 Service
 */
public interface BookService {

    /** 新增图书 */
    boolean addBook(Book book);

    /** 查询图书列表 */
    List<Book> listBooks();

    /** 按书名或作者模糊搜索 */
    List<Book> searchBooks(String keyword);

    /** 分页查询图书（可同时按关键词 + 分类筛选） */
    IPage<Book> pageBooks(Integer pageNum, Integer pageSize, String keyword, Integer categoryId);
}
