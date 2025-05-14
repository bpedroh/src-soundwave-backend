package com.br.soundwave.api.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class ordersController {
	
	@GetMapping("/my-orders")
	public String getMyOrder() {
		return "Hello there";
	}
	
}
