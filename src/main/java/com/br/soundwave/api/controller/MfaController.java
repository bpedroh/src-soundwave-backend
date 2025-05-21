package com.br.soundwave.api.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.br.soundwave.Core.Model.ClientModel;
import com.br.soundwave.Core.Repository.ClientRepository;
import com.br.soundwave.Core.Utils.MFAUtil;
import com.br.soundwave.Core.Utils.TOTPGeneratorUtil;

@RestController
@RequestMapping("/mfa")
public class MfaController {
	
	
	@Autowired
	private ClientRepository clientRepository;
	
	
	 @PostMapping("/setup")
	    public ResponseEntity<byte[]> setupMFA(@RequestParam Long userId) throws Exception {
	        ClientModel client = clientRepository.findById(userId).orElseThrow();
	        
	        
	        if (client.getTotpSecret() == null) {
	            String secretKey = MFAUtil.generateSecretKey();
	            client.setTotpSecret(secretKey);
	            clientRepository.save(client);
	        }

	        
	        byte[] qrCode = MFAUtil.generateQRCode(
	            client.getTotpSecret(),
	            client.getEmail(),
	            "soundwave" 
	        );

	        return ResponseEntity.ok()
	            .contentType(MediaType.IMAGE_PNG)
	            .body(qrCode);
	    }
	    
	    
	    @PostMapping("/validate")
	    public ResponseEntity<?> validateMFA(
	        @RequestParam Long userId,
	        @RequestParam String code
	    ) {
	        ClientModel client = clientRepository.findById(userId).orElseThrow();
	        
	        boolean isValid = TOTPGeneratorUtil.validateCode(client.getTotpSecret(), code, 1);
	        
	        if (isValid) {
	        	client.setMfaEnabled(true);
	        	clientRepository.save(client);
	            return ResponseEntity.ok("Autenticação MFA bem-sucedida!");
	        } else {
	            return ResponseEntity.status(401).body("Código inválido");
	        }
	    }
}
	

