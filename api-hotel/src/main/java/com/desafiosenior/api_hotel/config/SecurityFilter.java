package com.desafiosenior.api_hotel.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.desafiosenior.api_hotel.repository.UserRepository;
import com.desafiosenior.api_hotel.service.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

	private final UserRepository userRepository;
	private final TokenService tokenService;
	
	public SecurityFilter(UserRepository userRepository, TokenService tokenService) {
		this.userRepository = userRepository;
		this.tokenService = tokenService;
	}

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        
        if (token != null) {
            var login = tokenService.validateToken(token);
            var user = userRepository.findByLogin(login);

            var authentication = new UsernamePasswordAuthenticationToken(user.get(), null, user.get().getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        filterChain.doFilter(request, response);
    }

    protected String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        
        if (authHeader == null)
        	return null;
        
        return authHeader.replace("Bearer ", "");
    }
}
