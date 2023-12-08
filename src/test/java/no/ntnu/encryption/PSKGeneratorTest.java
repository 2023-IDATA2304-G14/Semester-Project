//package no.ntnu.encryption;
//
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class PSKGeneratorTest {
//  //Test creating a PSK key and encrypting then decrypting the message.
//  @Test
//  void testEncryptAndDecryptMessageWithPSKKey() throws Exception {
//
//    String messageToEncrypt = "Super Secret Message";
//    String passPhrase = PSKGenerator.generateKey();
//
//    byte[] encryptedMessage = SymmetricEncryption.encryptMessage(messageToEncrypt, passPhrase);
//    String decryptedMessage = SymmetricEncryption.decryptMessage(encryptedMessage, passPhrase);
//
//    assertEquals(messageToEncrypt, decryptedMessage);
//  }
//}
////