package com.desafiosenior.api_hotel.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.desafiosenior.api_hotel.model.Booking;
import com.desafiosenior.api_hotel.model.Room;
import com.desafiosenior.api_hotel.model.User;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
	List<Booking> findByDateCheckin(LocalDateTime dateCheckin);

	List<Booking> findByDateCheckinBeforeAndDateCheckoutIsNull(LocalDateTime dateCheckin);

	List<Booking> findByDateCheckinLessThanAndDateCheckoutGreaterThanEqual(LocalDateTime dateCheckin,
			LocalDateTime dateCheckout);

	Optional<Booking> findByBookingId(UUID bookingId);
	
	Optional<User> findByUser_UserIdAndDateCheckin(UUID userId, LocalDateTime dateCheckin);
	
	Optional<User> findByUser_UserIdAndDateCheckinAndDateCheckout(UUID userId, LocalDateTime dateCheckin, LocalDateTime dateCheckout);
	
	List<Optional<Booking>> findByStatus(String status);

	List<Booking> findByStatusAndRoom(String status, Room room);

	Optional<Booking> findByStatusAndDateCheckin(String status, LocalDateTime dateCheckin);

	List<Booking> findByDateCheckinAndRoom(LocalDateTime dateCheckin, Room room);

	List<Booking> findByUser_UserId(UUID userId);  
}
