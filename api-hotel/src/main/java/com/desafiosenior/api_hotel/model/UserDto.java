package com.desafiosenior.api_hotel.model;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserDto(
	List<Booking> bookings,
		
	@NotBlank
	@Pattern(regexp = "\\d{9,14}", message = "{label.document.valid.format.size}")
	String document,
	
	@NotBlank @Email @Size(min = 4, max = 50, message = "{label.email.valid.format.size}") 
	String email,
	
	@NotBlank @Size(min = 4, max = 12, message = "{label.login.valid.format.size}")
	String login, 
	
	@NotBlank @Size(min = 4, max = 60, message = "{label.name.valid.format.size}") 
	String name,
	
	@NotBlank @Size(min = 4, max = 8, message = "{label.password.valid.format.size}") 
	String password,
	
	@NotBlank
	@Pattern(regexp = "\\d{8,10}", message = "{label.phone.valid.format.size}")
	String phone,
	
	@NotBlank
	@Pattern(regexp = "^\\d{1,3}$", message = "{label.phoneDdd.valid.format.size}")
	String phoneDdd,
	
	@NotBlank
	@Pattern(regexp = "^\\d{1,2}$", message = "{label.phoneDdi.valid.format.size}")
	String phoneDdi,
	
	@NotBlank
	@Pattern(regexp = "^[AaGgUu]$", message = "{label.role.valid.format.size}") 
	String role
	) {
}
