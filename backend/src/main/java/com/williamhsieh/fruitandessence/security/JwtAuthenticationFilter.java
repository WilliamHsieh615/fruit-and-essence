package com.williamhsieh.fruitandessence.security;

import com.williamhsieh.fruitandessence.dao.MemberDao;
import com.williamhsieh.fruitandessence.model.Member;
import com.williamhsieh.fruitandessence.model.Role;
import com.williamhsieh.fruitandessence.util.CookieUtil;
import com.williamhsieh.fruitandessence.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private MemberDao memberDao;

    // 開發時如果在 localhost 不用 https，設定為 false；正式環境請用 true
    private static final boolean COOKIE_SECURE_FOR_PRODUCTION = true;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 從 Cookie 取出 Token
        String accessToken = getTokenFromCookie(request, "accessToken");
        String refreshToken = getTokenFromCookie(request, "refreshToken");

        Claims claims = null;

        try {
            // 如果 accessToken 不存在，但有 refreshToken，就嘗試透過 refresh token 生成新的 access token
            if (accessToken == null && refreshToken != null && validateRefreshToken(refreshToken)) {
                accessToken = refreshAccessToken(refreshToken, response);
            }

            if (accessToken != null) {
                try {
                    claims = JwtUtil.parseToken(accessToken);
                } catch (ExpiredJwtException e) {
                    // 如果 accessToken 過期，但 refresh token 還有效，也會幫你 refresh
                    if (refreshToken != null && validateRefreshToken(refreshToken)) {
                        accessToken = refreshAccessToken(refreshToken, response);
                        // 用 JwtUtil.parseToken() 解析 access token 拿到 claims
                        claims = JwtUtil.parseToken(accessToken);
                    } else {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
                        return;
                    }
                }

                // 透過 claims 取得 memberId
                Object memberIdObj = claims.get("memberId");
                if (memberIdObj == null) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token (no memberId)");
                    return;
                }
                Integer memberId = ((Number) memberIdObj).intValue();

                // 讀 DB 取得最新會員與角色（可以檢查是否被停權）
                Member member = memberDao.getMemberById(memberId);
                if (member == null) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Member not found");
                    return;
                }
                List<Role> roleList = memberDao.getRolesByMemberId(memberId);

                // 從 claims 或 DB 決定 roles（這裡以 DB 為準，較安全）
                List<SimpleGrantedAuthority> authorities = roleList.stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getRoleName()))
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(member.getMemberId(), null, authorities);

                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        } catch (JwtException e) {
            // JwtException 包含解析錯誤、簽章錯誤等
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    // 從 request cookie 讀值
    private String getTokenFromCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(name)) return cookie.getValue();
        }
        return null;
    }

    // 簡單檢查 refresh token (是否過期 & 是否能解析)
    private boolean validateRefreshToken(String refreshToken) {
        try {
            Claims refreshClaims = JwtUtil.parseToken(refreshToken);
            return !JwtUtil.isExpired(refreshClaims);
        } catch (JwtException e) {
            return false;
        }
    }

    // 用 refresh token 生成新的 access token、回寫 cookie，並回傳新的 access token 值
    private String refreshAccessToken(String refreshToken, HttpServletResponse response) {
        Claims claims = JwtUtil.parseToken(refreshToken);
        // refresh token subject 存 memberId 字串 (generateRefreshToken 放的是 memberId.toString())
        Integer memberId = Integer.valueOf(claims.getSubject());

        Member member = memberDao.getMemberById(memberId);
        List<Role> roleList = memberDao.getRolesByMemberId(memberId);

        // 將角色串為 comma-separated
        String rolesStr = roleList.stream()
                .map(Role::getRoleName)
                .collect(Collectors.joining(","));

        // 產生新的 access token (claim 中會含 memberId 與 roles)
        String newAccessToken = JwtUtil.generateAccessToken(member.getMemberId(), member.getEmail(), rolesStr);

        // 回寫 cookie：開發時如果本機測試需改 secure=false
        ResponseCookie cookie = CookieUtil.createTokenCookie("accessToken", newAccessToken, 30 * 60, COOKIE_SECURE_FOR_PRODUCTION);
        CookieUtil.addCookie(response, cookie);

        return newAccessToken;
    }
}
