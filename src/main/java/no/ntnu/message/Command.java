package no.ntnu.message;


import no.ntnu.greenhouse.GreenhouseSimulator;

/**
 * A command sent from the client to the server (from controlPanel to GreenhouseNode).
 */
public interface Command extends Message {
    /**
     * Execute the command.
     *
     * @param logic The GreenhouseSimulator logic to be affected by this command
     * @return The message which contains the output of the command
     */
    public abstract Message execute(GreenhouseSimulator logic);
}