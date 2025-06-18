package com.br.soundwave.api.ModelDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EncryptedPayloadDTO {
	private String encryptedData;
	private String encryptedIv;
	private String encryptedAesKey;
	
}
