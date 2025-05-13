package com.br.soundwave.Core.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.soundwave.Core.Model.ConfirmationTokenModel;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationTokenModel, Long>{

}
