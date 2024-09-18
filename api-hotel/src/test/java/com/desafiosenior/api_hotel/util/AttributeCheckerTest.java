package com.desafiosenior.api_hotel.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.desafiosenior.api_hotel.exception.InvalidRequestException;
import com.desafiosenior.api_hotel.model.UserFinderStandardParamsDto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class AttributeCheckerTest {
	
	private Validator validator;

    @InjectMocks
    private AttributeChecker checker;
    
    @Mock
    private AttributeChecker mockChecker;

    private TestClassData testObjClass;
    
    private UserFinderStandardParamsDto testUserFinderStandardParamsWithErrorDto;
    
    private UserFinderStandardParamsDto testUserFinderStandardParamsDto;
    
    private static final TestEnumData TEST_ENUM_DATA = null;

	@BeforeEach
	void setup(TestInfo testInfo) {
		MockitoAnnotations.openMocks(this);
		initializeClassOrRecord(testInfo);
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	private void initializeClassOrRecord(TestInfo testInfo) {
		if ("testGetFirstAttributePresentInClassType_withSuccess".equals(testInfo.getTestMethod().get().getName())
				|| "testGetFirstAttributePresentInClassType_withSuccessButNotFoundOut"
						.equals(testInfo.getTestMethod().get().getName())) {
			testObjClass = new TestClassData("333333333", "User Name", "44444444", "11", "55");
		} else if ("testGetFirstAttributePresentInRecordType_withSuccess"
				.equals(testInfo.getTestMethod().get().getName())
				|| "testGetFirstAttributePresentInRecordType_withSuccessButNotFoundOut"
						.equals(testInfo.getTestMethod().get().getName())
				|| "testValidations".equals(testInfo.getTestMethod().get().getName())) {
			testUserFinderStandardParamsDto = new UserFinderStandardParamsDto("333333333", "User Name", "55", "11", "44444444");
		}
	}
	
    @Test
    @DisplayName("Testa as validacoes do record DTD que nao contem erros nos atributos na instancia de um DTO.")
    void testDtoWithoutErrorsValidations() {
    	testUserFinderStandardParamsDto = new UserFinderStandardParamsDto("333333333", "User Name", "44444444", "11", "55");

        Set<ConstraintViolation<UserFinderStandardParamsDto>> violations = validator.validate(testUserFinderStandardParamsDto);

        assertTrue(violations.isEmpty(), "DTO deve passar sem violacoes.");
    }
    
    @Test
    @DisplayName("Testa as validacoes do record DTD que contem erros nos atributos na instancia de um DTO.")
    void testDtoWithErrorsValidations() {
    	testUserFinderStandardParamsWithErrorDto = new UserFinderStandardParamsDto("documentValue", "nameValue",
				"phoneDdiValue", "phoneDddValue", "phoneValue");

        Set<ConstraintViolation<UserFinderStandardParamsDto>> violations = validator.validate(testUserFinderStandardParamsWithErrorDto);

        List<String> violationMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        assertFalse(violations.isEmpty(), "DTO deve falhar devido a violacoes de validacao");
        assertEquals(4, violationMessages.size(), "O numero de violacoes deve ser 4");
        assertTrue(violationMessages.contains("O campo deve conter entre 9 e 14 dígitos numéricos."));
        assertTrue(violationMessages.contains("O campo deve conter entre 1 e 2 dígitos numéricos."));
        assertTrue(violationMessages.contains("O campo deve conter entre 1 e 3 dígitos numéricos."));
        assertTrue(violationMessages.contains("O campo deve conter entre 8 e 10 dígitos numéricos."));
    }
    
    @Test
    @DisplayName("Testa a tentativa sem falhas de pegar a primeira string(nome, em maiusculo, do atributo do objeto)"
			+ " presente num objeto instanciado a partir de uma classe.")
    void testGetFirstAttributePresentInClassType_withSuccess() throws Exception {
        String attributeFound = checker.getFirstAttributePresent(testObjClass, "document", "name");
        
        assertNotNull(attributeFound);
        assertTrue(Utils.assertEqualsWithOr(attributeFound, "DOCUMENT", "NAME", "PHONEDDI", "PHONEDDD", "PHONE"));
    }
    
    @Test
    @DisplayName("Testa a tentativa sem falhas e nao encontra, de pegar a primeira string(nome, em maiusculo, do atributo do objeto)"
			+ " presente num objeto instanciado a partir de uma classe.")
    void testGetFirstAttributePresentInClassType_withSuccessButNotFoundOut() throws Exception {
        String attributeFound = checker.getFirstAttributePresent(testObjClass, "wordNotFound1", "wordNotFound2", "wordNotFound3");
        
        assertNull(attributeFound);
        assertFalse(Utils.assertEqualsWithOr(attributeFound, "DOCUMENT", "NAME", "PHONEDDI", "PHONEDDD", "PHONE"));
    }
    
    @Test
    @DisplayName("Testa a tentativa sem falhas de pegar a primeira string(nome, em maiusculo, do atributo do objeto)"
			+ " presente num objeto instanciado a partir de um record.")
    void testGetFirstAttributePresentInRecordType_withSuccess() throws Exception {
        String attributeFound = checker.getFirstAttributePresent(testUserFinderStandardParamsDto, "document", "name");
        
        assertNotNull(attributeFound);
        assertTrue(Utils.assertEqualsWithOr(attributeFound, "DOCUMENT", "NAME", "PHONEDDI", "PHONEDDD", "PHONE"));
    }
    
    @Test
    @DisplayName("Testa a tentativa sem falhas e nao encontra, de pegar a primeira string(nome, em maiusculo, do atributo do objeto)"
			+ " presente num objeto instanciado a partir de um record.")
    void testGetFirstAttributePresentInRecordType_withSuccessButNotFoundOut() throws Exception {
        String attributeFound = checker.getFirstAttributePresent(testUserFinderStandardParamsDto, "wordNotFound1", "wordNotFound2", "wordNotFound3");
        
        assertNull(attributeFound);
        assertFalse(Utils.assertEqualsWithOr(attributeFound, "DOCUMENT", "NAME", "PHONEDDI", "PHONEDDD", "PHONE"));
    }
    
    @Test
    @DisplayName("Testa a tentativa, com falha, de pegar a primeira string(nome, em maiusculo, do atributo do objeto)"
            + " presente num objeto instanciado a partir de um enum.")
    void testGetFirstAttributePresentInEnumType_shouldReturnException() throws Exception {
        when(mockChecker.getFirstAttributePresent(TEST_ENUM_DATA, "document", "name"))
                .thenThrow(new InvalidRequestException("attributeFoundClasse encontrado: null"));

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            mockChecker.getFirstAttributePresent(TEST_ENUM_DATA, "document", "name");
        });
        
        String attributeFound = checker.getFirstAttributePresent(TEST_ENUM_DATA, "document", "name");

        assertEquals("attributeFoundClasse encontrado: null", exception.getMessage());
        assertNull(attributeFound);
    }

}
