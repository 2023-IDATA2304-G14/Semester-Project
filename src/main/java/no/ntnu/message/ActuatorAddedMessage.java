package no.ntnu.message;

public class ActuatorAddedMessage {
    private int nodeId;
    private int actuatorId;

    public ActuatorAddedMessage(int nodeId, int actuatorId) {
        this.nodeId = nodeId;
        this.actuatorId = actuatorId;
    }

    public int getNodeId() {
        return nodeId;
    }

    public int getActuatorId() {
        return actuatorId;
    }
}
