package no.ntnu.controlpanel;

import java.util.LinkedList;
import java.util.List;

import no.ntnu.greenhouse.Actuator;
import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.listeners.common.CommunicationChannelListener;
import no.ntnu.listeners.controlpanel.GreenhouseEventListener;
import no.ntnu.tools.Logger;

/**
 * The central logic of a control panel node. It uses a communication channel to send commands
 * and receive events. It supports listeners who will be notified on changes (for example, a new
 * node is added to the network, or a new sensor reading is received).
 * Note: this class may look like unnecessary forwarding of events to the GUI. In real projects
 * (read: "big projects") this logic class may do some "real processing" - such as storing events
 * in a database, doing some checks, sending emails, notifications, etc. Such things should never
 * be placed inside a GUI class (JavaFX classes). Therefore, we use proper structure here, even
 * though you may have no real control-panel logic in your projects.
 */
public class ControlPanelLogic implements GreenhouseEventListener, ActuatorListener,
        CommunicationChannelListener {
  private final List<GreenhouseEventListener> listeners = new LinkedList<>();

  private CommunicationChannel communicationChannel;
  private CommunicationChannelListener communicationChannelListener;

  /**
   * Set the channel over which control commands will be sent to sensor/actuator nodes.
   *
   * @param communicationChannel The communication channel, the event sender
   */
  public void setCommunicationChannel(CommunicationChannel communicationChannel) {
    this.communicationChannel = communicationChannel;
  }

  /**
   * Set listener which will get notified when communication channel is closed.
   *
   * @param listener The listener
   */
  public void setCommunicationChannelListener(CommunicationChannelListener listener) {
    this.communicationChannelListener = listener;
  }

  /**
   * Add an event listener.
   *
   * @param listener The listener who will be notified on all events
   */
  public void addListener(GreenhouseEventListener listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  @Override
  public void onNodeUpdated(GreenhouseNodeInfo nodeInfo) {
    listeners.forEach(listener -> listener.onNodeUpdated(nodeInfo));
  }

  @Override
  public void onNodeRemoved(int nodeId) {
    listeners.forEach(listener -> listener.onNodeRemoved(nodeId));
  }

  @Override
  public void onSensorDataChanged(int nodeId, int sensorId, double value) {
    listeners.forEach(listener -> listener.onSensorDataChanged(nodeId, sensorId, value));
  }

  /**
   * This event is fired when a sensor changes state or is added to the greenhouse.
   *
   * @param nodeId   ID of the node to which the sensor is attached
   * @param sensorId ID of the sensor
   * @param type     The type of the sensor
   * @param value    The new value of the sensor
   * @param min      The minimum value of the sensor
   * @param max      The maximum value of the sensor
   * @param unit     The unit of the sensor
   */
  @Override
  public void onSensorStateChanged(int nodeId, int sensorId, String type, double value, double min, double max, String unit) {

  }

  @Override
  public void onActuatorDataChanged(int nodeId, int actuatorId, boolean isOn, int strength) {
    listeners.forEach(listener -> listener.onActuatorDataChanged(nodeId, actuatorId, isOn, strength));
  }

  /**
   * This event is fired when an actuator changes state.
   *
   * @param nodeId      ID of the node to which the actuator is attached
   * @param actuatorId  ID of the actuator
   * @param isOn        When true, actuator is on; off when false.
   * @param strength    Strength of the actuator
   * @param minStrength Minimum strength of the actuator
   * @param maxStrength Maximum strength of the actuator
   * @param unit        Unit of the actuator
   */
  @Override
  public void onActuatorStateChanged(int nodeId, int actuatorId, String type, boolean isOn, int strength, int minStrength, int maxStrength, String unit) {
    listeners.forEach(listener -> listener.onActuatorStateChanged(nodeId, actuatorId, type, isOn, strength, minStrength, maxStrength, unit));
  }


  @Override
  public void onCommunicationChannelClosed() {
    Logger.info("Communication closed, updating logic...");
    if (communicationChannelListener != null) {
      communicationChannelListener.onCommunicationChannelClosed();
    }
  }

  public void onActuatorRemoved(int nodeId, int actuatorId) {
    listeners.forEach(listener -> listener.onActuatorRemoved(nodeId, actuatorId));
  }

  /**
   * This event is fired when a node is changed or added to the greenhouse.
   *
   * @param nodeId ID of the node
   * @param name   Name of the node
   */
  @Override
  public void onNodeStateChanged(int nodeId, String name) {
    listeners.forEach(listener -> listener.onNodeStateChanged(nodeId, name));
  }

  /**
   * This event is fired when a node is removed from the greenhouse.
   *
   * @param nodeId   ID of the node
   * @param sensorId ID of the sensor
   */
  @Override
  public void onSensorRemoved(int nodeId, int sensorId) {
    listeners.forEach(listener -> listener.onSensorRemoved(nodeId, sensorId));
  }

  /**
   * This event is fired when the client has successfully subscribed to a node.
   *
   * @param nodeId ID of the node to which the client has subscribed
   */
  @Override
  public void onSubscribeNode(int nodeId) {
    listeners.forEach(listener -> listener.onSubscribeNode(nodeId));
  }

  /**
   * This event is fired when the client has successfully unsubscribed from a node.
   *
   * @param nodeId ID of the node from which the client has unsubscribed
   */
  @Override
  public void onUnsubscribeNode(int nodeId) {
    listeners.forEach(listener -> listener.onUnsubscribeNode(nodeId));
  }

  /**
   * This event is fired when an error message is received from the server.
   *
   * @param message The error message
   */
  @Override
  public void onErrorReceived(String message) {
    listeners.forEach(listener -> listener.onErrorReceived(message));
  }

  /**
   * This event is fired when an error message for an unknown message is received from the server.
   *
   * @param message The unknown message
   */
  @Override
  public void onUnknownMessageReceived(String message) {
    listeners.forEach(listener -> listener.onUnknownMessageReceived(message));
  }

  /**
   * An event that is fired every time an actuator changes state.
   *
   * @param nodeId   ID of the node on which this actuator is placed
   * @param actuator The actuator that has changed its state
   */
  @Override
  public void actuatorUpdated(int nodeId, Actuator actuator) {
    if (communicationChannel != null) {
      communicationChannel.sendActuatorChange(nodeId, actuator.getId(), actuator.isOn(), actuator.getStrength());
    }
  }
}
