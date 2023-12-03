package no.ntnu.gui.controlpanel;

import no.ntnu.controlpanel.SensorActuatorNodeInfo;
import no.ntnu.greenhouse.SensorReading;
import no.ntnu.listeners.controlpanel.GreenhouseEventListener;

import java.util.List;

// TODO: Check if this is the correct listener
public class ControlPanelModel implements GreenhouseEventListener {

    /**
     * This event is fired when a new node is added to the greenhouse.
     *
     * @param nodeInfo Information about the added node
     */
    @Override
    public void onNodeAdded(SensorActuatorNodeInfo nodeInfo) {
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
     * @param sensors List of all current sensor values
     */
    @Override
    public void onSensorData(int nodeId, List<SensorReading> sensors) {
        // TODO: Implement
    }

    /**
     * This event is fired when an actuator changes state.
     *
     * @param nodeId     ID of the node to which the actuator is attached
     * @param actuatorId ID of the actuator
     * @param isOn       When true, actuator is on; off when false.
     */
    @Override
    public void onActuatorStateChanged(int nodeId, int actuatorId, boolean isOn) {
        // TODO: Implement
    }
}
