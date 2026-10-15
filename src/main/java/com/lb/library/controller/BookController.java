package com.lb.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lb.library.common.Result;
import com.lb.library.entity.Book;
import com.lb.library.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book")
@Tag(name = "图书管理", description = "图书新增、查询、搜索、分页")
public class BookController {

    @Autowired
    private BookService bookService;

    @Operation(summary = "新增图书（仅管理员）")
    @PostMapping("/add")
    public Result<Boolean> addBook(@RequestBody Book book,
                                   @RequestAttribute Integer role) {
        if (role == null || role != 1) {
            return Result.error(403, "无权限，仅管理员可新增图书");
        }
        return Result.success(bookService.addBook(book));
    }

    @Operation(summary = "查询图书列表")
    @GetMapping("/list")
    public Result<List<Book>> listBooks() {
        return Result.success(bookService.listBooks());
    }

    @Operation(summary = "按书名或作者模糊搜索")
    @GetMapping("/search")
    public Result<List<Book>> searchBooks(@Parameter(description = "关键词") @RequestParam(required = false) String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return Result.success(bookService.listBooks());
        }
        return Result.success(bookService.searchBooks(keyword.trim()));
    }

    @Operation(summary = "分页查询图书（可按关键词+分类筛选）")
    @GetMapping("/page")
    public Result<IPage<Book>> pageBooks(
            @Parameter(description = "页码，从 1 开始", example = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数", example = "10") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "关键词（书名/作者）") @RequestParam(required = false) String keyword,
            @Parameter(description = "分类 id") @RequestParam(required = false) Integer categoryId) {
        return Result.success(bookService.pageBooks(pageNum, pageSize, keyword, categoryId));
    }
}
