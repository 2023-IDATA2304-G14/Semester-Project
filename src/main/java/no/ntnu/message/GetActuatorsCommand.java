package no.ntnu.message;

import no.ntnu.greenhouse.GreenhouseSimulator;

import java.util.List;

public record GetActuatorsCommand(int nodeId) implements ListCommand {
    public static final String PREFIX = "gA";

    @Override
    public String getPrefix() {
        return PREFIX;
    }

    @Override
    public String serialize() {
        return new MessageParameterizer(PREFIX)
                .setNodeId(String.valueOf(nodeId()))
                .parameterize();
    }

    @Override
    public ListCommand deserialize(String message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        MessageParameterizer parameterizer = new MessageParameterizer(PREFIX).deparameterize(message);

        return new GetActuatorsCommand(
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
    return logic.getNode(nodeId).getActuators().stream()
        .map(actuator -> (StateMessage) new ActuatorStateMessage(nodeId, actuator.getId(), actuator.isOn(), actuator.getStrength(), actuator.getMinStrength(), actuator.getMaxStrength(), actuator.getUnit(), actuator.getType()))
        .toList();

  }
}
