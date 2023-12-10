package no.ntnu.gui.controlpanel;

import no.ntnu.controlpanel.SensorActuatorNodeInfo;
import no.ntnu.listeners.controlpanel.GreenhouseEventListener;

// TODO: Check if this is the correct listener
public class ControlPanelModel implements GreenhouseEventListener {

    /**
     * This event is fired when a new node is added to the greenhouse.
     *
     * @param nodeInfo Information about the added node
     */
    @Override
    public void onNodeUpdated(SensorActuatorNodeInfo nodeInfo) {
        // TODO: Implement
    }

    /**
     * This event is fired when a node is removed from the greenhouse.
     *
     * @param nodeId ID of the node which has disappeared (removed)
     */
    @Override
    public void onNodeRemoved(int nodeId) {
        // TODO: Implement
    }

    /**
     * This event is fired when new sensor data is received from a node.
     *
     * @param nodeId  ID of the node
     * @param sensorId ID of the sensor
     * @param value   The new value of the sensor
     */
    @Override
    public void onSensorDataChanged(int nodeId, int sensorId, double value) {
        // TODO: Implement
    }

    /**
     * This event is fired when a sensor changes state or is added to the greenhouse.
     *
     * @param nodeId   ID of the node to which the sensor is attached
     * @param sensorId ID of the sensor
     * @param type
     * @param value    The new value of the sensor
     * @param min      The minimum value of the sensor
     * @param max      The maximum value of the sensor
     * @param unit     The unit of the sensor
     */
    @Override
    public void onSensorStateChanged(int nodeId, int sensorId, String type, double value, double min, double max, String unit) {
//        TODO: Implement
    }

    /**
     * This event is fired when an actuator changes state.
     *
     * @param nodeId     ID of the node to which the actuator is attached
     * @param actuatorId ID of the actuator
     * @param isOn       When true, actuator is on; off when false.
     */
    @Override
    public void onActuatorDataChanged(int nodeId, int actuatorId, boolean isOn, int strength) {
        // TODO: Implement
    }

    /**
     * This event is fired when an actuator changes state.
     *
     * @param nodeId      ID of the node to which the actuator is attached
     * @param actuatorId  ID of the actuator
     * @param isOn        When true, actuator is on; off when false.
     * @param strength    Strength of the actuator
     * @param minStrength Minimum strength of the actuator
     * @param maxStrength Maximum strength of the actuator
     * @param unit        Unit of the actuator
     */
    @Override
    public void onActuatorStateChanged(int nodeId, int actuatorId, String type, boolean isOn, int strength, int minStrength, int maxStrength, String unit) {
//        TODO: Implement
    }

    /**
     * This event is fired when an actuator is removed from the greenhouse.
     *
     * @param nodeId     ID of the node to which the actuator is attached
     * @param actuatorId ID of the actuator
     */
    @Override
    public void onActuatorRemoved(int nodeId, int actuatorId) {
//        TODO: Implement
    }
}
