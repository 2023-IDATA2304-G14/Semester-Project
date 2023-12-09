package no.ntnu.message;

public record ErrorMessage(String message) implements Message {
    public static final String PREFIX = "e";

    @Override
    public String getPrefix() {
        return PREFIX;
    }

    @Override
    public String serialize() {
        return new MessageParameterizer(PREFIX)
                .setValue(message())
                .parameterize();
    }

    @Override
    public Message deserialize(String message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        MessageParameterizer parameterizer = new MessageParameterizer(PREFIX).deparameterize(message);

        return new ErrorMessage(
                parameterizer.getValue()
        );
    }
}
