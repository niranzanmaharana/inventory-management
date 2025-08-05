package com.niranzan.inventory.management.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(Customizer.withDefaults())
                .authorizeHttpRequests(authorize ->
                        authorize
                                // Public resources
                                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                                .requestMatchers("/register/**", "/login").permitAll()

                                // Shared home/profile/dashboard access
                                .requestMatchers("/home", "/profile/**").hasAnyRole("ADMIN", "MANAGER", "STAFF")

                                // User management (Admin only)
                                .requestMatchers("/user/**").hasRole("ADMIN")

                                // Supplier and attribute config (Admin + Manager)
                                .requestMatchers("/supplier/**", "/attribute-type/**", "/product-category/**").hasAnyRole("ADMIN", "MANAGER")

                                // Product access (Manager can manage, Staff can view)
                                .requestMatchers(HttpMethod.GET, "/product/**").hasAnyRole("ADMIN", "MANAGER", "STAFF")
                                .requestMatchers("/product/**").hasAnyRole("MANAGER")

                                // Purchase order creation/placing/receiving (Manager), viewing (All)
                                .requestMatchers(HttpMethod.GET, "/purchase-order/**").hasAnyRole("ADMIN", "MANAGER", "STAFF")
                                .requestMatchers("/purchase-order/**").hasAnyRole("MANAGER", "ADMIN")

                                // Stock receiving and inventory (Staff + Admin)
                                .requestMatchers("/stock/**").hasAnyRole("ADMIN", "MANAGER", "STAFF")

                                // Sell access (Staff only)
                                .requestMatchers("/sell/**").hasRole("STAFF")

                                // Catch-all: Authenticated access required
                                .anyRequest().authenticated()
                ).formLogin(
                        form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/home", true)
                                .permitAll()
                ).logout(
                        logout -> logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/login?logout")
                                .invalidateHttpSession(true)
                                .clearAuthentication(true)
                ).exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer.accessDeniedPage("/error/access-denied"));
        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }
}
