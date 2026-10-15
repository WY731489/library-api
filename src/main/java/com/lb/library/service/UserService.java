package com.lb.library.service;

import com.lb.library.entity.User;

import java.util.Map;

/**
 * 用户 Service
 */
public interface UserService {

    /** 注册（默认读者角色） */
    void register(User user);

    /** 登录，返回 token 和用户信息 */
    Map<String, Object> login(User user);
}
