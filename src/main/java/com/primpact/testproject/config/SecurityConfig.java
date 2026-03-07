package com.primpact.testproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

// CHANGE SCENARIO: SECURITY
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // SECURITY TEST POINT
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // SECURITY TEST POINT - CSRF DISABLED (CRITICAL config change!)
                .csrf(csrf -> csrf.disable())
                // SECURITY TEST POINT - Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // CHANGE SCENARIO: CONFIG - permitAll ADDED (CRITICAL!)
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/api/orders/**").authenticated()
                        .requestMatchers("/api/admin/**").authenticated()
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers("/api/data/**").authenticated()
                        .requestMatchers("/actuator/**").authenticated()
                        .requestMatchers("/h2-console/**").authenticated()
                        .anyRequest().authenticated()
                )
                .httpBasic(basic -> {})
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                );
        
        return http.build();
    }

    // SECURITY TEST POINT
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .build();
        
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("USER", "ADMIN")
                .build();
        
        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
