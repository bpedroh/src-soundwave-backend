package com.br.soundwave.api.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

	
	@GetMapping("/healthCheck")
	public String healthCheck() {
		return "status:OK";
	}
	
}
