package com.desafiosenior.api_hotel.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.desafiosenior.api_hotel.exception.InvalidRequestException;
import com.desafiosenior.api_hotel.exception.ResourceNotFoundException;
import com.desafiosenior.api_hotel.model.User;
import com.desafiosenior.api_hotel.model.UserDto;
import com.desafiosenior.api_hotel.service.UserService;

class UserControllerTest {
	
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @SuppressWarnings("unused")
	private MockMvc mockMvc;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    @DisplayName("Testa a remocao de um hospede da tabela users.")
    public void testDeleteUser_shouldReturnNoContent() {
        UUID userId = UUID.randomUUID();
        when(userService.delete(userId)).thenReturn(Optional.of(ResponseEntity.status(HttpStatus.NO_CONTENT).build()));

        ResponseEntity<Object> response = userController.delete(userId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).delete(userId);
    }
    
    @Test
    @DisplayName("Testa a remocao de um hospede inexistente da tabela users.")
    public void testDeleteUser_NotFound_shouldReturnResourceNotFoundException() {
        UUID userId = UUID.randomUUID();
        when(userService.delete(userId)).thenReturn(Optional.empty());

        try {
            userController.delete(userId);
        } catch (ResourceNotFoundException e) {
            assertEquals("User não encontrado para o ID: " + userId, e.getMessage());
        }

        verify(userService, times(1)).delete(userId);
    }

    @Test
    @DisplayName("Testa a remocao de todos os hospedes da tabela users.")
    public void testDeleteAllUsers_shouldReturnNoContent() {
        doNothing().when(userService).deleteAll();

        ResponseEntity<Object> response = userController.deleteAll();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteAll();
    }
    
    @Test
    @DisplayName("Testa a consulta de um hospede da tabela users, pelo seu user_id.")
    public void testFindOneUser_shouldReturnHttpStatusOk() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        when(userService.findById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<Object> response = userController.findOneUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).findById(userId);
    }
    
    @Test
    @DisplayName("Testa a consulta de um hospede inexistente da tabela users, pelo seu user_id.")
    public void testFindOneUser_NotFound_shouldReturnResourceNotFoundException() {
        UUID userId = UUID.randomUUID();
        when(userService.findById(userId)).thenReturn(Optional.empty());

        try {
            userController.findOneUser(userId);
        } catch (ResourceNotFoundException e) {
            assertEquals("User não encontrado para o ID: " + userId, e.getMessage());
        }

        verify(userService, times(1)).findById(userId);
    }
    
    @Test
    @DisplayName("Testa a consulta de todos os hospedes da tabela users.")
    public void testListAllUsers_shouldReturnHttpStatusOk() {
        List<User> users = List.of(new User(), new User());
        when(userService.findAll()).thenReturn(users);

        ResponseEntity<List<User>> response = userController.listAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
        verify(userService, times(1)).findAll();
    }
    
    @Test
    @DisplayName("Testa a criacao de um novo hospede na tabela users.")
    public void testSaveUser_shouldReturnHttpStatusCreated() {
        UserDto userDto = new UserDto(null, null, null, null, null, null, null, null, null);
        User savedUser = new User();
        when(userService.save(any(UserDto.class))).thenReturn(savedUser);

        ResponseEntity<Object> response = userController.save(userDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedUser, response.getBody());
        verify(userService, times(1)).save(any(UserDto.class));
    }
    
    @Test
    @DisplayName("Testa a alteracao de um hospede existente na tabela users.")
    public void testUpdateUser_shouldReturnNoContent() {
        UUID userId = UUID.randomUUID();
        UserDto userDto = new UserDto(null, null, null, null, null, null, null, null, null);
        User user = new User();
        when(userService.update(userId, userDto)).thenReturn(Optional.of(user));

        ResponseEntity<Void> response = userController.update(userId, userDto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).update(userId, userDto);
    }
    
    @Test
    @DisplayName("Testa a alteracao de um hospede inexistente na tabela users.")
    public void testUpdateUser_NotFound_shouldReturnResourceNotFoundException() {
        UUID userId = UUID.randomUUID();
        UserDto userDto = new UserDto(null, null, null, null, null, null, null, null, null);
        when(userService.update(userId, userDto)).thenReturn(Optional.empty());

        try {
            userController.update(userId, userDto);
        } catch (ResourceNotFoundException e) {
            assertEquals("User não encontrado para o ID: " + userId, e.getMessage());
        }

        verify(userService, times(1)).update(userId, userDto);
    }
    
    @Test
    @DisplayName("Testa a alteracao de um hospede inexistente na tabela users forcando o erro InvalidRequestException.")
    public void testUpdateUser_shouldReturnInvalidRequestException() {
        UUID userId = UUID.randomUUID();
        UserDto userDto = new UserDto(null, null, null, null, null, null, null, null, null);

        when(userService.update(userId, userDto)).thenThrow(new InvalidRequestException("Dados inválidos para o ID: " + userId));

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            userController.update(userId, userDto);
        });

        assertEquals("Dados inválidos para o ID: " + userId, exception.getMessage());
    }
}

