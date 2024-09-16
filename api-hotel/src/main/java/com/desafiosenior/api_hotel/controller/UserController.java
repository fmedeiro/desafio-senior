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
import com.desafiosenior.api_hotel.exception.ResourceNotFoundException;
import com.desafiosenior.api_hotel.model.User;
import com.desafiosenior.api_hotel.model.UserDto;
import com.desafiosenior.api_hotel.model.UserFinderStandardParamsDto;
import com.desafiosenior.api_hotel.model.UserNameDto;
import com.desafiosenior.api_hotel.model.UserPhoneDto;
import com.desafiosenior.api_hotel.model.UserRole;
import com.desafiosenior.api_hotel.service.UserService;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<Object> delete(@PathVariable UUID userId) {
		var objectResponse = userService.delete(userId);

		if (objectResponse.isEmpty()) {
			throw new ResourceNotFoundException("User não encontrado para o ID: " + userId);
		}

		return objectResponse.get();
	}

	@DeleteMapping()
	public ResponseEntity<Object> deleteAll() {
		userService.deleteAll();

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	@GetMapping("/admins/phone")
	public ResponseEntity<Object> findOneAdminByPhone(@RequestBody @Valid UserPhoneDto userPhoneDto) {
		var adminDb = userService.findByPhoneDdiAndPhoneDddAndPhoneAndRole(userPhoneDto.phoneDdi(),
				userPhoneDto.phoneDdd(), userPhoneDto.phone(), UserRole.ADMIN.getRole());

		if (adminDb.isEmpty()) {
			throw new ResourceNotFoundException("Admin não encontrado para o telefone: " + "("
					+ userPhoneDto.phoneDdi() + ") (" + userPhoneDto.phoneDdd() + ") " + userPhoneDto.phone());
		}

		return ResponseEntity.status(HttpStatus.OK).body(adminDb.get());
	}
	
	@GetMapping("/attendants/phone")
	public ResponseEntity<Object> findOneAttendantByPhone(@RequestBody @Valid UserPhoneDto userPhoneDto) {
		var attendantDb = userService.findByPhoneDdiAndPhoneDddAndPhoneAndRole(userPhoneDto.phoneDdi(),
				userPhoneDto.phoneDdd(), userPhoneDto.phone(), UserRole.USER_ATTENDANT.getRole());

		if (attendantDb.isEmpty()) {
			throw new ResourceNotFoundException("Atendente não encontrado para o telefone: " + "("
					+ userPhoneDto.phoneDdi() + ") (" + userPhoneDto.phoneDdd() + ") " + userPhoneDto.phone());
		}

		return ResponseEntity.status(HttpStatus.OK).body(attendantDb.get());
	}
	
	
	@GetMapping("/guests/document/{document}")
	public ResponseEntity<Object> findOneGuestByDocument(@PathVariable String document) {
		var guestDb = userService.findByDocumentAndRole(document, UserRole.GUEST.getRole());

		if (guestDb.isEmpty()) {
			throw new ResourceNotFoundException("Hóspede não encontrado para o documento número: " + document);
		}

		return ResponseEntity.status(HttpStatus.OK).body(guestDb.get());
	}
	
	@GetMapping("/guests/name")
	public ResponseEntity<Object> findOneGuestByName(@RequestBody @Valid UserNameDto userNameDto) {
		var guestDb = userService.findByNameAndRole(userNameDto.name(), UserRole.GUEST.getRole());

		if (guestDb.isEmpty()) {
			throw new ResourceNotFoundException("Hóspede não encontrado: " + userNameDto.name());
		}

		return ResponseEntity.status(HttpStatus.OK).body(guestDb.get());
	}

	@GetMapping("/guests/phone")
	public ResponseEntity<Object> findOneGuestByPhone(@RequestBody @Valid UserPhoneDto userPhoneDto) {
		var guestDb = userService.findByPhoneDdiAndPhoneDddAndPhoneAndRole(userPhoneDto.phoneDdi(),
				userPhoneDto.phoneDdd(), userPhoneDto.phone(), UserRole.GUEST.getRole());

		if (guestDb.isEmpty()) {
			throw new ResourceNotFoundException("Hóspede não encontrado para o telefone: " + "("
					+ userPhoneDto.phoneDdi() + ") (" + userPhoneDto.phoneDdd() + ") " + userPhoneDto.phone());
		}

		return ResponseEntity.status(HttpStatus.OK).body(guestDb.get());
	}
	
	@GetMapping("/guests/hosted")
	public ResponseEntity<Object> findOneGuestStayingAtHotel(@RequestBody @Valid UserFinderStandardParamsDto userHostedDto) {
		var guestDb = userService.findByGuestStayingAtHotel(userHostedDto, UserRole.GUEST.getRole());

		if (guestDb.isEmpty()) {
			throw new ResourceNotFoundException("Hóspede não encontrado: " + userHostedDto.toString());
		}

		return ResponseEntity.status(HttpStatus.OK).body(guestDb.get());
	}
	
	
	@GetMapping("/{userId}")
	public ResponseEntity<Object> findOneUser(@PathVariable UUID userId) {
		var userDb = userService.findById(userId);

		if (userDb.isEmpty()) {
			throw new ResourceNotFoundException("User não encontrado para o ID: " + userId);
		}

		return ResponseEntity.status(HttpStatus.OK).body(userDb.get());
	}

	@GetMapping()
	public ResponseEntity<List<User>> listAll() {
		return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
	}

	@PostMapping()
	public ResponseEntity<Object> save(@RequestBody @Valid UserDto userDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(userDto));
	}

	@PutMapping("/{userId}")
	public ResponseEntity<Void> update(@PathVariable UUID userId, @RequestBody @Valid UserDto userDto) {
		try {
			var userDb = userService.update(userId, userDto);

			if (userDb.isEmpty()) {
				throw new ResourceNotFoundException("User não encontrado para o ID: " + userId);
			}

			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (InvalidRequestException ex) {
			// Excecao sera capturada e tratada pelo ControllerAdvice
			throw ex;
		}
	}
}
