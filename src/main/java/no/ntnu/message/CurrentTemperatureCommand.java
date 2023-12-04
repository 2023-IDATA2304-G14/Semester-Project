package no.ntnu.message;

import no.ntnu.greenhouse.SensorActuatorNode;

/**
 * A command requesting to know the current temperature in the greenhouse.
 */
public class CurrentTemperatureCommand {
    @Override
    public Message execute(SensorActuatorNode logic) {
        Message response;
        try {
            int temperature = logic.getCurrentTemperature();
            response = new CurrentTemperatureMessage();
        } catch (Exception e) {
            response = new ErrorMessage(e.getMessage());
        }
        return response;
    }
}
