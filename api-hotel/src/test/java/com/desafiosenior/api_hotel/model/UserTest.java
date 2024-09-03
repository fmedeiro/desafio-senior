package com.desafiosenior.api_hotel.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class UserTest {

    private User user;

    @Mock
    private UserRole userRole;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
    }

    @Test
    @DisplayName("Testa a geracao de um user_id de um novo hospede e confere se ele gerou.")
    void testGenerateUUID() {
        user.generateUUID();
        assertNotNull(user.getUserId());
    }
    
    @Test
    @DisplayName("Testa se um user_id de um hospede pre-existente na DB se mantem mesmo executando o generateUUID para ele.")
    void testGenerateUUID_alreadySet() {
        UUID existingId = UUID.randomUUID();
        user.setUserId(existingId);
        user.generateUUID();
        assertEquals(existingId, user.getUserId());
    }

    @Test
    @DisplayName("Testa se as roles sao corretamente setadas ao papel de ADMIN.")
    void testGetAuthorities_admin() {
        user.setRole("A");
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        
        assertEquals(3, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER_ATTENDANT")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_GUEST")));
    }

    @Test
    @DisplayName("Testa se a role eh corretamente setada ao papel de USER_ATTENDANT.")
    void testGetAuthorities_userAttendant() {
        user.setRole("U");
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER_ATTENDANT")));
    }

    @Test
    @DisplayName("Testa se a role eh corretamente setada ao papel de GUEST.")
    void testGetAuthorities_user() {
        user.setRole("G");
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_GUEST")));
    }

    @Test
    @DisplayName("Testa o get de Username que deve retornar o login.")
    void testGetUsername_shouldReturnLogin() {
        user.setLogin("test_login");
        assertEquals("test_login", user.getUsername());
    }

    @Test
    @DisplayName("Testa o EqualsAndHashCode de dois user, eles devem ser iguais.")
    void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        user.setUserId(id);

        User user2 = new User();
        user2.setUserId(id);

        assertEquals(user, user2);
        assertEquals(user.hashCode(), user2.hashCode());
    }

    @Test
    @DisplayName("Testa o EqualsAndHashCode de dois user, eles devem ser diferentes.")
    void testNotEquals() {
        user.setUserId(UUID.randomUUID());
        User user2 = new User();
        user2.setUserId(UUID.randomUUID());

        assertNotEquals(user, user2);
    }

}

