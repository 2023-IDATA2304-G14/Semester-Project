package no.ntnu.controlpanel;

/**
 * A communication channel for disseminating control commands to the sensor nodes
 * (sending commands to the server) and receiving notifications about events.
 * Your socket class on the control panel side should implement this.
 */
public interface CommunicationChannel {
  /**
   * Request that state of an actuator is changed.
   *
   * @param nodeId     ID of the node to which the actuator is attached
   * @param actuatorId Node-wide unique ID of the actuator
   * @param isOn       When true, actuator must be turned on; off when false.
   * @param strength   Strength of the actuator. Different actuators may have different strength levels.
   */
  void sendActuatorChange(int nodeId, int actuatorId, boolean isOn, int strength);

  /**
   * Request a list of all actuators on a node.
   */
  void getActuators(int nodeId);

  /**
   * Request a list of all sensors on a node.
   */
  void getSensors(int nodeId);

  /**
   * Request a list of all nodes.
   */
  void getNodes();

  /**
   * Get the actuator data for a given actuator.
   * @param nodeId The ID of the node to which the actuator is attached
   * @param actuatorId The ID of the actuator
   */
  void getActuatorData(int nodeId, int actuatorId);

  /**
   * Get the current state of the actuator.
   * This method is called when the control panel starts up, to get the current state of the
   * actuators.
   * @param nodeId The ID of the node to which the actuator is attached
   * @param actuatorId The ID of the actuator
   */
  void getActuatorState(int nodeId, int actuatorId);

  /**
   * Gets the current state of the sensor.
   * @param nodeId The ID of the node to which the sensor is attached
   * @param sensorId The ID of the sensor
   */
  void getSensorState(int nodeId, int sensorId);

  /**
   * Get the sensor data for a given sensor.
   * @param nodeId The ID of the node to which the sensor is attached
   * @param sensorId The ID of the sensor
   */
  void getSensorData(int nodeId, int sensorId);

  /**
   * Subscribe to data for a node.
   * @param nodeId The ID of the node to which the sensor is attached
   */
  void subscribeToNode(int nodeId);

  /**
   * Unsubscribe to data for a node.
   * @param nodeId The ID of the node to which the sensor is attached
   */
  void unsubscribeToNode(int nodeId);

  /**
   * Close the communication channel.
   */
  void close();

}
