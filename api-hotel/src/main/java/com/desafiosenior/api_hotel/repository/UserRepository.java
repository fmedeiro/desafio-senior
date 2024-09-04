package com.desafiosenior.api_hotel.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import com.desafiosenior.api_hotel.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
	Optional<UserDetails> findByLogin(String login);

	Optional<User> findByUserId(UUID userId);

	Optional<User> findByPhoneDdiAndPhoneDddAndPhone(String phoneDdi, String phoneDdd, String phone);
	
	Optional<User> findByName(String name);
	
	Optional<User> findByEmail(String email);

	Optional<User> findByDocument(String document);
}
