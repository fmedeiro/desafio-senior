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
import com.desafiosenior.api_hotel.model.BookingStatus;
import com.desafiosenior.api_hotel.model.User;
import com.desafiosenior.api_hotel.model.UserDto;
import com.desafiosenior.api_hotel.model.UserFinderStandardParamsDto;
import com.desafiosenior.api_hotel.repository.UserRepository;
import com.desafiosenior.api_hotel.util.AttributeChecker;

@Service
public class UserService {
	private final MessageSource messageSource;
	private final UserRepository userRepository;

	public UserService(MessageSource messageSource, UserRepository userRepository) {
		this.messageSource = messageSource;
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
	
	public List<Optional<User>> getUsersByAttributeChecker(UserFinderStandardParamsDto userHostedDto, String role) {
		var checker = new AttributeChecker();
		var attributeFound = checker.getFirstAttributePresent(userHostedDto, "document", "name");

		if (attributeFound != null) {
			if ("DOCUMENT".equals(attributeFound)) {
				return userRepository.findByDocumentAndRole(userHostedDto.document(), role);
			} else {
				return userRepository.findByNameIgnoreCaseAndRoleIgnoringSpaces(userHostedDto.name(), role);
			}
		}
		
		return checker.getFirstAttributePresent(userHostedDto, "phoneDdi") == null ? List.of(Optional.empty())
				: checker.getFirstAttributePresent(userHostedDto, "phoneDdd") == null ? List.of(Optional.empty())
						: checker.getFirstAttributePresent(userHostedDto, "phone") == null ? List.of(Optional.empty())
								: userRepository.findByPhoneDdiAndPhoneDddAndPhoneAndRole(userHostedDto.phoneDdi(),
										userHostedDto.phoneDdd(), userHostedDto.phone(), role);
	}
	
	private List<Optional<User>> getUsersWithFilteredUnhostedGuestsBookings(List<Optional<User>> usersDb) {
	    return usersDb.stream()
	            .filter(Optional::isPresent)
	            .map(Optional::get)
	            .filter(user -> user.getBookings().stream().anyMatch(booking -> 
	                booking.getDateCheckin().isAfter(LocalDateTime.now())
	                && BookingStatus.SCHEDULED.getStatus().equals(booking.getStatus())
	                && (booking.getDateCheckout() == null 
	                    || booking.getDateCheckout() != null 
	                        && (booking.getDateCheckout().isAfter(LocalDateTime.now()) 
	                            || booking.getDateCheckout().isEqual(LocalDateTime.now()))
	                )
	            ))
	            .map(Optional::of)
	            .toList();
	}

	public List<Optional<User>> findByGuestWhithBookingButNotIsHostedAtHotelYet(UserFinderStandardParamsDto userUnhostedDto, String role) {
		chekingInputParametersException(userUnhostedDto);
		List<Optional<User>> usersDb = getUsersByAttributeChecker(userUnhostedDto, role);
		List<Optional<User>> usersWithFilteredBookings = getUsersWithFilteredUnhostedGuestsBookings(usersDb);

		if (isThereNoUserDb(usersWithFilteredBookings))
			return List.of(Optional.empty());

		return usersWithFilteredBookings;
	}
	
	private boolean isThereNoUserDb(List<Optional<User>> usersDb) {
	    return usersDb.stream()
	            .allMatch(userDb -> userDb.isEmpty() || userDb.get().getBookings() == null || userDb.get().getBookings().isEmpty());
	}
	
	private List<Optional<User>> getUsersWithFilteredHostedGuestsBookings(List<Optional<User>> usersDb) {
		return usersDb.stream().filter(Optional::isPresent).map(Optional::get)
				.filter(user -> user.getBookings().stream()
						.anyMatch(booking -> (booking.getDateCheckout() == null
								&& booking.getDateCheckin().isBefore(LocalDateTime.now())
								&& BookingStatus.CHECKIN.getStatus().equals(booking.getStatus()))
								|| (BookingStatus.CHECKIN.getStatus().equals(booking.getStatus())
										&& booking.getDateCheckout() != null
										&& (booking.getDateCheckout().isAfter(LocalDateTime.now())
												|| booking.getDateCheckout().isEqual(LocalDateTime.now())))))
				.map(Optional::of).toList();
	}

	public List<Optional<User>> findByGuestStayingAtHotel(UserFinderStandardParamsDto userHostedDto, String role) {
		chekingInputParametersException(userHostedDto);
		List<Optional<User>> usersDb = getUsersByAttributeChecker(userHostedDto, role);
		List<Optional<User>> usersWithFilteredBookings = getUsersWithFilteredHostedGuestsBookings(usersDb);

		if (isThereNoUserDb(usersWithFilteredBookings))
			return List.of(Optional.empty());

		return usersDb;
	}

	private void chekingInputParametersException(UserFinderStandardParamsDto userFinderStandardParamsDto) {
		if (userFinderStandardParamsDto.document() == null && userFinderStandardParamsDto.name() == null
				&& (userFinderStandardParamsDto.phoneDdd() == null || userFinderStandardParamsDto.phoneDdi() == null
				|| userFinderStandardParamsDto.phone() == null)) {
			var messageException = messageSource.getMessage("error.method.standard.arguments.not.valid.exception.422.unprocessable.entity", null, LocaleContextHolder.getLocale());
			throw new InvalidRequestException(messageException);
		}
	}

}
