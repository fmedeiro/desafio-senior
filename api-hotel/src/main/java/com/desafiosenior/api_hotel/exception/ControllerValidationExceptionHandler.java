package com.desafiosenior.api_hotel.exception;

import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;

public interface ControllerValidationExceptionHandler {

	ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex);

}
