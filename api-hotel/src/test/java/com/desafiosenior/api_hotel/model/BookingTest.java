package com.desafiosenior.api_hotel.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {
	
    @Mock
    private Room room;

    @Mock
    private Payment payment;

    @Mock
    private User user;

    private Booking booking;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        booking = new Booking(LocalDateTime.now());
        
        booking.setRoom(room);
        booking.setPayment(payment);
        booking.setUser(user);
        booking.setDateCheckin(LocalDateTime.now());
        booking.setDateLastChange(LocalDateTime.now());
        booking.setStatus(BookingStatus.FREE.getStatus());
    }

    @Test
    @DisplayName("Testa a tentativa de setar um booking_id quando o objeto Booking ainda nao tiver um id, ou seja, id = null.")
    void testGenerateUUIDWhenBookingIdIsNull_shouldSetNewBookingId() {
        booking.setBookingId(null);
        booking.generateUUID();

        assertNotNull(booking.getBookingId());
    }

	@Test
	@DisplayName("Testa a tentativa de setar um booking_id quando o objeto Booking ja tiver um id setado, ou seja, o id "
			+ "setado deve permancer sendo o id apos rodar o generateUUID.")
	void testGenerateUUIDWhenBookingIdIsNotNull_thePreviousExistingBookingIdShouldStillSeted() {
		UUID existingId = UUID.randomUUID();
		booking.setBookingId(existingId);
		booking.generateUUID();

		assertEquals(existingId, booking.getBookingId());
	}

    @Test
    @DisplayName("Testa os setters e getters do objeto Booking.")
    void testGettersAndSetters() {
        LocalDateTime now = LocalDateTime.now();
        UUID bookingId = UUID.randomUUID();

        booking.setBookingId(bookingId);
        booking.setDateCheckout(now);
        booking.setPayment(payment);
        booking.setStatus(BookingStatus.SCHEDULED.getStatus());

        assertEquals(bookingId, booking.getBookingId());
        assertEquals(now, booking.getDateCheckout());
        assertEquals(payment, booking.getPayment());
        assertEquals(BookingStatus.SCHEDULED.getStatus(), booking.getStatus());
    }

    @Test
    @DisplayName("Testa o EqualsAndHashCode do objeto Booking.")
    void testEqualsAndHashCode() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Booking booking1 = new Booking();
        booking1.setBookingId(id1);

        Booking booking2 = new Booking();
        booking2.setBookingId(id1);

        Booking booking3 = new Booking();
        booking3.setBookingId(id2);

        assertEquals(booking1, booking2);
        assertNotEquals(booking1, booking3);
        assertEquals(booking1.hashCode(), booking2.hashCode());
        assertNotEquals(booking1.hashCode(), booking3.hashCode());
    }

    @Test
    @DisplayName("Testa a instanciacao de um objeto Booking com o parametro dateRegister.")
    void testConstructorWithDateRegister() {
        LocalDateTime dateRegister = LocalDateTime.now();
        Booking newBooking = new Booking(dateRegister);

        assertEquals(dateRegister, newBooking.getDateRegister());
    }

    @Test
    @DisplayName("Testa o metodo PrePersistGeneratesUUID atraves da instanciacao de um objeto Booking "
    		+ "com o parametro dateRegister.")
    void testPrePersistGeneratesUUID() {
    	Booking newBooking = new Booking(LocalDateTime.now());
        assertNull(newBooking.getBookingId());

        newBooking.generateUUID();
        assertNotNull(newBooking.getBookingId());
    }

}
