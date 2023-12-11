package no.ntnu.message;

public record NodeStateMessage(int nodeId, String name) implements StateMessage {
    public static final String PREFIX = "nS";

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

        return new NodeStateMessage(
                Integer.parseInt(parameterizer.getNodeId()),
                parameterizer.getValue()
        );
    }
}
