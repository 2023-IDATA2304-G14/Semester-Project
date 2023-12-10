package no.ntnu.message;

import no.ntnu.greenhouse.ClientHandler;
import no.ntnu.greenhouse.GreenhouseSimulator;

public interface NodeSubscriptionCommand extends Message {
    /**
     * Execute the command.
     * GreenhouseSimulator logic to be affected by this command
     * ClientHandler subscriber that sent the command
     */
    public abstract Message execute(GreenhouseSimulator logic, ClientHandler subscriber);
}
