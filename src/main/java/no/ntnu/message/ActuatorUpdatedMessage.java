package no.ntnu.message;

/**
 * Message that is sent when an actuator is updated or added.
 * @param nodeId ID of the node on which the actuator is placed
 * @param actuatorId ID of the actuator that has been updated
 */
public record ActuatorUpdatedMessage(int nodeId, int actuatorId) implements Message {
    private static final String PREFIX = "aU";

    public ActuatorUpdatedMessage {
        if (nodeId < 0 || actuatorId < 0) {
            throw new IllegalArgumentException("Node ID and Actuator ID must be non-negative.");
        }
    }

    @Override
    public String getPrefix() {
        return PREFIX;
    }

    @Override
    public String serialize() {
        return new MessageParameterizer(PREFIX)
                .setNodeId(String.valueOf(nodeId()))
                .setItemId(String.valueOf(actuatorId()))
                .parameterize();
    }

    @Override
    public Message deserialize(String message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        MessageParameterizer parameterizer = new MessageParameterizer(PREFIX).deparameterize(message);

        return new ActuatorUpdatedMessage(
                Integer.parseInt(parameterizer.getNodeId()),
                Integer.parseInt(parameterizer.getItemId())
        );
    }
}
