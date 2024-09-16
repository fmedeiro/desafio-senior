package com.desafiosenior.api_hotel.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.desafiosenior.api_hotel.model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
	Optional<Room> findByRoomId(UUID roomId);

	Optional<Room> findByNumber(Integer number);
}
