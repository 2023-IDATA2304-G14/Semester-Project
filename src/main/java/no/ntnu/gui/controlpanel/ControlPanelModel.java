package no.ntnu.gui.controlpanel;

import no.ntnu.controlpanel.GreenhouseNodeInfo;
import no.ntnu.listeners.controlpanel.GreenhouseEventListener;

// TODO: Check if this is the correct listener

/**
 * The model of the control panel. It is responsible for handling events from the server and
 * notifying the view (GUI) about changes.
 */
public class ControlPanelModel implements GreenhouseEventListener {

    /**
     * Test class to
     */
    public void configure(){

    }


    /**
     * This event is fired when a new node is added to the greenhouse.
     *
     * @param nodeInfo Information about the added node
     */
    @Override
    public void onNodeUpdated(GreenhouseNodeInfo nodeInfo) {
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
     * @param type     The type of the sensor
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

    /**
     * This event is fired when a node is changed or added to the greenhouse.
     *
     * @param nodeId ID of the node
     * @param name   Name of the node
     */
    @Override
    public void onNodeStateChanged(int nodeId, String name) {
//        TODO: Implement
    }

    /**
     * This event is fired when a node is removed from the greenhouse.
     *
     * @param nodeId   ID of the node
     * @param sensorId ID of the sensor
     */
    @Override
    public void onSensorRemoved(int nodeId, int sensorId) {
//        TODO: Implement
    }

    /**
     * This event is fired when the client has successfully subscribed to a node.
     *
     * @param nodeId ID of the node to which the client has subscribed
     */
    @Override
    public void onSubscribeNode(int nodeId) {
//        TODO: Implement
    }

    /**
     * This event is fired when the client has successfully unsubscribed from a node.
     *
     * @param nodeId ID of the node from which the client has unsubscribed
     */
    @Override
    public void onUnsubscribeNode(int nodeId) {
//        TODO: Implement
    }

    /**
     * This event is fired when an error message is received from the server.
     *
     * @param message The error message
     */
    @Override
    public void onErrorReceived(String message) {
//        TODO: Implement
    }

    /**
     * This event is fired when an error message for an unknown message is received from the server.
     *
     * @param message The unknown message
     */
    @Override
    public void onUnknownMessageReceived(String message) {
//        TODO: Implement
    }
}
