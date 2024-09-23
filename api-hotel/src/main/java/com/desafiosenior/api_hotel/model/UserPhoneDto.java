package com.desafiosenior.api_hotel.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserPhoneDto(	
	@NotBlank
	@Pattern(regexp = "^\\d{1,2}$", message = "{label.phoneDdi.valid.format.size}")
	String phoneDdi,
	
	@NotBlank
	@Pattern(regexp = "^\\d{1,3}$", message = "{label.phoneDdd.valid.format.size}")
	String phoneDdd,
	
	@NotBlank
	@Pattern(regexp = "\\d{8,10}", message = "{label.phone.valid.format.size}")
	String phone
	) {
}
