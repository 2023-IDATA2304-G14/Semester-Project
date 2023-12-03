package no.ntnu.message;


import no.ntnu.greenhouse.GreenhouseNode;

/**
 * A command sent from the client to the server (from controlPanel to GreenhouseNode).
 */
public interface Command extends Message {
    /**
     * Execute the command.
     *
     * @param logic The GreenhouseNode logic to be affected by this command
     * @return The message which contains the output of the command
     */
//    TODO: Check if this should be GreenhouseNode or not
//    TODO: Add nodeId or another identifier to the command
    public abstract Message execute(GreenhouseNode logic, String nodeId);
}