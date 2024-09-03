package com.desafiosenior.api_hotel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	private final SecurityFilter securityFilter;
	
	public WebSecurityConfig(SecurityFilter securityFilter) {
		this.securityFilter = securityFilter;
	}
	
	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return  httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                		.requestMatchers(HttpMethod.DELETE, "/users/{userId}").permitAll()
                		.requestMatchers(HttpMethod.DELETE, "/users").permitAll()
                		.requestMatchers(HttpMethod.GET, "/users/{userId}").permitAll()
                		.requestMatchers(HttpMethod.GET, "/users").permitAll()
                		.requestMatchers(HttpMethod.POST, "/users").permitAll()
                		.requestMatchers(HttpMethod.PUT, "/users/{userId}").permitAll()
                		.requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register").hasRole("ADMIN")
                		// Abaixo foram feitos apenas para testes pessoais, se quiser, podem desconsiderar
                        // nao implementei todos as uris verbos nos Controllers, coloquei acima apenas os pertinentes para a avaliacao
                		.requestMatchers(HttpMethod.GET, "/h2-console/**").permitAll()
                		.requestMatchers(HttpMethod.POST, "/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .logout((logout) -> logout.permitAll())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())) // Para o uso pelo browser do h2-console  
                .build();
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
