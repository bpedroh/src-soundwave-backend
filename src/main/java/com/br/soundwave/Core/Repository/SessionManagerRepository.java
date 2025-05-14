package com.br.soundwave.Core.Repository;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.soundwave.Core.Model.SessionManagerModel;

public interface SessionManagerRepository extends JpaRepository<SessionManagerModel, UUID>{
	
	void deleteAllByExpirationTimeBefore(Instant time);

	
}
