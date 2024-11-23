package com.scm.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.scm.demo.services.impl.SecurityCustomUserDetailService;

@Configuration
public class SecurityConfig {

    private final SecurityCustomUserDetailService userDetailService;
    private final OAuthAuthenicationSuccessHandler handler;

    @Autowired
    public SecurityConfig(SecurityCustomUserDetailService userDetailService,
            OAuthAuthenicationSuccessHandler handler) {
        this.userDetailService = userDetailService;
        this.handler = handler;
    }

    // configuraiton of authentication providerfor spring security
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        // user detail service ka object:
        daoAuthenticationProvider.setUserDetailsService(userDetailService);
        // password encoder ka object
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        // configuration

        // urls configure kiay hai ki koun se public rangenge aur koun se private
        // rangenge
        httpSecurity.authorizeHttpRequests(authorize -> {
            // authorize.requestMatchers("/home", "/register", "/services").permitAll();
            authorize.requestMatchers("/user/**").authenticated();
            authorize.anyRequest().permitAll();
        });

        // form default login
        // agar hame kuch bhi change karna hua to hama yaha ayenge: form login se
        // related
        httpSecurity.formLogin(formLogin -> {
            formLogin.loginPage("/login");
            formLogin.loginProcessingUrl("/authenticate");
            formLogin.successForwardUrl("/user/dashboard");
            formLogin.usernameParameter("email");
            formLogin.passwordParameter("password");
        });

        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        // oauth configurations
        httpSecurity.oauth2Login(oauth -> {
            oauth.loginPage("/login");
            oauth.successHandler(handler);
        });
        httpSecurity.logout(logoutForm -> {
            logoutForm.logoutUrl("/do-logout");
            logoutForm.logoutSuccessUrl("/login?logout=true");
        });
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}