package no.ntnu.message;

import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.GreenhouseSimulator;

public record SetActuatorCommand(int nodeId, int actuatorId, boolean isOn, int strength) implements Command {
  public static final String PREFIX = "setA";

  /**
   * Execute the command.
   *
   * @param logic The GreenhouseSimulator logic to be affected by this command
   * @return The message which contains the output of the command
   */
  @Override
  public Message execute(GreenhouseSimulator logic) {
    Actuator actuator = logic.getNode(nodeId).getActuator(actuatorId);
    actuator.setOn(isOn);
    actuator.setStrength(strength);

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
        .setValue(String.valueOf(isOn()))
        .setSecondaryValue(String.valueOf(strength()))
        .parameterize();
  }

  public static Message deserialize(String message) {
    if (message == null) {
      throw new IllegalArgumentException("Message cannot be null");
    }
    MessageParameterizer parameterizer = new MessageParameterizer(PREFIX).deparameterize(message);

    return new SetActuatorCommand(
        Integer.parseInt(parameterizer.getNodeId()),
        Integer.parseInt(parameterizer.getItemId()),
        Boolean.parseBoolean(parameterizer.getValue()),
        Integer.parseInt(parameterizer.getSecondaryValue())
    );
  }
}
