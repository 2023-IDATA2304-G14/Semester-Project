package no.ntnu.message;

import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.subcribers.NodeSubscriber;

public interface NodeSubscriptionCommand extends Message {
    /**
     * Execute the command.
     * GreenhouseSimulator logic to be affected by this command
     * NodeSubscriber subscriber to be affected by this command
     */
    public Message execute(GreenhouseSimulator logic, NodeSubscriber subscriber);
}
