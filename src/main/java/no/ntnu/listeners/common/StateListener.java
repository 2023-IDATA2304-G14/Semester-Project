package no.ntnu.listeners.common;

import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.Sensor;

public interface StateListener {
  /**
   * An event that is fired every time an actuator changes state or is added.
   *
   * @param actuator The actuator that has changed its state
   */
  void actuatorStateUpdated(Actuator actuator);

  /**
   * An event that is fired every time a sensor changes state or is added.
   *
   * @param sensor The sensor that has changed its state
   */
  void sensorStateUpdated(Sensor sensor);
}
