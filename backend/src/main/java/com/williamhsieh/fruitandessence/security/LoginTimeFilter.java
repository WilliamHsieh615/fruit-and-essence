package com.williamhsieh.fruitandessence.security;

import com.williamhsieh.fruitandessence.dao.MemberDao;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class LoginTimeFilter extends OncePerRequestFilter {

    @Autowired
    private MemberDao memberDao;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        if (uri.equals("/members/login")) {
            String userAgent = request.getHeader("User-Agent");
            String ipAddress = request.getRemoteAddr();

            request.setAttribute("loginAttemptTime", LocalDateTime.now());
            request.setAttribute("loginUserAgent", userAgent);
            request.setAttribute("loginIpAddress", ipAddress);

        }

        filterChain.doFilter(request, response);
    }
}

