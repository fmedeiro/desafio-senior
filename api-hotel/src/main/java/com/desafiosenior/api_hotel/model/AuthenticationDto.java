package com.desafiosenior.api_hotel.model;

import jakarta.validation.constraints.NotNull;

public record AuthenticationDto(@NotNull String login, @NotNull String password) {

}
