package com.br.soundwave.Core.Services;


import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.br.soundwave.Core.Exceptions.GenericExcpetion;
import com.br.soundwave.Core.Model.ClientModel;
import com.br.soundwave.Core.Model.SessionManagerModel;
import com.br.soundwave.Core.Repository.ClientRepository;


@Component
public class TokenService {
	
	@Autowired
	private ClientRepository clientRepository;
	
	public int generateEmailToken() {
		Random random = new Random();
		 int value = random.nextInt(99999, 999999);
		 return value;
	}
	
	
	public boolean  validateEmailToken(int token, Long id) {
		Optional<ClientModel> client = clientRepository.findById(id);
		if(client != null) {
			if(client.get().getTokenEmail() == token) {
				client.get().setEmailVerified(true);
				
				return true;
			}else {
				throw new GenericExcpetion("Código incorreto");
			}
		}else { throw new GenericExcpetion("Id não encontrado");}
		
	}
	
	public String generateSessionToken() {
		UUID token = UUID.randomUUID();
		return token.toString();
	}
	
	public boolean validateSession(SessionManagerModel session) {
		
	
		
		return false;
	}
	
	
}
