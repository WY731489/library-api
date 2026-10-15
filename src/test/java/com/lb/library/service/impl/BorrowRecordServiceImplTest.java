package com.lb.library.service.impl;

import com.lb.library.entity.Book;
import com.lb.library.entity.BorrowRecord;
import com.lb.library.mapper.BookMapper;
import com.lb.library.mapper.BorrowRecordMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 借阅业务单元测试
 * 用 Mockito mock 掉 Mapper，纯单元测试，不连数据库
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("借阅业务测试")
class BorrowRecordServiceImplTest {

    @Mock
    private BookMapper bookMapper;
    @Mock
    private BorrowRecordMapper borrowRecordMapper;

    @InjectMocks
    private BorrowRecordServiceImpl borrowRecordService;

    private Book testBook;
    private BorrowRecord testRecord;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId(1);
        testBook.setBookName("Java编程思想");
        testBook.setStock(5);

        testRecord = new BorrowRecord();
        testRecord.setId(10);
        testRecord.setBookId(1);
        testRecord.setUserId(2);
        testRecord.setStatus(0); // 未归还
    }

    // ============ 借阅图书 ============
    @Nested
    @DisplayName("借阅图书 borrowBook()")
    class BorrowBookTest {

        @Test
        @DisplayName("图书不存在 - 抛异常")
        void borrowBook_BookNotFound_ThrowsException() {
            when(bookMapper.selectById(1)).thenReturn(null);

            assertThatThrownBy(() -> borrowRecordService.borrowBook(1, 2))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("图书不存在");

            // 校验：异常分支下不应该继续扣库存、插记录
            verify(bookMapper, never()).updateById(any());
            verify(borrowRecordMapper, never()).insert(any());
        }

        @Test
        @DisplayName("库存为0 - 抛异常")
        void borrowBook_StockZero_ThrowsException() {
            testBook.setStock(0);
            when(bookMapper.selectById(1)).thenReturn(testBook);

            assertThatThrownBy(() -> borrowRecordService.borrowBook(1, 2))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("库存不足");

            verify(bookMapper, never()).updateById(any());
            verify(borrowRecordMapper, never()).insert(any());
        }

        @Test
        @DisplayName("库存为 null - 抛异常")
        void borrowBook_StockNull_ThrowsException() {
            testBook.setStock(null);
            when(bookMapper.selectById(1)).thenReturn(testBook);

            assertThatThrownBy(() -> borrowRecordService.borrowBook(1, 2))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("库存不足");
        }

        @Test
        @DisplayName("借阅成功 - 扣库存 + 插记录")
        void borrowBook_Success_DecreasesStockAndInsertsRecord() {
            when(bookMapper.selectById(1)).thenReturn(testBook);

            boolean result = borrowRecordService.borrowBook(1, 2);

            assertThat(result).isTrue();

            // 校验：库存从 5 变成 4
            ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
            verify(bookMapper).updateById(bookCaptor.capture());
            assertThat(bookCaptor.getValue().getStock()).isEqualTo(4);

            // 校验：插入了借阅记录，且 userId、bookId 正确
            ArgumentCaptor<BorrowRecord> recordCaptor = ArgumentCaptor.forClass(BorrowRecord.class);
            verify(borrowRecordMapper).insert(recordCaptor.capture());
            BorrowRecord inserted = recordCaptor.getValue();
            assertThat(inserted.getBookId()).isEqualTo(1);
            assertThat(inserted.getUserId()).isEqualTo(2);
            assertThat(inserted.getStatus()).isEqualTo(0);
            assertThat(inserted.getBorrowTime()).isNotNull();
            assertThat(inserted.getShouldReturnTime()).isNotNull();
            // 应还时间在借出时间之后（30天）
            assertThat(inserted.getShouldReturnTime()).isAfter(inserted.getBorrowTime());
        }
    }

    // ============ 归还图书 ============
    @Nested
    @DisplayName("归还图书 returnBook()")
    class ReturnBookTest {

        @Test
        @DisplayName("记录不存在 - 抛异常")
        void returnBook_RecordNotFound_ThrowsException() {
            when(borrowRecordMapper.selectById(99)).thenReturn(null);

            assertThatThrownBy(() -> borrowRecordService.returnBook(99, 2, 0))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("借阅记录不存在");
        }

        @Test
        @DisplayName("已归还 - 抛异常（防止重复归还）")
        void returnBook_AlreadyReturned_ThrowsException() {
            testRecord.setStatus(1); // 已归还
            when(borrowRecordMapper.selectById(10)).thenReturn(testRecord);

            assertThatThrownBy(() -> borrowRecordService.returnBook(10, 2, 0))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("已归还");
        }

        @Test
        @DisplayName("读者归还他人记录 - 抛异常（无权限）")
        void returnBook_ReaderNotOwner_ThrowsException() {
            // 记录属于 userId=2，但 userId=3 来归还
            testRecord.setUserId(2);
            when(borrowRecordMapper.selectById(10)).thenReturn(testRecord);

            assertThatThrownBy(() -> borrowRecordService.returnBook(10, 3, 0))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("无权限");
        }

        @Test
        @DisplayName("读者归还自己记录 - 成功")
        void returnBook_ReaderReturnsOwnRecord_Success() {
            testRecord.setUserId(2);
            when(borrowRecordMapper.selectById(10)).thenReturn(testRecord);
            when(bookMapper.selectById(1)).thenReturn(testBook);

            boolean result = borrowRecordService.returnBook(10, 2, 0);

            assertThat(result).isTrue();

            // 校验：记录状态变成 1，returnTime 被设置
            ArgumentCaptor<BorrowRecord> recordCaptor = ArgumentCaptor.forClass(BorrowRecord.class);
            verify(borrowRecordMapper).updateById(recordCaptor.capture());
            BorrowRecord updated = recordCaptor.getValue();
            assertThat(updated.getStatus()).isEqualTo(1);
            assertThat(updated.getReturnTime()).isNotNull();

            // 校验：图书库存 +1（5 → 6）
            ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
            verify(bookMapper).updateById(bookCaptor.capture());
            assertThat(bookCaptor.getValue().getStock()).isEqualTo(6);
        }

        @Test
        @DisplayName("管理员代还他人记录 - 成功（绕过归属校验）")
        void returnBook_AdminCanReturnOthers_Success() {
            // 记录属于 userId=2，管理员(userId=1, role=1)代还
            testRecord.setUserId(2);
            when(borrowRecordMapper.selectById(10)).thenReturn(testRecord);
            when(bookMapper.selectById(1)).thenReturn(testBook);

            boolean result = borrowRecordService.returnBook(10, 1, 1);

            assertThat(result).isTrue();
            // 校验：没有走权限拒绝分支（不会抛"无权限"）
            verify(borrowRecordMapper).updateById(any(BorrowRecord.class));
            verify(bookMapper).updateById(any(Book.class));
        }

        @Test
        @DisplayName("库存为 null 时归还 - 库存变 1 不抛 NPE")
        void returnBook_BookStockNull_Success() {
            testBook.setStock(null);
            when(borrowRecordMapper.selectById(10)).thenReturn(testRecord);
            when(bookMapper.selectById(1)).thenReturn(testBook);

            boolean result = borrowRecordService.returnBook(10, 2, 0);

            assertThat(result).isTrue();
            ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
            verify(bookMapper).updateById(bookCaptor.capture());
            // null → 0 + 1 = 1
            assertThat(bookCaptor.getValue().getStock()).isEqualTo(1);
        }
    }
}
