package com.desafiosenior.api_hotel.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
import com.desafiosenior.api_hotel.model.BookingCreateDto;
import com.desafiosenior.api_hotel.model.BookingUpdateDto;
import com.desafiosenior.api_hotel.model.Room;
import com.desafiosenior.api_hotel.service.BookingService;
import com.desafiosenior.api_hotel.service.RoomService;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/bookings")
public class BookingController {

	private final BookingService bookingService;
	private final RoomService roomService;

	public BookingController(BookingService bookingService, RoomService roomService) {
		this.bookingService = bookingService;
		this.roomService = roomService;
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
	
	@PutMapping("/cancel/{bookingId}")
	public ResponseEntity<Object> doCancel(@PathVariable @Valid UUID bookingId) {
		try {
			var bookingDb = bookingService.doCancel(bookingId);

			if (bookingDb.isEmpty()) {
				throw new ResourceNotFoundException("Reserva não encontrado para o ID: " + bookingId);
			}

			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (InvalidRequestException ex) {
			// Excecao sera capturada e tratada pelo ControllerAdvice
			throw ex;
		}
	}

	@PutMapping("/checking/{bookingId}")
	public ResponseEntity<Object> doChecking(@PathVariable UUID bookingId,
			@RequestBody @Valid BookingUpdateDto bookingUpdateDto) {
		try {
			var bookingDb = bookingService.doChecking(bookingId, bookingUpdateDto);

			if (bookingDb == null) {
				throw new InvalidRequestException(
						"Reserva com status inválido no DB (diferente de SCHEDULED) ou com a data de check-in: "
								+ bookingUpdateDto.dateCheckin()
								+ " do input menor do que a data de check-in registrado na reserva do DB, para o ID: "
								+ bookingId);
			}

			if (bookingDb.isEmpty()) {
				throw new ResourceNotFoundException("Reserva não encontrado para o ID: " + bookingId);
			}

			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (InvalidRequestException ex) {
			// Excecao sera capturada e tratada pelo ControllerAdvice
			throw ex;
		}
	}
	
	@PutMapping("/checkout/{bookingId}")
	public ResponseEntity<Object> doCheckout(@PathVariable @Valid UUID bookingId) {
		try {
			var bookingDb = bookingService.doCheckout(bookingId);

			if (bookingDb == null) {
				throw new ResourceNotFoundException(
						"Reserva com status inválido no DB (diferente de CHECKIN) para esta data de check-out: "
								+ LocalDateTime.now() + ", para o ID: " + bookingId);
			}

			if (bookingDb.isEmpty()) {
				throw new ResourceNotFoundException("Reserva não encontrado para o ID: " + bookingId);
			}

			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (InvalidRequestException ex) {
			// Excecao sera capturada e tratada pelo ControllerAdvice
			throw ex;
		}
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
	public ResponseEntity<Object> save(@RequestBody @Valid BookingCreateDto bookingCreateDto) {
		Booking booking = bookingService.save(bookingCreateDto);

		if (booking == null) {
			Integer numberRoom = bookingCreateDto.roomDto().number();
			
			if (numberRoom == null) {
				Optional<Room> room = roomService.findByRoomId(bookingCreateDto.roomDto().roomId());
				numberRoom = room.get().getNumber();
			}
			
			throw new ResourceConflictException("Reserva já existe para o período de: " + bookingCreateDto.dateCheckin()
					+ " para o quarto: " + numberRoom);
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(booking);
	}

	@PutMapping("/{bookingId}")
	public ResponseEntity<Object> update(@PathVariable UUID bookingId, @RequestBody @Valid BookingUpdateDto bookingUpdateDto) {
		try {
			var bookingDb = bookingService.update(bookingId, bookingUpdateDto);
			
			if (bookingDb == null) {
				throw new ResourceConflictException("Reserva já existe para o período de: " + bookingUpdateDto.dateCheckin()
				+ " para o ID: " + bookingId);
			}

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
