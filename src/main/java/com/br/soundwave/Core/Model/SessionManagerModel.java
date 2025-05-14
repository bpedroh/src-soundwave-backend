package com.br.soundwave.Core.Model;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "gereciador_sessao")
public class SessionManagerModel {
	
	@Id
	private UUID sessionId = UUID.randomUUID();
	
	@OneToOne
	@JoinColumn(nullable = false, name = "client_id", referencedColumnName = "id")
	private ClientModel client;
	
	private Instant expirationTime = Instant.now().plus(Duration.ofHours(1));
	
	private boolean isValid = false;
		
	
}