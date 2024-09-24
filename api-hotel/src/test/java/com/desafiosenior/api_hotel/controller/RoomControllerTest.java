package com.desafiosenior.api_hotel.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.desafiosenior.api_hotel.exception.InvalidRequestException;
import com.desafiosenior.api_hotel.exception.ResourceNotFoundException;
import com.desafiosenior.api_hotel.model.Room;
import com.desafiosenior.api_hotel.model.RoomDto;
import com.desafiosenior.api_hotel.service.RoomService;

class RoomControllerTest {

    @Mock
    private RoomService roomService;

    @InjectMocks
    private RoomController roomController;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Testa a remocao de um quarto da tabela rooms.")
    public void testDelete_shouldReturnNoContent() {
        UUID roomId = UUID.randomUUID();
        when(roomService.delete(roomId)).thenReturn(Optional.of(ResponseEntity.status(HttpStatus.NO_CONTENT).build()));

        ResponseEntity<Object> response = roomController.delete(roomId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(roomService, times(1)).delete(roomId);
    }
    
    @Test
    @DisplayName("Testa a remocao de um quarto inexistente da tabela rooms.")
    public void testDelete_NotFound_shouldReturnResourceNotFoundException() {
        UUID roomId = UUID.randomUUID();
        when(roomService.delete(roomId)).thenReturn(Optional.empty());

        try {
            roomController.delete(roomId);
        } catch (ResourceNotFoundException e) {
            assertEquals("Quarto não encontrado para o ID: " + roomId, e.getMessage());
        }

        verify(roomService, times(1)).delete(roomId);
    }
    
    @Test
    @DisplayName("Testa a remocao de todos os quartos da tabela rooms.")
    public void testDeleteAll_shouldReturnNoContent() {
        doNothing().when(roomService).deleteAll();

        ResponseEntity<Object> response = roomController.deleteAll();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(roomService, times(1)).deleteAll();
    }

    @Test
    @DisplayName("Testa a consulta de um quarto da tabela rooms, pelo seu room_id.")
    public void testFindOneRoom_shouldReturnHttpStatusOk() {
        UUID roomId = UUID.randomUUID();
        Room room = new Room();
        when(roomService.findByRoomId(roomId)).thenReturn(Optional.of(room));

        ResponseEntity<Object> response = roomController.findOneRoom(roomId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(room, response.getBody());
        verify(roomService, times(1)).findByRoomId(roomId);
    }
    
    @Test
    @DisplayName("Testa a consulta de um quarto inexistente da tabela rooms, pelo seu room_id.")
    public void testFindOneRoom_NotFound_shouldReturnResourceNotFoundException() {
        UUID roomId = UUID.randomUUID();
        when(roomService.findByRoomId(roomId)).thenReturn(Optional.empty());

        try {
            roomController.findOneRoom(roomId);
        } catch (ResourceNotFoundException e) {
            assertEquals("Quarto não encontrado para o ID: " + roomId, e.getMessage());
        }

        verify(roomService, times(1)).findByRoomId(roomId);
    }
    
    @Test
    @DisplayName("Testa a consulta de todos os quartos da tabela rooms.")
    public void testListAll_shouldReturnHttpStatusOk() {
        List<Room> rooms = List.of(new Room(), new Room());
        when(roomService.findAll()).thenReturn(rooms);

        ResponseEntity<List<Room>> response = roomController.listAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rooms, response.getBody());
        verify(roomService, times(1)).findAll();
    }

    @Test
    @DisplayName("Testa a criacao de um novo quarto na tabela rooms.")
    public void testSave_shouldReturnHttpStatusCreated() {
        RoomDto roomDto = new RoomDto(null, null);
        Room savedRoom = new Room();
        when(roomService.save(any(RoomDto.class))).thenReturn(savedRoom);

        ResponseEntity<Object> response = roomController.save(roomDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedRoom, response.getBody());
        verify(roomService, times(1)).save(any(RoomDto.class));
    }

    @Test
    @DisplayName("Testa a alteracao de um quarto existente na tabela rooms.")
    public void testUpdate_shouldReturnNoContent() {
        UUID roomId = UUID.randomUUID();
        RoomDto roomDto = new RoomDto(null, null);
        Room room = new Room();
        when(roomService.update(roomId, roomDto)).thenReturn(Optional.of(room));

        ResponseEntity<Object> response = roomController.update(roomId, roomDto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(roomService, times(1)).update(roomId, roomDto);
    }
    
    @Test
    @DisplayName("Testa a alteracao de um quarto inexistente na tabela rooms.")
    public void testUpdate_NotFound_shouldReturnResourceNotFoundException() {
        UUID roomId = UUID.randomUUID();
        RoomDto roomDto = new RoomDto(null, null);
        when(roomService.update(roomId, roomDto)).thenReturn(Optional.empty());

        try {
            roomController.update(roomId, roomDto);
        } catch (ResourceNotFoundException e) {
            assertEquals("Quarto não encontrado para o ID: " + roomId, e.getMessage());
        }

        verify(roomService, times(1)).update(roomId, roomDto);
    }
    
    @Test
    @DisplayName("Testa a alteracao de um quarto inexistente na tabela rooms forcando o erro InvalidRequestException.")
    public void testUpdate_shouldReturnInvalidRequestException() {
        UUID roomId = UUID.randomUUID();
        RoomDto roomDto = new RoomDto(null, null);

        when(roomService.update(roomId, roomDto)).thenThrow(new InvalidRequestException("Dados inválidos para o ID: " + roomId));

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            roomController.update(roomId, roomDto);
        });

        assertEquals("Dados inválidos para o ID: " + roomId, exception.getMessage());
    }

}
