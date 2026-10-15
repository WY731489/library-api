package com.lb.library.controller;

import com.lb.library.common.Result;
import com.lb.library.entity.Category;
import com.lb.library.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 图书分类接口
 */
@RestController
@RequestMapping("/api/category")
@Tag(name = "图书分类", description = "分类的增删改查")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "新增分类（管理员）")
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody Category category,
                               @RequestAttribute Integer role) {
        if (role == null || role != 1) {
            return Result.error(403, "无权限，仅管理员可操作");
        }
        return Result.success(categoryService.addCategory(category));
    }

    @Operation(summary = "修改分类（管理员）")
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody Category category,
                                  @RequestAttribute Integer role) {
        if (role == null || role != 1) {
            return Result.error(403, "无权限，仅管理员可操作");
        }
        return Result.success(categoryService.updateCategory(category));
    }

    @Operation(summary = "删除分类（管理员）")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Integer id,
                                  @RequestAttribute Integer role) {
        if (role == null || role != 1) {
            return Result.error(403, "无权限，仅管理员可操作");
        }
        return Result.success(categoryService.deleteCategory(id));
    }

    @Operation(summary = "查询全部分类（登录用户）")
    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.success(categoryService.listCategories());
    }

    @Operation(summary = "按 id 查分类")
    @GetMapping("/{id}")
    public Result<Category> getById(@PathVariable Integer id) {
        return Result.success(categoryService.getById(id));
    }
}
