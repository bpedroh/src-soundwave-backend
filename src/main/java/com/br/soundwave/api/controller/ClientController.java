package com.br.soundwave.api.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.soundwave.Core.Repository.ClientRepository;
import com.br.soundwave.Core.Services.ClientService;
import com.br.soundwave.Core.Services.TokenService;
import com.br.soundwave.api.ModelDto.EmailModelDTO;
import com.br.soundwave.api.ModelDto.LoginModelDTO;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.br.soundwave.Core.Model.ClientModel;

@RestController
@RequestMapping("/client")
public class ClientController {
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private ClientRepository clientRepository;
	
	@GetMapping("/find-all")
	public List<ClientModel> listarTodos(){
		return clientRepository.findAll();
	}
	
	@GetMapping("/find-by/{id}")
	public Optional<ClientModel> findClientById(@PathVariable long id) {
		return clientRepository.findById(id);
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> createClient(@RequestBody ClientModel client) {
		if(clientService.saveClient(client)) {
			if(!clientService.sendConfirmEmail(client)) {
				client.setEmailVerified(false);
				return ResponseEntity.status(401).body("Erro para confirmar o email");
			}
		}else {
			return ResponseEntity.status(401).body("Erro ao realizar o cadastro, verifique as informações passadas.");
		}
		return ResponseEntity.status(200).body("Cadastro realizado com sucesso.");
	}
	
	@PostMapping("/change-password/{id}")
	public void changeClientPassword(@PathVariable Long id, @RequestBody String newPassword, String oldPassword) {
		clientService.changePassword(id, oldPassword, newPassword);
	}
	
	@PostMapping("/change-password-email")
	public void sendEmailToChangePassword(@RequestBody EmailModelDTO email) {
		clientService.sendChangePasswordEmail(email.getEmail());
	}
	
	@PostMapping("/login")
	public void login(@RequestBody LoginModelDTO loginRequest, HttpServletResponse session) {
		clientService.requestLogin(loginRequest, session);
	}
	
}
