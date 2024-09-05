package com.desafiosenior.api_hotel.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import com.desafiosenior.api_hotel.model.User;
import com.desafiosenior.api_hotel.model.UserDto;
import com.desafiosenior.api_hotel.repository.UserRepository;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;
    private UUID userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        user = new User();
        user.setUserId(userId);
        user.setDocument("12345678901234");
        user.setEmail("user@example.com");
        user.setLogin("userlogin");
        user.setName("User Name");
        user.setPassword("password123");
        user.setPhone("999999999");
        user.setPhoneDdd("11");
        user.setPhoneDdi("55");
        user.setRole("G");

        userDto = new UserDto(
            user.getDocument(),
            user.getEmail(),
            user.getLogin(),
            user.getName(),
            user.getPassword(),
            user.getPhone(),
            user.getPhoneDdd(),
            user.getPhoneDdi(),
            user.getRole()
        );
    }

    @Test
    @DisplayName("Testa a exclusao de um usuario existente, certificando-se de que o userRepository.delete() seja chamado.")
    void testDelete_existingUser_shouldReturnHttpStatusNoContent() {
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));

        Optional<ResponseEntity<Object>> response = userService.delete(userId);

        assertTrue(response.isPresent());
        assertEquals(HttpStatus.NO_CONTENT, response.get().getStatusCode());
        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("Testa o cenario aonde o usuario nao existe e certifica-se de que nenhum usuario seja excluido.")
    void testDelete_nonExistingUser() {
        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        Optional<ResponseEntity<Object>> response = userService.delete(userId);

        assertFalse(response.isPresent());
        verify(userRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Testa se todos os convidados sao excluidos corretamente.")
    @Transactional
    void testDeleteAll() {
        userService.deleteAll();
        verify(userRepository).deleteAll();
    }

    @Test
    @DisplayName("Testa se todos os convidados sao retornados corretamente.")
    void testFindAll() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> users = userService.findAll();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(user, users.get(0));
    }

    @Test
    @DisplayName("Testa a recuperacao de um usuario existente pelo seu user_id.")
    void testFindById_existingUser() {
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findById(userId);

        assertTrue(foundUser.isPresent());
        assertEquals(user, foundUser.get());
    }

    @Test
    @DisplayName("Verifica o comportamento ao tentar recuperar um usuario que nao existe.")
    void testFindById_nonExistingUser() {
        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.findById(userId);

        assertFalse(foundUser.isPresent());
    }
    
    @Test
	@DisplayName("Testa a recuperacao de um hospede existente pelo seu nome.")
    void testFindGuestByNameAndRole_UserExists() {
		User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setName("John Doe");
        user.setRole("G");
		
        when(userRepository.findByNameAndRole(anyString(), anyString()))
            .thenReturn(Optional.of(user));

        Optional<User> foundUser = userRepository.findByNameAndRole("John Doe", "G");

        assertTrue(foundUser.isPresent());
        assertEquals(user, foundUser.get());
    }

    @Test
	@DisplayName("Verifica o comportamento ao tentar recuperar um hospede nao hospedado consultando por busca por nome.")
    void testFindGuestByNameAndRole_UserDoesNotExist() {
        when(userRepository.findByNameAndRole(anyString(), anyString()))
            .thenReturn(Optional.empty());

        Optional<User> foundUser = userRepository.findByNameAndRole("Jane Doe", "G");

        assertTrue(foundUser.isEmpty());
    }

    @Test
	@DisplayName("Testa a recuperacao de um hospede existente pelo seu telefone.")
    void testFindGuestByPhoneDdiAndPhoneDddAndPhoneAndRole_UserExists() {
		User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setRole("G");
        user.setPhoneDdi("55");
        user.setPhoneDdd("11");
        user.setPhone("999999999");
		
        when(userRepository.findByPhoneDdiAndPhoneDddAndPhoneAndRole(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(Optional.of(user));

        Optional<User> foundUser = userRepository.findByPhoneDdiAndPhoneDddAndPhoneAndRole("55", "11", "999999999", "G");

        assertTrue(foundUser.isPresent());
        assertEquals(user, foundUser.get());
    }

    @Test
	@DisplayName("Testa a recuperacao de um hospede nao hospedado, pelo seu telefone.")
    void testFindGuestByPhoneDdiAndPhoneDddAndPhoneAndRole_UserDoesNotExist() {
        when(userRepository.findByPhoneDdiAndPhoneDddAndPhoneAndRole(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(Optional.empty());

        Optional<User> foundUser = userRepository.findByPhoneDdiAndPhoneDddAndPhoneAndRole("55", "11", "888888888", "G");

        assertTrue(foundUser.isEmpty());
    }

    @Test
	@DisplayName("Testa a recuperacao de um hospede existente pelo seu documento.")
    void testFindGuestByDocumentAndRole_UserExists() {
		User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setRole("G");
        user.setDocument("12345678901");
		
        when(userRepository.findByDocumentAndRole(anyString(), anyString()))
            .thenReturn(Optional.of(user));

        Optional<User> foundUser = userRepository.findByDocumentAndRole("12345678901", "G");

        assertTrue(foundUser.isPresent());
        assertEquals(user, foundUser.get());
    }

    @Test
	@DisplayName("Verifica o comportamento ao tentar recuperar um hospede nao hospedado consultando por busca por documento.")
    void testFindGuestByDocumentAndRole_UserDoesNotExist() {
        when(userRepository.findByDocumentAndRole(anyString(), anyString()))
            .thenReturn(Optional.empty());

        Optional<User> foundUser = userRepository.findByDocumentAndRole("98765432100", "G");

        assertTrue(foundUser.isEmpty());
    }

    @Test
    @Transactional
    @DisplayName("Testa o salvamento de um novo usuario e verifica se os valores foram corretamente copiados.")
    void testSave() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.save(userDto);

        assertNotNull(savedUser);
        assertEquals(user.getUserId(), savedUser.getUserId());
        assertEquals(user.getDocument(), savedUser.getDocument());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @Transactional
    @DisplayName("Testa a atualizacao de um usuario existente.")
    void testUpdate_existingUser() {
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        Optional<User> updatedUser = userService.update(userId, userDto);

        assertTrue(updatedUser.isPresent());
        assertEquals(user.getUserId(), updatedUser.get().getUserId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName(" Testa o comportamento ao tentar atualizar um usuario que nao existe.")
    void testUpdate_nonExistingUser() {
        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        Optional<User> updatedUser = userService.update(userId, userDto);

        assertFalse(updatedUser.isPresent());
        verify(userRepository, never()).save(any(User.class));
    }
}

