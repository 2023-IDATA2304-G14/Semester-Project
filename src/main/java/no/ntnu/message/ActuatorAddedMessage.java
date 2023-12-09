package no.ntnu.message;

public record ActuatorAddedMessage(int nodeId, int actuatorId) {
    public ActuatorAddedMessage {
        if (nodeId < 0 || actuatorId < 0) {
            throw new IllegalArgumentException("Node ID and Actuator ID must be non-negative.");
        }
    }
}
