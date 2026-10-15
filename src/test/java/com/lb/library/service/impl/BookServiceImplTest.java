package com.lb.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lb.library.entity.Book;
import com.lb.library.mapper.BookMapper;
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
 * 图书业务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("图书业务测试")
class BookServiceImplTest {

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    @Nested
    @DisplayName("新增图书 addBook()")
    class AddBookTest {

        @Test
        @DisplayName("新增成功 - 返回 true")
        void addBook_Success_ReturnsTrue() {
            Book book = new Book();
            book.setBookName("深入理解 JVM");
            book.setStock(5);
            when(bookMapper.insert(any(Book.class))).thenReturn(1); // 1 表示影响 1 行

            boolean result = bookService.addBook(book);

            assertThat(result).isTrue();
            verify(bookMapper, times(1)).insert(any(Book.class));
        }

        @Test
        @DisplayName("插入失败（返回 0）- 返回 false")
        void addBook_InsertFails_ReturnsFalse() {
            Book book = new Book();
            book.setBookName("测试书");
            when(bookMapper.insert(any(Book.class))).thenReturn(0);

            boolean result = bookService.addBook(book);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("查询图书列表 listBooks()")
    class ListBooksTest {

        @Test
        @DisplayName("查询成功 - 返回图书列表")
        void listBooks_Success_ReturnsBooks() {
            Book b1 = new Book();
            b1.setId(1);
            b1.setBookName("Java");
            Book b2 = new Book();
            b2.setId(2);
            b2.setBookName("Spring");
            when(bookMapper.selectList(any())).thenReturn(Arrays.asList(b1, b2));

            List<Book> result = bookService.listBooks();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getBookName()).isEqualTo("Java");
            assertThat(result.get(1).getBookName()).isEqualTo("Spring");
        }

        @Test
        @DisplayName("库为空 - 返回空列表")
        void listBooks_Empty_ReturnsEmptyList() {
            when(bookMapper.selectList(any())).thenReturn(Collections.emptyList());

            List<Book> result = bookService.listBooks();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("模糊搜索 searchBooks()")
    class SearchBooksTest {

        @Test
        @DisplayName("按关键词搜索 - 返回匹配结果")
        void searchBooks_Success_ReturnsMatchedBooks() {
            Book b = new Book();
            b.setId(1);
            b.setBookName("Java 编程");
            b.setAuthor("Bruce Eckel");
            when(bookMapper.selectList(any())).thenReturn(List.of(b));

            List<Book> result = bookService.searchBooks("Java");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getBookName()).contains("Java");
        }

        @Test
        @DisplayName("无匹配 - 返回空列表")
        void searchBooks_NoMatch_ReturnsEmptyList() {
            when(bookMapper.selectList(any())).thenReturn(Collections.emptyList());

            List<Book> result = bookService.searchBooks("不存在的书");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("分页查询 pageBooks()")
    class PageBooksTest {

        @Test
        @DisplayName("分页查询成功 - 返回分页对象")
        void pageBooks_Success_ReturnsPage() {
            // 准备分页返回值
            Page<Book> mockPage = new Page<>(1, 5);
            Book b = new Book();
            b.setId(1);
            b.setBookName("Java");
            mockPage.setRecords(List.of(b));
            mockPage.setTotal(1);
            when(bookMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(mockPage);

            IPage<Book> result = bookService.pageBooks(1, 5, "Java", null);

            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
            assertThat(result.getTotal()).isEqualTo(1);
            assertThat(result.getCurrent()).isEqualTo(1);
            assertThat(result.getSize()).isEqualTo(5);
        }

        @Test
        @DisplayName("分页查询无参数 - 返回空页")
        void pageBooks_NoData_ReturnsEmptyPage() {
            Page<Book> mockPage = new Page<>(1, 5);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);
            when(bookMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(mockPage);

            IPage<Book> result = bookService.pageBooks(1, 5, null, null);

            assertThat(result.getRecords()).isEmpty();
            assertThat(result.getTotal()).isEqualTo(0);
        }

        @Test
        @DisplayName("带分类筛选的分页查询 - 调用 mapper")
        void pageBooks_WithCategory_CallsMapperWithCorrectParams() {
            Page<Book> mockPage = new Page<>(1, 5);
            mockPage.setRecords(Collections.emptyList());
            mockPage.setTotal(0);
            when(bookMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(mockPage);

            bookService.pageBooks(1, 5, "Java", 1);

            // 校验：确实调用了 selectPage，且传了分页对象和 wrapper
            verify(bookMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
        }
    }
}
