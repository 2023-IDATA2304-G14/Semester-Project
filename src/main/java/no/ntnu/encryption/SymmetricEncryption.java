package no.ntnu.encryption;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

class SymmetricEncryption {

  /**
   * This class should not be instantiated.
   */
  private SymmetricEncryption() {
  }

  public static byte[] encryptMessage(String messageToEncrypt, String passPhrase) throws IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

    SecretKey secretKey = generateSecretKeyFromPassword(passPhrase);
    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);

    return cipher.doFinal(messageToEncrypt.getBytes());
  }

  public static String decryptMessage(byte[] encryptedMessage, String passPhrase) throws IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException {
    SecretKey secretKey = generateSecretKeyFromPassword(passPhrase);
    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    cipher.init(Cipher.DECRYPT_MODE, secretKey);

    byte[] decryptBytes = cipher.doFinal(encryptedMessage);

    return new String(decryptBytes);
  }


  private static SecretKey generateSecretKeyFromPassword(String password) throws IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException {
    byte[] salt = "SecureSeaSalt!".getBytes();
    int iterations = 10000;

    return generateSecretKeyFromPassword(password, salt, iterations);
  }

  private static SecretKey generateSecretKeyFromPassword(String password, byte[] salt, int iterations) throws IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException {
    SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterations, 256);
    SecretKey tmp = secretKeyFactory.generateSecret(keySpec);

    return new SecretKeySpec(tmp.getEncoded(), "AES");
  }


}
