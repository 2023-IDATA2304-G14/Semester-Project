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
   * Get the sensor reading for a given sensor.
   * @param nodeId The ID of the node to which the sensor is attached
   * @param sensorId The ID of the sensor
   */
  void getSensorReading(int nodeId, int sensorId);

  /**
   * Open the communication channel.
   *
   * @return True when the communication channel is successfully opened, false on error
   */
  boolean open();

  /**
   * Close the communication channel.
   */
  void close();

}
