package no.ntnu.message;

import no.ntnu.greenhouse.GreenhouseNode;
import no.ntnu.greenhouse.SensorReading;

public class GetSensorReadingCommand implements GetCommand {
    @Override
    public Message execute(GreenhouseNode logic, int nodeId) {
        Message response;
        try {
            SensorReading reading = logic.getSensor(nodeId).getReading();
            response = new GetSensorReadingMessage(reading);
        } catch (IllegalStateException e) {
            response = new ErrorMessage(e.getMessage());
        }
}
