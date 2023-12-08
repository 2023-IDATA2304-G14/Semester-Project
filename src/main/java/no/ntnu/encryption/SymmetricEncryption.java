package no.ntnu.encryption;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

class SymmetricEncryption {

  /**
   * This class should not be instantiated.
   */
  private SymmetricEncryption() {
  }

  public static byte[] encryptMessage(String messageToEncrypt, String passPhrase) throws IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException {
    SecretKey secretKey = generateSecretKeyFromPassword(passPhrase);

    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

    SecureRandom random = new SecureRandom();
    byte[] iv = new byte[12];
    random.nextBytes(iv);

    GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv);

    cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);

    byte[] ivAndEncrypted = new byte[iv.length + cipher.getOutputSize(messageToEncrypt.getBytes().length)];
    System.arraycopy(iv, 0, ivAndEncrypted, 0, iv.length);

    int encryptedLength = cipher.doFinal(messageToEncrypt.getBytes(), 0, messageToEncrypt.getBytes().length, ivAndEncrypted, iv.length);

    return Arrays.copyOf(ivAndEncrypted, iv.length + encryptedLength);
  }

  public static String decryptMessage(byte[] encryptedMessage, String passPhrase) throws IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, InvalidAlgorithmParameterException {
    SecretKey secretKey = generateSecretKeyFromPassword(passPhrase);

    // Get the GCM parameters from the encrypted message
    AlgorithmParameters parameters = AlgorithmParameters.getInstance("GCM");
    parameters.init(new GCMParameterSpec(128, encryptedMessage, 0, 12)); // Adjust the length based on your requirements

    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    cipher.init(Cipher.DECRYPT_MODE, secretKey, parameters);

    byte[] decryptBytes = cipher.doFinal(encryptedMessage, 12, encryptedMessage.length - 12);

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
