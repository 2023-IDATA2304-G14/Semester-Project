package no.ntnu.message;

import no.ntnu.greenhouse.GreenhouseNode;
import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.greenhouse.SensorReading;

public class GetSensorReadingCommand implements GetCommand {
    private final int nodeId;
    private final int sensorId;

    public GetSensorReadingCommand(int nodeId, int sensorId) {
        this.nodeId = nodeId;
        this.sensorId = sensorId;
    }

    public int getNodeId() {
        return nodeId;
    }

    public int getSensorId() {
        return sensorId;
    }
    @Override
    public Message execute(GreenhouseSimulator logic) {
        Message response;
        try {
            GreenhouseNode node = logic.getNode(nodeId);
            SensorReading reading = node.getSensor(sensorId).getReading();
            response = new SensorReadingMessage(nodeId, sensorId, reading);
        } catch (IllegalStateException e) {
            response = new ErrorMessage(e.getMessage());
        }
        return response;
    }
}
