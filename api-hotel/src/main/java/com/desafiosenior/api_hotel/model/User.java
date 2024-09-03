package com.desafiosenior.api_hotel.model;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "userId")
public class User implements UserDetails {
	private static final long serialVersionUID = 1L;

	@Id 
	@Column(updatable = false, nullable = false)
	@Getter
	@Setter
	private UUID userId;
	
	@Getter
	@Setter
	@NonNull
	@Column(unique=true, length=14)
	private String document;
	
	@Getter
	@Setter
	@Column(length=50)
	private String email;

	@Getter
	@Setter
	@Column(unique=true, length=12)
	private String login;
	
	@Getter
	@Setter
	@NonNull
	@Column(length=60)
	private String name;
	
	@Getter
	@Setter
	@Column(length=8)
	private String password;
	
	@Getter
	@Setter
	@NonNull
	@Column(length=9)
	private String phone;

	@Getter
	@Setter
	@NonNull
	@Column(length=2)
	private String phoneDdd;
	
	@Getter
	@Setter
	@NonNull
	@Column(length=2)
	private String phoneDdi;
	
	@Getter
	@Setter
	@Column(length=1)
	private String role;
	
	@PrePersist 
	public void generateUUID() {
		if (this.userId == null) {
			this.userId = UUID.randomUUID(); 
		}
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (this.role.equals(UserRole.ADMIN.getRole())) {
			return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER_ATTENDANT"), new SimpleGrantedAuthority("ROLE_GUEST"));
		} else if (this.role.equals(UserRole.USER_ATTENDANT.getRole())) {
			return List.of(new SimpleGrantedAuthority("ROLE_USER_ATTENDANT"));
		} else {
			return List.of(new SimpleGrantedAuthority("ROLE_GUEST"));
		}
	}

	@Override
	public String getUsername() {
		return login;
	}
}
