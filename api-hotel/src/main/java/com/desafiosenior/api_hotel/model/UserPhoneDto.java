package com.desafiosenior.api_hotel.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserPhoneDto(	
	@NotBlank
	@Pattern(regexp = "^\\d{1,2}$", message = "O campo deve conter entre 1 e 2 dígitos numéricos.")
	String phoneDdi,
	
	@NotBlank
	@Pattern(regexp = "^\\d{1,3}$", message = "O campo deve conter entre 1 e 3 dígitos numéricos.")
	String phoneDdd,
	
	@NotBlank
	@Pattern(regexp = "\\d{8,10}", message = "O campo deve conter entre 8 e 10 dígitos numéricos.")
	String phone
	) {
}
