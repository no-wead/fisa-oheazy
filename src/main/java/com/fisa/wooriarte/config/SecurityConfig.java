package com.fisa.wooriarte.config;

import com.fisa.wooriarte.jwt.JwtAuthenticationFilter;
import com.fisa.wooriarte.jwt.JwtTokenProvider;
import com.fisa.wooriarte.redis.RedisService;
import com.fisa.wooriarte.util.filter.CorsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.web.server.header.XXssProtectionServerHttpHeadersWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final CorsConfig corsConfig;

    @Autowired
    public SecurityConfig(RedisService redisService, JwtTokenProvider jwtTokenProvider, CorsConfig corsConfig) {
        this.redisService = redisService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.corsConfig = corsConfig;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws
            Exception {
        return httpSecurity
                // 수정 필요
//                .httpBasic().disable()
//                .csrf().disable()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .authorizeHttpRequests()
//                .requestMatchers("/user/jwtlogin").permitAll()
//                .requestMatchers("/project-managers/jwtlogin").permitAll()
//                .requestMatchers("/space-rentals/jwtlogin").permitAll()
//                .requestMatchers("/api/**").permitAll()
//                .requestMatchers("/user/**", "/ticket").hasAuthority("USER") // 여러 경로에 대해 같은 역할을 지정할 수 있습니다.
//                .requestMatchers("/exhibit/**").hasAnyAuthority("USER", "SPACE_RENTAL", "PROJECT_MANAGER") // 수정: hasAnyRole 사용
//                .requestMatchers("/matching/**").hasAnyAuthority("SPACE_RENTAL", "PROJECT_MANAGER")
//                .requestMatchers("/project-managers/**").hasAuthority("PROJECT_MANAGER")
//                .requestMatchers("/project-item/**", "/space-item/**").hasAnyAuthority("PROJECT_MANAGER", "SPACE_RENTAL") // 수정: hasAnyRole 사용
//                .requestMatchers("/space-rentals/**").hasAnyAuthority("SPACE_RENTAL")
//                .anyRequest().authenticated()
//                .and()
//                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisService),
//                        UsernamePasswordAuthenticationFilter.class)
//                .build();
                .httpBasic().disable()
                .csrf().disable()
                .cors().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                .anyRequest().permitAll() // 모든 요청에 대해 접근을 허용
                .and()
                .addFilter(corsConfig.corsFilter())
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisService),
                        UsernamePasswordAuthenticationFilter.class)
                .headers()
                .xssProtection()
                .and()
                .contentSecurityPolicy("script-src 'self'")
                .and()
                .frameOptions().deny()
                .and()
                .build();


    }
}

