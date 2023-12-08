package no.ntnu.message;

import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.subcribers.NodeSubscriber;

public record NodeSubscriptionNodeCommand(int nodeId) implements NodeSubscriptionCommand {
    @Override
    public Message execute(GreenhouseSimulator logic, NodeSubscriber subscriber) {
        Message response;
        try {
            subscriber.addNodeToSubscribers(logic.getNode(nodeId));
            response = new SubscribeNodeMessage(nodeId);
        } catch (IllegalStateException e) {
            response = new ErrorMessage(e.getMessage());
        }
        return response;
    }

}
