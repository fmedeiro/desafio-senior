package com.desafiosenior.api_hotel.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.desafiosenior.api_hotel.model.User;

@Service
public class TokenService {
	@Value("${api.security.token.secret}")
	private String secret;

	public String generateToken(User user) {
		Algorithm algorithm = Algorithm.HMAC256(secret);
		String token = JWT.create().withIssuer("auth-api").withSubject(user.getLogin())
				.withExpiresAt(genExpirationDate()).sign(algorithm);
		return token;
	}

	public String validateToken(String token) {
		Algorithm algorithm = Algorithm.HMAC256(secret);
		return JWT.require(algorithm).withIssuer("auth-api").build().verify(token).getSubject();
	}

	protected Instant genExpirationDate() {
		return LocalDateTime.now().plusHours(3).toInstant(ZoneOffset.of("-03:00"));
	}
}
