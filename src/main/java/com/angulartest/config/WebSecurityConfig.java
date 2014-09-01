package com.angulartest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebMvcSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/**", "/addReminder", "/getProviders", "/register").permitAll().anyRequest().authenticated();
		//http.formLogin().loginPage("/login").permitAll().and().logout().permitAll();
		
		http
        .authorizeRequests().anyRequest().authenticated();
http
        .formLogin().failureUrl("/login?error")
        .defaultSuccessUrl("/")
        .loginPage("/login")
        .permitAll()
        .and()
        .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login")
        .permitAll();
	}
	
	@Configuration
    protected static class AuthenticationConfiguration extends GlobalAuthenticationConfigurerAdapter    {

		@Override
		public void init(AuthenticationManagerBuilder auth) throws Exception {
			auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
		}
    }
}
