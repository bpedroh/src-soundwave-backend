package com.br.soundwave.Core.Services;

import java.util.HashMap;
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
import com.br.soundwave.api.ModelDto.ChangePasswordDTO;
import com.br.soundwave.api.ModelDto.LoginModelDTO;
import com.br.soundwave.api.ModelDto.RegisterModelDTO;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;

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
	
	
	public ClientModel findClientById(Long id) {
		return clientRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Cliente não encontrado"));
	}
	
	@Transactional
	public ClientModel saveClient(RegisterModelDTO clientDTO) {
		if(verfifyEmailDisponibility(clientDTO.getUsername()))  {
			if(!clientDTO.getUsername().isEmpty()) {
				ClientModel client = new ClientModel();
				client.setEmail(clientDTO.getUsername());
				client.setClientName(clientDTO.getName());
				client.setClientPassword(hashPassword(clientDTO.getPassword()));
				client.setTokenEmail(confirmationTokenService.generateEmailToken());
				
				return clientRepository.save(client);
			}
		
	  }
		  
		return null;
	}
	
	public boolean sendConfirmEmail(ClientModel client) {
		String confirmationLink = "http://localhost:5173/confirmar-email/"+ client.getId() + "/" + client.getTokenEmail();
		try {
			 Map<String, Object> variaveis = new HashMap<>();
		        variaveis.put("confirmationLink", confirmationLink);
		        variaveis.put("client", client);
		        
			var mensagem = Mensagem.builder()
					.assunto("Confirmar Conta")
					.destinatario(client.getEmail())
					.var(variaveis)
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
	public void changePassword(Long id, ChangePasswordDTO newPassword) {
		ClientModel client = clientRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Usuario não encontrado"));
		
		String newPasswordHash = hashPassword(newPassword.getNewPassword());
		
		
		if (client != null) {
			client.setClientPassword(newPasswordHash);
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
		
		String resetUrl = "http://localhost:5173/mudar-senha/" + client.getId();
		
		Map<String, Object> variaveis = new HashMap<>();
        variaveis.put("resetUrl", resetUrl);
        variaveis.put("client", client);
		
		if(client != null) {
			var mensagem = Mensagem.builder()
					.assunto("Recuperação de senha")
					.destinatario(client.getEmail())
					.var(variaveis)
					.corpo("forgot-password.html").build();

			emailService.enviar(mensagem);
		}
		
	}
	
	public boolean requestLogin(LoginModelDTO login, HttpServletResponse response) {
		ClientModel client = clientRepository.findByEmail(login.getUsername());
		
		
		if (client.getEmail().equals(login.getUsername()) && checkPassword(login.getPassword(), client.getClientPassword()) && client.isEmailVerified() ) {

			return true;
			
		}if(client.getEmail() == null) {
			throw new UserNotFoundException("Login ou Senha inválidas");
		}else {
			throw new UserNotFoundException("Login ou Senha inválidas");
		}
		
	}
	
	public void logout(String sessionId, HttpServletResponse response) {
		managerService.removeSession(sessionId);
		Cookie cookie = new Cookie("SESSION_ID", null);
	    cookie.setPath("/");
	    cookie.setDomain("localhost");
	    cookie.setHttpOnly(true);
	    cookie.setSecure(false);
	    cookie.setMaxAge(0); 
	    response.addCookie(cookie);
	}

	public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(10));
    }

    public static boolean checkPassword(String candidate, String hashed) {
        return BCrypt.checkpw(candidate, hashed);
    }
	
	
	
	
}
