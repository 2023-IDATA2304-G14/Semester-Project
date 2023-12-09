package no.ntnu.message;

/**
 * An abstract message sent or received by the server.
 */
public interface Message {
    String getPrefix();

    String serialize();

    Message deserialize(String message);
}
