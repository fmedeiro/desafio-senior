package com.desafiosenior.api_hotel.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingCreateDtoTest {

    private Validator validator;
    private RoomDto validRoomDto;
    private UserFinderStandardParamsDto validUserFinderDto;
    private String validBookingStatusSchedule = BookingStatus.SCHEDULED.getStatus();

    @BeforeEach
    void setUp() {
        //Configura o validador para usar na validacao de constraints
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        validRoomDto = new RoomDto(UUID.randomUUID(), 10);
        validUserFinderDto = new UserFinderStandardParamsDto("333333333", "User Name", "999999999", "47", "55");
    }

    @Test
    @DisplayName("Testa a criacao de um objeto BookingCreateDto valido.")
    void testValidBookingCreateDto() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                validRoomDto,
                validUserFinderDto,
                LocalDateTime.now().plusDays(1), //Check-in no futuro
                LocalDateTime.now().plusDays(5), //Check-out no futuro
                validBookingStatusSchedule
        );

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(bookingCreateDto);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Testa a criacao de um objeto BookingCreateDto invalido, com roomDto nulo.")
    void testBookingCreateDtoWithNullRoom() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                null,
                validUserFinderDto,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5),
                validBookingStatusSchedule
        );

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(bookingCreateDto);

        assertEquals(1, violations.size());
        ConstraintViolation<BookingCreateDto> violation = violations.iterator().next();
        assertEquals("não deve ser nulo", violation.getMessage());
    }

    @Test
    @DisplayName("Testa a criacao de um objeto BookingCreateDto invalido, com userFinderStandardParamsDto nulo.")
    void testBookingCreateDtoWithNullUserFinderStandardParamsDto() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                validRoomDto,
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5),
                validBookingStatusSchedule
        );

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(bookingCreateDto);

        assertEquals(1, violations.size());
        ConstraintViolation<BookingCreateDto> violation = violations.iterator().next();
        assertEquals("{label.userFinderStandardParamsDto.valid.format}", violation.getMessageTemplate());
    }

    @Test
    @DisplayName("Testa a criacao de um objeto BookingCreateDto invalido, com dateCheckin no passado.")
    void testBookingCreateDtoWithPastDateCheckin() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                validRoomDto,
                validUserFinderDto,
                LocalDateTime.now().minusDays(1), //Check-in no passado
                LocalDateTime.now().plusDays(5),
                validBookingStatusSchedule
        );

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(bookingCreateDto);

        assertEquals(1, violations.size());
        ConstraintViolation<BookingCreateDto> violation = violations.iterator().next();
        assertEquals("{label.dateCheckin.valid.format.size}", violation.getMessageTemplate());
    }

    @Test
    @DisplayName("Testa a criacao de um objeto BookingCreateDto invalido, com status invalido.")
    void testBookingCreateDtoWithInvalidStatus() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                validRoomDto,
                validUserFinderDto,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5),
                "X" //Status invalido
        );

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(bookingCreateDto);

        assertEquals(1, violations.size());
        ConstraintViolation<BookingCreateDto> violation = violations.iterator().next();
        assertEquals("{label.status.valid.format.size}", violation.getMessageTemplate());
    }

    @Test
    @DisplayName("Testa a criacao de um objeto BookingCreateDto valido, com status nulo, eh opcional,"
    		+ " portanto nao deve gerar erro.")
    void testBookingCreateDtoWithNullStatus() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                validRoomDto,
                validUserFinderDto,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5),
                null
        );

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(bookingCreateDto);

        assertTrue(violations.isEmpty());
    }
}
