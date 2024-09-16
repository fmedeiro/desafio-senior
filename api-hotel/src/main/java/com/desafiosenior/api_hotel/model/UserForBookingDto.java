package com.desafiosenior.api_hotel.model;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public record UserForBookingDto(
	@NotBlank
    UUID userId
	) {
}
