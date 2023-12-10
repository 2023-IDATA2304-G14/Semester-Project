package no.ntnu.message;

import no.ntnu.greenhouse.SensorReading;

public record SensorDataMessage(int nodeId, int sensorId, SensorReading reading) implements Message {
  public static final String PREFIX = "sD";

  @Override
  public String getPrefix() {
    return PREFIX;
  }

  @Override
  public String serialize() {
    String value = String.valueOf(reading().getValue());
    String unit = reading().getUnit();
    String type = reading().getType();
    return new MessageParameterizer(PREFIX)
        .setNodeId(String.valueOf(nodeId()))
        .setItemId(String.valueOf(sensorId()))
        .setValue(value)
        .setUnit(unit)
        .setType(type)
        .parameterize();
  }

  @Override
  public Message deserialize(String message) {
    if (message == null) {
      throw new IllegalArgumentException("Message cannot be null");
    }
    MessageParameterizer parameterizer = new MessageParameterizer(PREFIX).deparameterize(message);

    return new SensorDataMessage(
        Integer.parseInt(parameterizer.getNodeId()),
        Integer.parseInt(parameterizer.getItemId()),
        new SensorReading(
            parameterizer.getType(),
            Double.parseDouble(parameterizer.getValue()),
            parameterizer.getUnit()
        )
    );
  }
}
