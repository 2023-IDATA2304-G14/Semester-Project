package no.ntnu.message;

public record SubscribeNodeMessage(int nodeId) implements Message {
  public static final String PREFIX = "subbed";

  @Override
  public String getPrefix() {
    return PREFIX;
  }

  @Override
  public String serialize() {
    return new MessageParameterizer(PREFIX)
        .setNodeId(String.valueOf(nodeId()))
        .parameterize();
  }

  @Override
  public Message deserialize(String message) {
    if (message == null) {
      throw new IllegalArgumentException("Message cannot be null");
    }
    MessageParameterizer parameterizer = new MessageParameterizer(PREFIX).deparameterize(message);

    return new SubscribeNodeMessage(
        Integer.parseInt(parameterizer.getNodeId())
    );
  }
}
