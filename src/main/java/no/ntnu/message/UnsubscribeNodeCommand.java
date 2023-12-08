package no.ntnu.message;

import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.subcribers.NodeSubscriber;

public record UnsubscribeNodeCommand(int nodeId) implements NodeSubscriptionCommand {
    @Override
    public Message execute(GreenhouseSimulator logic, NodeSubscriber subscriber) {
        Message response;
        try {
            subscriber.removeNodeFromSubscribers(logic.getNode(nodeId));
            response = new UnsubscribeNodeMessage(nodeId);
        } catch (IllegalStateException e) {
            response = new ErrorMessage(e.getMessage());
        }
        return response;
    }
}
