package com.lb.library.service;

import com.lb.library.entity.BorrowRecord;

import java.util.List;

/**
 * 借阅记录 Service
 */
public interface BorrowRecordService {

    /** 借阅图书 */
    boolean borrowBook(Integer bookId, Integer userId);

    /** 归还图书（读者只能归还自己的，管理员可以代还） */
    boolean returnBook(Integer recordId, Integer userId, Integer role);

    /** 查询借阅记录（读者只看自己的，管理员看全部） */
    List<BorrowRecord> listBorrowRecords(Integer userId, Integer role);
}
