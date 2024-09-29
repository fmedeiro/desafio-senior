package com.desafiosenior.api_hotel.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class RoomTest {

    @Mock
    private List<Booking> bookings;

    private Room room;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        room = new Room(LocalDateTime.now());
        room.setBookings(new ArrayList<>());
        room.setNumber(101);
        room.setDateLastChange(LocalDateTime.now());
    }

    @Test
    @DisplayName("Testa a geracao de um room_id de um novo quarto e confere se ele gerou.")
    void testGenerateUUIDWhenRoomIdIsNull() {
        room.setRoomId(null);
        room.generateUUID();
        assertNotNull(room.getRoomId());
    }
    
    @Test
    @DisplayName("Testa a tentativa de geracao de um room_id quando ele ja existia, o room_id "
    		+ "pre-existente deve ser mantido.")
    void testGenerateUUIDWhenRoomIdIsNotNull() {
        UUID existingId = UUID.randomUUID();
        room.setRoomId(existingId);
        room.generateUUID();
        assertEquals(existingId, room.getRoomId());
    }
    
    @Test
    @DisplayName("Testa os Getters e Setters.")
    void testGettersAndSetters() {
        UUID roomId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        int number = 202;

        room.setRoomId(roomId);
        room.setNumber(number);
        room.setDateLastChange(now);

        assertEquals(roomId, room.getRoomId());
        assertEquals(number, room.getNumber());
        assertEquals(now, room.getDateLastChange());
    }
    
    @Test
    @DisplayName("Testa o EqualsAndHashCode.")
    void testEqualsAndHashCode() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Room room1 = new Room();
        room1.setRoomId(id1);

        Room room2 = new Room();
        room2.setRoomId(id1);

        Room room3 = new Room();
        room3.setRoomId(id2);

        assertEquals(room1, room2);
        assertNotEquals(room1, room3);
        assertEquals(room1.hashCode(), room2.hashCode());
        assertNotEquals(room1.hashCode(), room3.hashCode());
    }
    
    @Test
    @DisplayName("Testa o construtor usado com o dateRegister.")
    void testConstructorWithDateRegister() {
        LocalDateTime dateRegister = LocalDateTime.now();
        Room newRoom = new Room(dateRegister);
        assertEquals(dateRegister, newRoom.getDateRegister());
    }

    @Test
    @DisplayName("Testa a adicao de uma nova reserva em um quarto.")
    void testAddBooking() {
        Booking booking = new Booking();
        room.getBookings().add(booking);
        assertTrue(room.getBookings().contains(booking));
    }
    
    @Test
    @DisplayName("Testa o set e o get de novas reservas em um quarto.")
    void testSetBookings() {
    	Booking booking1 = new Booking();
    	Booking booking2 = new Booking();
    	List<Booking> bookings = new ArrayList<>(List.of(booking1, booking2));
        room.setBookings(bookings);
        assertTrue(room.getBookings().equals(bookings));
    }

    @Test
    @DisplayName("Testa a remocao de uma reserva de um quarto.")
    void testRemoveBooking() {
        Booking booking = new Booking();
        room.getBookings().add(booking);
        room.getBookings().remove(booking);
        assertFalse(room.getBookings().contains(booking));
    }
}
