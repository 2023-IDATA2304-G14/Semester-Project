package no.ntnu.gui.controlpanel;

import no.ntnu.listeners.controlpanel.GreenhouseEventListener;

import java.util.HashMap;
import java.util.Map;

// TODO: Check if this is the correct listener

/**
 * The model of the control panel. It is responsible for handling events from the server and
 * notifying the view (GUI) about changes.
 */
public class ControlPanelModel implements GreenhouseEventListener {

    private Map<Integer, Map<Integer, Double>> nodeSensorData;
    private Map<Integer, Map<Integer, ActuatorInfo>> nodeActuatorData;

    public ControlPanelModel() {
        nodeSensorData = new HashMap<>();
        nodeActuatorData = new HashMap<>();
    }

    /**
     * Test class to
     */
    public void configure(){

    }

    /**
     * This event is fired when a node is removed from the greenhouse.
     *
     * @param nodeId ID of the node which has disappeared (removed)
     */
    @Override
    public void onNodeRemoved(int nodeId) {
        System.out.println("Node removed: " + nodeId);
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
        updateSensorData(nodeId, sensorId, value);
        System.out.println("Sensor data changed - Node ID: " + nodeId + ", Sensor ID: " + sensorId + ", New Value: " + value);
    }

    private void updateSensorData(int nodeId, int sensorId, double value) {
        nodeSensorData.putIfAbsent(nodeId, new HashMap<>());
        nodeSensorData.get(nodeId).put(sensorId, value);
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
        updateSensorData(nodeId, sensorId, value);
        System.out.println("Sensor state changed - Node ID: " + nodeId + ", Sensor ID: " + sensorId + ", Type: " + type + ", Value: " + value + ", Min: " + min + ", Max: " + max + ", Unit: " + unit);
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
    }

    private void updateActuatorState(int nodeId, int actuatorId, String type, boolean isOn, int strength, int minStrength, int maxStrength, String unit) {
        nodeActuatorData.putIfAbsent(nodeId, new HashMap<>());
        nodeActuatorData.get(nodeId).put(actuatorId, new ActuatorInfo(isOn, strength));
    }

    private class ActuatorInfo {
        private boolean isOn;
        private int strength;

        public ActuatorInfo(boolean isOn, int strength) {
            this.isOn = isOn;
            this.strength = strength;
        }
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
           updateActuatorState(nodeId, actuatorId, type, isOn, strength, minStrength, maxStrength, unit);
           System.out.println("Actuator state changed - Node ID: " + nodeId + ", Actuator ID: " + actuatorId + ", Type: " + type + ", State: " + (isOn ? "On" : "Off") + ", Strength: " + strength + ", Min Strength: " + minStrength + ", Max Strength: " + maxStrength + ", Unit: " + unit);
    }

    /**
     * This event is fired when an actuator is removed from the greenhouse.
     *
     * @param nodeId     ID of the node to which the actuator is attached
     * @param actuatorId ID of the actuator
     */
    @Override
    public void onActuatorRemoved(int nodeId, int actuatorId) {
        removeActuator(nodeId, actuatorId);
        System.out.println("Actuator removed - Node ID: " + nodeId + ", Actuator ID: " + actuatorId);
    }

    private void removeActuator(int nodeId, int actuatorId) {
        if (nodeActuatorData.containsKey(nodeId)) {
            nodeActuatorData.get(nodeId).remove(actuatorId);
        }
    }

    /**
     * This event is fired when a node is changed or added to the greenhouse.
     *
     * @param nodeId ID of the node
     * @param name   Name of the node
     */
    @Override
    public void onNodeStateChanged(int nodeId, String name) {
        updateNodeState(nodeId, name);
        System.out.println("Node state changed - Node ID: " + nodeId + ", Name: " + name);
    }

    private void updateNodeState(int nodeId, String name) {
        // TODO: Implement
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
        System.out.println("Received an unknown message " + message);
    }
}
