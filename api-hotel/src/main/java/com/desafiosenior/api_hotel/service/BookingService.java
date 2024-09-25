package com.desafiosenior.api_hotel.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.desafiosenior.api_hotel.exception.InvalidRequestException;
import com.desafiosenior.api_hotel.model.Booking;
import com.desafiosenior.api_hotel.model.BookingDto;
import com.desafiosenior.api_hotel.model.BookingStatus;
import com.desafiosenior.api_hotel.model.Room;
import com.desafiosenior.api_hotel.model.User;
import com.desafiosenior.api_hotel.model.UserRole;
import com.desafiosenior.api_hotel.repository.BookingRepository;

import lombok.NonNull;

@Service
public class BookingService {
	private final BookingRepository bookingRepository;
	private final MessageSource messageSource;
	private final RoomService roomService;
	private final UserService userService;

	public BookingService(BookingRepository bookingRepository, UserService userService, RoomService roomService,
			MessageSource messageSource) {
		this.bookingRepository = bookingRepository;
		this.messageSource = messageSource;
		this.roomService = roomService;
		this.userService = userService;
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
		var bookingDateByDateCheckinAndRoomsDb = bookingRepository.findByDateCheckinAndRoom_RoomId(bookingDateCheckin,
				room.getRoomId());
		var bookingDateByCheckinBeforeAndDateCheckoutIsNullDb = bookingRepository
				.findByDateCheckinBeforeAndDateCheckoutIsNullAndRoom_RoomId(bookingDateCheckin, room.getRoomId());
		var bookingDateByCheckinLessThanAndDateCheckoutGreaterThanEqualDb = bookingRepository
				.findByDateCheckinLessThanAndDateCheckoutGreaterThanEqualAndRoom_RoomId(bookingDateCheckin,
						bookingDateCheckout, room.getRoomId());

		return (bookingDateByDateCheckinAndRoomsDb.isEmpty()
				&& bookingDateByCheckinBeforeAndDateCheckoutIsNullDb.isEmpty()
				&& bookingDateByCheckinLessThanAndDateCheckoutGreaterThanEqualDb.isEmpty());
	}

	@Transactional
	public Booking save(BookingDto bookingDto) {
		checkingIfIsValidUserFinderStandardParamsDto(bookingDto);

		Optional<Room> room = getRoom(bookingDto);
		Optional<User> user = userService.getUserByAttributeChecker(bookingDto.userFinderStandardParamsDto(),
				UserRole.GUEST.getRole());

		if (room.isPresent() && user.isPresent() && isThisBookingPermitedForThisRoomAndDates(bookingDto, room.get())) {
			var booking = new Booking(LocalDateTime.now());

			if (bookingDto.status() == null || bookingDto.status().isBlank()) {
				booking.setStatus(BookingStatus.SCHEDULED.getStatus());
			}

			String[] ignoredProperties = { "RoomDto", "UserFinderStandardParamsDto" };
			BeanUtils.copyProperties(bookingDto, booking, ignoredProperties);
			booking.setRoom(room.get());
			booking.setStatus(booking.getStatus().toUpperCase());
			booking.setUser(user.get());

			booking.setDateLastChange(LocalDateTime.now());

			return bookingRepository.save(booking);
		}

		return null;
	}

	private void checkingIfIsValidUserFinderStandardParamsDto(BookingDto bookingDto) {
		if (bookingDto.userFinderStandardParamsDto().document() == null
				&& bookingDto.userFinderStandardParamsDto().name() == null
				&& bookingDto.userFinderStandardParamsDto().phone() == null
				&& bookingDto.userFinderStandardParamsDto().phoneDdd() == null
				&& bookingDto.userFinderStandardParamsDto().phoneDdi() == null) {
			var messageWarning = messageSource.getMessage("label.userFinderStandardParamsDto.valid.format", null, LocaleContextHolder.getLocale());
			throw new InvalidRequestException("Campo chave UserFinderStandardParamsDto mal configurado. " + messageWarning);
		}
	}

	private Optional<Room> getRoom(BookingDto bookingDto) {
		UUID roomId = bookingDto.roomDto().roomId();
		Optional<Room> room = roomId != null ? roomService.findByRoomId(roomId)
				: roomService.findByNumber(bookingDto.roomDto().number());
		return room;
	}
	
	@Transactional
	public Optional<Booking> update(UUID bookingId, BookingDto bookingDto) {
		var bookingDb = bookingRepository.findByBookingId(bookingId);

		if (bookingDb.isEmpty())
			return Optional.empty();

		updateBookingDbFromBookingDto(bookingDto, bookingDb.get());
		bookingDb.get().setDateLastChange(LocalDateTime.now());

		return Optional.of(bookingRepository.save(bookingDb.get()));
	}

	private void updateBookingDbFromBookingDto(BookingDto bookingDto, Booking bookingDb) {
		var oldStatusDb = bookingDb.getStatus();
		BeanUtils.copyProperties(bookingDto, bookingDb);

		fillinOrChangeStatusValueToCapitalLetter(bookingDto, bookingDb, oldStatusDb);
	}

	private void fillinOrChangeStatusValueToCapitalLetter(BookingDto bookingDto, Booking bookingDb,
			@NonNull String oldStatusDb) {
		if (bookingDto.status() == null || bookingDto.status().isBlank()) {
			bookingDb.setStatus(oldStatusDb);
		} else {
			bookingDb.setStatus(bookingDb.getStatus().toUpperCase());
		}
	}

}
