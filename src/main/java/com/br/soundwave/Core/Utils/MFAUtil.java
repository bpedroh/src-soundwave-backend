package com.br.soundwave.Core.Utils;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base32;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class MFAUtil {
	
	public static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return new Base32().encodeToString(bytes);
    }
	
	public static byte[] generateQRCode(String secretKey, String usuario, String appName) throws Exception {
        String otpauthUrl = String.format(
            "otpauth://totp/%s:%s?secret=%s&issuer=%s&digits=6&period=30",
            appName, usuario, secretKey, appName
        );

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(otpauthUrl, BarcodeFormat.QR_CODE, 200, 200);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", out);
        return out.toByteArray();
    }
	
}
