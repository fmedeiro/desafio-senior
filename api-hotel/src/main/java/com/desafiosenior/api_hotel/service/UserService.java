package com.desafiosenior.api_hotel.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.desafiosenior.api_hotel.model.User;
import com.desafiosenior.api_hotel.model.UserDto;
import com.desafiosenior.api_hotel.repository.UserRepository;

import jakarta.transaction.Transactional;

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
		var user = new User();
		BeanUtils.copyProperties(userDto, user);
		user.setRole(user.getRole().toUpperCase());
        
		return userRepository.save(user);
	}

	@Transactional
	public Optional<User> update(UUID userId, UserDto userDto) {
		var userDb = userRepository.findByUserId(userId);
		
		if (userDb.isEmpty())
			return Optional.empty();
		
		BeanUtils.copyProperties(userDto, userDb.get());
		userDb.get().setRole(userDb.get().getRole().toUpperCase());
		return Optional.of(userRepository.save(userDb.get()));
	}
}
