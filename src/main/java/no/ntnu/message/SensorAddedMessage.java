package no.ntnu.message;

public class SensorAddedMessage {
    private int nodeId;
    private int sensorId;

    public SensorAddedMessage(int nodeId, int sensorId) {
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
