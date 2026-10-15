package com.lb.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lb.library.entity.Book;
import com.lb.library.entity.BorrowRecord;
import com.lb.library.mapper.BookMapper;
import com.lb.library.mapper.BorrowRecordMapper;
import com.lb.library.service.BorrowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorrowRecordServiceImpl implements BorrowRecordService {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean borrowBook(Integer bookId, Integer userId) {
        // 1. 校验图书是否存在
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }
        // 2. 校验库存
        if (book.getStock() == null || book.getStock() <= 0) {
            throw new RuntimeException("库存不足，借阅失败");
        }
        // 3. 库存 -1
        book.setStock(book.getStock() - 1);
        bookMapper.updateById(book);

        // 4. 新增借阅记录
        BorrowRecord record = new BorrowRecord();
        record.setBookId(bookId);
        record.setUserId(userId);
        record.setBorrowTime(LocalDateTime.now());
        // 应归还时间 = 借阅时间 + 30 天
        record.setShouldReturnTime(LocalDateTime.now().plusDays(30));
        record.setStatus(0);
        borrowRecordMapper.insert(record);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean returnBook(Integer recordId, Integer userId, Integer role) {
        // 1. 校验借阅记录是否存在
        BorrowRecord record = borrowRecordMapper.selectById(recordId);
        if (record == null) {
            throw new RuntimeException("借阅记录不存在");
        }
        // 2. 校验是否已归还
        if (record.getStatus() != null && record.getStatus() == 1) {
            throw new RuntimeException("该图书已归还，请勿重复操作");
        }
        // 3. 权限校验：读者只能归还自己的记录，管理员可以代还
        if (role == null || role != 1) {
            if (!record.getUserId().equals(userId)) {
                throw new RuntimeException("无权限，只能归还自己的借阅记录");
            }
        }
        // 4. 更新记录状态为已归还
        record.setStatus(1);
        record.setReturnTime(LocalDateTime.now());
        borrowRecordMapper.updateById(record);

        // 5. 图书库存 +1
        Book book = bookMapper.selectById(record.getBookId());
        if (book != null) {
            book.setStock((book.getStock() == null ? 0 : book.getStock()) + 1);
            bookMapper.updateById(book);
        }
        return true;
    }

    @Override
    public List<BorrowRecord> listBorrowRecords(Integer userId, Integer role) {
        QueryWrapper<BorrowRecord> wrapper = new QueryWrapper<>();
        // 读者只能查看自己的借阅记录，管理员可查看全部
        if (role == null || role != 1) {
            wrapper.eq("user_id", userId);
        }
        // 按 id 倒序，最新借阅的排在前面
        wrapper.orderByDesc("id");
        return borrowRecordMapper.selectList(wrapper);
    }
}
