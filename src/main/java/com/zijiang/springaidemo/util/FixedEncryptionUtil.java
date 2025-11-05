package com.zijiang.springaidemo.util;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * @ClassName FixedEncryptionUtil
 * @Description TODO
 * @Author pzykangjie
 * @Date 2025/11/5
 * @Version 1.0
 **/
public class FixedEncryptionUtil {

    // 基于固定文本生成密钥
    public static SecretKey generateKeyFromText(String text) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = sha.digest(text.getBytes("UTF-8"));
        // 使用前128位作为AES密钥
        return new SecretKeySpec(key, 0, 16, "AES");
    }

    // 加密方法
    public static String encrypt(String plainText, String password) throws Exception {
        try {
            SecretKey secretKey = generateKeyFromText(password);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new Exception("加密失败", e);
        }
    }

    // 解密方法
    public static String decrypt(String encryptedText, String password) throws Exception {
        SecretKey secretKey = generateKeyFromText(password);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes);
    }

    public static void main(String[] args) {
        try {
            // 固定的密码文本
            String fixedPassword = "mySecretKey123";

            // 原始文本
            String originalText = "Hello, this is a secret message!";
            System.out.println("Original Text: " + originalText);

            // 使用固定文本作为密码进行加密
            String encryptedText = encrypt(originalText, fixedPassword);
            System.out.println("Encrypted Text: " + encryptedText);

            // 使用相同的固定文本作为密码进行解密
            String decryptedText = decrypt(encryptedText, fixedPassword);
            String decryptedText2 = decrypt(encryptedText, "mySecretKey");
            System.out.println("Decrypted Text: " + decryptedText);
            System.out.println("Decrypted Text2: " + decryptedText2);

            // 验证解密结果
            System.out.println("Decryption Success: " + originalText.equals(decryptedText));
            System.out.println("Decryption Success: " + originalText.equals(decryptedText2));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
