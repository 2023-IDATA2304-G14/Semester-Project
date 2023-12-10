package no.ntnu.message;

import no.ntnu.greenhouse.GreenhouseSimulator;

import java.util.List;

public interface ListCommand extends Message {
  /**
   * Execute the command.
   *
   * @param logic The GreenhouseSimulator logic to be affected by this command
   * @return The message which contains the output of the command
   */
  List<ActuatorStateMessage> execute(GreenhouseSimulator logic);

  /**
   * Deserialize a message into a command.
   */
  ListCommand deserialize(String message);
}
