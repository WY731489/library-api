package com.lb.library.controller;

import com.lb.library.common.Result;
import com.lb.library.entity.BorrowRecord;
import com.lb.library.service.BorrowRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow")
@Tag(name = "借阅管理", description = "借书、还书、查询借阅记录")
public class BorrowController {

    @Autowired
    private BorrowRecordService borrowRecordService;

    @Operation(summary = "借阅图书（登录用户，userId 从 token 获取）")
    @PostMapping("/{bookId}")
    public Result<Boolean> borrowBook(@PathVariable Integer bookId,
                                      @RequestAttribute Integer userId) {
        return Result.success(borrowRecordService.borrowBook(bookId, userId));
    }

    @Operation(summary = "归还图书（读者只能归还自己的，管理员可以代还）")
    @PutMapping("/return/{recordId}")
    public Result<Boolean> returnBook(@PathVariable Integer recordId,
                                      @RequestAttribute Integer userId,
                                      @RequestAttribute Integer role) {
        return Result.success(borrowRecordService.returnBook(recordId, userId, role));
    }

    @Operation(summary = "查询借阅记录（读者只看自己的，管理员看全部）")
    @GetMapping("/list")
    public Result<List<BorrowRecord>> listBorrowRecords(@RequestAttribute Integer userId,
                                                        @RequestAttribute Integer role) {
        return Result.success(borrowRecordService.listBorrowRecords(userId, role));
    }
}
