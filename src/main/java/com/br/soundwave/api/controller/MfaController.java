package com.br.soundwave.api.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mfa")
public class MfaController {

	@PostMapping("/cadastrar")
	public void generateMFACode() {
		
	}
	
}
