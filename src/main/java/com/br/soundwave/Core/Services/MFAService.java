package com.br.soundwave.Core.Services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.br.soundwave.Core.Model.ClientModel;
import com.br.soundwave.Core.Repository.ClientRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

@Component
public class MFAService {

	@Autowired
	private ClientRepository clientRepository;
	
	public void generateMFAToken(Long id) {
		Optional<ClientModel> client = clientRepository.findById(id);
		
		if(client != null) {
			GoogleAuthenticator gAuth = new GoogleAuthenticator();
			GoogleAuthenticatorKey key = gAuth.createCredentials();	
			client.get().setTotpSecret(key.getKey());
			
			
		}
		
	
	}
	
}
