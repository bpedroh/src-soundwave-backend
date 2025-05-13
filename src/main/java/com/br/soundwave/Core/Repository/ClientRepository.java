package com.br.soundwave.Core.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.soundwave.Core.Model.ClientModel;

public interface ClientRepository extends JpaRepository<ClientModel, Long>{
	
	ClientModel findByEmail(String email);
}
