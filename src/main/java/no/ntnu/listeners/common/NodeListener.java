package no.ntnu.listeners.common;

import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.Sensor;

/**
 * Listener for node events.
 * This could be used both on the sensor/actuator (greenhouse) side, as wall as
 * on the control panel side.
 */
public interface NodeListener {

  /**
   * An event that is fired every time an actuator is removed from a node.
   * @param actuator The actuator that has been removed
   */
  void actuatorRemoved(Actuator actuator);

  /**
   * An event that is fired every time a sensor is removed from a node.
   * @param sensor The sensor that has been removed
   */
  void sensorRemoved(Sensor sensor);

}
