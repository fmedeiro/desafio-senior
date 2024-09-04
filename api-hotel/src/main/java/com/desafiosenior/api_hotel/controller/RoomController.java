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
import com.desafiosenior.api_hotel.exception.ResourceConflictException;
import com.desafiosenior.api_hotel.exception.ResourceNotFoundException;
import com.desafiosenior.api_hotel.model.Room;
import com.desafiosenior.api_hotel.model.RoomDto;
import com.desafiosenior.api_hotel.service.RoomService;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/rooms")
public class RoomController {
	private final RoomService roomService;

	public RoomController(RoomService roomService) {
		this.roomService = roomService;
	}

	@DeleteMapping("/{roomId}")
	public ResponseEntity<Object> delete(@PathVariable UUID roomId) {
		var objectResponse = roomService.delete(roomId);

		if (objectResponse.isEmpty()) {
			throw new ResourceNotFoundException("Quarto não encontrado para o ID: " + roomId);
		}

		return objectResponse.get();
	}

	@DeleteMapping()
	public ResponseEntity<Object> deleteAll() {
		roomService.deleteAll();

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("/{roomId}")
	public ResponseEntity<Object> findOneRoom(@PathVariable UUID roomId) {
		var roomDb = roomService.findByRoomId(roomId);

		if (roomDb.isEmpty()) {
			throw new ResourceNotFoundException("Quarto não encontrado para o ID: " + roomId);
		}

		return ResponseEntity.status(HttpStatus.OK).body(roomDb.get());
	}

	@GetMapping()
	public ResponseEntity<List<Room>> listAll() {
		return ResponseEntity.status(HttpStatus.OK).body(roomService.findAll());
	}

	@PostMapping()
	public ResponseEntity<Object> save(@RequestBody @Valid RoomDto roomDto) {
		Room room = roomService.save(roomDto);

		if (room == null) {
			throw new ResourceConflictException("Número de quarto já existente na DB: " + roomDto.number());
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(room);
	}

	@PutMapping("/{roomId}")
	public ResponseEntity<Object> update(@PathVariable UUID roomId, @RequestBody @Valid RoomDto roomDto) {
		try {
			var roomDb = roomService.update(roomId, roomDto);

			if (roomDb.isEmpty()) {
				throw new ResourceNotFoundException("Quarto não encontrado para o ID: " + roomId);
			}

			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (InvalidRequestException ex) {
			// Excecao sera capturada e tratada pelo ControllerAdvice
			throw ex;
		}
	}
}
