package com.br.soundwave.Core.Utils;

import java.lang.reflect.UndeclaredThrowableException;
import java.security.GeneralSecurityException;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;

public class TOTPGeneratorUtil {
	
	private static final int TIME_STEP = 30; 
    private static final int CODE_DIGITS = 6; 
    
    public static String getTOTPCode(String secretKey) {
        long time = new Date().getTime() / 1000 / TIME_STEP;
        return getTOTPCode(secretKey, time);
    }

    private static String getTOTPCode(String secretKey, long time) {
        byte[] key = new Base32().decode(secretKey);
        byte[] data = new byte[8];
        for (int i = 7; i >= 0; i--) {
            data[i] = (byte) (time & 0xFF);
            time >>= 8;
        }

        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] hash = mac.doFinal(data);

            int offset = hash[hash.length - 1] & 0xF;
            long truncatedHash = 0;
            for (int i = 0; i < 4; i++) {
                truncatedHash <<= 8;
                truncatedHash |= (hash[offset + i] & 0xFF);
            }
            truncatedHash &= 0x7FFFFFFF;
            truncatedHash %= Math.pow(10, CODE_DIGITS);

            return String.format("%06d", truncatedHash);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Falha ao gerar código TOTP", e);
        }
    }

    
    public static boolean validateCode(String secretKey, String code, int window) {
        long time = new Date().getTime() / 1000 / TIME_STEP;
        for (int i = -window; i <= window; i++) {
            String testCode = getTOTPCode(secretKey, time + i);
            if (testCode.equals(code)) {
                return true;
            }
        }
        return false;
    }
	
}
