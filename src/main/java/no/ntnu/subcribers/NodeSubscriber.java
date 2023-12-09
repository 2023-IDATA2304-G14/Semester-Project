package no.ntnu.subcribers;

import no.ntnu.greenhouse.GreenhouseNode;
import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.listeners.greenhouse.SensorListener;

import java.util.ArrayList;
import java.util.List;

public interface NodeSubscriber extends ActuatorListener, SensorListener {

    List<GreenhouseNode> getSubscribedNodes();

    /**
     * Add a node to the list of nodes that this subscriber is subscribed to.
     * @param node The node to add.
     */
    default void addNodeToSubscribers(GreenhouseNode node) {
        if (!getSubscribedNodes().contains(node)) {
            getSubscribedNodes().add(node);
        }
    }

    /**
     * Remove a node from the list of nodes that this subscriber is subscribed to.
     * @param node The node to remove.
     */
    default void removeNodeFromSubscribers(GreenhouseNode node) {
        getSubscribedNodes().remove(node);
    }
}
