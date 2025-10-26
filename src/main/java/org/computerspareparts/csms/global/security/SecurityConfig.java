package org.computerspareparts.csms.global.security;


import org.computerspareparts.csms.global.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // use the lambda-style csrf configuration to avoid using deprecated api
                .csrf(csrf -> csrf.disable())
                // ensure the DaoAuthenticationProvider bean is registered with HttpSecurity
                .authenticationProvider(authProvider())
                .authorizeHttpRequests(auth -> auth
                        // Public pages
                        .requestMatchers("/", "/css/**", "/js/**").permitAll()

                        // Error pages - allow access so custom error pages can be shown without auth
                        .requestMatchers("/error", "/error/**").permitAll()

                        // Customer routes: allow customers and employees to view customer-facing pages
                        .requestMatchers("/customer/login", "/customer/signup", "/customer/register").permitAll()
                        .requestMatchers("/customer/**").hasAnyRole("CUSTOMER", "MANAGER", "SALES_STAFF", "FINANCE_ACCOUNTANT", "IT_TECHNICIAN")

                        // Employee public endpoints
                        .requestMatchers("/employee/login", "/employee/signup", "/employee/register").permitAll()

                        // Employee role-specific routes
                        .requestMatchers("/employee/manager/**").hasRole("MANAGER")
                        .requestMatchers("/employee/sales/**").hasRole("SALES_STAFF")
                        .requestMatchers("/employee/accountant/**").hasRole("FINANCE_ACCOUNTANT")
                        .requestMatchers("/employee/it/**").hasRole("IT_TECHNICIAN")

                         // Supplier public endpoints
                        .requestMatchers("/supplier/login", "/supplier/signup", "/supplier/register").permitAll()
                        .requestMatchers("/supplier/**").hasRole("SUPPLIER")

                        // Everything else
                        .anyRequest().authenticated()
                )
                // ensure access denied is handled by our custom handler, which forwards to /error/403
                .exceptionHandling(ex -> ex.accessDeniedHandler(customAccessDeniedHandler))
                .formLogin(form -> form
                        // show the custom login page and post to the processing URL /login
                        .loginPage("/customer/login")
                        .loginProcessingUrl("/login")
                        .successHandler(customAuthenticationSuccessHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }
}
