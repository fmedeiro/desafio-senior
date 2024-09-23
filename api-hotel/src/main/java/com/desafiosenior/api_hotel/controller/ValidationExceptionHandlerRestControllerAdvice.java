package com.desafiosenior.api_hotel.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.hibernate.TransientPropertyValueException;
import org.postgresql.util.PSQLException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.desafiosenior.api_hotel.exception.ControllerValidationExceptionHandler;
import com.desafiosenior.api_hotel.exception.InvalidRequestException;
import com.desafiosenior.api_hotel.exception.ResourceConflictException;
import com.desafiosenior.api_hotel.exception.ResourceNotFoundException;

import jakarta.validation.UnexpectedTypeException;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class ValidationExceptionHandlerRestControllerAdvice implements ControllerValidationExceptionHandler {
	
	private static final String SQL_STATE_ERROR = "23505";
	private final MessageSource messageSource;
	private String detailKey;
	private String errorKey;
	
	public ValidationExceptionHandlerRestControllerAdvice(MessageSource messageSource) {
		this.messageSource = messageSource;
    }

	private Map<String, String> getMessageDetailsTemplate(Exception e) {
		log.error(e.getMessage(), e);
		Map<String, String> errorDetails = new LinkedHashMap<>();
		errorKey = messageSource.getMessage("error.key", null, LocaleContextHolder.getLocale());
		detailKey = messageSource.getMessage("detail.key", null, LocaleContextHolder.getLocale());
		errorDetails.put(errorKey, e.getMessage());

		return errorDetails;
	}

	private void extractMessageParts(Map<String, String> errorDetails, String detailMessage) {
		var parts = detailMessage.split(" ");
		for (String part : parts) {
			if (part.contains("=")) {
				var keyValue = part.split("=");
				var field = keyValue[0].replaceAll("[()]", "");
				var value = keyValue[1].replaceAll("[()]", "");
				var resolvedMessage = messageSource.getMessage("error.template.repeated.values", null,
						LocaleContextHolder.getLocale());
				errorDetails.put(field, resolvedMessage + value);
			}
		}
	}

	@Override
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(
			DataIntegrityViolationException ex) {
		log.error(ex.getMessage(), ex);
		Map<String, String> errorDetails = new LinkedHashMap<>();
		errorKey = messageSource.getMessage("error.key", null, LocaleContextHolder.getLocale());
		detailKey = messageSource.getMessage("detail.key", null, LocaleContextHolder.getLocale());
		var errorMessage = "";

		var rootCause = ex.getRootCause();

		if (rootCause instanceof PSQLException) {
			PSQLException sqlException = (PSQLException) rootCause;
			var sqlState = sqlException.getSQLState();

			if (SQL_STATE_ERROR.equals(sqlState)) {
				var detailMessage = sqlException.getServerErrorMessage().getDetail();
				extractMessageParts(errorDetails, detailMessage);
				errorMessage = messageSource.getMessage("error.postgresql.unique.violation.23505.409.conflict", null,
						LocaleContextHolder.getLocale());
				errorDetails.put(errorKey, errorMessage);

				return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetails);
			} else {
				errorMessage = messageSource.getMessage(
						"error.postgresql.other.sqlexception.violation.422.unprocessable.entity", null,
						LocaleContextHolder.getLocale());
				errorDetails.put(detailKey, sqlException.getMessage());
				errorDetails.put(errorKey, errorMessage);
			}
		} else {
			errorMessage = messageSource.getMessage("detail.sql.unknow.violation.422.unprocessable.entity", null,
					LocaleContextHolder.getLocale());
			errorDetails.put(detailKey, rootCause != null ? rootCause.getMessage() : errorMessage);
			errorMessage = messageSource.getMessage("error.sql.unknow.violation.422.unprocessable.entity", null,
					LocaleContextHolder.getLocale());
			errorDetails.put(errorKey, errorMessage);
		}

		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorDetails);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(
			HttpMessageNotReadableException ex) {
		Map<String, String> errorDetails = getMessageDetailsTemplate(ex);
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorDetails);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleInvalidArgumentException(MethodArgumentNotValidException ex) {
		log.error(ex.getMessage(), ex);
		Map<String, String> errors = new LinkedHashMap<>();
		var errorKey = messageSource.getMessage(
				"error.method.argument.not.valid.exception.422.unprocessable.entity.key", null,
				LocaleContextHolder.getLocale());
		var errorMessage = messageSource.getMessage(
				"error.method.argument.not.valid.exception.422.unprocessable.entity.value", null,
				LocaleContextHolder.getLocale());
		errors.put(errorKey, errorMessage);

		ex.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errors);
	}

	@ExceptionHandler(InvalidDataAccessResourceUsageException.class)
	public ResponseEntity<Map<String, String>> handleInvalidDataAccessResourceUsageException(
			InvalidDataAccessResourceUsageException ex) {
		Map<String, String> errorDetails = getMessageDetailsTemplate(ex);
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorDetails);
	}

	@ExceptionHandler(InvalidRequestException.class)
	public ResponseEntity<Map<String, String>> handleInvalidRequestException(InvalidRequestException ex) {
		Map<String, String> errorDetails = getMessageDetailsTemplate(ex);
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorDetails);
	}

	@ExceptionHandler(JWTCreationException.class)
	public ResponseEntity<Map<String, String>> handleJWTCreationException(JWTCreationException ex) {
		Map<String, String> errorDetails = getMessageDetailsTemplate(ex);
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorDetails);
	}

	@ExceptionHandler(JWTVerificationException.class)
	public ResponseEntity<Map<String, String>> handleJWTVerificationException(JWTVerificationException ex) {
		Map<String, String> errorDetails = getMessageDetailsTemplate(ex);
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorDetails);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Map<String, String>> handleMethodArgumentTypeMismatchException(
			MethodArgumentTypeMismatchException ex) {
		log.error(ex.getMessage(), ex);
		Map<String, String> error = new LinkedHashMap<>();
		errorKey = messageSource.getMessage("error.key", null, LocaleContextHolder.getLocale());
		detailKey = messageSource.getMessage("detail.key", null, LocaleContextHolder.getLocale());
		var errorMessage = "";

		if (ex.getParameter().getAnnotatedElement().toString().contains("UserController.update")) {
			errorMessage = messageSource.getMessage(
					"error.method.argument.type.mismatch.exception.400.bad.request.userid", null,
					LocaleContextHolder.getLocale());
			error.put(errorKey, errorMessage + ex.getCause().getMessage());
		} else {
			errorMessage = messageSource.getMessage(
					"error.method.argument.type.mismatch.exception.400.bad.request.unknow", null,
					LocaleContextHolder.getLocale());
			error.put(detailKey, errorMessage + ex.getCause());
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<Map<String, String>> handleNullPointerException(NullPointerException ex) {
		Map<String, String> errorDetails = getMessageDetailsTemplate(ex);
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorDetails);
	}

	@ExceptionHandler(ResourceConflictException.class)
	public ResponseEntity<Map<String, String>> handleResourceConflictException(ResourceConflictException ex) {
		Map<String, String> errorDetails = getMessageDetailsTemplate(ex);
		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetails);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
		Map<String, String> errorDetails = getMessageDetailsTemplate(ex);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
	}
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
		Map<String, String> errorDetails = getMessageDetailsTemplate(ex);
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorDetails);
	}
	
	@ExceptionHandler(TransientPropertyValueException.class)
	public ResponseEntity<Map<String, String>> handleTransientPropertyValueException(TransientPropertyValueException ex) {
		Map<String, String> errorDetails = getMessageDetailsTemplate(ex);
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorDetails);
	}

	@ExceptionHandler(UnexpectedTypeException.class)
	public ResponseEntity<Map<String, String>> handleUnexpectedTypeException(UnexpectedTypeException ex) {
		Map<String, String> errorDetails = getMessageDetailsTemplate(ex);
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorDetails);
	}

}
