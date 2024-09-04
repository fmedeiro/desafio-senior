package com.desafiosenior.api_hotel.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record BookingDto(@NotNull Room room,
	@NotNull 
	UUID userId,

	@NotNull 
	LocalDateTime dateCheckin,

	LocalDateTime dateCheckout,

	@Pattern(regexp = "^[CcFfSs ]$", message = "O campo deve conter apenas uma das letras: C (CHECKIN), F (FREE) ou S (SCHEDULED), maiúsculas ou minúsculas.") 
	String status) {
}
