package no.ntnu.message;

import no.ntnu.greenhouse.GreenhouseNode;
import no.ntnu.greenhouse.SensorReading;

public class GetSensorReadingCommand implements GetCommand {
    private final int nodeId;

    public GetSensorReadingCommand(int nodeId) {
        this.nodeId = nodeId;
    }
    @Override
    public Message execute(GreenhouseNode logic) {
        Message response;
        try {
            SensorReading reading = logic.getSensor(nodeId).getReading();
            response = new GetSensorReadingMessage(reading, nodeId);
        } catch (IllegalStateException e) {
            response = new ErrorMessage(e.getMessage());
        }
        return response;
    }
}
