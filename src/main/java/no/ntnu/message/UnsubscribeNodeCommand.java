package no.ntnu.message;

import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.subcribers.NodeSubscriber;

public record UnsubscribeNodeCommand(int nodeId) implements NodeSubscriptionCommand {
    public static final String PREFIX = "unsub";
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
    public Message deserialize(String message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        MessageParameterizer parameterizer = new MessageParameterizer(PREFIX).deparameterize(message);

        return new UnsubscribeNodeCommand(
                Integer.parseInt(parameterizer.getNodeId())
        );
    }
}
