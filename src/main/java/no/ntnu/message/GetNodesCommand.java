package no.ntnu.message;

import no.ntnu.greenhouse.GreenhouseSimulator;

import java.util.List;

public record GetNodesCommand() implements ListCommand {
    public static final String PREFIX = "gN";

    public static String getPrefix() {
        return PREFIX;
    }

    @Override
    public String serialize() {
        return new MessageParameterizer(PREFIX)
                .parameterize();
    }

  /**
   * Execute the command.
   *
   * @param logic The GreenhouseSimulator logic to be affected by this command
   * @return The message which contains the output of the command
   */
  @Override
  public List<StateMessage> execute(GreenhouseSimulator logic) {
    return logic.getNodes().stream()
        .map(node -> (StateMessage) new NodeStateMessage(node.getId(), node.getName()))
        .toList();
  }

    public static ListCommand deserialize(String message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }

        return new GetNodesCommand();
    }
}
