package com.desafiosenior.api_hotel.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class UserDtoTest {

    private Validator validator;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = (Validator) factory.getValidator();
    }

    @Test
    @DisplayName("Testa a criacao de um userDto com sucesso, com todos os atributos OKs no que sao esperados.")
    void testValidUserDto() {
        UserDto userDto = new UserDto(
            "12345678901",
            "email@example.com",
            "loginUser",
            "User Name",
            "password",
            "12345678",
            "11",
            "55",
            "A"
        );

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Testa a criacao de um userDto com o atributo document nao respeitando a regra de tamanho minimo.")
    void testInvalidDocument_withSizeSmallerThanMinimum() {
        UserDto userDto = new UserDto(
            "123",
            "email@example.com",
            "loginUser",
            "User Name",
            "password",
            "12345678",
            "11",
            "55",
            "A"
        );

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertEquals(1, violations.size());
        assertEquals("O campo deve conter entre 9 e 14 dígitos numéricos.", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Testa a criacao de um userDto com o atributo email invalido.")
    void testInvalidEmail() {
        UserDto userDto = new UserDto(
            "12345678901",
            "invalid-email",
            "loginUser",
            "User Name",
            "password",
            "12345678",
            "11",
            "55",
            "A"
        );

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertEquals(1, violations.size());
        assertEquals("deve ser um endereço de e-mail bem formado", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Testa a criacao de um userDto com o atributo login nao respeitando a regra de tamanho maximo.")
    void testInvalidLogin_withSizeLargerThanMaximum() {
        UserDto userDto = new UserDto(
            "12345678901",
            "email@example.com",
            "loginloginlogin",
            "User Name",
            "password",
            "12345678",
            "11",
            "55",
            "A"
        );

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertEquals(1, violations.size());
        assertEquals("Este campo tem que ter de 4 a 12 caracteres.", violations.iterator().next().getMessage());
    }
    
    @Test
    @DisplayName("Testa a criacao de um userDto com o atributo name nao respeitando a regra de tamanho minimo.")
    void testInvalidName_withSizeSmallerThanMinimum() {
        UserDto userDto = new UserDto(
            "12345678910",
            "email@example.com",
            "loginUser",
            "Nam",
            "password",
            "12345678",
            "11",
            "55",
            "A"
        );

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertEquals(1, violations.size());
        assertEquals("Este campo tem que ter de 4 a 60 caracteres.", violations.iterator().next().getMessage());
    }
    
    @Test
    @DisplayName("Testa a criacao de um userDto com o atributo password nao respeitando a regra de tamanho maximo.")
    void testInvalidPassword_withSizeLargerThanMaximum() {
        UserDto userDto = new UserDto(
            "12345678901",
            "email@example.com",
            "login",
            "User Name",
            "passwordpassword",
            "12345678",
            "11",
            "55",
            "A"
        );

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertEquals(1, violations.size());
        assertEquals("Este campo tem que ter de 4 a 8 caracteres.", violations.iterator().next().getMessage());
    }
    
    @Test
    @DisplayName("Testa a criacao de um userDto com o atributo phone nao respeitando a regra de tamanho minimo.")
    void testInvalidPhone_withSizeSmallerThanMinimum() {
        UserDto userDto = new UserDto(
            "12345678910",
            "email@example.com",
            "loginUser",
            "User Name",
            "password",
            "1234567",
            "11",
            "55",
            "A"
        );

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertEquals(1, violations.size());
        assertEquals("O campo deve conter entre 8 e 9 dígitos numéricos.", violations.iterator().next().getMessage());
    }
    
    @Test
    @DisplayName("Testa a criacao de um userDto com o atributo phone_ddd nao respeitando a regra de tamanho maximo.")
    void testInvalidPhoneDdd_withSizeLargerThanMaximum() {
        UserDto userDto = new UserDto(
            "12345678901",
            "email@example.com",
            "login",
            "User Name",
            "password",
            "12345678",
            "1111",
            "55",
            "A"
        );

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertEquals(1, violations.size());
        assertEquals("O campo deve conter 2 dígitos numéricos.", violations.iterator().next().getMessage());
    }
    
    @Test
    @DisplayName("Testa a criacao de um userDto com o atributo phone_ddi nao respeitando a regra de caracteres apenas numericos.")
    void testInvalidPhoneDdi_withNoNumericsCharacteres() {
        UserDto userDto = new UserDto(
            "12345678901",
            "email@example.com",
            "login",
            "User Name",
            "password",
            "12345678",
            "11",
            "ab",
            "A"
        );

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertEquals(1, violations.size());
        assertEquals("O campo deve conter 2 dígitos numéricos.", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Testa a criacao de um userDto com o atributo role invalido.")
    void testInvalidRole() {
        UserDto userDto = new UserDto(
            "12345678901",
            "email@example.com",
            "loginUser",
            "User Name",
            "password",
            "12345678",
            "11",
            "55",
            "X"
        );

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertEquals(1, violations.size());
        assertEquals("O campo deve conter apenas uma das letras: A (ADMIN), G (GUEST) ou U (USER_ATTENDANT), maiúsculas ou minúsculas.", violations.iterator().next().getMessage());
    }
}


