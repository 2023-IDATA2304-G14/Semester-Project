package no.ntnu.message;

import no.ntnu.greenhouse.GreenhouseSimulator;

import java.util.List;

public record GetSensorsCommand(int nodeId) implements ListCommand {
    public static final String PREFIX = "gS";

    public static String getPrefix() {
        return PREFIX;
    }

    @Override
    public String serialize() {
        return new MessageParameterizer(PREFIX)
                .setNodeId(String.valueOf(nodeId()))
                .parameterize();
    }

    public static ListCommand deserialize(String message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        MessageParameterizer parameterizer = new MessageParameterizer(PREFIX).deparameterize(message);

        return new GetSensorsCommand(
                Integer.parseInt(parameterizer.getNodeId())
        );
    }

  /**
   * Execute the command.
   *
   * @param logic The GreenhouseSimulator logic to be affected by this command
   * @return The message which contains the output of the command
   */
  @Override
  public List<StateMessage> execute(GreenhouseSimulator logic) {
    return logic.getNode(nodeId).getSensors().stream()
        .map(sensor -> (StateMessage) new SensorStateMessage(nodeId, sensor.getId(), sensor.getType(), sensor.getMin(), sensor.getMax(), sensor.getValue(), sensor.getUnit()))
        .toList();
  }
}
