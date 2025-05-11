/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ServidorHTTPJuegosInteractivos;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

/**
 * Utility class for AES encryption and decryption.
 */
public class CryptoUtil {
    // TODO code application logic here
    
    private static SecretKeySpec secretKey;
    private static byte[] key;
    private static final String AES_KEY_STRING = "EsteEsMiSuperSecretoParaAES"; // Use a strong, constant key

    // Initialize the key for AES
    static {
        try {
            key = AES_KEY_STRING.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // Use only first 128 bit (16 bytes)
            secretKey = new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Encrypts a string using AES.
     * @param strToEncrypt The string to encrypt.
     * @return The encrypted string (Base64 encoded).
     */
    public static String encrypt(String strToEncrypt) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    /**
     * Decrypts an AES encrypted string.
     * @param strToDecrypt The Base64 encoded string to decrypt.
     * @return The decrypted string.
     */
    public static String decrypt(String strToDecrypt) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
}