package com.br.soundwave.api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.soundwave.Core.Repository.ClientRepository;
import com.br.soundwave.Core.Services.ClientService;
import com.br.soundwave.Core.Services.ConfirmationTokenService;
import com.br.soundwave.Core.Model.ClientModel;

@RestController
@RequestMapping("/client")
public class ClientController {
	
	private ClientModel clientEntity;
	
	@Autowired
	private ConfirmationTokenService confirmationTokenService;
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private ClientRepository clientRepository;
	
	@GetMapping("/listar-todos")
	public List<ClientModel> listarTodos(){
		return clientRepository.findAll();
	}
	
	@GetMapping("/buscar-por/{id}")
	public Optional<ClientModel> findClientById(@PathVariable long id) {
		return clientRepository.findById(id);
	}
	
	@PostMapping("/criar-user")
	public void createClient(@RequestBody ClientModel client) {
		
		clientService.saveClient(client);
	}
	
	@PostMapping("/trocar-senha/{id}")
	public void changeClientPassword(@PathVariable Long id, @RequestBody String newPassword, String oldPassword) {
		clientService.changePassword(id, oldPassword, newPassword);
	}
	
}
