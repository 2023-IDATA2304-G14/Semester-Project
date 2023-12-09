package no.ntnu.message;

public record SensorRemoveMessage(int nodeId, int sensorId) implements Message {

  public static final String PREFIX = "sR";

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

    return new SensorRemoveMessage(
        Integer.parseInt(parameterizer.getNodeId()),
        Integer.parseInt(parameterizer.getItemId())
    );
  }
}
