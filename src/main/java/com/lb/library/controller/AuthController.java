package com.lb.library.controller;

import com.lb.library.common.Result;
import com.lb.library.entity.User;
import com.lb.library.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证接口：注册、登录（无需 token）
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "用户认证", description = "注册、登录（无需 token）")
public class AuthController {

    @Autowired
    private UserService userService;

    @Operation(summary = "注册（默认读者角色）")
    @PostMapping("/register")
    public Result<Boolean> register(@RequestBody User user) {
        userService.register(user);
        return Result.success(true);
    }

    @Operation(summary = "登录，返回 token 和用户信息")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User user) {
        return Result.success(userService.login(user));
    }
}
