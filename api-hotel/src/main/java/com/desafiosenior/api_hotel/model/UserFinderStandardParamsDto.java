package com.desafiosenior.api_hotel.model;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserFinderStandardParamsDto(
	@Pattern(regexp = "\\d{9,14}", message = "{label.document.valid.format.size}")
	String document,
	
	@Size(min = 4, max = 60, message = "{label.name.valid.format.size}") 
	String name,
	
	@Pattern(regexp = "\\d{8,10}", message = "{label.phone.valid.format.size}")
	String phone,
	
	@Pattern(regexp = "^\\d{1,3}$", message = "{label.phoneDdd.valid.format.size}")
	String phoneDdd,
	
	@Pattern(regexp = "^\\d{1,2}$", message = "{label.phoneDdi.valid.format.size}")
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
