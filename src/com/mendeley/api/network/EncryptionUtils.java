package com.mendeley.api.network;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtils {
    // Encryption key for AES cipher:
    private static byte[] keyBytes = new byte[] { 0x09, 0x0f, 0x07, 0x15, 0x02, 0x04, 0x17, 0x10, 0x00, 0x05,
            0x07, 0x08, 0x09, 0x11, 0x0a, 0x01, 0x0b, 0x0c, 0x0d, 0x0e, 0x12, 0x14, 0x06, 0x11 };

    /**
     * Encrypting text using the private keyBytes byte array.
     *
     * @param clearText the text to encrypt
     * @return the encrypted text
     * @throws Exception
     */
    public static String encrypt(String clearText) {
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] cipherText = cipher.doFinal(clearText.getBytes());

            return new String(Base64.encode(cipherText, Base64.DEFAULT), "UTF-8");
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * Decrypting text using the private keyBytes byte array.
     *
     * @param cipherText the cipher text to decrypt
     * @return the clear text string
     * @throws Exception
     */
    public static String decrypt(String cipherText) {
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] clearText = cipher.doFinal(Base64.decode(cipherText.getBytes(), Base64.DEFAULT));

            return new String(clearText);
        } catch (Exception e) {
            return "unknown";
        }
    }
}
