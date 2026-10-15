package com.lb.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lb.library.common.JwtUtil;
import com.lb.library.entity.User;
import com.lb.library.mapper.UserMapper;
import com.lb.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void register(User user) {
        // 1. 校验必填项
        if (user.getUsername() == null || user.getUsername().isBlank()
                || user.getPassword() == null || user.getPassword().isBlank()) {
            throw new RuntimeException("账号和密码不能为空");
        }
        // 2. 账号查重
        Long count = userMapper.selectCount(new QueryWrapper<User>().eq("username", user.getUsername()));
        if (count != null && count > 0) {
            throw new RuntimeException("该账号已被注册");
        }
        // 3. 密码 MD5 加密存储；注册用户默认为读者角色
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)));
        user.setRole(0);
        userMapper.insert(user);
    }

    @Override
    public Map<String, Object> login(User user) {
        // 1. 校验必填项
        if (user.getUsername() == null || user.getPassword() == null) {
            throw new RuntimeException("账号和密码不能为空");
        }
        // 2. 查询用户
        User dbUser = userMapper.selectOne(new QueryWrapper<User>().eq("username", user.getUsername()));
        if (dbUser == null) {
            throw new RuntimeException("账号不存在");
        }
        // 3. 密码比对（MD5）
        String md5Pwd = DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8));
        if (!md5Pwd.equals(dbUser.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        // 4. 生成 token
        String token = JwtUtil.createToken(dbUser.getId(), dbUser.getRole());
        // 5. 组装返回数据（不返回密码）
        dbUser.setPassword(null);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", dbUser);
        return data;
    }
}
