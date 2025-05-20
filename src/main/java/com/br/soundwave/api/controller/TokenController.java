package com.br.soundwave.api.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.br.soundwave.Core.Services.MFAService;
import com.br.soundwave.Core.Services.TokenService;
import com.br.soundwave.api.ModelDto.TokenModelDTO;


@RestController
@RequestMapping("/token")
public class TokenController {
	
	@Autowired
	private TokenService confirmationTokenService;
	
	@Autowired
	private MFAService mfaService;
	
	@PostMapping("/validate-token/{id}/{token}")
	public ResponseEntity<?> validateEmailToken(@PathVariable long id, @PathVariable int token) {
		if(confirmationTokenService.validateEmailToken(token, id)) {
			return ResponseEntity.status(200).body("e-mail confirmado com sucesso!");
		}
		return ResponseEntity.status(401).body("Ops ocorreu um problema, verifique o código inserido");
		
	}
	
}
