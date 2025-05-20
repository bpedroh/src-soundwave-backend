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
import com.br.soundwave.api.ModelDto.TokenModelDTO;

import jakarta.transaction.Transactional;


@Component
public class TokenService {
	
	@Autowired
	private ClientRepository clientRepository;
	
	public int generateEmailToken() {
		Random random = new Random();
		 int value = random.nextInt(99999, 999999);
		 return value;
	}
	
	@Transactional
	public boolean  validateEmailToken(int tokenDTO, Long id) {
		
		ClientModel client = clientRepository.findById(id).orElseThrow(() -> new GenericExcpetion("Não foi possivel identificar o cliente"));
		if(client != null) {
			if(client.isEmailVerified() == false || client.getTokenEmail() != 0) {
				if(client.getTokenEmail() == tokenDTO) {
					client.setEmailVerified(true);
					client.setTokenEmail(0);
					clientRepository.save(client);
					return true;
				}else {
					throw new GenericExcpetion("Código incorreto");
				}
			}
		}
		return false;
	}
	
	public String generateSessionToken() {
		UUID token = UUID.randomUUID();
		return token.toString();
	}
	
	
	
	public boolean validateSession(SessionManagerModel session) {
		
	
		
		return false;
	}
	
	
}
