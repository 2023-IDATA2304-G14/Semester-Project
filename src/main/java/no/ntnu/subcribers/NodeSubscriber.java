package no.ntnu.subcribers;

import no.ntnu.greenhouse.GreenhouseNode;
import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.listeners.greenhouse.SensorListener;

import java.util.ArrayList;
import java.util.List;

public abstract class NodeSubscriber implements ActuatorListener, SensorListener {
    private final List<GreenhouseNode> nodeSubscribers = new ArrayList<>();

    public void addNodeToSubscribers(GreenhouseNode node) {
        nodeSubscribers.add(node);
    }

    public void removeNodeFromSubscribers(GreenhouseNode node) {
        nodeSubscribers.remove(node);
    }
}
