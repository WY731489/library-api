package com.lb.library.service.impl;

import com.lb.library.entity.Category;
import com.lb.library.mapper.CategoryMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 分类业务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("分类业务测试")
class CategoryServiceImplTest {

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Nested
    @DisplayName("新增分类 addCategory()")
    class AddCategoryTest {

        @Test
        @DisplayName("新增成功 - 返回 true")
        void addCategory_Success() {
            Category category = new Category();
            category.setCategoryName("计算机");
            when(categoryMapper.insert(any(Category.class))).thenReturn(1);

            boolean result = categoryService.addCategory(category);

            assertThat(result).isTrue();

            ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
            verify(categoryMapper).insert(captor.capture());
            assertThat(captor.getValue().getCategoryName()).isEqualTo("计算机");
        }
    }

    @Nested
    @DisplayName("修改分类 updateCategory()")
    class UpdateCategoryTest {

        @Test
        @DisplayName("修改成功")
        void updateCategory_Success() {
            Category category = new Category();
            category.setId(1);
            category.setCategoryName("计算机科学");
            when(categoryMapper.updateById(any(Category.class))).thenReturn(1);

            boolean result = categoryService.updateCategory(category);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("修改的记录不存在 - 返回 false")
        void updateCategory_NotFound_ReturnsFalse() {
            when(categoryMapper.updateById(any(Category.class))).thenReturn(0);

            boolean result = categoryService.updateCategory(new Category());

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("删除分类 deleteCategory()")
    class DeleteCategoryTest {

        @Test
        @DisplayName("删除成功")
        void deleteCategory_Success() {
            when(categoryMapper.deleteById(1)).thenReturn(1);

            boolean result = categoryService.deleteCategory(1);

            assertThat(result).isTrue();
            verify(categoryMapper).deleteById(1);
        }
    }

    @Nested
    @DisplayName("查询分类列表 listCategories()")
    class ListCategoriesTest {

        @Test
        @DisplayName("查询成功 - 返回分类列表")
        void listCategories_Success() {
            Category c1 = new Category();
            c1.setId(1);
            c1.setCategoryName("计算机");
            Category c2 = new Category();
            c2.setId(2);
            c2.setCategoryName("文学");
            when(categoryMapper.selectList(any())).thenReturn(Arrays.asList(c1, c2));

            List<Category> result = categoryService.listCategories();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getCategoryName()).isEqualTo("计算机");
            assertThat(result.get(1).getCategoryName()).isEqualTo("文学");
        }

        @Test
        @DisplayName("无分类 - 返回空列表")
        void listCategories_Empty() {
            when(categoryMapper.selectList(any())).thenReturn(Collections.emptyList());

            List<Category> result = categoryService.listCategories();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("按 id 查分类 getById()")
    class GetByIdTest {

        @Test
        @DisplayName("查询成功 - 返回分类")
        void getById_Success() {
            Category category = new Category();
            category.setId(1);
            category.setCategoryName("计算机");
            when(categoryMapper.selectById(1)).thenReturn(category);

            Category result = categoryService.getById(1);

            assertThat(result).isNotNull();
            assertThat(result.getCategoryName()).isEqualTo("计算机");
        }

        @Test
        @DisplayName("id 不存在 - 返回 null")
        void getById_NotFound_ReturnsNull() {
            when(categoryMapper.selectById(99)).thenReturn(null);

            Category result = categoryService.getById(99);

            assertThat(result).isNull();
        }
    }
}
