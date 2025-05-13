package com.br.soundwave.Core.Services;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.br.soundwave.Core.Exceptions.GenericExcpetion;
import com.br.soundwave.Core.Model.ClientModel;
import com.br.soundwave.Core.Repository.ClientRepository;
import com.br.soundwave.Core.Services.SendEmailService.Mensagem;

import jakarta.transaction.Transactional;

@Component
public class ClientService {
	
	@Autowired
	private ClientRepository clientRepository;
	
	@Autowired
	private SendEmailService emailService;
	
	@Autowired
	private ConfirmationTokenService confirmationTokenService;
	
	
	@Transactional
	public boolean saveClient(ClientModel client) {
		
		if(verfifyEmailDisponibility(client.getEmail())) {
			
			client.setTokenEmail(confirmationTokenService.generateEmailToken());
			
			if(clientRepository.save(client) != null) {
				return true;
			}
			
		}
		return false;
	}
	
	public boolean sendConfirmEmail(ClientModel client) {
		try {
			var mensagem = Mensagem.builder()
					.assunto("Confirmar Conta")
					.destinatario(client.getEmail())
					.var(Map.of("client", client))
					.corpo("confirm-email.html").build();
			
					if(emailService.enviar(mensagem)) {
						return true;
					}
		}catch (Exception e) {
			throw new GenericExcpetion("Falha para popular dados do email" + e);
		}
		return false;
	}
	
	@Transactional
	public void changePassword(Long id, String oldPassword, String newPassword) {
		Optional<ClientModel> client = clientRepository.findById(id);
		
		if (client != null) {
			if(client.get().getClientPassword() == oldPassword) {
				client.get().setClientPassword(newPassword);
			}
		}
	}
	
	public Boolean verfifyEmailDisponibility(String email) {
		if(clientRepository.findByEmail(email) == null ) {
			return true;
		}
		return false;
	}
	
	public void sendChangePasswordEmail(String email) {
		
		ClientModel client = clientRepository.findByEmail(email);
		
		if(client != null) {
			var mensagem = Mensagem.builder()
					.assunto("Mudança de senha")
					.destinatario(client.getEmail())
					.var(Map.of("client", client))
					.corpo("forgot-password.html").build();

			emailService.enviar(mensagem);
		}
		
	}
	
}
