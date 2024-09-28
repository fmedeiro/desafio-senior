package com.desafiosenior.api_hotel.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.desafiosenior.api_hotel.model.Booking;
import com.desafiosenior.api_hotel.model.User;
import com.desafiosenior.api_hotel.model.UserDto;
import com.desafiosenior.api_hotel.model.UserFinderStandardParamsDto;
import com.desafiosenior.api_hotel.repository.UserRepository;
import com.desafiosenior.api_hotel.util.AttributeChecker;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private AttributeChecker attributeChecker;

    @InjectMocks
    private UserService userService;

    private Booking booking;
    private User user;
    private UserDto userDto;
    private UUID userId;
    
    private UserFinderStandardParamsDto userHostedDtoWithOnlyDocument;
    private UserFinderStandardParamsDto userUnhostedDtoWithOnlyDocument;
    
    private Boolean isTheDateToCheckIsBeforeToday(LocalDateTime dateToVerify) {
    	return dateToVerify.isBefore(LocalDateTime.now());
    }
    
    private void setGuest(boolean hosted) {
    	if (hosted)
    		setHostedGuest();
    	else
    		setUnhostedGuest();
    }
    
    private void setHostedGuest() {
        // Cria um objeto Booking com um check-in anterior a data atual e check-out nulo, indicando que o hospede ainda esta no hotel
        booking = new Booking();
        booking.setDateCheckin(LocalDateTime.now().minusDays(1));
        booking.setDateCheckout(null);
        booking.setStatus("C");

        user.setBookings(List.of(booking));
    }
    
    private void setUnhostedGuest() {
    	// Cria um objeto Booking com um check-in e check-out anteriores a data atual, indicando que o hospede NAO esta mais no hotel
        booking = new Booking();
        booking.setDateCheckin(LocalDateTime.now().minusDays(3));
        booking.setDateCheckout(LocalDateTime.now().minusDays(1));

        user.setBookings(List.of(booking));
    }
    
    private void setUser() {
    	userId = UUID.randomUUID();
        user = new User();
        user.setUserId(userId);
        user.setDocument("12345678901234");
        user.setEmail("user@example.com");
        user.setLogin("userlogin");
        user.setName("User Name");
        user.setPassword("password123");
        user.setPhone("999999999");
        user.setPhoneDdd("11");
        user.setPhoneDdi("55");
        user.setRole("G");
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        setUser();
        userDto = new UserDto(
            null, user.getDocument(),
            user.getEmail(),
            user.getLogin(),
            user.getName(),
            user.getPassword(),
            user.getPhone(),
            user.getPhoneDdd(),
            user.getPhoneDdi(),
            user.getRole()
        );
    }

    @Test
    @DisplayName("Testa a exclusao de um usuario existente, certificando-se de que o userRepository.delete() seja chamado.")
    void testDelete_existingUser_shouldReturnHttpStatusNoContent() {
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));

        Optional<ResponseEntity<Object>> response = userService.delete(userId);

        assertTrue(response.isPresent());
        assertEquals(HttpStatus.NO_CONTENT, response.get().getStatusCode());
        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("Testa o cenario aonde o usuario nao existe e certifica-se de que nenhum usuario seja excluido.")
    void testDelete_nonExistingUser() {
        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        Optional<ResponseEntity<Object>> response = userService.delete(userId);

        assertFalse(response.isPresent());
        verify(userRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Testa se todos os convidados sao excluidos corretamente.")
    void testDeleteAll() {
        userService.deleteAll();
        verify(userRepository).deleteAll();
    }

    @Test
    @DisplayName("Testa se todos os convidados sao retornados corretamente.")
    void testFindAll() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> users = userService.findAll();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(user, users.get(0));
    }

    @Test
    @DisplayName("Testa a recuperacao de um usuario existente pelo seu user_id.")
    void testFindById_existingUser() {
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findById(userId);

        assertTrue(foundUser.isPresent());
        assertEquals(user, foundUser.get());
    }

    @Test
    @DisplayName("Verifica o comportamento ao tentar recuperar um usuario que nao existe.")
    void testFindById_nonExistingUser() {
        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.findById(userId);

        assertFalse(foundUser.isPresent());
    }
    
    @Test
	@DisplayName("Testa a recuperacao de um hospede existente pelo seu nome.")
    void testFindGuestByNameAndRole_UserExists() {
		User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setName("John Doe");
        user.setRole("G");
        
        when(userRepository.findByNameIgnoreCaseAndRoleIgnoringSpaces(anyString(), anyString()))
            .thenReturn(List.of(Optional.of(user)));

        List<Optional<User>> foundUser = userRepository.findByNameIgnoreCaseAndRoleIgnoringSpaces("John Doe", "G");

        assertTrue(foundUser.get(0).isPresent());
        assertEquals(Optional.of(user), foundUser.get(0));
    }

    @Test
	@DisplayName("Verifica o comportamento ao tentar recuperar um hospede nao hospedado consultando por busca por nome.")
    void testFindGuestByNameAndRole_UserDoesNotExist() {
        when(userRepository.findByNameIgnoreCaseAndRoleIgnoringSpaces(anyString(), anyString()))
            .thenReturn(List.of(Optional.empty()));

        List<Optional<User>> foundUser = userRepository.findByNameIgnoreCaseAndRoleIgnoringSpaces("Jane Doe", "G");

        assertTrue(foundUser.get(0).isEmpty());
    }

    @Test
	@DisplayName("Testa a recuperacao de um hospede existente pelo seu telefone.")
    void testFindGuestByPhoneDdiAndPhoneDddAndPhoneAndRole_UserExists() {
		User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setRole("G");
        user.setPhoneDdi("55");
        user.setPhoneDdd("11");
        user.setPhone("999999999");
		
        when(userRepository.findByPhoneDdiAndPhoneDddAndPhoneAndRole(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(List.of(Optional.of(user)));

        List<Optional<User>> foundUser = userRepository.findByPhoneDdiAndPhoneDddAndPhoneAndRole("55", "11", "999999999", "G");

        assertTrue(foundUser.get(0).isPresent());
        assertEquals(Optional.of(user), foundUser.get(0));
    }

    @Test
	@DisplayName("Testa a recuperacao de um hospede nao hospedado, pelo seu telefone.")
    void testFindGuestByPhoneDdiAndPhoneDddAndPhoneAndRole_UserDoesNotExist() {
        when(userRepository.findByPhoneDdiAndPhoneDddAndPhoneAndRole(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(List.of(Optional.empty()));

        List<Optional<User>> foundUser = userRepository.findByPhoneDdiAndPhoneDddAndPhoneAndRole("55", "11", "888888888", "G");

        assertTrue(foundUser.get(0).isEmpty());
    }

    @Test
	@DisplayName("Testa a recuperacao de um hospede existente pelo seu documento.")
    void testFindGuestByDocumentAndRole_UserExists() {
		User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setRole("G");
        user.setDocument("12345678901");
		
        when(userRepository.findByDocumentAndRole(anyString(), anyString()))
            .thenReturn(List.of(Optional.of(user)));

        List<Optional<User>> foundUser = userRepository.findByDocumentAndRole("12345678901", "G");

        assertTrue(foundUser.get(0).isPresent());
        assertEquals(Optional.of(user), foundUser.get(0));
    }

    @Test
	@DisplayName("Verifica o comportamento ao tentar recuperar um hospede nao hospedado consultando por busca por documento.")
    void testFindGuestByDocumentAndRole_UserDoesNotExist() {
        when(userRepository.findByDocumentAndRole(anyString(), anyString()))
            .thenReturn(List.of(Optional.empty()));

        List<Optional<User>> foundUser = userRepository.findByDocumentAndRole("98765432100", "G");

        assertTrue(foundUser.get(0).isEmpty());
    }

    @Test
    @DisplayName("Deve retornar um hospede quando o documento estiver presente e o hospede estiver hospedado no hotel.")
    void testFindByGuestStayingAtHotel_Success() {
    	setGuest(true);
    	userHostedDtoWithOnlyDocument = new UserFinderStandardParamsDto("12345678901234", null, null, null, null);
    	when(userService.getUsersByAttributeChecker(userHostedDtoWithOnlyDocument, "G")).thenReturn(List.of(Optional.of(user)));

        List<Optional<User>> result = userService.findByGuestStayingAtHotel(userHostedDtoWithOnlyDocument, "G");

        assertTrue(result.get(0).isPresent());
        assertTrue(isTheDateToCheckIsBeforeToday(result.get(0).get().getBookings().get(0).getDateCheckin()));
        assertNull(result.get(0).get().getBookings().get(0).getDateCheckout(), "O atributo dateCheckout deve ser NULL pois o hospede ainda esta no hotel.");
        assertEquals("G", result.get(0).get().getRole());
    }
    
    @Test
    @DisplayName("Deve procurar por um hospede por documento e o nao encontra hospede pois ele nao esta mais hospedado no hotel.")
    void testFindByGuestStayingAtHotel_NotFoundout() {
    	setGuest(false);
    	userUnhostedDtoWithOnlyDocument = new UserFinderStandardParamsDto("12345678901234", null, null, null, null);
        when(userService.getUsersByAttributeChecker(userUnhostedDtoWithOnlyDocument, "G")).thenReturn(List.of(Optional.of(user)));

        List<Optional<User>> result = userService.findByGuestStayingAtHotel(userUnhostedDtoWithOnlyDocument, "G");

        assertFalse(result.get(0).isPresent());
        assertTrue(isTheDateToCheckIsBeforeToday(user.getBookings().get(0).getDateCheckin()));
        assertTrue(isTheDateToCheckIsBeforeToday(user.getBookings().get(0).getDateCheckout()));
    }

    @Test
    @DisplayName("Testa o salvamento de um novo usuario e verifica se os valores foram corretamente copiados.")
    void testSave() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.save(userDto);

        assertNotNull(savedUser);
        assertEquals(user.getUserId(), savedUser.getUserId());
        assertEquals(user.getDocument(), savedUser.getDocument());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Testa a atualizacao de um usuario existente.")
    void testUpdate_existingUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        Optional<User> updatedUser = userService.update(userId, userDto);

        assertTrue(updatedUser.isPresent());
        assertEquals(user.getUserId(), updatedUser.get().getUserId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName(" Testa o comportamento ao tentar atualizar um usuario que nao existe.")
    void testUpdate_nonExistingUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<User> updatedUser = userService.update(userId, userDto);

        assertFalse(updatedUser.isPresent());
        verify(userRepository, never()).save(any(User.class));
    }
}

