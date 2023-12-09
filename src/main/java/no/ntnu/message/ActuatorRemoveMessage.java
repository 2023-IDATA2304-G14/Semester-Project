package no.ntnu.message;

public record ActuatorRemoveMessage(int nodeId, int actuatorId) {
    public ActuatorRemoveMessage {
        if (nodeId < 0 || actuatorId < 0) {
            throw new IllegalArgumentException("Node ID and Actuator ID must be non-negative.");
        }
    }
}
