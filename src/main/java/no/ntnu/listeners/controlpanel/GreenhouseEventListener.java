package no.ntnu.listeners.controlpanel;

import no.ntnu.controlpanel.GreenhouseNodeInfo;

/**
 * Listener of events happening "inside a greenhouse", such as a node appearing, disappearing,
 * new sensor readings, etc.
 * While the name can be misleading, this interface will actually be usable on the
 * control-panel side, not the greenhouse side.
 * The idea is that a control panel can get events when some new information is received
 * about some changes in a greenhouse.
 */
public interface GreenhouseEventListener {

  /**
   * This event is fired when new sensor data is received from a node.
   *
   * @param nodeId  ID of the node
   * @param sensorId ID of the sensor
   * @param value   The new value of the sensor
   */
  void onSensorDataChanged(int nodeId, int sensorId, double value);

  /**
   * This event is fired when a sensor changes state or is added to the greenhouse.
   *
   * @param nodeId ID of the node to which the sensor is attached
   * @param sensorId ID of the sensor
   * @param value   The new value of the sensor
   * @param min   The minimum value of the sensor
   * @param max   The maximum value of the sensor
   * @param unit   The unit of the sensor
   */
  void onSensorStateChanged(int nodeId, int sensorId, String type, double value, double min, double max, String unit);

  /**
   * This event is fired when an actuator changes state.
   *
   * @param nodeId ID of the node to which the actuator is attached
   * @param actuatorId ID of the actuator
   * @param isOn  When true, actuator is on; off when false.
   * @param strength  Strength of the actuator
   */
  void onActuatorDataChanged(int nodeId, int actuatorId, boolean isOn, int strength);

  /**
   * This event is fired when an actuator changes state or is added to the greenhouse.
   *
   * @param nodeId ID of the node to which the actuator is attached
   * @param actuatorId ID of the actuator
   * @param isOn  When true, actuator is on; off when false.
   * @param strength  Strength of the actuator
   * @param minStrength  Minimum strength of the actuator
   * @param maxStrength  Maximum strength of the actuator
   * @param unit  Unit of the actuator
   */
    void onActuatorStateChanged(int nodeId, int actuatorId, String type, boolean isOn, int strength, int minStrength, int maxStrength, String unit);

  /**
   * This event is fired when an actuator is removed from the greenhouse.
   * @param nodeId ID of the node to which the actuator is attached
   * @param actuatorId ID of the actuator
   */
  void onActuatorRemoved(int nodeId, int actuatorId);

  /**
   * This event is fired when a node is changed or added to the greenhouse.
   * @param nodeId ID of the node
   * @param name Name of the node
   */
  void onNodeStateChanged(int nodeId, String name);

  /**
   * This event is fired when a node is removed from the greenhouse.
   * @param nodeId ID of the node
   * @param sensorId ID of the sensor
   */
  void onSensorRemoved(int nodeId, int sensorId);

  /**
   * This event is fired when the client has successfully subscribed to a node.
   * @param nodeId ID of the node to which the client has subscribed
   */
  void onSubscribeNode(int nodeId);

  /**
   * This event is fired when the client has successfully unsubscribed from a node.
   * @param nodeId ID of the node from which the client has unsubscribed
   */
  void onUnsubscribeNode(int nodeId);

  /**
   * This event is fired when an error message is received from the server.
   * @param message The error message
   */
  void onErrorReceived(String message);

  /**
   * This event is fired when an error message for an unknown message is received from the server.
   * @param message The unknown message
   */
  void onUnknownMessageReceived(String message);

  /**
   * This event is fired when a node is removed from the greenhouse.
   * @param nodeId ID of the node
   */
  void onNodeRemoved(int nodeId);
}
