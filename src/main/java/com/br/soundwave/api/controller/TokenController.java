package com.br.soundwave.api.Controller;

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

import com.br.soundwave.Core.Model.ClientModel;
import com.br.soundwave.Core.Repository.SessionManagerRepository;
import com.br.soundwave.Core.Services.ClientService;
import com.br.soundwave.Core.Services.MFAService;
import com.br.soundwave.Core.Services.SessionManagerService;
import com.br.soundwave.Core.Services.TokenService;
import com.br.soundwave.api.ModelDto.TokenModelDTO;
import com.br.soundwave.api.ModelDto.ClientDTO;


@RestController
@RequestMapping("/token")
public class TokenController {

	@Autowired
	private TokenService confirmationTokenService;
	
	@Autowired
	private SessionManagerService managerService;
	
	@Autowired
	private ClientService clientService;
	
	
	@PostMapping("/validate-token/{id}/{token}")
	public ResponseEntity<?> validateEmailToken(@PathVariable long id, @PathVariable int token) {
		if(confirmationTokenService.validateEmailToken(token, id)) {
			return ResponseEntity.status(200).body("e-mail confirmado com sucesso!");
		}
		return ResponseEntity.status(401).body("Ops ocorreu um problema, verifique o código inserido");	
	}
	
	@GetMapping("/check-auth")
	public ResponseEntity<ClientDTO> checkAuthByToken(@CookieValue(name = "SESSION_ID", required=false) String sessionId){
		if(sessionId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		Long clientId = managerService.getClientIdByToken(sessionId);
		
		if(clientId == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		
		ClientModel clientRaw = clientService.findClientById(clientId);
		ClientDTO clientDto = new ClientDTO();
		clientDto.setId(clientRaw.getId());
		clientDto.setEmail(clientRaw.getEmail());
		clientDto.setNome(clientRaw.getClientName());
		
		
		return ResponseEntity.ok(clientDto);
		
	}
	
	
	
}
