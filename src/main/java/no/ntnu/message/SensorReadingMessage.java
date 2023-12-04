package no.ntnu.message;

import no.ntnu.greenhouse.SensorReading;

public record SensorReadingMessage(SensorReading reading, int nodeId, int sensorId) implements BroadcastMessage {
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

    public int getSensorId() {
        return sensorId;
    }
}
