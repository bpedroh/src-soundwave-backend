package com.br.soundwave.Core.Services;


import java.util.Map;
import java.util.Set;


import lombok.Builder;
import lombok.Getter;

public interface SendEmailService {
	
void enviar(Mensagem mensagem);
	

	@Builder
	@Getter
	static class Mensagem{
		
		private Set<String> destinatarios;
		private String destinatario;
		private String assunto;
		private String corpo;
		
		
		private Map<String, Object> var;
		
		
	}
}
