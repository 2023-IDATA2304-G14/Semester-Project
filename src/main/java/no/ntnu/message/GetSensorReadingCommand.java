package no.ntnu.message;

import no.ntnu.greenhouse.GreenhouseNode;
import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.greenhouse.SensorReading;

public record GetSensorReadingCommand(int nodeId, int sensorId) implements GetCommand {
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
