package com.williamhsieh.fruitandessence.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())

                .cors(cors -> cors
                        .configurationSource(createCorsConfig())
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new LoginTimeFilter(), JwtAuthenticationFilter.class)

                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())

                .authorizeHttpRequests(request -> request

                        // 公開 API
                        .requestMatchers(HttpMethod.POST,
                                "/members/register",
                                "/members/login",
                                "/members/forgot-password",
                                "/members/reset-password").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products").permitAll()

                        // 管理員專屬操作
                        .requestMatchers(HttpMethod.GET, "/admin/orders").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/products").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/products/{productId}",
                                "/members/{memberId}/orders/{orderId}",
                                "/members/{memberId}/orders/{orderId}/status",
                                "/members/{memberId}/orders/{orderId}/cancel").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/products/{productId}").hasRole("ADMIN")
                        .requestMatchers("/admin/shipping-method/**", "/admin/payment-method/**", "/admin/order-discount/**").hasRole("ADMIN")

                        // 登入用戶專屬操作
                        .requestMatchers(HttpMethod.GET,
                                "/members/{memberId}/orders",
                                "/members/{memberId}",
                                "/members/{memberId}/subscriptions",
                                "/members/{memberId}/orders",
                                "/members/{memberId}/orders/{orderId}").authenticated()
                        .requestMatchers(HttpMethod.POST,
                                "/members/{memberId}/orders",
                                "/members/logout",
                                "/members/{memberId}/orders").authenticated()
                        .requestMatchers(HttpMethod.PUT,
                                "/members/{memberId}/change-password",
                                "/members/{memberId}",
                                "/members/{memberId}/subscriptions").authenticated()

                        // 其餘通用路徑
                        .requestMatchers("/members/**", "/products/**").permitAll()

                        // 預設拒絕
                        .anyRequest().denyAll()
                )

                .build();

    }

    private CorsConfigurationSource createCorsConfig() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

}

/*
接下來的注意事項（必讀，3 件事）
將 SECRET 改為環境變數（或 application.yml），切勿把真實金鑰硬寫到程式碼。
本機開發測試 cookie：若你在 http://localhost:3000 測試，CookieUtil.COOKIE_SECURE_FOR_PRODUCTION 或 createTokenCookie(..., secure) 要設 false，否則瀏覽器不會設定 cookie。
確保你的 Role 類有 getRoleName()（或改成你實際 getter 名稱），memberDao.getRolesByMemberId() 能回傳 List<Role>。
 */