package com.desafiosenior.api_hotel.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.desafiosenior.api_hotel.model.User;

class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;
    
    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "mySecretKey");
    }

    @Test
    @DisplayName("Testa a geracao de um token valido.")
    void generateToken_shouldGenerateValidToken() {
        User user = new User();
        user.setLogin("testUser");

        //Gera token
        String token = tokenService.generateToken(user);

        //Valida token gerado
        assertNotNull(token);

        //Extrai e valida informacoes do token
        String subject = JWT.require(Algorithm.HMAC256("mySecretKey"))
                            .withIssuer("auth-api")
                            .build()
                            .verify(token)
                            .getSubject();

        assertEquals("testUser", subject);
    }

    @Test
    @DisplayName("Testa a validacao de um token valido, deve retornar um subject valido/esperado.")
    void validateToken_shouldReturnValidSubject() {
        User user = new User();
        user.setLogin("testUser");

        String token = tokenService.generateToken(user);

        String subject = tokenService.validateToken(token);

        assertEquals("testUser", subject);
    }

    @Test
    @DisplayName("Testa a validacao de um token invalido, deve retornar um exception.")
    void validateToken_shouldThrowExceptionForInvalidToken() {
        String invalidToken = "invalidToken";

        //Valida um token invalido e esperar uma excecao
        assertThrows(Exception.class, () -> {
            tokenService.validateToken(invalidToken);
        });
    }

    @Test
    @DisplayName("Testa a recuperacao de uma data de expiracao de um token valido, deve retornar 3 horas de validade.")
    void genExpirationDate_shouldReturnCorrectExpirationTime() {
        //Mock da data/hora atual para controlar o teste
        LocalDateTime now = LocalDateTime.of(2024, 1, 1, 12, 0);
        try (MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(now);

            Instant expiration = tokenService.genExpirationDate();

            Instant expectedExpiration = now.plusHours(3).toInstant(ZoneOffset.of("-03:00"));
            assertEquals(expectedExpiration, expiration);
        }
    }
}

