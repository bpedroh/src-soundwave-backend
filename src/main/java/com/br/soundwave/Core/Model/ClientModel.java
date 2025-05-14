package com.br.soundwave.Core.Model;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "clientes")
public class ClientModel {
	
	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	 	
	 	@Column(name = "nome")
	    private String clientName;

	    @Column(name = "email",unique = true)
	    private String email;

	    @Column(name = "senha")
	    private String clientPassword;

	    @Column(name = "data_cadastro")
	    private LocalDateTime clientCreatedAt = LocalDateTime.now();
	    
	    @Column(name = "email_verified")
	    private boolean emailVerified = false;
	    
	    private int tokenEmail;
	    
	    private String totpSecret;
	    
	    private boolean mfaEnabled = false;
}

