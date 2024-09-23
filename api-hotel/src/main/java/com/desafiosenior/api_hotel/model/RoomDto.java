package com.desafiosenior.api_hotel.model;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RoomDto(
	UUID roomId,
	
	@NotNull(message = "{label.number.valid.format.notNull}")
	@Min(value = 1, message = "{label.number.valid.format.size.min}")
	@Max(value = 1000, message = "{label.number.valid.format.size.max}")
	Integer number
	) {
}
