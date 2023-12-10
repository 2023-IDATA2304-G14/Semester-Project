package no.ntnu.controlpanel;

import no.ntnu.message.*;

public class ControlPanelChannel implements CommunicationChannel {

  private final ControlPanelLogic logic;
  private final ControlPanelClient client;

  public ControlPanelChannel(ControlPanelLogic logic) {
    this.logic = logic;
    this.client = new ControlPanelClient(logic);
  }

  public ControlPanelChannel(ControlPanelLogic logic, String host, int port) {
    this.logic = logic;
    this.client = new ControlPanelClient(host, port, logic);
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
   * Request a list of all actuators on a node.
   *
   * @param nodeId
   */
  @Override
  public void getActuators(int nodeId) {
    client.sendCommand(new GetActuatorsCommand(nodeId));
  }

  /**
   * Request a list of all sensors on a node.
   *
   * @param nodeId
   */
  @Override
  public void getSensors(int nodeId) {
    client.sendCommand(new GetSensorsCommand(nodeId));
  }

  /**
   * Request a list of all nodes.
   */
  @Override
  public void getNodes() {
    client.sendCommand(new GetNodesCommand());
  }

  /**
   * Get the actuator data for a given actuator.
   *
   * @param nodeId     The ID of the node to which the actuator is attached
   * @param actuatorId The ID of the actuator
   */
  @Override
  public void getActuatorData(int nodeId, int actuatorId) {
    client.sendCommand(new GetActuatorDataCommand(nodeId, actuatorId));
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
  public void getSensorData(int nodeId, int sensorId) {
    client.sendCommand(new GetSensorDataCommand(nodeId, sensorId));
  }

  /**
   * Subscribe to data for a node.
   *
   * @param nodeId The ID of the node to which the sensor is attached
   */
  @Override
  public void subscribeToNode(int nodeId) {
    client.sendCommand(new SubscribeNodeCommand(nodeId));
  }

  /**
   * Unsubscribe to data for a node.
   *
   * @param nodeId The ID of the node to which the sensor is attached
   */
  @Override
  public void unsubscribeToNode(int nodeId) {
    client.sendCommand(new UnsubscribeNodeCommand(nodeId));
  }

  /**
   * Close the communication channel.
   */
  @Override
  public void close() {
    client.stopClient();
  }
}
