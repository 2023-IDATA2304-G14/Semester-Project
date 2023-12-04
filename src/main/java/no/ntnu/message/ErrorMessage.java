package no.ntnu.message;

public record ErrorMessage(String message) implements Message {
    /**
     * Get the error message.
     *
     * @return the error message.
     */
    public String getMessage() {
        return message;
    }
}
