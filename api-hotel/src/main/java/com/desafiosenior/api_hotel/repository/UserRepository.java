package com.desafiosenior.api_hotel.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import com.desafiosenior.api_hotel.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
	Optional<UserDetails> findByLogin(String login);

	Optional<User> findByUserId(UUID userId);
	
	Optional<User> findByPhoneDdiAndPhoneDddAndPhoneAndRole(String phoneDdi, String phoneDdd, String phone, String role);

	@Query("SELECT u FROM User u WHERE LOWER(REPLACE(u.name, ' ', '')) = LOWER(REPLACE(:name, ' ', '')) AND u.role = :role")
	Optional<User> findByNameIgnoreCaseAndRoleIgnoringSpaces(String name, String role);
	
	Optional<User> findByDocumentAndRole(String document, String role);
}

