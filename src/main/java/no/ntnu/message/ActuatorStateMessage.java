package no.ntnu.message;

public record ActuatorStateMessage(int nodeId, int actuatorId, boolean on, int strength, int minStrength, int maxStrength, String unit, String type) implements StateMessage {
    public static final String PREFIX = "aS";

    @Override
    public String getPrefix() {
        return PREFIX;
    }

    @Override
    public String serialize() {
        return new MessageParameterizer(PREFIX)
                .setNodeId(String.valueOf(nodeId()))
                .setItemId(String.valueOf(actuatorId()))
                .setValue(String.valueOf(on()))
                .setSecondaryValue(String.valueOf(strength()))
                .setMax(String.valueOf(maxStrength()))
                .setMin(String.valueOf(minStrength()))
                .setUnit(unit())
                .setType(type())
                .parameterize();
    }

    @Override
    public Message deserialize(String message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        MessageParameterizer parameterizer = new MessageParameterizer(PREFIX).deparameterize(message);

        return new ActuatorStateMessage(
                Integer.parseInt(parameterizer.getNodeId()),
                Integer.parseInt(parameterizer.getItemId()),
                Boolean.parseBoolean(parameterizer.getValue()),
                Integer.parseInt(parameterizer.getSecondaryValue()),
                Integer.parseInt(parameterizer.getMin()),
                Integer.parseInt(parameterizer.getMax()),
                parameterizer.getUnit(),
                parameterizer.getType()
        );
    }
}
