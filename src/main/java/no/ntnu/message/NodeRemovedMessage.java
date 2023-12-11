package no.ntnu.message;

public record NodeRemovedMessage(int nodeId) implements BroadcastMessage {

  public static final String PREFIX = "nR";

  public static String getPrefix() {
    return PREFIX;
  }

  @Override
  public String serialize() {
    return new MessageParameterizer(PREFIX)
        .setNodeId(String.valueOf(nodeId()))
        .parameterize();
  }

  public static Message deserialize(String message) {
    if (message == null) {
      throw new IllegalArgumentException("Message cannot be null");
    }
    MessageParameterizer parameterizer = new MessageParameterizer(PREFIX).deparameterize(message);

    return new NodeRemovedMessage(
        Integer.parseInt(parameterizer.getNodeId())
    );
  }
}
