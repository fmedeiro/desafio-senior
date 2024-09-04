package com.desafiosenior.api_hotel.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RoomDto(
	Booking booking,
	
	@NotNull(message = "O número do quarto é obrigatório")
	@Min(value = 1, message = "O número do quarto deve ser maior do que 0")
	@Max(value = 1000, message = "O número do quarto deve ser menor do que 1000")
	Integer number
	) {
}
