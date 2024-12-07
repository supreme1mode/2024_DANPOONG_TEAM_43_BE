package com.carely.backend.service.easyCodef;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;


@Component
public class EasyCodefUtil {

    public static String encryptRSA(String plainText, String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] bytePublicKey = Base64.getDecoder().decode(publicKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey key = keyFactory.generatePublic(new X509EncodedKeySpec(bytePublicKey));

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] bytePlain = cipher.doFinal(plainText.getBytes());

        return Base64.getEncoder().encodeToString(bytePlain);
    }


    public static String encodeToFileString(String filePath) throws IOException {
        File file = new File(filePath);

        byte[] fileContent = FileUtils.readFileToByteArray(file);

        return Base64.getEncoder().encodeToString(fileContent);
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, Object> getTokenMap(String token) throws IOException {

        String[] split_string = token.split("\\.");
        String base64EncodedBody = split_string[1];
        String tokenBody = new String(Base64.getDecoder().decode(base64EncodedBody));

        /** �� ��ȯ */
        return new ObjectMapper().readValue(tokenBody, HashMap.class);
    }

    public static boolean checkValidity(int expInt) {
        long now = new Date().getTime();
        String expStr = expInt + "000";	// ���� �ð� Ÿ�ӽ������� �ڸ��� ���߱�(13�ڸ�)
        long exp  = Long.parseLong(expStr);
        // ��ȿ�Ⱓ Ȯ��::��ȿ�Ⱓ�� �����ų� �ѽð� �̳��� ����Ǵ� ���
        return now <= exp && (exp - now >= 3600000);
    }
}