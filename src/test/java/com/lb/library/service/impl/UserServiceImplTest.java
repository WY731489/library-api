package com.lb.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lb.library.common.JwtUtil;
import com.lb.library.entity.User;
import com.lb.library.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 用户业务单元测试
 * 用 mockStatic 隔离 JwtUtil.createToken（静态方法）
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户业务测试")
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    // ============ 注册 ============
    @Nested
    @DisplayName("注册 register()")
    class RegisterTest {

        @Test
        @DisplayName("账号为空 - 抛异常")
        void register_EmptyUsername_ThrowsException() {
            User user = new User();
            user.setUsername("");
            user.setPassword("123456");

            assertThatThrownBy(() -> userService.register(user))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("账号和密码不能为空");

            verify(userMapper, never()).insert(any());
        }

        @Test
        @DisplayName("密码为空 - 抛异常")
        void register_EmptyPassword_ThrowsException() {
            User user = new User();
            user.setUsername("newuser");
            user.setPassword("");

            assertThatThrownBy(() -> userService.register(user))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("账号和密码不能为空");
        }

        @Test
        @DisplayName("账号已存在 - 抛异常")
        void register_DuplicateUsername_ThrowsException() {
            User user = new User();
            user.setUsername("admin");
            user.setPassword("123456");
            when(userMapper.selectCount(any())).thenReturn(1L);

            assertThatThrownBy(() -> userService.register(user))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("已被注册");
        }

        @Test
        @DisplayName("注册成功 - 密码 MD5 加密 + 默认读者角色")
        void register_Success_EncryptsPasswordAndSetsRole() {
            User user = new User();
            user.setUsername("newuser");
            user.setPassword("123456");
            when(userMapper.selectCount(any())).thenReturn(0L);
            when(userMapper.insert(any())).thenReturn(1);

            userService.register(user);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userMapper).insert(userCaptor.capture());
            User inserted = userCaptor.getValue();

            // 密码被 MD5 加密了，不是明文
            String expectedMd5 = DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8));
            assertThat(inserted.getPassword()).isEqualTo(expectedMd5);
            // 默认角色是 0（读者）
            assertThat(inserted.getRole()).isEqualTo(0);
        }
    }

    // ============ 登录 ============
    @Nested
    @DisplayName("登录 login()")
    class LoginTest {

        @Test
        @DisplayName("账号不存在 - 抛异常")
        void login_UserNotFound_ThrowsException() {
            User user = new User();
            user.setUsername("ghost");
            user.setPassword("123456");
            when(userMapper.selectOne(any())).thenReturn(null);

            assertThatThrownBy(() -> userService.login(user))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("账号不存在");
        }

        @Test
        @DisplayName("密码错误 - 抛异常")
        void login_WrongPassword_ThrowsException() {
            User user = new User();
            user.setUsername("admin");
            user.setPassword("wrong-pwd");

            User dbUser = new User();
            dbUser.setId(1);
            dbUser.setUsername("admin");
            dbUser.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8)));
            when(userMapper.selectOne(any())).thenReturn(dbUser);

            assertThatThrownBy(() -> userService.login(user))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("密码错误");
        }

        @Test
        @DisplayName("登录成功 - 返回 token + user（不含密码）")
        void login_Success_ReturnsTokenAndUser() {
            User user = new User();
            user.setUsername("admin");
            user.setPassword("123456");

            User dbUser = new User();
            dbUser.setId(1);
            dbUser.setUsername("admin");
            dbUser.setRole(1);
            dbUser.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8)));
            when(userMapper.selectOne(any())).thenReturn(dbUser);

            // 用 mockStatic 拦截 JwtUtil.createToken，避免真实生成 token
            try (MockedStatic<JwtUtil> mockedJwt = mockStatic(JwtUtil.class)) {
                mockedJwt.when(() -> JwtUtil.createToken(1, 1)).thenReturn("mock-token-xyz");

                Map<String, Object> result = userService.login(user);

                assertThat(result).isNotNull();
                assertThat(result.get("token")).isEqualTo("mock-token-xyz");
                User returnedUser = (User) result.get("user");
                assertThat(returnedUser.getUsername()).isEqualTo("admin");
                // 关键：返回的 user 不含密码
                assertThat(returnedUser.getPassword()).isNull();
            }
        }
    }
}
