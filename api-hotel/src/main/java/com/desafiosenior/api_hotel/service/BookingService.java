package com.desafiosenior.api_hotel.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.desafiosenior.api_hotel.model.Booking;
import com.desafiosenior.api_hotel.model.BookingDto;
import com.desafiosenior.api_hotel.model.BookingStatus;
import com.desafiosenior.api_hotel.model.Room;
import com.desafiosenior.api_hotel.model.User;
import com.desafiosenior.api_hotel.repository.BookingRepository;
import com.desafiosenior.api_hotel.repository.RoomRepository;
import com.desafiosenior.api_hotel.repository.UserRepository;

@Service
public class BookingService {
	private final BookingRepository bookingRepository;
	private final RoomRepository roomRepository;
	private final UserRepository userRepository;

	public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository, UserRepository userRepository) {
		this.bookingRepository = bookingRepository;
		this.roomRepository = roomRepository;
		this.userRepository = userRepository;
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

	private boolean isThisBookingPermitedForThisRoomAndDates(BookingDto bookingDto, Room room) {		
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
		Room room = getRoom(bookingDto);
		User user = userRepository.findByUserId(bookingDto.userForBookingDto().userId()).get();
		if (isThisBookingPermitedForThisRoomAndDates(bookingDto, room)) {
			var booking = new Booking(LocalDateTime.now());

			if (bookingDto.status() == null || bookingDto.status().isBlank()) {
				booking.setStatus(BookingStatus.SCHEDULED.getStatus());
			}
			
			String[] ignoredProperties = {"RoomDto", "UserForBookingDto"};
			BeanUtils.copyProperties(bookingDto, booking, ignoredProperties);
			booking.setRoom(room);
			booking.setUser(user);
			
			booking.setDateLastChange(LocalDateTime.now());

			return bookingRepository.save(booking);
		}

		return null;
	}

	private Room getRoom(BookingDto bookingDto) {
		UUID roomId = bookingDto.roomDto().roomId();
		Room room = roomId != null ? roomRepository.findByRoomId(roomId).get() : roomRepository.findByNumber(bookingDto.roomDto().number()).get();
		return room;
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
