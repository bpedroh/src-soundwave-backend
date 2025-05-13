package com.br.soundwave.Core.Services;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.br.soundwave.Core.Exceptions.GenericExcpetion;
import com.br.soundwave.Core.Model.ClientModel;
import com.br.soundwave.Core.Model.ConfirmationTokenModel;
import com.br.soundwave.Core.Repository.ClientRepository;
import com.br.soundwave.Core.Repository.ConfirmationTokenRepository;

@Component
public class ConfirmationTokenService {

	
	private ConfirmationTokenModel confirmationTokenModel;
	
	@Autowired
	private ConfirmationTokenRepository confirmationTokenRepository;
	
	@Autowired
	private ClientRepository clientRepository;
	
	public int generateEmailToken() {
		Random random = new Random();
		 int value = random.nextInt(99999, 999999);
		 return value;
	}
	
	
	public void validateEmailToken(int token, Long id) {
		Optional<ClientModel> client = clientRepository.findById(id);
		if(client != null) {
			if(client.get().getTokenEmail() == token) {
				client.get().setEmailverified(true);
			}else {
				throw new GenericExcpetion("Código incorreto");
			}
		}else { throw new GenericExcpetion("Id não encontrado");}
		
	}
	
	public void generateToken(ClientModel client) {
		confirmationTokenModel.setClient(client);
		confirmationTokenModel.setToken(UUID.randomUUID().toString());
		confirmationTokenModel.setExpireIn(LocalDateTime.now().plusHours(1));
		confirmationTokenRepository.save(confirmationTokenModel);
	}
	
}
