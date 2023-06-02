package com.example.demo.autorization;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    public SecurityConfig(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {


        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeHttpRequests().requestMatchers("/users/login", "/users/registration", "/api/token/refresh")
                .permitAll();
        http.authorizeHttpRequests().requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
                .permitAll();
        http.authorizeHttpRequests()
                .requestMatchers("/products_property/**")
                .permitAll();
        http.authorizeHttpRequests()
                .requestMatchers("/bank/**")
                .permitAll();
        http.authorizeHttpRequests()
                .requestMatchers("/finish/**")
                .permitAll();
        http.authorizeHttpRequests()
                .requestMatchers("/products/size")
                .permitAll();
        http.authorizeHttpRequests().requestMatchers("/users/**").hasAnyAuthority("USER", "EMPLOYEE");
        http.authorizeHttpRequests().requestMatchers("/employee/**").hasAuthority("EMPLOYEE");
        http.authorizeHttpRequests().requestMatchers("/orders/**").permitAll();

        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
