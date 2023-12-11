package no.ntnu.message;

/**
 * Message sent from the server to the client when an actuator is removed from a node.

 * @param nodeId ID of the node to which the actuator is attached
 * @param actuatorId ID of the actuator
 */
public record ActuatorRemovedMessage(int nodeId, int actuatorId) implements Message {
  private static final String PREFIX = "aR";

  /**
   * Create a new instance of this message.

   * @param nodeId ID of the node to which the actuator is attached
   * @param actuatorId ID of the actuator
   */
  public ActuatorRemovedMessage {
    if (nodeId < 0 || actuatorId < 0) {
      throw new IllegalArgumentException("Node ID and Actuator ID must be non-negative.");
    }
  }

  public static String getPrefix() {
    return PREFIX;
  }

  @Override
  public String serialize() {
    return new MessageParameterizer(PREFIX)
        .setNodeId(String.valueOf(nodeId()))
        .setItemId(String.valueOf(actuatorId()))
        .parameterize();
  }

  public static Message deserialize(String message) {
    if (message == null) {
      throw new IllegalArgumentException("Message cannot be null");
    }
    MessageParameterizer parameterizer = new MessageParameterizer(PREFIX).deparameterize(message);

    return new ActuatorRemovedMessage(
      Integer.parseInt(parameterizer.getNodeId()),
      Integer.parseInt(parameterizer.getItemId())
    );
  }
}
