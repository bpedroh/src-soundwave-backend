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
	
	
	@Transactional
	public ClientModel saveClient(RegisterModelDTO clientDTO) {
		
		
		if(verfifyEmailDisponibility(clientDTO.getUsername())) {
				ClientModel client = new ClientModel();
				client.setEmail(clientDTO.getUsername());
				client.setClientName(clientDTO.getName());
				client.setClientPassword(hashPassword(clientDTO.getPassword()));
				client.setTokenEmail(confirmationTokenService.generateEmailToken());
				
				return clientRepository.save(client);
		
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
		
		if (client.getEmail().equals(login.getUsername()) && checkPassword(login.getPassword(), client.getClientPassword()) && client.isEmailVerified() ) {
		
			SessionManagerModel session = managerService.createSession(client);
			String token = session.getSessionId().toString();
			
			Cookie cookie = new Cookie("SESSION_ID", token);
		    cookie.setHttpOnly(true);
		    cookie.setSecure(false);
		    cookie.setPath("/");
		    cookie.setMaxAge(3600);
		    cookie.setDomain("localhost"); 
		    response.addCookie(cookie);
			return true;
			
		}if(client.getEmail() == null) {
			throw new UserNotFoundException("Login ou Senha inválidas");
		}else {
			throw new UserNotFoundException("Login ou Senha inválidas");
		}
		
	}
	

	public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(10));
    }

    public static boolean checkPassword(String candidate, String hashed) {
        return BCrypt.checkpw(candidate, hashed);
    }
	
	public void logout(Long id) {
		
	}
	
	
}
