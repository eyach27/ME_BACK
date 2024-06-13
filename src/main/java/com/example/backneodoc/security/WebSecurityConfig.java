package com.example.backneodoc.security;

import com.example.backneodoc.security.jwt.AuthEntryPointJwt;
import com.example.backneodoc.security.jwt.AuthTokenFilter;
import com.example.backneodoc.security.services.UserDetailsServiceImpl;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        // securedEnabled = true,
        // jsr250Enabled = true,
        prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	

http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/test/**").permitAll()
                .antMatchers("/api/**").permitAll()
                .antMatchers("/api/gestion/users/**").permitAll()
                .antMatchers("/api/gestion/users/accept/**").permitAll()
                .antMatchers("/document/**").permitAll()
                .antMatchers("/document/recherche/**").permitAll()
                .antMatchers("/document/update/**").permitAll()
                .antMatchers("/api/favorite/doc/**").permitAll()
                .antMatchers("/api/departments/**").permitAll()
                .antMatchers("/api/gestion/PDP/**").permitAll()
                .antMatchers("/api/formations/**").permitAll()
                .antMatchers("/api/formations/**/**").permitAll()
                .antMatchers("/api/gestion/photo/delete/**").permitAll()
                .antMatchers("/api/departments/**").permitAll()
                .antMatchers("/uploads/**").authenticated()
                .antMatchers("/uploads/PDP/**").authenticated()
                .antMatchers("/api/planning/**").permitAll()
                .antMatchers("/api/planning/formateur/**").permitAll()
                .antMatchers("/api/planning/list/**").permitAll()
                .antMatchers("/api/planning/**/**").permitAll()
                .antMatchers("/api/lot/**").permitAll()
                .antMatchers("/api/lot/**/**").permitAll();


        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
    @Bean 

    CorsConfigurationSource corsConfigurationSource() { 

    CorsConfiguration configuration = new CorsConfiguration(); 

    configuration.setAllowedOrigins(Collections.singletonList("http://localhost:4200")); 
   // configuration.setAllowedOrigins(Collections.singletonList("http://10.53.1.149:85"));
    configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS")); 

    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type" , "Access-Control-Allow-Origin")); 
    configuration.addAllowedOrigin("http://localhost:4200") ;
   // configuration.addAllowedOrigin("http://10.53.1.149:85") ; 

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); 

    source.registerCorsConfiguration("/**", configuration); 

    return source; 

    } 
}