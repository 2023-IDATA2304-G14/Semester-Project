package no.ntnu.message;

public record UnknownMessage(String message) implements Message {

    public static final String PREFIX = "uM";
    /**
     * Get the error message.
     *
     * @return the error message.
     */
    public String getMessage() {
        return message;
    }

    public static String getPrefix() {
        return PREFIX;
    }

    @Override
    public String serialize() {
        return new MessageParameterizer(PREFIX)
                .setValue(message())
                .parameterize();
    }

    public static Message deserialize(String message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        MessageParameterizer parameterizer = new MessageParameterizer(PREFIX).deparameterize(message);

        return new UnknownMessage(
                parameterizer.getValue()
        );
    }
}
