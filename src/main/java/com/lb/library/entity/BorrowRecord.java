package com.lb.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 借阅记录实体类（对应 borrow_record 表）
 */
@Data
@TableName("borrow_record")
public class BorrowRecord {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 用户id（借阅人） */
    private Integer userId;

    /** 图书id */
    private Integer bookId;

    /** 借阅时间 */
    private LocalDateTime borrowTime;

    /** 归还时间 */
    private LocalDateTime returnTime;

    /** 应归还时间 */
    @TableField("should_return_time")
    private LocalDateTime shouldReturnTime;

    /** 状态: 0未归还 1已归还 */
    private Integer status;
}
