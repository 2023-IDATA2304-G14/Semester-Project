package no.ntnu.message;


public record ActuatorReadingMessage(int nodeId, int actuatorId, int reading) implements BroadcastMessage {
}
