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

import com.desafiosenior.api_hotel.exception.InvalidRequestException;
import com.desafiosenior.api_hotel.exception.ResourceNotFoundException;
import com.desafiosenior.api_hotel.model.Booking;
import com.desafiosenior.api_hotel.model.BookingDto;
import com.desafiosenior.api_hotel.service.BookingService;

class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Testa a remocao de uma reserva de um hospede da tabela bookings.")
    public void testDelete_shouldReturnNoContent() {
        UUID bookingId = UUID.randomUUID();
        when(bookingService.delete(bookingId)).thenReturn(Optional.of(ResponseEntity.status(HttpStatus.NO_CONTENT).build()));

        ResponseEntity<Object> response = bookingController.delete(bookingId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(bookingService, times(1)).delete(bookingId);
    }
    
    @Test
    @DisplayName("Testa a tentativa de remocao de uma reserva inexistente da tabela bookings.")
    public void testDelete_NotFound_shouldReturnResourceNotFoundException() {
        UUID bookingId = UUID.randomUUID();
        when(bookingService.delete(bookingId)).thenReturn(Optional.empty());

        try {
            bookingController.delete(bookingId);
        } catch (ResourceNotFoundException e) {
            assertEquals("Reserva não encontrado para o ID: " + bookingId, e.getMessage());
        }

        verify(bookingService, times(1)).delete(bookingId);
    }
    
    @Test
    @DisplayName("Testa a remocao de todos as reservas da tabela bookings.")
    public void testDeleteAll_shouldReturnNoContent() {
        doNothing().when(bookingService).deleteAll();

        ResponseEntity<Object> response = bookingController.deleteAll();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(bookingService, times(1)).deleteAll();
    }
    
    @Test
    @DisplayName("Testa a consulta de uma reserva de um hospede da tabela bookings, pelo seu booking_id.")
    public void testFindOneBooking_shouldReturnHttpStatusOk() {
        UUID bookingId = UUID.randomUUID();
        Booking booking = new Booking();
        when(bookingService.findByBookingId(bookingId)).thenReturn(Optional.of(booking));

        ResponseEntity<Object> response = bookingController.findOneBooking(bookingId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(booking, response.getBody());
        verify(bookingService, times(1)).findByBookingId(bookingId);
    }

    @Test
    @DisplayName("Testa a consulta de uma reserva inexistente da tabela bookings, pelo seu booking_id.")
    public void testFindOneBooking_NotFound_shouldReturnResourceNotFoundException() {
        UUID bookingId = UUID.randomUUID();
        when(bookingService.findByBookingId(bookingId)).thenReturn(Optional.empty());

        try {
            bookingController.findOneBooking(bookingId);
        } catch (ResourceNotFoundException e) {
            assertEquals("Reserva não encontrado para o ID: " + bookingId, e.getMessage());
        }

        verify(bookingService, times(1)).findByBookingId(bookingId);
    }
    
    @Test
    @DisplayName("Testa a consulta de todas as reservas da tabela bookings.")
    public void testListAll_shouldReturnHttpStatusOk() {
        List<Booking> bookings = List.of(new Booking(), new Booking());
        when(bookingService.findAll()).thenReturn(bookings);

        ResponseEntity<List<Booking>> response = bookingController.listAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookings, response.getBody());
        verify(bookingService, times(1)).findAll();
    }
    
    @Test
    @DisplayName("Testa a criacao de uma nova reserva na tabela bookings.")
    public void testSave_shouldReturnHttpStatusCreated() {
        BookingDto bookingDto = new BookingDto(null, null, null, null, null);
        Booking savedBooking = new Booking();
        when(bookingService.save(any(BookingDto.class))).thenReturn(savedBooking);

        ResponseEntity<Object> response = bookingController.save(bookingDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedBooking, response.getBody());
        verify(bookingService, times(1)).save(any(BookingDto.class));
    }

    @Test
    @DisplayName("Testa a alteracao de uma reserva existente na tabela bookings.")
    public void testUpdateBooking_shouldReturnNoContent() {
        UUID bookingId = UUID.randomUUID();
        BookingDto bookingDto = new BookingDto(null, null, null, null, null);
        Booking booking = new Booking();
        when(bookingService.update(bookingId, bookingDto)).thenReturn(Optional.of(booking));

        ResponseEntity<Object> response = bookingController.update(bookingId, bookingDto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(bookingService, times(1)).update(bookingId, bookingDto);
    }
    
    @Test
    @DisplayName("Testa a alteracao de uma reserva inexistente na tabela bookings.")
    public void testUpdateBooking_NotFound_shouldReturnResourceNotFoundException() {
        UUID bookingId = UUID.randomUUID();
        BookingDto bookingDto = new BookingDto(null, null, null, null, null);
        when(bookingService.update(bookingId, bookingDto)).thenReturn(Optional.empty());

        try {
            bookingController.update(bookingId, bookingDto);
        } catch (ResourceNotFoundException e) {
            assertEquals("Reserva não encontrado para o ID: " + bookingId, e.getMessage());
        }

        verify(bookingService, times(1)).update(bookingId, bookingDto);
    }
    
    @Test
    @DisplayName("Testa a alteracao de uma reserva inexistente na tabela bookings forcando o erro InvalidRequestException.")
    public void testUpdateBooking_shouldReturnInvalidRequestException() {
        UUID bookingId = UUID.randomUUID();
        BookingDto bookingDto = new BookingDto(null, null, null, null, null);

        when(bookingService.update(bookingId, bookingDto)).thenThrow(new InvalidRequestException("Dados inválidos para o ID: " + bookingId));

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            bookingController.update(bookingId, bookingDto);
        });

        assertEquals("Dados inválidos para o ID: " + bookingId, exception.getMessage());
    }

}
