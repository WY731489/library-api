package com.lb.library.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lb.library.common.JwtUtil;
import com.lb.library.common.Result;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器：校验 token，把 userId / role 放入 request 供 Controller 使用
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        // 支持 "Bearer xxx" 格式
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token == null || token.isBlank()) {
            writeJson(response, Result.error(401, "未登录，请先登录"));
            return false;
        }
        try {
            Claims claims = JwtUtil.parseToken(token);
            request.setAttribute("userId", claims.get("userId", Integer.class));
            request.setAttribute("role", claims.get("role", Integer.class));
            return true;
        } catch (ExpiredJwtException e) {
            writeJson(response, Result.error(401, "登录已过期，请重新登录"));
        } catch (JwtException | IllegalArgumentException e) {
            writeJson(response, Result.error(401, "无效token，请重新登录"));
        }
        return false;
    }

    private void writeJson(HttpServletResponse response, Result<?> result) throws Exception {
        // HTTP 状态码保持 200，业务码放在统一返回体里
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
    }
}
