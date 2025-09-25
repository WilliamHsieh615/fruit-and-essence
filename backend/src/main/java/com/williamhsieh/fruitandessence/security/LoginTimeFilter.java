package com.williamhsieh.fruitandessence.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

public class LoginTimeFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();

        if(uri.equals("/members/login")) {
            String userAgent = request.getHeader("User-Agent");
            LocalDateTime now = LocalDateTime.now();

            System.out.println("使用者於 " + now + " 嘗試從 " + userAgent + " 登入");
        }

        filterChain.doFilter(request, response);
    }
}

/* 之後再把登入紀錄寫入資料庫
CREATE TABLE login_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    login_time TIMESTAMP NOT NULL,
    user_agent VARCHAR(255),
    ip_address VARCHAR(50),
    success BOOLEAN NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member(id)
); */

