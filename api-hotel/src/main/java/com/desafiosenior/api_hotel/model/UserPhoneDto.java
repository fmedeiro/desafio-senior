package com.desafiosenior.api_hotel.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserPhoneDto(	
	@NotBlank
	@Pattern(regexp = "^\\d{2}$", message = "O campo deve conter 2 dígitos numéricos.")
	String phoneDdi,
	
	@NotBlank
	@Pattern(regexp = "^\\d{2}$", message = "O campo deve conter 2 dígitos numéricos.")
	String phoneDdd,
	
	@NotBlank
	@Pattern(regexp = "\\d{8,9}", message = "O campo deve conter entre 8 e 9 dígitos numéricos.")
	String phone
	) {
}
