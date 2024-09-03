package com.desafiosenior.api_hotel.model;

import lombok.Getter;

public enum UserRole {
	ADMIN("A"),
	GUEST("G"),
	USER_ATTENDANT("U");

	@Getter
    private String role;

	UserRole(String role){
        this.role = role;
    }
}
