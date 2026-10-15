-- 图书借阅管理系统 数据库初始化脚本
-- 由 docker-compose 启动 MySQL 时自动挂载到 /docker-entrypoint-initdb.d/ 执行
-- 注意：MySQL 官方镜像启动时只对新建的数据库执行初始化脚本，已有数据则跳过

CREATE DATABASE IF NOT EXISTS `book_library` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `book_library`;

-- ============= 图书分类表 =============
CREATE TABLE IF NOT EXISTS category (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '分类id',
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============= 用户表（管理员 + 读者） =============
CREATE TABLE IF NOT EXISTS `user` (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(30) NOT NULL UNIQUE COMMENT '账号',
    password VARCHAR(64) NOT NULL COMMENT '密码(MD5)',
    real_name VARCHAR(20) COMMENT '姓名',
    phone VARCHAR(11) COMMENT '手机号',
    role TINYINT NOT NULL COMMENT '0读者 1管理员'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============= 图书表 =============
CREATE TABLE IF NOT EXISTS book (
    id INT PRIMARY KEY AUTO_INCREMENT,
    book_name VARCHAR(100) NOT NULL COMMENT '书名',
    author VARCHAR(50) COMMENT '作者',
    category_id INT COMMENT '分类id',
    stock INT DEFAULT 0 COMMENT '库存',
    publish_time DATE COMMENT '出版时间',
    INDEX idx_book_name(book_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============= 借阅记录表 =============
CREATE TABLE IF NOT EXISTS borrow_record (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL COMMENT '借阅人id',
    book_id INT NOT NULL COMMENT '图书id',
    borrow_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '借出时间',
    return_time DATETIME NULL COMMENT '归还时间',
    should_return_time DATETIME NOT NULL COMMENT '应还时间',
    status TINYINT DEFAULT 0 COMMENT '0未归还 1已归还',
    INDEX idx_user_book(user_id, book_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============= 初始化测试数据 =============
INSERT INTO category(category_name) VALUES
    ('计算机'), ('文学'), ('小说'), ('科普')
ON DUPLICATE KEY UPDATE category_name = VALUES(category_name);

-- 密码均为 123456 的 MD5：e10adc3949ba59abbe56e057f20f883e
INSERT INTO `user`(username, password, real_name, phone, role) VALUES
    ('admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', '13393688360', 1),
    ('reader', 'e10adc3949ba59abbe56e057f20f883e', '读者小王', '13300001111', 0)
ON DUPLICATE KEY UPDATE password = VALUES(password);

INSERT INTO book(book_name, author, category_id, stock, publish_time) VALUES
    ('Java编程思想', 'Bruce Eckel', 1, 20, '2020-01-10'),
    ('Vue实战教程', '尤雨溪',     1, 15, '2021-03-05'),
    ('三体',         '刘慈欣',     3, 30, '2008-01-01')
ON DUPLICATE KEY UPDATE book_name = VALUES(book_name);
