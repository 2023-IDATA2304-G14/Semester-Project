package no.ntnu.message;

public record ErrorMessage(String message) implements Message {
    public static final String PREFIX = "e";

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

        return new ErrorMessage(
                parameterizer.getValue()
        );
    }
}
