package no.ntnu.message;

/**
 * An abstract message sent or received by the server.
 */
public interface Message {
    static String getPrefix() {
        throw new UnsupportedOperationException("Not implemented");
    }
    String serialize();

    static Message deserialize(String message) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
