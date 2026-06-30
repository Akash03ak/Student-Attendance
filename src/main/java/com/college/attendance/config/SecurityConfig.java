package com.college.attendance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(Customizer.withDefaults())

            .addFilterBefore((request, response, chain) ->{
            	HttpServletRequest req= (HttpServletRequest) request;
            	HttpServletResponse res= (HttpServletResponse) response;
            	if("POST".equalsIgnoreCase(req.getMethod()) && "/login".equals(req.getRequestURI())) {
            		String userInput= req.getParameter("answer");
            		Integer correctAnswer= (Integer) req.getSession().getAttribute("captcha_answer");
            		if(correctAnswer!=null) {
            			try {
            				int userAns= Integer.parseInt(userInput);
            				if(userAns!= correctAnswer) {
            					res.sendRedirect("/login?error=Invalid+Captcha");
            					return;
            				}
            			}
            			catch(Exception e) {
            				res.sendRedirect("/login?error=Invalid+Captcha");
            				return ;
            			}
            		}
            	}
            	
            	chain.doFilter(request, response);
            }, UsernamePasswordAuthenticationFilter.class)
            
            .authorizeHttpRequests(auth -> auth

            	.requestMatchers("/", "/login","/forgot-password","/reset-password","/student-login","/student-send-otp","/student-verify-otp").permitAll()
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/staff/**").hasAuthority("ROLE_STAFF")
                .requestMatchers("/departments/**").hasAnyAuthority("ROLE_STAFF","ROLE_ADMIN")
                .requestMatchers("/classes/**").hasAnyAuthority("ROLE_ADMIN","ROLE_STAFF")
                .requestMatchers("/student/**").authenticated()
                .anyRequest().permitAll()
            )

            .formLogin(login -> login
                .loginPage("/login")
                .defaultSuccessUrl("/redirect-dashboard", true)
                .permitAll()
            )

            .logout(logout -> logout
            	.logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            .headers(header -> header.cacheControl(cache -> cache.disable()))
            ;

        return http.build();
    }
}
