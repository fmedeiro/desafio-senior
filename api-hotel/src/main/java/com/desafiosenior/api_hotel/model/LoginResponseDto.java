package com.desafiosenior.api_hotel.model;

import jakarta.validation.constraints.NotNull;

public record LoginResponseDto(@NotNull String token) {

}
