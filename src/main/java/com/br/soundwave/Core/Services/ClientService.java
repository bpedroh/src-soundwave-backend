package com.br.soundwave.Core.Services;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.br.soundwave.Core.Exceptions.GenericExcpetion;
import com.br.soundwave.Core.Exceptions.UserNotFoundException;
import com.br.soundwave.Core.Model.ClientModel;
import com.br.soundwave.Core.Model.SessionManagerModel;
import com.br.soundwave.Core.Repository.ClientRepository;
import com.br.soundwave.Core.Services.SendEmailService.Mensagem;
import com.br.soundwave.api.ModelDto.LoginModelDTO;
import com.br.soundwave.api.ModelDto.RegisterModelDTO;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Component
public class ClientService {
	
	@Autowired
	private ClientRepository clientRepository;
	
	@Autowired
	private SendEmailService emailService;
	
	@Autowired
	private TokenService confirmationTokenService;
	
	@Autowired
	private SessionManagerService managerService;
	
	
	@Transactional
	public ClientModel saveClient(RegisterModelDTO clientDTO) {
		
		
		if(verfifyEmailDisponibility(clientDTO.getUsername())) {
			if(clientDTO.getPassword().equals(clientDTO.getConfirmPassword())) {
				ClientModel client = new ClientModel();
				client.setEmail(clientDTO.getUsername());
				client.setClientName(clientDTO.getName());
				client.setClientPassword(clientDTO.getPassword());
				client.setTokenEmail(confirmationTokenService.generateEmailToken());
				
				return clientRepository.save(client);
				
			
			
		}
	  }
		  
		return null;
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
					.assunto("Recuperação de senha")
					.destinatario(client.getEmail())
					.var(Map.of("client", client))
					.corpo("forgot-password.html").build();

			emailService.enviar(mensagem);
		}
		
	}
	
	public boolean requestLogin(LoginModelDTO login, HttpServletResponse response) {
		ClientModel client = clientRepository.findByEmail(login.getUsername());
		
		if (client.getEmail().equals(login.getUsername()) && client.getClientPassword().equals(login.getPassword())) {
			
			SessionManagerModel session = managerService.createSession(client);
			String token = session.getSessionId().toString();
			
			Cookie cookie = new Cookie("SESSION_ID", token);
		    cookie.setHttpOnly(true);
		    cookie.setSecure(true);
		    cookie.setPath("/");
		    cookie.setMaxAge(3600);
		    response.addCookie(cookie);
			
			return true;
			
		}if(client.getEmail() == null) {
			throw new UserNotFoundException("Login ou Senha inválidas");
		}else {
			throw new UserNotFoundException("Login ou Senha inválidas");
		}
		
	}
	
	
}
