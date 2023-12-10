package no.ntnu.message;

public record SensorStateMessage(int nodeId, int sensorId, String type, double min, double max, double value, String unit) implements SensorActuatorStateMessage {

  public static final String PREFIX = "sS";

  @Override
  public String getPrefix() {
    return PREFIX;
  }

  @Override
  public String serialize() {
    return new MessageParameterizer(PREFIX)
        .setNodeId(String.valueOf(nodeId()))
        .setItemId(String.valueOf(sensorId()))
        .setType(type())
        .setMin(String.valueOf(min()))
        .setMax(String.valueOf(max()))
        .setValue(String.valueOf(value()))
        .setUnit(unit())
        .parameterize();
  }

  @Override
  public Message deserialize(String message) {
    if (message == null) {
      throw new IllegalArgumentException("Message cannot be null");
    }
    MessageParameterizer parameterizer = new MessageParameterizer(PREFIX).deparameterize(message);

    return new SensorStateMessage(
        Integer.parseInt(parameterizer.getNodeId()),
        Integer.parseInt(parameterizer.getItemId()),
        parameterizer.getType(),
        Double.parseDouble(parameterizer.getMin()),
        Double.parseDouble(parameterizer.getMax()),
        Double.parseDouble(parameterizer.getValue()),
        parameterizer.getUnit()
    );
  }
}
