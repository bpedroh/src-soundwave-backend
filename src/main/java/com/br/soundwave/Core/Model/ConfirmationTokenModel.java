package com.br.soundwave.Core.Model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tokens_confirmacao")
public class ConfirmationTokenModel {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(unique = true)
	private String token;
	
	@OneToOne
	@JoinColumn(nullable = false, name = "client_id")
	private ClientModel client;
	
	private LocalDateTime expireIn;
	
	private boolean used = false;
}
