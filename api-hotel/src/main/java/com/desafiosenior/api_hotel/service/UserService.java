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
import com.desafiosenior.api_hotel.model.BookingStatus;
import com.desafiosenior.api_hotel.model.User;
import com.desafiosenior.api_hotel.model.UserDto;
import com.desafiosenior.api_hotel.model.UserFinderStandardParamsDto;
import com.desafiosenior.api_hotel.repository.UserRepository;
import com.desafiosenior.api_hotel.util.AttributeChecker;

@Service
public class UserService {
	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional
	public Optional<ResponseEntity<Object>> delete(UUID userId) {
		var userDb = userRepository.findByUserId(userId);

		if (userDb.isEmpty())
			return Optional.empty();

		userRepository.delete(userDb.get());
		return Optional.of(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
	}

	@Transactional
	public void deleteAll() {
		userRepository.deleteAll();
	}

	public List<User> findAll() {
		return userRepository.findAll();
	}

	public Optional<User> findById(UUID userId) {
		var userDb = userRepository.findByUserId(userId);

		if (userDb.isEmpty())
			return Optional.empty();

		return userDb;
	}

	@Transactional
	public User save(UserDto userDto) {
		var user = new User(LocalDateTime.now());
		BeanUtils.copyProperties(userDto, user);
		user.setRole(user.getRole().toUpperCase());
		user.setDateLastChange(LocalDateTime.now());

		return userRepository.save(user);
	}

	@Transactional
	public Optional<User> update(UUID userId, UserDto userDto) {
		var userDb = userRepository.findById(userId);

		if (userDb.isEmpty())
			return Optional.empty();
		
	    BeanUtils.copyProperties(userDto, userDb.get(), "bookings", "password");
		userDb.get().setRole(userDb.get().getRole().toUpperCase());
		userDb.get().setDateLastChange(LocalDateTime.now());
		return Optional.of(userRepository.save(userDb.get()));
	}
	

	public Optional<User> findByPhoneDdiAndPhoneDddAndPhoneAndRole(String phoneDdi, String phoneDdd, String phone, String role) {
		var userDb = userRepository.findByPhoneDdiAndPhoneDddAndPhoneAndRole(phoneDdi, phoneDdd, phone, role);

		if (userDb.isEmpty())
			return Optional.empty();
		
		return userDb;
	}

	public Optional<User> findByDocumentAndRole(String document, String role) {
		var userDb = userRepository.findByDocumentAndRole(document, role);

		if (userDb.isEmpty())
			return Optional.empty();

		return userDb;
	}

	public Optional<User> findByNameAndRole(String name, String role) {
		var userDb = userRepository.findByNameIgnoreCaseAndRoleIgnoringSpaces(name, role);

		if (userDb.isEmpty())
			return Optional.empty();

		return userDb;
	}
	
	public Optional<User> getUserByAttributeChecker(UserFinderStandardParamsDto userHostedDto, String role) {
		var checker = new AttributeChecker();
		var attributeFound = checker.getFirstAttributePresent(userHostedDto, "document", "name");

		if (attributeFound != null) {
			if ("DOCUMENT".equals(attributeFound)) {
				return userRepository.findByDocumentAndRole(userHostedDto.document(), role);
			} else {
				return userRepository.findByNameIgnoreCaseAndRoleIgnoringSpaces(userHostedDto.name(), role);
			}
		}
		
		return checker.getFirstAttributePresent(userHostedDto, "phoneDdi") == null ? Optional.empty()
				: checker.getFirstAttributePresent(userHostedDto, "phoneDdd") == null ? Optional.empty()
						: checker.getFirstAttributePresent(userHostedDto, "phone") == null ? Optional.empty()
								: userRepository.findByPhoneDdiAndPhoneDddAndPhoneAndRole(userHostedDto.phoneDdi(),
										userHostedDto.phoneDdd(), userHostedDto.phone(), role);
	}
	
	private boolean isThereNoUserDb(Optional<User> userDb) {
		return userDb.isEmpty() || userDb.get().getBookings() == null || userDb.get().getBookings().isEmpty();
	}

	public Optional<User> findByGuestWhithBookingButNotIsHostedAtHotelYet(UserFinderStandardParamsDto userHostedDto, String role) {
		Optional<User> userDb = getUserByAttributeChecker(userHostedDto, role);

		if (isThereNoUserDb(userDb))
			return Optional.empty();

		if (!isTheGuestNotAtHotelYet(userDb.get()))
			return Optional.empty();

		return userDb;
	}
	
	private boolean isTheGuestNotAtHotelYet(User userDb) {
		List<Booking> bookings = userDb.getBookings();

		if (bookings != null && !bookings.isEmpty())
			return bookings.stream().anyMatch(booking -> (booking.getDateCheckout() == null
					&& BookingStatus.SCHEDULED.getStatus().equals(booking.getStatus())
					|| booking.getDateCheckout() != null && booking.getDateCheckout().isAfter(LocalDateTime.now())
					|| booking.getDateCheckout().isEqual(LocalDateTime.now())
							&& BookingStatus.SCHEDULED.getStatus().equals(booking.getStatus())));

		return false;
	}
	
	public Optional<User> findByGuestStayingAtHotel(UserFinderStandardParamsDto userHostedDto, String role) {
		Optional<User> userDb = getUserByAttributeChecker(userHostedDto, role);

		if (isThereNoUserDb(userDb))
			return Optional.empty();

		if (!isTheGuestStillAtTheHotel(userDb.get()))
			return Optional.empty();

		return userDb;
	}

	private boolean isTheGuestStillAtTheHotel(User userDb) {
		List<Booking> bookings = userDb.getBookings();

		if (bookings != null && !bookings.isEmpty())
			return bookings.stream()
					.anyMatch(booking -> (booking.getDateCheckout() == null
							&& booking.getDateCheckin().isBefore(LocalDateTime.now())
							&& BookingStatus.CHECKIN.getStatus().equals(booking.getStatus()))
							|| (BookingStatus.CHECKIN.getStatus().equals(booking.getStatus())
									&& booking.getDateCheckout() != null
									&& (booking.getDateCheckout().isAfter(LocalDateTime.now())
											|| booking.getDateCheckout().isEqual(LocalDateTime.now()))));

		return false;
	}

}
