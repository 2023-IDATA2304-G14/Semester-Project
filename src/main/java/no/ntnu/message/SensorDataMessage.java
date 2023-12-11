package no.ntnu.message;

import no.ntnu.greenhouse.SensorReading;

public record SensorDataMessage(int nodeId, int sensorId, double value, String unit, String type) implements Message {
  public static final String PREFIX = "sD";

  public static String getPrefix() {
    return PREFIX;
  }

  @Override
  public String serialize() {
    return new MessageParameterizer(PREFIX)
        .setNodeId(String.valueOf(nodeId()))
        .setItemId(String.valueOf(sensorId()))
        .setValue(String.valueOf(value()))
        .setUnit(unit())
        .setType(type())
        .parameterize();
  }

  public static Message deserialize(String message) {
    if (message == null) {
      throw new IllegalArgumentException("Message cannot be null");
    }
    MessageParameterizer parameterizer = new MessageParameterizer(PREFIX).deparameterize(message);

    return new SensorDataMessage(
        Integer.parseInt(parameterizer.getNodeId()),
        Integer.parseInt(parameterizer.getItemId()),
        Double.parseDouble(parameterizer.getValue()),
        parameterizer.getUnit(),
        parameterizer.getType()
    );
  }
}
