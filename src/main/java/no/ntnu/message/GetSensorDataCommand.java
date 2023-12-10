package no.ntnu.message;

import no.ntnu.greenhouse.GreenhouseNode;
import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.greenhouse.SensorReading;

public record GetSensorDataCommand(int nodeId, int sensorId) implements GetCommand {
    public static final String PREFIX = "gSd";
    @Override
    public Message execute(GreenhouseSimulator logic) {
        Message response;
        try {
            GreenhouseNode node = logic.getNode(nodeId);
            SensorReading reading = node.getSensor(sensorId).getReading();
            response = new SensorDataMessage(nodeId, sensorId, reading.getValue(), reading.getUnit(), reading.getType());
        } catch (IllegalStateException e) {
            response = new ErrorMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public String getPrefix() {
        return PREFIX;
    }

    @Override
    public String serialize() {
        return new MessageParameterizer(PREFIX)
                .setNodeId(String.valueOf(nodeId()))
                .setItemId(String.valueOf(sensorId()))
                .parameterize();
    }

    @Override
    public Message deserialize(String message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        MessageParameterizer parameterizer = new MessageParameterizer(PREFIX).deparameterize(message);

        return new GetSensorDataCommand(
                Integer.parseInt(parameterizer.getNodeId()),
                Integer.parseInt(parameterizer.getItemId())
        );
    }
}
