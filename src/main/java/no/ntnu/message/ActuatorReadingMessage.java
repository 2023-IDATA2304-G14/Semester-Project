package no.ntnu.message;


public record ActuatorReadingMessage(int nodeId, int actuatorId, boolean isOn, int strength) implements Message {
    public static final String PREFIX = "aD";

    @Override
    public String getPrefix() {
        return PREFIX;
    }

    @Override
    public String serialize() {
        return new MessageParameterizer(PREFIX)
                .setNodeId(String.valueOf(nodeId()))
                .setItemId(String.valueOf(actuatorId()))
                .setValue(String.valueOf(isOn()))
                .setSecondaryValue(String.valueOf(strength()))
                .parameterize();
    }

    @Override
    public Message deserialize(String input) throws IllegalArgumentException {
        if (input == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        MessageParameterizer parameterizer = new MessageParameterizer(PREFIX).deparameterize(input);

        return new ActuatorReadingMessage(
                Integer.parseInt(parameterizer.getNodeId()),
                Integer.parseInt(parameterizer.getItemId()),
                Boolean.parseBoolean(parameterizer.getValue()),
                Integer.parseInt(parameterizer.getSecondaryValue())
        );
    }
}
