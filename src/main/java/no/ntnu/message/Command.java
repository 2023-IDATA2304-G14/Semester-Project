package no.ntnu.message;


import no.ntnu.greenhouse.SensorActuatorNode;

/**
 * A command sent from the client to the server (from remote to TV).
 */
public interface Command extends Message {
    /**
     * Execute the command.
     *
     * @param logic The TV logic to be affected by this command
     * @return The message which contains the output of the command
     */
//    TODO: Check if this should be SensorActuatorNode or not
    public abstract Message execute(SensorActuatorNode logic);
}