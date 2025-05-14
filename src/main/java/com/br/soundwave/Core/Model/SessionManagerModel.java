package com.br.soundwave.Core.Model;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.Setter;


class SessionData{
	
	private Long userId;
    private Instant expirationTime;

    public SessionData(Long userId, Instant duration) {
        this.userId = userId;
        this.expirationTime = duration;
    }
    
    public boolean isExpired() {
        return Instant.now().isAfter(expirationTime);
    }
    
    public Long getUserId() {
        return userId;
    }
}

@Getter
@Setter
public class SessionManagerModel {
	
	private Map<String, SessionData> sessions = new ConcurrentHashMap<>();
	
	public void createSession(String token, Long userId) {
		Instant expirationTime = Instant.now().plus(Duration.ofHours(1));
        sessions.put(token, new SessionData(userId, expirationTime));
    }
	
	public Long getUserIdFromToken(String token) {
        SessionData session = sessions.get(token);
        if (session == null || session.isExpired()) {
            sessions.remove(token);
            return null;
        }
        return session.getUserId();
    }
	
	public void invalidate(String token) {
        sessions.remove(token);
    }
	
	
}
