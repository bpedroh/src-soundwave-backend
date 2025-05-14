package com.br.soundwave.Core.Services;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.br.soundwave.Core.Exceptions.GenericExcpetion;
import com.br.soundwave.Core.Model.ClientModel;
import com.br.soundwave.Core.Model.SessionManagerModel;
import com.br.soundwave.Core.Repository.SessionManagerRepository;

import jakarta.transaction.Transactional;

@Component
public class SessionManagerService {
	
	@Autowired
	private SessionManagerRepository managerRepository;
	
	@Transactional
	public SessionManagerModel createSession(ClientModel client) {
		SessionManagerModel session = new SessionManagerModel();
		session.setClient(client);
		session.setValid(true);
		return managerRepository.save(session);
	}
	
	public boolean checkSeassion(String token) {
		UUID uuid = UUID.fromString(token);
		SessionManagerModel session = managerRepository.getById(uuid);
		if(session != null) {
			 Instant expiration =  session.getExpirationTime();
			 if(Instant.now().isAfter(expiration)) {
				 return false;
			 }else {
				 return true;
			 }
		}
		return false;
	}
	
	public Long getClientIdByToken(String token) {
		UUID uuid = UUID.fromString(token);
		Long id = managerRepository.findById(uuid)
			    .orElseThrow(() -> new GenericExcpetion("Sessão não encontrada"))
			    .getClient().getId();
		if(id != null) {
			return id;
		}else {
			throw new GenericExcpetion("Alguma coisa deu errada, realize login novamente.");
		}
		
		
	}
	
	
	@Scheduled(fixedRate = 30 * 60 * 1000) 
	@Transactional
	public void clearAllExpiredSessions() {
	    Instant now = Instant.now();
	    managerRepository.deleteAllByExpirationTimeBefore(now);
	}
	
	

}
