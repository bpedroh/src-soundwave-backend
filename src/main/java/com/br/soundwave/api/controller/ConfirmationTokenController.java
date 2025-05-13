package com.br.soundwave.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.soundwave.Core.Services.ConfirmationTokenService;

@RestController
@RequestMapping("/token")
public class ConfirmationTokenController {
	
	@Autowired
	private ConfirmationTokenService confirmationTokenService;
	
	@GetMapping("/validate-token/{id}")
	public void validateEmailToken(@RequestBody int token, @PathVariable long id) {
		confirmationTokenService.validateEmailToken(token, id);
	}
	
}
