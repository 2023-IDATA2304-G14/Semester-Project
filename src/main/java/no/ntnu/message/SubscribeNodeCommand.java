package no.ntnu.message;

import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.subcribers.NodeSubscriber;

public record SubscribeNodeCommand(int nodeId) implements NodeSubscriptionCommand {
    public static final String PREFIX = "sub";

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

        return new SubscribeNodeCommand(
                Integer.parseInt(parameterizer.getNodeId())
        );
    }
}
