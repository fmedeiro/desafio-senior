package com.desafiosenior.api_hotel.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.desafiosenior.api_hotel.exception.InvalidRequestException;
import com.desafiosenior.api_hotel.exception.ResourceNotFoundException;
import com.desafiosenior.api_hotel.model.User;
import com.desafiosenior.api_hotel.model.UserDto;
import com.desafiosenior.api_hotel.service.UserService;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@DeleteMapping("/{userId}")
	public ResponseEntity<Object> delete(@PathVariable UUID userId) {		
		var objectResponse = userService.delete(userId);
		
		if (objectResponse.isEmpty()) {
            throw new ResourceNotFoundException("User não encontrado para o ID: " + userId);
        }
					
		return objectResponse.get();
	}
	
	@DeleteMapping()
	public ResponseEntity<Object> deleteAll() {		
		userService.deleteAll();
					
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	@GetMapping("/{userId}") 
	public ResponseEntity<Object> findOneUser(@PathVariable UUID userId) {
		var userDb = userService.findById(userId);
		
		if (userDb.isEmpty()) {
            throw new ResourceNotFoundException("User não encontrado para o ID: " + userId);
        }
		
		return ResponseEntity.status(HttpStatus.OK).body(userDb.get()); 
	} 
	
	@GetMapping()
	public ResponseEntity<List<User>> listAll() {		
		return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
	}
	
    @PostMapping()
    public ResponseEntity<Object> save(@RequestBody @Valid UserDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(userDto));
    }
    
    @PutMapping("/{userId}")
    public ResponseEntity<Void> update(@PathVariable UUID userId, @RequestBody @Valid UserDto userDto) {
        try {
            var userDb = userService.update(userId, userDto);
            
            if (userDb.isEmpty()) {
                throw new ResourceNotFoundException("User não encontrado para o ID: " + userId);
            }
            
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (InvalidRequestException ex) {
            // Excecao sera capturada e tratada pelo ControllerAdvice
            throw ex;
        }
    }
}
