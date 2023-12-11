package no.ntnu.message;

import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.GreenhouseSimulator;

public record GetActuatorDataCommand(int nodeId, int actuatorId) implements Command {
    public static final String PREFIX = "gAd";

  /**
   * Execute the command.
   *
   * @param logic The GreenhouseSimulator logic to be affected by this command
   * @return The message which contains the output of the command
   */
  @Override
  public Message execute(GreenhouseSimulator logic) {
    Actuator actuator = logic.getNode(nodeId).getActuator(actuatorId);
    return new ActuatorDataMessage(nodeId, actuatorId, actuator.isOn(), actuator.getStrength());
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

        return new GetActuatorDataCommand(
                Integer.parseInt(parameterizer.getNodeId()),
                Integer.parseInt(parameterizer.getItemId())
        );
    }
}
