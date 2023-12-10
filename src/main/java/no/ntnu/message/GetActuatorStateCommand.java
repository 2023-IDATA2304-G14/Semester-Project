package no.ntnu.message;

import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.GreenhouseSimulator;

public record GetActuatorStateCommand(int nodeId, int actuatorId) implements Command {

  public static final String PREFIX = "gAs";
  /**
   * Execute the command.
   *
   * @param logic The GreenhouseSimulator logic to be affected by this command
   * @return The message which contains the output of the command
   */
  @Override
  public Message execute(GreenhouseSimulator logic) {
    Actuator actuator = logic.getNode(nodeId).getActuator(actuatorId);
    return new ActuatorStateMessage(nodeId, actuatorId, actuator.isOn(), actuator.getStrength(), actuator.getMinStrength(), actuator.getMaxStrength(), actuator.getUnit(), actuator.getType());
  }

  @Override
  public String getPrefix() {
    return PREFIX;
  }

  @Override
  public String serialize() {
    return new MessageParameterizer(PREFIX)
        .setNodeId(String.valueOf(nodeId()))
        .setItemId(String.valueOf(actuatorId()))
        .parameterize();
  }

  @Override
  public Message deserialize(String message) {
    if (message == null) {
      throw new IllegalArgumentException("Message cannot be null");
    }
    MessageParameterizer parameterizer = new MessageParameterizer(PREFIX).deparameterize(message);

    return new GetActuatorStateCommand(
        Integer.parseInt(parameterizer.getNodeId()),
        Integer.parseInt(parameterizer.getItemId())
    );
  }
}
