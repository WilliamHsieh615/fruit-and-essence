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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())

                .cors(cors -> cors
                        .configurationSource(createCorsConfig())
                )

                .addFilterBefore(new LoginTimeFilter(), BasicAuthenticationFilter.class)

                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())

                .authorizeHttpRequests(request -> request

                        // 公開 API
                        .requestMatchers("/members/register", "/members/login", "/members/verify", "/members/retrieve").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products").permitAll()

                        // 管理員專屬操作
                        .requestMatchers(HttpMethod.POST, "/products").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/products/{productId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/products/{productId}").hasRole("ADMIN")

                        // 登入用戶專屬操作
                        .requestMatchers(HttpMethod.GET, "/members/profile").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/members/profile").authenticated()
                        .requestMatchers(HttpMethod.GET, "/members/{memberId}/orders").authenticated()
                        .requestMatchers(HttpMethod.POST, "/members/{memberId}/orders").authenticated()

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
