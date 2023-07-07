package com.rodrigo.sistemafacturas.app;

import com.rodrigo.sistemafacturas.app.auth.handler.LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

    private final LoginSuccessHandler loginSuccessHandler;


    public WebSecurityConfig(LoginSuccessHandler loginSuccessHandler) {
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {

        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

        manager.createUser(User.withUsername("user")
                .password(passwordEncoder().encode("user"))
                .roles("USER").build());

        manager.createUser(User.withUsername("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("ADMIN", "USER").build());

        return manager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authz -> authz
                        .requestMatchers("/", "/css/**", "/js/**", "/img/**").permitAll()
//                        .requestMatchers("/ver/**").hasAnyRole("USER")
//                        .requestMatchers("/uploads/**").hasAnyRole("USER")
//                        .requestMatchers("/form/**").hasAnyRole("ADMIN")
//                        .requestMatchers("/eliminar/**").hasAnyRole("ADMIN")
//                        .requestMatchers("/factura/**").hasAnyRole("ADMIN")
                        .anyRequest().authenticated()
                )
//                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
                .formLogin(formLogin -> formLogin
                        .successHandler(loginSuccessHandler)
                        .loginPage("/login") // URL de la página de inicio de sesión personalizada
                         //.defaultSuccessUrl("/", true) URL a la que se debe redirigir después del inicio de sesión
                        .permitAll()
                )
                .logout(LogoutConfigurer::permitAll)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedPage("/error_403") // URL de la página de acceso denegado
                );

        return http.build();
    }


}