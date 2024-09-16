package com.desafiosenior.api_hotel.model;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserDto(
	List<Booking> bookings,
		
	@NotBlank
	@Pattern(regexp = "\\d{9,14}", message = "O campo deve conter entre 9 e 14 dígitos numéricos.")
	String document,
	
	@NotBlank @Email @Size(min = 4, max = 50, message = "Este campo tem que ter de 4 a 50 caracteres.") 
	String email,
	
	@NotBlank @Size(min = 4, max = 12, message = "Este campo tem que ter de 4 a 12 caracteres.")
	String login, 
	
	@NotBlank @Size(min = 4, max = 60, message = "Este campo tem que ter de 4 a 60 caracteres.") 
	String name,
	
	@NotBlank @Size(min = 4, max = 8, message = "Este campo tem que ter de 4 a 8 caracteres.") 
	String password,
	
	@NotBlank
	@Pattern(regexp = "\\d{8,9}", message = "O campo deve conter entre 8 e 9 dígitos numéricos.")
	String phone,
	
	@NotBlank
	@Pattern(regexp = "^\\d{2}$", message = "O campo deve conter 2 dígitos numéricos.")
	String phoneDdd,
	
	@NotBlank
	@Pattern(regexp = "^\\d{2}$", message = "O campo deve conter 2 dígitos numéricos.")
	String phoneDdi,
	
	@NotBlank
	@Pattern(regexp = "^[AaGgUu]$", message = "O campo deve conter apenas uma das letras: A (ADMIN), G (GUEST) ou U (USER_ATTENDANT), maiúsculas ou minúsculas.") 
	String role
	) {
}
