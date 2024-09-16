package com.desafiosenior.api_hotel.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "rooms")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "roomId")
public class Room {
	@Id
	@Column(updatable = false, nullable = false)
	@Getter
	@Setter
	private UUID roomId;
	
	@JsonIgnoreProperties({"room"})
	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
	@Getter
	@Setter
	private List<Booking> bookings;

	@Getter
	@Setter
	@NonNull
	@Column(length = 29)
	private LocalDateTime dateLastChange;

	@Getter
	@NonNull
	@Column(length = 29)
	private LocalDateTime dateRegister;

	@Getter
	@Setter
	@NonNull
	@Column(unique = true)
	private Integer number;

	@PrePersist
	public void generateUUID() {
		if (this.roomId == null) {
			this.roomId = UUID.randomUUID();
		}
	}

	public Room(LocalDateTime dateRegister) {
		this.dateRegister = dateRegister;
	}
}
