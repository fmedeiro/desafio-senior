package com.desafiosenior.api_hotel.model;

import lombok.Getter;

public enum BookingStatus {
	CHECKIN("C"), 
	FREE("F"), // reserva cancelada ou checkout
	SCHEDULED("S");

	@Getter
	private String status;

	BookingStatus(String status) {
		this.status = status;
	}
}
