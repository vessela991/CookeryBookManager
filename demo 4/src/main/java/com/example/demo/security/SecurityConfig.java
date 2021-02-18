package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
        auth.authenticationProvider(authProvider);
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers("/*.css").permitAll()
                .antMatchers("/*.jpg").permitAll()
                .antMatchers("/").permitAll()
                .antMatchers("/login").anonymous()
                .antMatchers("/logout").authenticated()
                .antMatchers("/register").anonymous()
                .antMatchers("/changepassword").authenticated()
                .antMatchers("/createuser").hasRole("ADMIN")
                .antMatchers("/users/delete").hasRole("ADMIN")
                .antMatchers("/users/edit").authenticated()
                .antMatchers("/users/{id}**").authenticated()
                .antMatchers("/users**").hasRole("ADMIN")
                .antMatchers("/books/{id}/delete").authenticated()
                .antMatchers("/books/{id}/edit").authenticated()
                .antMatchers("/books/createbook").authenticated()
                .antMatchers("/books/{id}**").permitAll()
                .antMatchers("/books**").permitAll()
                .antMatchers("/recipes/{id}/delete").authenticated()
                .antMatchers("/recipes/{id}/edit").authenticated()
                .antMatchers("/recipes/create").authenticated()
                .antMatchers("/recipes/{id}**").permitAll()
                .antMatchers("/recipes**").permitAll()
                .and().formLogin().loginPage("/login").failureForwardUrl("/login")
                .and().logout().logoutUrl("/logout").logoutSuccessUrl("/recipes").invalidateHttpSession(true).deleteCookies("JSESSIONID")
                .and().sessionManagement().invalidSessionUrl("/").sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .and().exceptionHandling().accessDeniedPage("/");
    }
}
