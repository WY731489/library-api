package com.lb.library.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * JWT 工具类测试
 * 测纯函数，不需要 Spring 容器
 */
@DisplayName("JWT 工具测试")
class JwtUtilTest {

    @Nested
    @DisplayName("生成 token createToken()")
    class CreateTokenTest {

        @Test
        @DisplayName("生成 token - 非空且包含三段（用 . 分隔）")
        void createToken_ReturnsNonEmptyTokenWithThreeParts() {
            String token = JwtUtil.createToken(1, 1);

            assertThat(token).isNotNull().isNotEmpty();
            // JWT 格式: header.payload.signature
            assertThat(token.split("\\.")).hasSize(3);
        }

        @Test
        @DisplayName("不同用户生成不同 token")
        void createToken_DifferentUsers_ReturnDifferentTokens() {
            String token1 = JwtUtil.createToken(1, 0);
            String token2 = JwtUtil.createToken(2, 0);

            assertThat(token1).isNotEqualTo(token2);
        }
    }

    @Nested
    @DisplayName("解析 token parseToken()")
    class ParseTokenTest {

        @Test
        @DisplayName("合法 token - 正确解析出 userId 和 role")
        void parseToken_ValidToken_ReturnsClaims() {
            String token = JwtUtil.createToken(5, 1);

            Claims claims = JwtUtil.parseToken(token);

            assertThat(claims).isNotNull();
            assertThat(claims.get("userId", Integer.class)).isEqualTo(5);
            assertThat(claims.get("role", Integer.class)).isEqualTo(1);
            // 应包含签发时间和过期时间
            assertThat(claims.getIssuedAt()).isNotNull();
            assertThat(claims.getExpiration()).isNotNull();
        }

        @Test
        @DisplayName("非法 token - 抛 JwtException")
        void parseToken_InvalidToken_ThrowsJwtException() {
            String badToken = "not.a.valid.jwt.token";

            assertThatThrownBy(() -> JwtUtil.parseToken(badToken))
                    .isInstanceOf(JwtException.class);
        }

        @Test
        @DisplayName("伪造签名的 token - 抛 JwtException（防篡改）")
        void parseToken_ForgedSignature_ThrowsJwtException() {
            // 用合法 token 改了最后一段（签名）
            String realToken = JwtUtil.createToken(1, 0);
            String[] parts = realToken.split("\\.");
            // 把签名段改成随机字符串
            String forgedToken = parts[0] + "." + parts[1] + ".forge-signature-xxx";

            assertThatThrownBy(() -> JwtUtil.parseToken(forgedToken))
                    .isInstanceOf(JwtException.class);
        }

        @Test
        @DisplayName("空 token - 抛异常")
        void parseToken_EmptyToken_ThrowsException() {
            assertThatThrownBy(() -> JwtUtil.parseToken(""))
                    .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("篡改 payload 的 role - 验签失败（核心安全特性）")
        void parseToken_TamperedPayload_FailsVerification() {
            // 真实场景：攻击者把 role=0 改成 role=1，但不知道密钥无法生成正确签名
            String realToken = JwtUtil.createToken(1, 0);
            // 把 payload 单独解码篡改后塞回去，签名保持不变 → 验签必然失败
            String[] parts = realToken.split("\\.");
            // 构造一个看似合法但签名和 payload 不匹配的 token
            String fakePayload = "eyJ1c2VySWQiOjEsInJvbGUiOjEsImlhdCI6MTcwMDAwMDAwMCwiZXhwIjoxNzAwMDAwMDAwMH0";
            String tamperedToken = parts[0] + "." + fakePayload + "." + parts[2];

            assertThatThrownBy(() -> JwtUtil.parseToken(tamperedToken))
                    .isInstanceOf(JwtException.class);
        }
    }
}
