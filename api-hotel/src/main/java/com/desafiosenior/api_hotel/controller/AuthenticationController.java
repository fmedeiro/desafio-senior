package com.desafiosenior.api_hotel.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.desafiosenior.api_hotel.model.AuthenticationDto;
import com.desafiosenior.api_hotel.model.LoginResponseDto;
import com.desafiosenior.api_hotel.model.UserDto;
import com.desafiosenior.api_hotel.service.AuthenticationService;
import com.desafiosenior.api_hotel.service.UserService;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
	
	private final AuthenticationManager authenticationManager;
	private final AuthenticationService authenticationService;
	private final UserService userService;
	
	public AuthenticationController(AuthenticationManager authenticationManager, AuthenticationService authenticationService, UserService userService) {
		this.authenticationManager = authenticationManager;
		this.authenticationService = authenticationService;
		this.userService = userService;
	}
	
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid AuthenticationDto authenticationDto) {
		return authenticationService.login(authenticationDto, authenticationManager);
	}
	
    @PostMapping("/logout")
    public ResponseEntity<Object> logout() {
        SecurityContextHolder.clearContext();

        return ResponseEntity.status(HttpStatus.OK).body("Logout successful");
    }
	
	@PostMapping("/register")
	public ResponseEntity<Object> save(@RequestBody @Valid UserDto userDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(userDto));
	}

}
