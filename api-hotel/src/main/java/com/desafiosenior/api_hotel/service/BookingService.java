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
import com.desafiosenior.api_hotel.exception.ResourceNotFoundException;
import com.desafiosenior.api_hotel.model.Booking;
import com.desafiosenior.api_hotel.model.BookingCreateDto;
import com.desafiosenior.api_hotel.model.BookingStatus;
import com.desafiosenior.api_hotel.model.BookingUpdateDto;
import com.desafiosenior.api_hotel.model.Room;
import com.desafiosenior.api_hotel.model.User;
import com.desafiosenior.api_hotel.model.UserRole;
import com.desafiosenior.api_hotel.repository.BookingRepository;

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

	private boolean isThisBookingPermitedForThisRoomAndDates(LocalDateTime bookingDateCheckin,
			LocalDateTime bookingDateCheckout, Room room) {
		var bookingsByStatusFreeForThisRoomOnDb = bookingsByStatusFreeForThisRoomOnDb(room);

		if (!bookingsByStatusFreeForThisRoomOnDb.isEmpty())
			return true;

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
	public Booking save(BookingCreateDto bookingCreateDto) {
		checkingIfIsValidUserFinderStandardParamsDto(bookingCreateDto);

		Optional<Room> room = getRoom(bookingCreateDto);
		Optional<User> user = userService.getUserByAttributeChecker(bookingCreateDto.userFinderStandardParamsDto(),
				UserRole.GUEST.getRole());

		if (room.isEmpty())
			throw new ResourceNotFoundException("Quarto não encontrado, número: " + bookingCreateDto.roomDto().number());

		if (user.isEmpty())
			throw new ResourceNotFoundException("Hóspede não encontrado: " + bookingCreateDto.userFinderStandardParamsDto().name());

		if (isThisBookingPermitedForThisRoomAndDates(bookingCreateDto.dateCheckin(), bookingCreateDto.dateCheckout(), room.get())) {
			var booking = new Booking(LocalDateTime.now());

			if (bookingCreateDto.status() == null || bookingCreateDto.status().isBlank()) {
				booking.setStatus(BookingStatus.SCHEDULED.getStatus());
			}

			String[] ignoredProperties = { "RoomDto", "UserFinderStandardParamsDto" };
			BeanUtils.copyProperties(bookingCreateDto, booking, ignoredProperties);
			booking.setRoom(room.get());
			booking.setStatus(booking.getStatus().toUpperCase());
			booking.setUser(user.get());

			booking.setDateLastChange(LocalDateTime.now());

			return bookingRepository.save(booking);
		}

		return null;
	}

	private void checkingIfIsValidUserFinderStandardParamsDto(BookingCreateDto bookingCreateDto) {
		if (bookingCreateDto.userFinderStandardParamsDto().document() == null
				&& bookingCreateDto.userFinderStandardParamsDto().name() == null
				&& bookingCreateDto.userFinderStandardParamsDto().phone() == null
				&& bookingCreateDto.userFinderStandardParamsDto().phoneDdd() == null
				&& bookingCreateDto.userFinderStandardParamsDto().phoneDdi() == null) {
			var messageWarning = messageSource.getMessage("label.userFinderStandardParamsDto.valid.format", null, LocaleContextHolder.getLocale());
			throw new InvalidRequestException("Campo chave UserFinderStandardParamsDto mal configurado. " + messageWarning);
		}
	}

	private Optional<Room> getRoom(BookingCreateDto bookingCreateDto) {
		UUID roomId = bookingCreateDto.roomDto().roomId();
		Optional<Room> room = roomId != null ? roomService.findByRoomId(roomId)
				: roomService.findByNumber(bookingCreateDto.roomDto().number());
		return room;
	}
	
	@Transactional
	public Optional<Booking> update(UUID bookingId, BookingUpdateDto bookingUpdateDto) {
		var bookingDb = bookingRepository.findByBookingId(bookingId);

		if (bookingDb.isEmpty())
			return Optional.empty();

		if (isThisBookingPermitedForThisRoomAndDates(bookingUpdateDto.dateCheckin(), bookingUpdateDto.dateCheckout(),
				bookingDb.get().getRoom())) {
			updateBookingDbFromBookingUpdateDto(bookingUpdateDto, bookingDb.get());
			bookingDb.get().setDateLastChange(LocalDateTime.now());

			return Optional.of(bookingRepository.save(bookingDb.get()));
		}

		return null;
	}

	private void updateBookingDbFromBookingUpdateDto(BookingUpdateDto bookingUpdateDto, Booking bookingDb) {
		var oldStatusDb = bookingDb.getStatus();
		BeanUtils.copyProperties(bookingUpdateDto, bookingDb);

		fillinOrChangeStatusValueToCapitalLetter(bookingUpdateDto, bookingDb, oldStatusDb);
	}

	private void fillinOrChangeStatusValueToCapitalLetter(BookingUpdateDto bookingUpdateDto, Booking bookingDb,
			String oldStatusDb) {
		if (bookingUpdateDto.status() == null || bookingUpdateDto.status().isBlank()) {
			bookingDb.setStatus(oldStatusDb);
		} else {
			bookingDb.setStatus(bookingDb.getStatus().toUpperCase());
		}
	}

}
