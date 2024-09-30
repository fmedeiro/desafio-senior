package com.desafiosenior.api_hotel.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.desafiosenior.api_hotel.model.AuthenticationDto;
import com.desafiosenior.api_hotel.model.LoginResponseDto;
import com.desafiosenior.api_hotel.model.User;
import com.desafiosenior.api_hotel.repository.UserRepository;

@Service
public class AuthenticationService implements UserDetailsService {

	private final TokenService tokenService;
	private final UserRepository userRepository;

	public AuthenticationService(TokenService tokenService, UserRepository userRepository) {
		this.tokenService = tokenService;
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByLogin(username).get();
	}

	public ResponseEntity<LoginResponseDto> login(AuthenticationDto authenticationDto,
			AuthenticationManager authenticationManager) {
		var loginPassword = new UsernamePasswordAuthenticationToken(authenticationDto.login(),
				authenticationDto.password());
		var auth = authenticationManager.authenticate(loginPassword);
		var token = tokenService.generateToken((User) auth.getPrincipal());

		return ResponseEntity.ok(new LoginResponseDto(token));
	}
}
