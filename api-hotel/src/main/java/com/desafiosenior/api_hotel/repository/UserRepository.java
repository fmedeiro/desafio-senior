package com.desafiosenior.api_hotel.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.desafiosenior.api_hotel.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {
	Optional<UserDetails> findByLogin(String login);
	Optional<User> findByUserId(UUID userId);
	Optional<User> findByEmail(String email);
	Optional<User> findByDocument(String document);
}
