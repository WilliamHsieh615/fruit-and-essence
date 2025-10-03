package com.williamhsieh.fruitandessence.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;


public class CookieUtil {

    // 建立 Cookie
    public static ResponseCookie createTokenCookie(String name, String value, long maxAgeSeconds, boolean secure) {
        return ResponseCookie.from(name, value)
                .httpOnly(true) // 前端 JS 無法存取（避免 XSS）
                .secure(secure) // 正式環境建議 secure=true（HTTPS）。如果在本機測試（http://localhost），把 secure 參數改為 false
                .path("/")
                .maxAge(Duration.ofSeconds(maxAgeSeconds))
                .sameSite("Lax") // 防止 CSRF，但允許基礎跨站操作
                .build();
    }

    public static ResponseCookie createTokenCookie(String name, String value, long maxAgeSeconds) {
        return createTokenCookie(name, value, maxAgeSeconds, true);
    }

    // 新增 Cookie 到 Response
    public static void addCookie(HttpServletResponse response, ResponseCookie cookie) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // 刪除 Cookie
    public static ResponseCookie deleteCookie(String name, boolean secure) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }

    public static ResponseCookie deleteCookie(String name) {
        return deleteCookie(name, true);
    }

}
