package no.ntnu.message;

public record ActuatorUpdatedMessage(int nodeId, int actuatorId) {
    public ActuatorUpdatedMessage {
        if (nodeId < 0 || actuatorId < 0) {
            throw new IllegalArgumentException("Node ID and Actuator ID must be non-negative.");
        }
    }
}
