package no.ntnu.message;

import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.greenhouse.Sensor;

public record GetSensorStateCommand(int nodeId, int sensorId) implements Command {
  public static final String PREFIX = "gSs";

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

    return new GetSensorStateCommand(
        Integer.parseInt(parameterizer.getNodeId()),
        Integer.parseInt(parameterizer.getItemId())
    );
  }

  /**
   * Execute the command.
   *
   * @param logic The GreenhouseSimulator logic to be affected by this command
   * @return The message which contains the output of the command
   */
  @Override
  public Message execute(GreenhouseSimulator logic) {
    Sensor sensor = logic.getNode(nodeId).getSensor(sensorId);
//    int nodeId, String type, double min, double max, double current, String unit
    return new SensorStateMessage(nodeId, sensorId, sensor.getType(), sensor.getMin(), sensor.getMax(), sensor.getReading().getValue(), sensor.getUnit());
  }
}
