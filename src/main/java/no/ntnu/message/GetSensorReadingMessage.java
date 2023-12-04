package no.ntnu.message;

import no.ntnu.greenhouse.SensorReading;

public record GetSensorReadingMessage(SensorReading reading, int nodeId) implements BroadcastMessage {
    /**
     * Get the sensor reading from the message.
     *
     * @return the reading.
     */
    public SensorReading getReading() {
        return reading;
    }

    public int getNodeId() {
        return nodeId;
    }
}
