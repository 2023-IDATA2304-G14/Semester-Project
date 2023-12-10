package no.ntnu.encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

/**
 * This class handles the encryption and decryption of messages based
 * on a share symmetric key between the devices.
 *
 * @author Daniel Neset
 * @version 08.12.2023
 */
public class SymmetricEncryption {

  /**
   * This class should not be instantiated.
   */
  private SymmetricEncryption() {
  }

  /**
   * Encrypt a message with a passPhrase.
   *
   * @param messageToEncrypt The message to be encrypted
   * @param passPhrase The passPhrase to encrypt the message
   * @return return the encrypted message
   * @throws IllegalArgumentException Throw exception if there is an error
   * @throws NoSuchAlgorithmException Throw exception if there is an error
   * @throws InvalidKeySpecException Throw exception if there is an error
   * @throws NoSuchPaddingException Throw exception if there is an error
   * @throws InvalidKeyException Throw exception if there is an error
   * @throws IllegalBlockSizeException Throw exception if there is an error
   * @throws BadPaddingException Throw exception if there is an error
   * @throws InvalidAlgorithmParameterException Throw exception if there is an error
   * @throws ShortBufferException Throw exception if there is an error
   */
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

  /**
   * Decrypt an encrypted message with a passPhrase
   *
   * @param encryptedMessage The encrypted message to be decrypted
   * @param passPhrase The passPhrase to encrypt the message
   * @return return the decrypted message
   * @throws IllegalArgumentException Throw exception if there is an error
   * @throws NoSuchAlgorithmException Throw exception if there is an error
   * @throws InvalidKeySpecException Throw exception if there is an error
   * @throws IllegalBlockSizeException Throw exception if there is an error
   * @throws BadPaddingException Throw exception if there is an error
   * @throws NoSuchPaddingException Throw exception if there is an error
   * @throws InvalidKeyException Throw exception if there is an error
   * @throws InvalidParameterSpecException Throw exception if there is an error
   * @throws InvalidAlgorithmParameterException Throw exception if there is an error
   */
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

  /**
   * Helper method to generate a secret key from a passPhrase
   *
   * @param password The passPhrase used to create a secret key
   * @return return a SecretKeySPec
   * @throws IllegalArgumentException Throw exception if there is an error
   * @throws NoSuchAlgorithmException Throw exception if there is an error
   * @throws InvalidKeySpecException Throw exception if there is an error
   */
  private static SecretKey generateSecretKeyFromPassword(String password) throws IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException {
    byte[] salt = "SecureSeaSalt!".getBytes();
    int iterations = 10000;

    return generateSecretKeyFromPassword(password, salt, iterations);
  }

  /**
   * Helper method to generate a secret key from a passPhrase with
   * custom salt and iterations.
   *
   * @param password The passPhrase used to create a secret key
   * @param salt The salt used to create a secret key
   * @param iterations The amount of iterations used to create a secret key
   * @return return a SecretKeySPec
   * @throws IllegalArgumentException Throw exception if there is an error
   * @throws NoSuchAlgorithmException Throw exception if there is an error
   * @throws InvalidKeySpecException Throw exception if there is an error
   */
  private static SecretKey generateSecretKeyFromPassword(String password, byte[] salt, int iterations) throws IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException {
    SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterations, 256);
    SecretKey tmp = secretKeyFactory.generateSecret(keySpec);

    return new SecretKeySpec(tmp.getEncoded(), "AES");
  }
}
