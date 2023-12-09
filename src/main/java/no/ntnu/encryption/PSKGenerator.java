package no.ntnu.encryption;

import java.security.SecureRandom;

/**
 * This class generate a (PSK)Pre-Share Key that is used as the key in SymmetricEncryption.
 * Random key that is shown to the user on one device, and entered manually on another device
 * whom they are going to communicate with.
 *
 * @author Daniel Neset
 * @version 08.12.2023
 */
public class PSKGenerator {

  /**
   * Generate a random key.
   *
   * @return return a random key as String
   */
  public static String generateKey(){
    int keyLength = 6;
    byte[] randomKey = generateRandomKey(keyLength);
    String hexKey = bytesToHex(randomKey);
    return hexKey;
  }

  /**
   * Helper method to generate a random key in bytes.
   *
   * @param length The length of the key
   * @return Return the random key in bytes
   */
  private static byte[] generateRandomKey(int length){
    SecureRandom secureRandom = new SecureRandom();
    byte[] key = new byte[length];
    secureRandom.nextBytes(key);
    return key;
  }

  /**
   * Helper method to convert bytes to hex.
   *
   * @param bytes The bytes to be converted to Hex
   * @return Return a String with the random key
   */
  private static String bytesToHex(byte[] bytes){
    StringBuilder hexStringBuilder = new StringBuilder(2 * bytes.length);
    for (byte b : bytes){
      hexStringBuilder.append(String.format("%02x", b));
    }
    return hexStringBuilder.toString();
  }

}
