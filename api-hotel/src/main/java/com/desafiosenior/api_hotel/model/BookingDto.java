package com.desafiosenior.api_hotel.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record BookingDto(@NotNull RoomDto roomDto,
	@NotNull(message = "Este campo tem que conter uma chave, userId, com um valor que representa um identificador universalmente exclusivo (UUID) imutável. Um UUID representa um valor de 128 bits.")
	UserForBookingDto userForBookingDto,

	@NotNull
	@FutureOrPresent(message = "A data de checkin deve ser agora ou no futuro. É um LocalDateTime, exemplos de uso: '2024-09-14T13:29:00.75049'")
	LocalDateTime dateCheckin,

	@FutureOrPresent(message = "A data de checkout é um campo opcional e deve ser destinada ao futuro. É um LocalDateTime, exemplos de uso: '2024-09-14T13:29:00.75049'")
	LocalDateTime dateCheckout,

	@Pattern(regexp = "^[CcFfSs ]$", message = "O campo deve conter apenas uma das letras: C (CHECKIN), F (FREE) ou S (SCHEDULED), maiúsculas ou minúsculas.") 
	String status) {
}
