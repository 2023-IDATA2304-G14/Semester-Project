package no.ntnu.controlpanel;

import no.ntnu.message.GetActuatorStateCommand;
import no.ntnu.message.GetSensorStateCommand;
import no.ntnu.message.SetActuatorCommand;

public class ControlPanelChannel implements CommunicationChannel {

  private final ControlPanelLogic logic;
  private final ControlPanelClient client;

  public ControlPanelChannel(ControlPanelLogic logic) {
    this.logic = logic;
    this.client = new ControlPanelClient(this);
  }

  public ControlPanelChannel(ControlPanelLogic logic, String host, int port) {
    this.logic = logic;
    this.client = new ControlPanelClient(host, port, this);
  }
  /**
   * Request that state of an actuator is changed.
   *
   * @param nodeId     ID of the node to which the actuator is attached
   * @param actuatorId Node-wide unique ID of the actuator
   * @param isOn       When true, actuator must be turned on; off when false.
   * @param strength   Strength of the actuator. Different actuators may have different strength levels.
   */
  @Override
  public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn, int strength) {
    client.sendCommand(new SetActuatorCommand(nodeId, actuatorId, isOn, strength));
  }

  /**
   * Get the current state of the actuator.
   * This method is called when the control panel starts up, to get the current state of the
   * actuators.
   *
   * @param nodeId     The ID of the node to which the actuator is attached
   * @param actuatorId The ID of the actuator
   */
  @Override
  public void getActuatorState(int nodeId, int actuatorId) {
    client.sendCommand(new GetActuatorStateCommand(nodeId, actuatorId));
  }

  /**
   * Gets the current state of the sensor.
   *
   * @param nodeId   The ID of the node to which the sensor is attached
   * @param sensorId The ID of the sensor
   */
  @Override
  public void getSensorState(int nodeId, int sensorId) {
    client.sendCommand(new GetSensorStateCommand(nodeId, sensorId));
  }

  /**
   * Get the sensor reading for a given sensor.
   *
   * @param nodeId   The ID of the node to which the sensor is attached
   * @param sensorId The ID of the sensor
   */
  @Override
  public void getSensorReading(int nodeId, int sensorId) {

  }

  /**
   * Open the communication channel.
   *
   * @return True when the communication channel is successfully opened, false on error
   */
  @Override
  public boolean open() {
    return false;
  }

  /**
   * Close the communication channel.
   */
  @Override
  public void close() {
    client.stopClient();
  }

  /**
   * Called when the reading of an actuator has changed.
   *
   * @param nodeId     The ID of the node to which the actuator is attached
   * @param actuatorId The ID of the actuator
   * @param on         True if the actuator is on, false otherwise
   * @param strength   The strength of the actuator
   */
  @Override
  public void onActuatorReadingChanged(int nodeId, int actuatorId, boolean on, int strength) {
    logic.onActuatorDataChanged(nodeId, actuatorId, on, strength);
  }

  /**
   * Called when an actuator has been removed from the node.
   *
   * @param nodeId     The ID of the node to which the actuator was attached
   * @param actuatorId The ID of the actuator
   */
  @Override
  public void onActuatorRemoved(int nodeId, int actuatorId) {
    logic.onActuatorRemoved(nodeId, actuatorId);
  }
}
