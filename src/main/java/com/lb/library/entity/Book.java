package com.lb.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

/**
 * 图书实体类（对应 book 表）
 */
@Data
@TableName("book")
public class Book {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 图书名称 */
    private String bookName;

    /** 作者 */
    private String author;

    /** 分类 id */
    private Integer categoryId;

    /** 库存 */
    private Integer stock;

    /** 出版日期 */
    private LocalDate publishTime;
}
