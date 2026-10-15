package com.lb.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lb.library.entity.Book;
import com.lb.library.mapper.BookMapper;
import com.lb.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookMapper bookMapper;

    @Override
    public boolean addBook(Book book) {
        return bookMapper.insert(book) > 0;
    }

    @Override
    public List<Book> listBooks() {
        QueryWrapper<Book> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        return bookMapper.selectList(wrapper);
    }

    @Override
    public List<Book> searchBooks(String keyword) {
        QueryWrapper<Book> wrapper = new QueryWrapper<>();
        wrapper.and(w -> w.like("book_name", keyword).or().like("author", keyword));
        wrapper.orderByDesc("id");
        return bookMapper.selectList(wrapper);
    }

    @Override
    public IPage<Book> pageBooks(Integer pageNum, Integer pageSize, String keyword, Integer categoryId) {
        Page<Book> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Book> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like("book_name", kw).or().like("author", kw));
        }
        if (categoryId != null) {
            wrapper.eq("category_id", categoryId);
        }
        wrapper.orderByDesc("id");
        return bookMapper.selectPage(page, wrapper);
    }
}
