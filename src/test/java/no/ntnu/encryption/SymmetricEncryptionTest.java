package no.ntnu.encryption;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SymmetricEncryptionTest {

  //Test if the message can be encrypted and decrypted with the right passPhrase
  @Test
  void testEncryptionWithCorrectPassPhrase() throws Exception {
    SymmetricEncryption symmetricEncryption = new SymmetricEncryption();
    String messageToEncrypt = "Super Secret Message";
    String passPhrase = "B3stPassPhr4se";
    byte[] encryptedMessage = symmetricEncryption.encryptMessage(messageToEncrypt, passPhrase);
    String decryptedMessage = symmetricEncryption.decryptMessage(encryptedMessage, passPhrase);

    assertEquals(messageToEncrypt, decryptedMessage);
  }

  //Test if error (Exception) are thrown if the passPhrase is wrong
  @Test
  void testEncryptionWithNotCorrectPassPhrase() throws Exception {
    SymmetricEncryption symmetricEncryption = new SymmetricEncryption();
    String messageToEncrypt = "Super Secret Message";
    String passPhrase1 = "B3stPassPhr4se";
    String passPhrase2 = "B3stP4ssPhr4s3";
    byte[] encryptedMessage = symmetricEncryption.encryptMessage(messageToEncrypt, passPhrase1);

    assertThrows(Exception.class, () -> symmetricEncryption.decryptMessage(encryptedMessage, passPhrase2));
  }


}