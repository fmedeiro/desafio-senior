package com.desafiosenior.api_hotel.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.desafiosenior.api_hotel.exception.InvalidRequestException;
import com.desafiosenior.api_hotel.exception.ResourceConflictException;
import com.desafiosenior.api_hotel.exception.ResourceNotFoundException;
import com.desafiosenior.api_hotel.model.Booking;
import com.desafiosenior.api_hotel.model.BookingDto;
import com.desafiosenior.api_hotel.service.BookingService;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/bookings")
public class BookingController {

	private final BookingService bookingService;

	public BookingController(BookingService bookingService) {
		this.bookingService = bookingService;
	}

	@DeleteMapping("/{bookingId}")
	public ResponseEntity<Object> delete(@PathVariable UUID bookingId) {
		var objectResponse = bookingService.delete(bookingId);

		if (objectResponse.isEmpty()) {
			throw new ResourceNotFoundException("Reserva não encontrado para o ID: " + bookingId);
		}

		return objectResponse.get();
	}

	@DeleteMapping()
	public ResponseEntity<Object> deleteAll() {
		bookingService.deleteAll();

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> findOneBooking(@PathVariable UUID bookingId) {
		var bookingDb = bookingService.findByBookingId(bookingId);

		if (bookingDb.isEmpty()) {
			throw new ResourceNotFoundException("Reserva não encontrado para o ID: " + bookingId);
		}

		return ResponseEntity.status(HttpStatus.OK).body(bookingDb.get());
	}

	@GetMapping()
	public ResponseEntity<List<Booking>> listAll() {
		return ResponseEntity.status(HttpStatus.OK).body(bookingService.findAll());
	}

	@PostMapping()
	public ResponseEntity<Object> save(@RequestBody @Valid BookingDto bookingDto) {
		Booking booking = bookingService.save(bookingDto);

		if (booking == null) {
			throw new ResourceConflictException("Reserva já existe para o período de: " + bookingDto.dateCheckin()
					+ " para o quarto: " + bookingDto.room()); // .get(0));
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(booking);
	}

	@PutMapping("/{bookingId}")
	public ResponseEntity<Object> update(@PathVariable UUID bookingId, @RequestBody @Valid BookingDto bookingDto) {
		try {
			var bookingDb = bookingService.update(bookingId, bookingDto);

			if (bookingDb.isEmpty()) {
				throw new ResourceNotFoundException("Reserva não encontrado para o ID: " + bookingId);
			}

			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (InvalidRequestException ex) {
			// Excecao sera capturada e tratada pelo ControllerAdvice
			throw ex;
		}
	}

}
