package no.ntnu.controlpanel;

import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.ActuatorCollection;
import no.ntnu.greenhouse.Sensor;
import no.ntnu.greenhouse.SensorCollection;

/**
 * Contains information about one sensor/actuator node. This is NOT the node itself, rather
 * an information that can be used on the control-panel side to represent the node.
 */
public class GreenhouseNodeInfo {

  private final int nodeId;
  private final ActuatorCollection actuators = new ActuatorCollection();
  private final SensorCollection sensors = new SensorCollection();

  public GreenhouseNodeInfo(int nodeId) {
    this.nodeId = nodeId;
  }


  /**
   * Get ID of the node.
   *
   * @return The unique ID of the node
   */
  public int getId() {
    return nodeId;
  }

  /**
   * Add a sensor to the node.
   * @param sensor The sensor to add
   */
  public void addSensor(Sensor sensor) {
    sensors.add(sensor);
  }

  /**
   * Remove a sensor from the node.
   * @param sensorId The ID of the sensor to remove
   */
  public void removeSensor(int sensorId) {
    sensors.remove(sensorId);
  }

  /**
   * Get all the sensors of the sensor/actuator node.
   *
   * @return The sensor collection
   */
  public SensorCollection getSensors() {
    return sensors;
  }

  /**
   * Get the sensor with the given id.
   *
   * @param sensorId ID of the sensor
   * @return The sensor or null if none found
   */
  public Sensor getSensor(int sensorId) {
    return sensors.get(sensorId);
  }

  /**
   * Add an actuator to the node.
   * @param actuator The actuator to add
   */
  public void addActuator(Actuator actuator) {
    actuators.add(actuator);
  }

  /**
   * Get all the actuators of the sensor/actuator node.
   *
   * @return The actuator collection
   */
  public ActuatorCollection getActuators() {
    return actuators;
  }

  /**
   * Get an actuator with the given id.
   *
   * @param actuatorId ID of the actuator
   * @return The actuator or null if none found
   */
  public Actuator getActuator(int actuatorId) {
    return actuators.get(actuatorId);
  }
}
