package com.library.library_manager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration // Đánh dấu lớp này là một lớp cấu hình Spring
@EnableWebSecurity // Kích hoạt cấu hình Security
@EnableMethodSecurity
public class SecurityConfig {
    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;

    // Định nghĩa một bean của SecurityFilterChain để cấu hình bảo mật cho ứng dụng
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // Cấu hình quyền truy cập cho các yêu cầu HTTP
        httpSecurity.authorizeHttpRequests(request -> {
            request
                    // USER: greeting
                    .requestMatchers("/api/auth/**").permitAll()
                    .anyRequest().authenticated(); // nhưng request còn lại phải được xác thực
        });

        // Vô hiệu hóa bảo mật CSRF (Cross-Site Request Forgery)
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        httpSecurity.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder())
                        .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
        );

        // Xây dựng và trả về đối tượng SecurityFilterChain
        return httpSecurity.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Tạo khóa bí mật với thuật toán HS256
        SecretKeySpec secretKeySpec = new SecretKeySpec(SIGNER_KEY.getBytes(), "HS256");

        // Cấu hình và tạo JwtDecoder với khóa bí mật và thuật toán mã hóa
        return NimbusJwtDecoder
                .withSecretKey(secretKeySpec)  // Đặt khóa bí mật
                .macAlgorithm(MacAlgorithm.HS256)  // Chọn thuật toán HS256
                .build();  // Xây dựng JwtDecoder
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        // Tạo JwtGrantedAuthoritiesConverter và đặt tiền tố quyền hạn
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        // Tạo JwtAuthenticationConverter và thiết lập JwtGrantedAuthoritiesConverter
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        // Trả về JwtAuthenticationConverter cấu hình sẵn
        return jwtAuthenticationConverter;
    }
}