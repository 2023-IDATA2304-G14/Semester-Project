package no.ntnu.listeners.common;

/**
 * Listener for node events.
 * This could be used both on the sensor/actuator (greenhouse) side, as wall as
 * on the control panel side.
 */
public interface NodeListener {

  /**
   * An event that is fired every time an actuator is added to a node.
   * @param nodeId ID of the node on which this actuator is placed
   * @param actuatorId ID of the actuator that has been added
   */
  void actuatorAdded(int nodeId, int actuatorId);

  /**
   * An event that is fired every time an actuator is removed from a node.
   * @param nodeId ID of the node on which this actuator is placed
   * @param actuatorId ID of the actuator that has been removed
   */
  void actuatorRemoved(int nodeId, int actuatorId);

  /**
   * An event that is fired every time a sensor is added to a node.
   * @param nodeId ID of the node on which this sensor is placed
   * @param sensorId ID of the sensor that has been added
   */
  void sensorAdded(int nodeId, int sensorId);

  /**
   * An event that is fired every time a sensor is removed from a node.
   * @param nodeId ID of the node on which this sensor is placed
   * @param sensorId ID of the sensor that has been removed
   */
  void sensorRemoved(int nodeId, int sensorId);
}
