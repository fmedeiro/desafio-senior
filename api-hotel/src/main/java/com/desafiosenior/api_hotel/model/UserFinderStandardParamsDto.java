package com.desafiosenior.api_hotel.model;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserFinderStandardParamsDto(
	@Pattern(regexp = "\\d{9,14}", message = "O campo deve conter entre 9 e 14 dígitos numéricos.")
	String document,
	
	@Size(min = 4, max = 60, message = "Este campo tem que ter de 4 a 60 caracteres.") 
	String name,
	
	@Pattern(regexp = "\\d{8,9}", message = "O campo deve conter entre 8 e 9 dígitos numéricos.")
	String phone,
	
	@Pattern(regexp = "^\\d{2}$", message = "O campo deve conter 2 dígitos numéricos.")
	String phoneDdd,
	
	@Pattern(regexp = "^\\d{2}$", message = "O campo deve conter 2 dígitos numéricos.")
	String phoneDdi
	) {
	
    @Override
    public String toString() {
        return "UserFinderStandardParamsDto{" +
                "document='" + document + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", phoneDdd='" + phoneDdd + '\'' +
                ", phoneDdi='" + phoneDdi + '\'' +
                '}';
    }
}
