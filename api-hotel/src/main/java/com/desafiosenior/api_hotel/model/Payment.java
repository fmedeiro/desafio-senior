package com.desafiosenior.api_hotel.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "paymentId")
public class Payment {

	@Id
	@Column(updatable = false, nullable = false)
	@Getter
	@Setter
	private UUID paymentId;
	
	@OneToOne(mappedBy = "payment")
	@Setter
	private Booking booking;
	
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
	private BigDecimal total;

	public Payment(LocalDateTime dateRegister) {
		this.dateRegister = dateRegister;
	}

	@PrePersist
	public void generateUUID() {
		if (this.paymentId == null) {
			this.paymentId = UUID.randomUUID();
		}
	}

}
