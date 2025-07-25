package com.br.soundwave.api.Controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.br.soundwave.Core.Repository.ClientRepository;
import com.br.soundwave.Core.Services.ClientService;
import com.br.soundwave.Core.Services.SessionManagerService;
import com.br.soundwave.Core.Services.TokenService;
import com.br.soundwave.Core.Utils.CipherUtils;
import com.br.soundwave.api.ModelDto.EmailModelDTO;
import com.br.soundwave.api.ModelDto.EncryptedPayloadDTO;
import com.br.soundwave.api.ModelDto.LoginModelDTO;
import com.br.soundwave.api.ModelDto.RegisterModelDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.br.soundwave.api.ModelDto.ChangePasswordDTO;
import com.br.soundwave.api.ModelDto.ClientDTO;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.br.soundwave.Core.Model.ClientModel;

@RestController
@RequestMapping("/client")
public class ClientController {
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private ClientRepository clientRepository;
	
	@Autowired
	private CipherUtils cipherUtils;
	
	@GetMapping("/find-all")
	public List<ClientModel> listarTodos(){
		return clientRepository.findAll();
	}
	
	@GetMapping("/find-by/{id}")
	public Optional<ClientModel> findClientById(@PathVariable long id) {
		return clientRepository.findById(id);
	}
	
	@GetMapping("/find-by-email")
	public ClientModel findClientByEmail(@RequestParam("email") String email) {
		return clientRepository.findByEmail(email);
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping("/register")
	public ResponseEntity<?> createClient(@RequestBody EncryptedPayloadDTO payload) {
		String jsonDecrypted = cipherUtils.decodeEncryptedPayload(payload);
		
		String nome = "";
	    String email = "";
	    String senhaHash = "";
		
		Map<String, String> dados;
		try {
			 dados = objectMapper.readValue(jsonDecrypted, Map.class);
			 nome = dados.get("name");
		     email = dados.get("username");
		     senhaHash = dados.get("password");
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

	    System.out.println("Nome: " + nome);
	    System.out.println("Email: " + email);
	    System.out.println("Senha hash: " + senhaHash);
	    
		RegisterModelDTO client = new RegisterModelDTO();
		
		client.setUsername(email);
		client.setName(nome);
		client.setPassword(senhaHash);
		
		ClientModel clientModel = clientService.saveClient(client);
		if(clientModel != null) {
			if(!clientService.sendConfirmEmail(clientModel)) {
				return ResponseEntity.status(401).body("Erro enviar o email.");
			}
		}else {
			return ResponseEntity.status(401).body("Erro ao realizar o cadastro, verifique as informações passadas.");
		}
		return ResponseEntity.status(200).body("Cadastro realizado com sucesso.");
	}
	
	public void validateEmail() {
		
	}
	
	@PostMapping("/change-password/{id}")
	public void changeClientPassword(@PathVariable Long id, @RequestBody ChangePasswordDTO newPassword) {
		clientService.changePassword(id, newPassword);
	}
	
	@PostMapping("/change-password-email")
	public void sendEmailToChangePassword(@RequestBody EmailModelDTO email) {
		clientService.sendChangePasswordEmail(email.getUsername());
	}
	
	@PostMapping("/login")
	public void login(@RequestBody LoginModelDTO loginRequest, HttpServletResponse session) {
		clientService.requestLogin(loginRequest, session);
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(@CookieValue(name = "SESSION_ID", required=false) String sessionId, HttpServletResponse session) {
		if(sessionId == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		clientService.logout(sessionId, session);
		return ResponseEntity.ok("Logout realizado");
	}
	
}
