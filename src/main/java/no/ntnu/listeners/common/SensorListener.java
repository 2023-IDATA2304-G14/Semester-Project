package no.ntnu.listeners.common;

import no.ntnu.greenhouse.Sensor;

/**
 * Listener for sensor update events.
 */
public interface SensorListener {
  /**
   * An event that is fired every time the sensor data is updated.
   *
   * @param sensor The sensor that has new data
   */
  void sensorDataUpdated(Sensor sensor);
}
