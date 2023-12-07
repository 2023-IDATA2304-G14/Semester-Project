package no.ntnu.message;

public class ActuatorRemoveMessage {
    private int nodeId;
    private int actuatorId;

    public ActuatorRemoveMessage(int nodeId, int actuatorId) {
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
