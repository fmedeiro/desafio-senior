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

import com.desafiosenior.api_hotel.model.Room;
import com.desafiosenior.api_hotel.model.RoomDto;
import com.desafiosenior.api_hotel.repository.RoomRepository;

@Service
public class RoomService {
	private final RoomRepository roomRepository;

	public RoomService(RoomRepository roomRepository) {
		this.roomRepository = roomRepository;
	}

	@Transactional
	public Optional<ResponseEntity<Object>> delete(UUID roomId) {
		var roomDb = roomRepository.findByRoomId(roomId);

		if (roomDb.isEmpty())
			return Optional.empty();

		roomRepository.delete(roomDb.get());
		return Optional.of(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
	}

	@Transactional
	public void deleteAll() {
		roomRepository.deleteAll();
	}

	public List<Room> findAll() {
		return roomRepository.findAll();
	}
	
	public Optional<Room> findByNumber(Integer number) {
		var roomDb = roomRepository.findByNumber(number);

		if (roomDb.isEmpty())
			return Optional.empty();

		return roomDb;
	}

	public Optional<Room> findByRoomId(UUID roomId) {
		var roomDb = roomRepository.findByRoomId(roomId);

		if (roomDb.isEmpty())
			return Optional.empty();

		return roomDb;
	}

	@Transactional
	public Room save(RoomDto roomDto) {
		var room = new Room(LocalDateTime.now());
		BeanUtils.copyProperties(roomDto, room);
		room.setDateLastChange(LocalDateTime.now());

		return roomRepository.save(room);
	}

	@Transactional
	public Optional<Room> update(UUID roomId, RoomDto roomDto) {
		var roomDb = roomRepository.findByRoomId(roomId);

		if (roomDb.isEmpty())
			return Optional.empty();

		BeanUtils.copyProperties(roomDto, roomDb.get());
		roomDb.get().setDateLastChange(LocalDateTime.now());
		return Optional.of(roomRepository.save(roomDb.get()));
	}
}
