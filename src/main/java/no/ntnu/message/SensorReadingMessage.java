package no.ntnu.message;

import no.ntnu.greenhouse.SensorReading;

public record SensorReadingMessage(int nodeId, int sensorId, SensorReading reading) implements BroadcastMessage {
}
