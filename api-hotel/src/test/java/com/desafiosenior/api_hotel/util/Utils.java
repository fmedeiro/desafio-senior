package com.desafiosenior.api_hotel.util;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintViolation;

@Component
public class Utils {
	
	private final MessageSource messageSource;
	
	public Utils(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public <T> boolean assertEqualsWithOr(T actual, @SuppressWarnings("unchecked") T... expectedValues) {
		if (actual == null) return false;
		for (T expected : expectedValues) {
			if (actual.equals(expected)) {
				return true;
			}
		}
		return false;
	}
	
	public List<String> getMessagesErrors(Set<? extends ConstraintViolation<?>> violations) {
	    return violations.stream()
	        .map(violation -> messageSource.getMessage(
	            violation.getMessage().replaceAll("[\\{\\}]", ""), 
	            null, 
	            LocaleContextHolder.getLocale()))
	        .collect(Collectors.toList());
	}
}
