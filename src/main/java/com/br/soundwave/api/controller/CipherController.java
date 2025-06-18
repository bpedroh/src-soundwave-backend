package com.br.soundwave.api.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.soundwave.Core.Utils.CipherUtils;

import java.io.StringWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/criptografia")
public class CipherController {
	
	@Autowired
	private CipherUtils cipherUtils;
	
	@GetMapping("/public-key")
	 public ResponseEntity<String> getPublicKey() {
        String publicKeyPem = cipherUtils.convertPublicKeyToPem(cipherUtils.getPublicKey());
        return ResponseEntity.ok(publicKeyPem);
    }
	
}
