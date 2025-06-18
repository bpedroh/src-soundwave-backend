package com.br.soundwave.Core.Utils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import com.br.soundwave.api.ModelDto.EncryptedPayloadDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

import java.security.spec.MGF1ParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

@Component
public class CipherUtils {
	
	private KeyPair keyPair;
	
	
	  //@PostConstruct
	   // public void init() throws NoSuchAlgorithmException {
	      //  KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
	     //   keyPairGenerator.initialize(2048); 
	     //   this.keyPair = keyPairGenerator.generateKeyPair();
	   // }

	  @PostConstruct
	    public void init() throws NoSuchAlgorithmException {
		  	File publicKeyFile = new File("public.pem");
		    File privateKeyFile = new File("private.pem");
		    
		    if (publicKeyFile.exists() && privateKeyFile.exists()) {
		        try {
					this.keyPair = loadKeyPair();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        System.out.println("Par de chaves carregado.");
		    } else {
		        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		        keyPairGenerator.initialize(2048);
		        this.keyPair = keyPairGenerator.generateKeyPair();
		        try {
					saveKeyPair(keyPair);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        System.out.println("Par de chaves gerado e salvo.");
		    }
	    }
	
	public String decodeEncryptedPayload(EncryptedPayloadDTO payload) {
		try {
			PrivateKey privateKey = keyPair.getPrivate();
			System.out.println("Encrypted AES Key: " + payload.getEncryptedAesKey());
			byte[] decryptedAesKeyBytes = decryptRSA(
					Base64.getDecoder().decode(payload.getEncryptedAesKey()), privateKey);
			
			 byte[] decryptedIvBytes = decryptRSA(
	                    Base64.getDecoder().decode(payload.getEncryptedIv()), privateKey);
			
			 byte[] encryptedDataBytes = Base64.getDecoder().decode(payload.getEncryptedData());
	         byte[] decryptedDataBytes = decryptAES(
	                    encryptedDataBytes, decryptedAesKeyBytes, decryptedIvBytes);
			 
	         String decryptedJsonPayload = new String(decryptedDataBytes, StandardCharsets.UTF_8);
	         System.out.println("Payload descriptografado: " + decryptedJsonPayload);
	         return decryptedJsonPayload;
	         
		} catch (Exception e) {
			return "err: " + e.getCause();
		}
		
	}
	
	public PublicKey getPublicKey() {
		return keyPair.getPublic();
	}
	
	public String convertPublicKeyToPem(PublicKey publicKey) {
        
        byte[] publicKeyBytes = publicKey.getEncoded();

        String base64Encoded = Base64.getEncoder().encodeToString(publicKeyBytes);
        StringBuilder pemBuilder = new StringBuilder();
        pemBuilder.append("-----BEGIN PUBLIC KEY-----\n");

        int lineLength = 64;
        for (int i = 0; i < base64Encoded.length(); i += lineLength) {
            int end = Math.min(i + lineLength, base64Encoded.length());
            pemBuilder.append(base64Encoded.substring(i, end)).append("\n");
        }

        pemBuilder.append("-----END PUBLIC KEY-----\n");

        return pemBuilder.toString();
    }
	
	public static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    public static String encodePublicKey(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public static String encodePrivateKey(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public static PublicKey decodePublicKey(String publicKeyEncoded) throws Exception {
        byte[] publicBytes = Base64.getDecoder().decode(publicKeyEncoded);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    public static PrivateKey decodePrivateKey(String privateKeyEncoded) throws Exception {
        byte[] privateBytes = Base64.getDecoder().decode(privateKeyEncoded);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
    
    public static byte[] decryptRSA(byte[] encryptedData, PrivateKey privateKey) throws Exception {
        System.out.println("--- Início decryptRSA ---");
        System.out.println("Tamanho do encryptedData (bytes): " + (encryptedData != null ? encryptedData.length : "null"));
        System.out.println("Private Key é nula? " + (privateKey == null));

        if (encryptedData == null || privateKey == null) {
            System.err.println("Erro: encryptedData ou privateKey é nulo.");
            throw new IllegalArgumentException("Dados ou chave nulos para decriptografia RSA.");
        }

        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
            OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                    "SHA-256", 
                    "MGF1", 
                    new MGF1ParameterSpec("SHA-256"), 
                    PSource.PSpecified.DEFAULT);
            cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParams);
            
            byte[] decryptedBytes = cipher.doFinal(encryptedData);
            System.out.println("--- Fim decryptRSA: SUCESSO ---");
            return decryptedBytes;

        } catch (javax.crypto.BadPaddingException e) {
            System.err.println("ERRO RSA (BadPaddingException): O padding da mensagem está incorreto");
            throw e; 
        } catch (javax.crypto.IllegalBlockSizeException e) {
            System.err.println("ERRO RSA (IllegalBlockSizeException): O tamanho do bloco de entrada para decriptografia RSA está incorreto. Esperado 256 bytes para chave de 2048 bits.");
            e.printStackTrace();
            throw e; 
        } catch (java.security.InvalidKeyException e) {
            System.err.println("ERRO RSA (InvalidKeyException): A chave privada é inválida.");
            e.printStackTrace();
            throw e; 
        } catch (Exception e) {
            System.err.println("ERRO RSA (Exceção Genérica): Ocorreu um erro inesperado durante a decriptografia RSA: " + e.getMessage());
            e.printStackTrace();
            throw e; 
        } finally {
            System.out.println("--- Finalizando decryptRSA ---");
        }
    }

    public static byte[] decryptSymmetricKey(String encryptedSymmetricKeyBase64, PrivateKey privateKey) throws Exception {
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedSymmetricKeyBase64);
        return decryptRSA(encryptedBytes, privateKey);
    }
    
    public static byte[] encryptAES(byte[] data, byte[] symmetricKey, byte[] iv) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(symmetricKey, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);
        return cipher.doFinal(data);
    }

    public static byte[] decryptAES(byte[] encryptedData, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        return cipher.doFinal(encryptedData);
    }
    
    private KeyPair loadKeyPair() throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(Files.readString(Paths.get("public.pem")));
        byte[] privateKeyBytes = Base64.getDecoder().decode(Files.readString(Paths.get("private.pem")));

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

        return new KeyPair(publicKey, privateKey);
    }
    
    private void saveKeyPair(KeyPair keyPair) throws Exception {
        String publicKeyContent = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKeyContent = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

        Files.writeString(Paths.get("public.pem"), publicKeyContent);
        Files.writeString(Paths.get("private.pem"), privateKeyContent);
    }
}
