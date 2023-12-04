package no.ntnu.encryption;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;

public class SymmetricEncryption {

  public static byte[] encryptMessage(String messageToEncrypt, String passPhrase) throws Exception {

    SecretKey secretKey = generateSecretKeyFromPassword(passPhrase);
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);

    byte[] encryptedBytes = cipher.doFinal(messageToEncrypt.getBytes());

    return encryptedBytes;
  }

  public static String decryptMessage(byte[] encryptedMessage, String passPhrase) throws Exception {
    SecretKey secretKey = generateSecretKeyFromPassword(passPhrase);
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.DECRYPT_MODE, secretKey);

    byte[] decryptBytes = cipher.doFinal(encryptedMessage);
    String decryptedMessage = new String(decryptBytes);

    return decryptedMessage;
  }


  private static SecretKey generateSecretKeyFromPassword(String password) throws Exception {
    byte[] salt = "SecureSeaSalt!".getBytes();
    int iterations = 10000;
    SecretKey secretKey = generateSecretKeyFromPassword(password, salt, iterations);

    return secretKey;
  }

  private static SecretKey generateSecretKeyFromPassword(String password, byte[] salt, int iterations) throws Exception {
    SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterations, 256);
    SecretKey tmp = secretKeyFactory.generateSecret(keySpec);
    SecretKey secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

    return secretKey;
  }


}
