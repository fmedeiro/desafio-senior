package com.desafiosenior.api_hotel.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserNameDto(
	@NotBlank @Size(min = 4, max = 60, message = "Este campo tem que ter de 4 a 60 caracteres.") 
	String name
	) {
}
