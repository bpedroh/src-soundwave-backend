package com.br.soundwave.api.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.br.soundwave.Core.Services.MFAService;
import com.br.soundwave.Core.Services.TokenService;


@RestController
@RequestMapping("/token")
public class TokenController {
	
	@Autowired
	private TokenService confirmationTokenService;
	
	@Autowired
	private MFAService mfaService;
	
	@GetMapping("/validate-token/{id}")
	public void validateEmailToken(@RequestBody int token, @PathVariable long id) {
		confirmationTokenService.validateEmailToken(token, id);
		
	}
	
}
