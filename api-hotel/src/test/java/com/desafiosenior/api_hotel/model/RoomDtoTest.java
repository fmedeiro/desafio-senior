package com.desafiosenior.api_hotel.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoomDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Testa a criacao de um objeto RoomDto valido.")
    void testValidRoomDto() {
        RoomDto roomDto = new RoomDto(UUID.randomUUID(), 10);

        Set<ConstraintViolation<RoomDto>> violations = validator.validate(roomDto);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Testa a criacao de um objeto RoomDto invalido, com o parametro number NULL.")
    void testRoomDtoWithNullNumber() {
        RoomDto roomDto = new RoomDto(UUID.randomUUID(), null);

        Set<ConstraintViolation<RoomDto>> violations = validator.validate(roomDto);

        assertEquals(1, violations.size());
        ConstraintViolation<RoomDto> violation = violations.iterator().next();
        assertEquals("{label.number.valid.format.notNull}", violation.getMessageTemplate());
    }

    @Test
    @DisplayName("Testa a criacao de um objeto RoomDto invalido, com o parametro number menor do que o minimo: 1.")
    void testRoomDtoWithNumberTooLow() {
        RoomDto roomDto = new RoomDto(UUID.randomUUID(), 0);

        Set<ConstraintViolation<RoomDto>> violations = validator.validate(roomDto);

        assertEquals(1, violations.size());
        ConstraintViolation<RoomDto> violation = violations.iterator().next();
        assertEquals("{label.number.valid.format.size.min}", violation.getMessageTemplate());
    }

    @Test
    @DisplayName("Testa a criacao de um objeto RoomDto invalido, com o parametro number maior do que o maximo: 1000.")
    void testRoomDtoWithNumberTooHigh() {
        RoomDto roomDto = new RoomDto(UUID.randomUUID(), 1001);

        Set<ConstraintViolation<RoomDto>> violations = validator.validate(roomDto);

        assertEquals(1, violations.size());
        ConstraintViolation<RoomDto> violation = violations.iterator().next();
        assertEquals("{label.number.valid.format.size.max}", violation.getMessageTemplate());
    }

    @Test
    @DisplayName("Testa a criacao de um objeto RoomDto invalido, testando se ha erros nos limites de valores no mesmo metodo.")
    void testRoomDtoWithValidBoundaryValues() {
        RoomDto roomDtoMin = new RoomDto(UUID.randomUUID(), 1);
        Set<ConstraintViolation<RoomDto>> violationsMin = validator.validate(roomDtoMin);
        assertTrue(violationsMin.isEmpty());

        RoomDto roomDtoMax = new RoomDto(UUID.randomUUID(), 1000);
        Set<ConstraintViolation<RoomDto>> violationsMax = validator.validate(roomDtoMax);
        assertTrue(violationsMax.isEmpty());
    }
}
