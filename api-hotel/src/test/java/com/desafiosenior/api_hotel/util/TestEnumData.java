package com.desafiosenior.api_hotel.util;

import lombok.Getter;

public enum TestEnumData {
	AVALUE("A"), BVALUE("B"), CVALUE("C");

	@Getter
	private String value;

	TestEnumData(String value) {
		this.value = value;
	}
}
