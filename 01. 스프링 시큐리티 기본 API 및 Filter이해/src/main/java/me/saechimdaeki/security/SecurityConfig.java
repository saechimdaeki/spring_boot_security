package me.saechimdaeki.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.http.HttpSession;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .anyRequest().authenticated();

        http.formLogin();

        http.logout()
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login")
            .addLogoutHandler((request, response, authentication) -> {
                HttpSession session = request.getSession();
                session.invalidate();
            }).logoutSuccessHandler((request, response, authentication) -> response.sendRedirect("/login"))
            .and()
            .rememberMe()
            .rememberMeParameter("remember")
            .tokenValiditySeconds(3600)
            .userDetailsService(userDetailsService);
    }
}
