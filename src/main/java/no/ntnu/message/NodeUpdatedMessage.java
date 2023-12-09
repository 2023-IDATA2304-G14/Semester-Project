package no.ntnu.message;

public record NodeUpdatedMessage(int nodeId, String name) implements Message {
    public static final String PREFIX = "nU";

    @Override
    public String getPrefix() {
        return PREFIX;
    }

    @Override
    public String serialize() {
        return new MessageParameterizer(PREFIX)
                .setNodeId(String.valueOf(nodeId()))
                .setValue(name())
                .parameterize();
    }

    @Override
    public Message deserialize(String message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        MessageParameterizer parameterizer = new MessageParameterizer(PREFIX).deparameterize(message);

        return new NodeUpdatedMessage(
                Integer.parseInt(parameterizer.getNodeId()),
                parameterizer.getValue()
        );
    }
}
