package no.ntnu.message;

public class SensorRemoveMessage {
    private int nodeId;
    private int sensorId;

    public SensorRemoveMessage(int nodeId, int sensorId) {
        this.nodeId = nodeId;
        this.sensorId = sensorId;
    }

    public int getNodeId() {
        return nodeId;
    }

    public int getSensorId() {
        return sensorId;
    }
}
