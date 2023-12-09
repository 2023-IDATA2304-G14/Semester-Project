package no.ntnu.message;

public record SensorUpdatedMessage(int nodeId, int sensorId) implements Message {

  public static final String PREFIX = "sU";

  @Override
  public String getPrefix() {
    return PREFIX;
  }

  @Override
  public String serialize() {
    return new MessageParameterizer(PREFIX)
        .setNodeId(String.valueOf(nodeId()))
        .setItemId(String.valueOf(sensorId()))
        .parameterize();
  }

  @Override
  public Message deserialize(String message) {
    if (message == null) {
      throw new IllegalArgumentException("Message cannot be null");
    }
    MessageParameterizer parameterizer = new MessageParameterizer(PREFIX).deparameterize(message);

    return new SensorUpdatedMessage(
        Integer.parseInt(parameterizer.getNodeId()),
        Integer.parseInt(parameterizer.getItemId())
    );
  }
}
