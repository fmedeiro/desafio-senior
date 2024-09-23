package com.desafiosenior.api_hotel.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record BookingDto(@NotNull RoomDto roomDto,
	@NotNull(message = "{label.userForBookingDto.valid.format.size}")
	UserForBookingDto userForBookingDto,

	@NotNull
	@FutureOrPresent(message = "{label.dateCheckin.valid.format.size}")
	LocalDateTime dateCheckin,

	@FutureOrPresent(message = "{label.dateCheckout.valid.format.size}")
	LocalDateTime dateCheckout,

	@Pattern(regexp = "^[CcFfSs ]$", message = "{label.status.valid.format.size}") 
	String status) {
}
