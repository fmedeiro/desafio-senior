package com.desafiosenior.api_hotel.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.postgresql.util.PSQLException;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.desafiosenior.api_hotel.exception.InvalidRequestException;
import com.desafiosenior.api_hotel.exception.ResourceNotFoundException;
import com.desafiosenior.api_hotel.model.UserDto;

class ValidationExceptionHandlerRestControllerTest {

    @InjectMocks
    private ValidationExceptionHandlerRestController exceptionHandler;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    @DisplayName("Testa a ocorrencia de um exception devido a tentativa de inserir uma chave repetida de algum campo unique da tabela do PostgreSQL, "
    		+ "causando o erro DataIntegrityViolationException e retornando um HttpStatus.CONFLICT ao Controller.")
    void handleDataIntegrityViolationException_uniqueViolation_shouldReturnHttpStatusConflict() {
        String detailMessage = "Key (id)=(123) already exists.";
        PSQLException sqlException = mock(PSQLException.class);
        when(sqlException.getSQLState()).thenReturn("23505");
        when(sqlException.getServerErrorMessage()).thenReturn(mock(org.postgresql.util.ServerErrorMessage.class));
        when(sqlException.getServerErrorMessage().getDetail()).thenReturn(detailMessage);

        DataIntegrityViolationException ex = new DataIntegrityViolationException("Unique violation", sqlException);
        
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleDataIntegrityViolationException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Duplicate Key Violation", response.getBody().get("erro"));
    }

    @Test
    @DisplayName("Testa a ocorrencia de um exception de alguma incoerencia em algum campo de alguma tabela do PostgreSQL, causando o "
    		+ "erro DataIntegrityViolationException diferente de Unique violation, retornando um HttpStatus.UNPROCESSABLE_ENTITY ao Controller.")
    void handleDataIntegrityViolationException_shouldReturnHttpStatusUnprocessableEntity() {
        PSQLException sqlException = mock(PSQLException.class);
        when(sqlException.getSQLState()).thenReturn("23503");

        DataIntegrityViolationException ex = new DataIntegrityViolationException("Other violation", sqlException);
        
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleDataIntegrityViolationException(ex);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("Data Integrity Violation", response.getBody().get("erro"));
    }

    @Test
    @DisplayName("Testa a ocorrencia de um exception de algum paramatro do JSON input com dado invalido, ou chaves em branco ou nulas "
    		+ "causando o erro MethodArgumentNotValidException, retornando um HttpStatus.UNPROCESSABLE_ENTITY ao Controller.")
    void handleInvalidArgumentException_shouldReturnHttpStatusUnprocessableEntity() throws NoSuchMethodException {
		MethodParameter methodParameter = new MethodParameter(
				UserController.class.getMethod("update", UUID.class, UserDto.class), 1);
		
		MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);
		
		when(bindingResult.getFieldErrors()).thenReturn(List.of(new FieldError("objectName", "field", "defaultMessage")));

		ResponseEntity<Map<String, String>> response = exceptionHandler.handleInvalidArgumentException(ex);

		assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
		assertEquals("defaultMessage", response.getBody().get("field"));
	}

    @Test
    @DisplayName("Testa a ocorrencia de um exception de alguma requisicao invalida, causando o erro InvalidRequestException, "
    		+ "retornando um HttpStatus.UNPROCESSABLE_ENTITY ao Controller.")
    void handleInvalidRequestException_shouldReturnHttpStatusUnprocessableEntity() {
        InvalidRequestException ex = new InvalidRequestException("Invalid request");

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleInvalidRequestException(ex);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("Invalid request", response.getBody().get("erro"));
    }

    @Test
    @DisplayName("Testa a ocorrencia de um exception de JWTCreation de uma criacao de um token, causando o erro JWTCreationException, "
    		+ "retornando um HttpStatus.UNPROCESSABLE_ENTITY ao Controller.")
    void handleJWTCreationException_shouldReturnHttpStatusUnprocessableEntity() {
        JWTCreationException ex = new JWTCreationException("JWT creation failed", null);

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleJWTCreationException(ex);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("JWT creation failed", response.getBody().get("erro"));
    }

    @Test
    @DisplayName("Testa a ocorrencia de um exception de JWTCreation de uma verificacao de um token, causando o erro JWTVerificationException, "
    		+ "retornando um HttpStatus.UNPROCESSABLE_ENTITY ao Controller.")
    void handleJWTVerificationException_shouldReturnHttpStatusUnprocessableEntity() {
        JWTVerificationException ex = new JWTVerificationException("JWT verification failed");

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleJWTVerificationException(ex);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("JWT verification failed", response.getBody().get("erro"));
    }

    @Test
    @DisplayName("Testa um exception de runtime no Spring que ocorre quando ha uma incompatibilidade entre os tipos esperados "
    		+ "e reais de argumentos de um metodo, causando o erro NoSuchMethodException e retornando um HttpStatus.BAD_REQUEST ao Controller.")
    void handleMethodArgumentTypeMismatchException_shouldReturnHttpStatusBadRequest() throws NoSuchMethodException {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        
        MethodParameter methodParameter = new MethodParameter(UserController.class.getMethod("update", UUID.class, UserDto.class), 0);
        
        when(ex.getParameter()).thenReturn(methodParameter);
        when(ex.getCause()).thenReturn(new IllegalArgumentException("Invalid type"));

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleMethodArgumentTypeMismatchException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Path attribute 'userId'. Invalid type", response.getBody().get("erro"));
    }

    @Test
    @DisplayName("Testa a ocorrencia de um exception de algum recurso nao encontrado, causando o erro ResourceNotFoundException "
    		+ "e retornando um HttpStatus.NOT_FOUND ao Controller.")
    void handleResourceNotFoundException_shouldReturnHttpStatusNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleResourceNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Resource not found", response.getBody().get("erro"));
    }

}

