package com.desafiosenior.api_hotel.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.desafiosenior.api_hotel.model.User;
import com.desafiosenior.api_hotel.repository.UserRepository;
import com.desafiosenior.api_hotel.service.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class SecurityFilterTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private SecurityFilter securityFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Testa o metodo doFilterInternal com um token valido e verifica se o usuario foi autenticado corretamente, verificando, "
    		+ "no final, se o filtro continua a cadeia de filtros.")
    void doFilterInternal_withValidToken_shouldAuthenticateUser() throws ServletException, IOException {
        String token = "validToken";
        String login = "userLogin";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenService.validateToken(token)).thenReturn(login);
        
        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setLogin(login);
        user.setRole("A");

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));

        securityFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(login, ((User) authentication.getPrincipal()).getLogin());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Testa o metodo doFilterInternal com um token invalido e verifica se o usuario nao foi autenticado, verificando, "
    		+ "no final, se o filtro continua a cadeia de filtros.")
    void doFilterInternal_withInvalidToken_shouldNotAuthenticateUser() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        securityFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Testa a recuperacao de um token valido, com um header valido.")
    void recoverToken_withValidHeader_shouldReturnToken() {
        String token = "validToken";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        String result = securityFilter.recoverToken(request);
        assertEquals(token, result);
    }

    @Test
    @DisplayName("Testa a recuperacao de um token NULL, com um header NULL.")
    void recoverToken_withNullHeader_shouldReturnNull() {
        when(request.getHeader("Authorization")).thenReturn(null);

        String result = securityFilter.recoverToken(request);
        assertNull(result);
    }

    @Test
    @DisplayName("Testa a recuperacao de um token invalido, com um header invalido.")
    void recoverToken_withInvalidHeader() {
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader");

        String result = securityFilter.recoverToken(request);
        assertEquals(result, "InvalidHeader");
    }
}

