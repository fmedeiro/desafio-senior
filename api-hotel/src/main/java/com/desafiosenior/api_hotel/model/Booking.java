package com.desafiosenior.api_hotel.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "bookings")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "bookingId")
public class Booking {

	@Id
	@Column(updatable = false, nullable = false)
	@Getter
	@Setter
	private UUID bookingId;

	@OneToOne
	@JoinColumn(name = "room_id", referencedColumnName = "roomId")
	@JsonIgnoreProperties({ "dateLastChange", "dateRegister", "number" })
	@Getter
	@Setter
	private Room room;

	@Getter
	@Setter
	private UUID userId;

	@Getter
	@Setter
	@NonNull
	@Column(length = 29)
	private LocalDateTime dateCheckin;

	@Getter
	@Setter
	@Column(length = 29)
	private LocalDateTime dateCheckout;

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
	@Column(length = 1)
	private String status;

	public Booking(LocalDateTime dateRegister) {
		this.dateRegister = dateRegister;
	}

	@PrePersist
	public void generateUUID() {
		if (this.bookingId == null) {
			this.bookingId = UUID.randomUUID();
		}
	}
}
