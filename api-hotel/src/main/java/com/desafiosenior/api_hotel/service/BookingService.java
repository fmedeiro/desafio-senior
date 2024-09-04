package com.desafiosenior.api_hotel.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.desafiosenior.api_hotel.model.Booking;
import com.desafiosenior.api_hotel.model.BookingDto;
import com.desafiosenior.api_hotel.model.BookingStatus;
import com.desafiosenior.api_hotel.model.Room;
import com.desafiosenior.api_hotel.repository.BookingRepository;

import jakarta.transaction.Transactional;

@Service
public class BookingService {
	private final BookingRepository bookingRepository;

	public BookingService(BookingRepository bookingRepository) {
		this.bookingRepository = bookingRepository;
	}

	private List<Booking> bookingsByStatusFreeForThisRoomOnDb(Room room) {
		var bookingsByStatusFreeForThisRoomOnDb = bookingRepository
				.findByStatusAndRoom(BookingStatus.FREE.getStatus(), room);

		return bookingsByStatusFreeForThisRoomOnDb;
	}

	@Transactional
	public Optional<ResponseEntity<Object>> delete(UUID bookingId) {
		var bookDb = bookingRepository.findByBookingId(bookingId);

		if (bookDb.isEmpty())
			return Optional.empty();

		bookingRepository.delete(bookDb.get());
		return Optional.of(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
	}

	@Transactional
	public void deleteAll() {
		bookingRepository.deleteAll();
	}

	public List<Booking> findAll() {
		return bookingRepository.findAll();
	}

	public Optional<Booking> findByBookingId(UUID bookingId) {
		var bookingDb = bookingRepository.findByBookingId(bookingId);

		if (bookingDb.isEmpty())
			return Optional.empty();

		return bookingDb;
	}

	private boolean isThisBookingPermitedForThisRoomAndDates(BookingDto bookingDto) {
		var room = bookingDto.room();
		var bookingsByStatusFreeForThisRoomOnDb = bookingsByStatusFreeForThisRoomOnDb(room);

		if (!bookingsByStatusFreeForThisRoomOnDb.isEmpty())
			return true;

		var bookingDateCheckin = bookingDto.dateCheckin();
		var bookingDateCheckout = bookingDto.dateCheckout();

		return isThisRoomWillBeAvailableForThisDesiredCheckinDate(bookingDateCheckin, bookingDateCheckout, room);
	}

	private boolean isThisRoomWillBeAvailableForThisDesiredCheckinDate(LocalDateTime bookingDateCheckin,
			LocalDateTime bookingDateCheckout, Room room) {
		var bookingDateByDateCheckinAndRoomsDb = bookingRepository.findByDateCheckinAndRoom(bookingDateCheckin, room);
		var bookingDateByCheckinBeforeAndDateCheckoutIsNullDb = bookingRepository
				.findByDateCheckinBeforeAndDateCheckoutIsNull(bookingDateCheckin);
		var bookingDateByCheckinLessThanAndDateCheckoutGreaterThanEqualDb = bookingRepository
				.findByDateCheckinLessThanAndDateCheckoutGreaterThanEqual(bookingDateCheckin, bookingDateCheckout);

		return (bookingDateByDateCheckinAndRoomsDb.isEmpty()
				&& bookingDateByCheckinBeforeAndDateCheckoutIsNullDb.isEmpty()
				&& bookingDateByCheckinLessThanAndDateCheckoutGreaterThanEqualDb.isEmpty());
	}

	@Transactional
	public Booking save(BookingDto bookingDto) {
		if (isThisBookingPermitedForThisRoomAndDates(bookingDto)) {
			var booking = new Booking(LocalDateTime.now());

			if (bookingDto.status() == null || bookingDto.status().isBlank()) {
				booking.setStatus(BookingStatus.SCHEDULED.getStatus());
			}

			BeanUtils.copyProperties(bookingDto, booking);
			booking.setDateLastChange(LocalDateTime.now());

			return bookingRepository.save(booking);
		}

		return null;
	}

	@Transactional
	public Optional<Booking> update(UUID bookingId, BookingDto bookingDto) {
		var bookingDb = bookingRepository.findByBookingId(bookingId);

		if (bookingDb.isEmpty())
			return Optional.empty();

		fillinOrChangeStatusValueToCapitalLetter(bookingDto, bookingDb);
		bookingDb.get().setDateLastChange(LocalDateTime.now());

		return Optional.of(bookingRepository.save(bookingDb.get()));
	}

	private void fillinOrChangeStatusValueToCapitalLetter(BookingDto bookingDto, Optional<Booking> bookingDb) {
		var statusDb = bookingDb.get().getStatus();
		BeanUtils.copyProperties(bookingDto, bookingDb.get());

		if (bookingDto.status() == null || bookingDto.status().isBlank()) {
			bookingDb.get().setStatus(statusDb);
		} else {
			bookingDb.get().setStatus(bookingDb.get().getStatus().toUpperCase());
		}
	}

}
