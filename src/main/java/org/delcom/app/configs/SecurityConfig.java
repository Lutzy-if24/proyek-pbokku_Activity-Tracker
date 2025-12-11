package org.delcom.app.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF untuk development
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/auth/**", 
                    "/assets/**", 
                    "/api/**",
                    "/css/**", 
                    "/js/**", 
                    "/uploads/**",
                    "/static/**"
                )
                .permitAll()
                .anyRequest().permitAll()) // Ubah jadi permitAll untuk development
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable()) // Disable basic auth
            .logout(logout -> logout
                .logoutSuccessUrl("/auth/login")
                .permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}