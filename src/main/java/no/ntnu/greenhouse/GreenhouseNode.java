package no.ntnu.greenhouse;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import no.ntnu.listeners.common.*;
import no.ntnu.listeners.greenhouse.NodeStateListener;
import no.ntnu.tools.Logger;

/**
 * Represents one greenhouse node with sensors and actuators.
 */
public class GreenhouseNode implements CommunicationChannelListener {
  // How often to generate new sensor values, in seconds.
  private static final long SENSING_DELAY = 5000;
  private final int id;
  private String name;

  private final SensorCollection sensors = new SensorCollection();
  private final ActuatorCollection actuators = new ActuatorCollection();

  private final List<SensorListener> sensorListeners = new LinkedList<>();
  private final List<ActuatorListener> actuatorListeners = new LinkedList<>();
  private final List<StateListener> stateListeners = new LinkedList<>();
  private final List<NodeStateListener> nodeStateListeners = new LinkedList<>();
  private final List<NodeListener> nodeListeners = new LinkedList<>();

  Timer sensorReadingTimer;

  private boolean running;
  private final Random random = new Random();

  /**
   * Create a sensor/actuator node. Note: the node itself does not check whether the ID is unique.
   * This is done at the greenhouse-level.
   *
   * @param id A unique ID of the node
   */
  public GreenhouseNode(int id, String name) {
    this.id = id;
    this.running = false;
    this.name = name;
  }

  /**
   * Get the unique ID of the node.
   *
   * @return the ID
   */
  public int getId() {
    return id;
  }

  /**
   * Get the name of the node.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Set the name of the node.
   *
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Add sensors to the node.
   *
   * @param template The template to use for the sensors. The template will be cloned.
   *                 This template defines the type of sensors, the value range, value
   *                 generation algorithms, etc.
   * @param n        The number of sensors to add to the node.
   */
  public void addSensors(Sensor template, int n) {

    if (template == null) {
      throw new IllegalArgumentException("Sensor template is missing");
    }
    String type = template.getType();
    if (type == null || type.isEmpty()) {
      throw new IllegalArgumentException("Sensor type missing");
    }
    if (n <= 0) {
      throw new IllegalArgumentException("Can't add a negative number of sensors");
    }

    for (int i = 0; i < n; ++i) {
      Sensor newSensor = template.createClone();
      newSensor.setListeners(sensorListeners);
      newSensor.setStateListeners(stateListeners);
      sensors.add(newSensor);
      Logger.info("Created " + newSensor.getType() + "[" + newSensor.getId() + "] on node " + id);
    }
  }

  /**
   * Add an actuator to the node.
   *
   * @param actuator The actuator to add
   */
  public void addActuator(Actuator actuator) {
    actuator.setListeners(actuatorListeners);
    actuator.setStateListeners(stateListeners);
    actuators.add(actuator);
    Logger.info("Created " + actuator.getType() + "[" + actuator.getId() + "] on node " + id);
  }

  /**
   * Register a new listener for sensor updates.
   *
   * @param listener The listener which will get notified every time sensor values change.
   */
  public void addSensorListener(SensorListener listener) {
    if (!sensorListeners.contains(listener)) {
      sensorListeners.add(listener);
    }
  }

  /**
   * Register a new listener for actuator updates.
   *
   * @param listener The listener which will get notified every time actuator state changes.
   */
  public void addActuatorListener(ActuatorListener listener) {
    if (!actuatorListeners.contains(listener)) {
      actuatorListeners.add(listener);
    }
  }

  /**
   * Register a new listener for sensor/actuator state updates.
   *
   * @param listener The listener which will get notified every time sensor/actuator state changes.
   */
  public void addStateListener(StateListener listener) {
    if (!stateListeners.contains(listener)) {
      stateListeners.add(listener);
    }
  }

  /**
   * Register a new listener for node state updates.
   *
   * @param listener The listener which will get notified when the state of this node changes
   */
  public void addNodeStateListener(NodeStateListener listener) {
    if (!nodeStateListeners.contains(listener)) {
      nodeStateListeners.add(listener);
    }
  }

  /**
   * Register a new listener for node updates.
   * @param listener The listener which will get notified when
   */
  public void addNodeListener(NodeListener listener) {
    if (!nodeListeners.contains(listener)) {
      nodeListeners.add(listener);
    }
  }

  /**
   * Start simulating the sensor node's operation.
   */
  public void start() {
    if (!running) {
      startPeriodicSensorReading();
      running = true;
      notifyStateChanges(true);
    }
  }

  /**
   * Stop simulating the sensor node's operation.
   */
  public void stop() {
    if (running) {
      Logger.info("-- Stopping simulation of node " + id);
      stopPeriodicSensorReading();
      running = false;
      notifyStateChanges(false);
    }
  }

  /**
   * Check whether the node is currently running.
   *
   * @return True if it is in a running-state, false otherwise
   */
  public boolean isRunning() {
    return running;
  }

  private void startPeriodicSensorReading() {
    sensorReadingTimer = new Timer();
    TimerTask newSensorValueTask = new TimerTask() {
      @Override
      public void run() {
        generateNewSensorValues();
      }
    };
    long randomStartDelay = random.nextLong(SENSING_DELAY);
    sensorReadingTimer.scheduleAtFixedRate(newSensorValueTask, randomStartDelay, SENSING_DELAY);
  }

  private void stopPeriodicSensorReading() {
    if (sensorReadingTimer != null) {
      sensorReadingTimer.cancel();
    }
  }

  /**
   * Generate new sensor values and send a notification to all listeners.
   */
  public void generateNewSensorValues() {
    Logger.infoNoNewline("Node #" + id);
    addRandomNoiseToSensors();
    notifySensorChanges();
    debugPrint();
  }

  private void addRandomNoiseToSensors() {
    for (Sensor sensor : sensors) {
      sensor.addRandomNoise();
    }
  }

  private void debugPrint() {
    for (Sensor sensor : sensors) {
      Logger.infoNoNewline(" " + sensor.getReading().getFormatted());
    }
    Logger.infoNoNewline(" :");
    actuators.debugPrint();
    Logger.info("");
  }

  /**
   * Toggle an actuator attached to this device.
   *
   * @param actuatorId The ID of the actuator to toggle
   * @throws IllegalArgumentException If no actuator with given configuration is found on this node
   */
  public void toggleActuator(int actuatorId) {
    Actuator actuator = getActuator(actuatorId);
    if (actuator == null) {
      throw new IllegalArgumentException("actuator[" + actuatorId + "] not found on node " + id);
    }
    actuator.toggle();
  }

  public Actuator getActuator(int actuatorId) {
    return actuators.get(actuatorId);
  }

  public Sensor getSensor(int sensorId) {
    return sensors.get(sensorId);
  }

  private void notifySensorChanges() {
    for (SensorListener listener : sensorListeners) {
      for (Sensor sensor : sensors) {
        listener.sensorDataUpdated(sensor);
      }
    }
  }

  private void notifyActuatorChange(Actuator actuator) {
    String onOff = actuator.isOn() ? "ON" : "off";
    Logger.info(" => " + actuator.getType() + " on node " + id + " " + onOff);
    for (ActuatorListener listener : actuatorListeners) {
      listener.actuatorDataUpdated(actuator);
    }
  }


  /**
   * Notify the listeners that the state of this node has changed.
   *
   * @param isReady When true, let them know that this node is ready;
   *                when false - that this node is shut down
   */
  private void notifyStateChanges(boolean isReady) {
    Logger.info("Notify state changes for node " + id);
    for (NodeStateListener listener : nodeStateListeners) {
      if (isReady) {
        listener.onNodeReady(this);
      } else {
        listener.onNodeStopped(this);
      }
    }
  }

  /**
   * An actuator has been turned on or off. Apply an impact from it to all sensors of given type.
   *
   * @param sensorType The type of sensors affected
   * @param impact     The impact to apply
   */
  public void applyActuatorImpact(String sensorType, double impact) {
    for (Sensor sensor : sensors) {
      if (sensor.getType().equals(sensorType)) {
        sensor.applyImpact(impact);
      }
    }
  }

  /**
   * Get all the sensors available on the device.
   *
   * @return List of all the sensors
   */
  public SensorCollection getSensors() {
    return sensors;
  }

  /**
   * Get all the actuators available on the node.
   *
   * @return A collection of the actuators
   */
  public ActuatorCollection getActuators() {
    return actuators;
  }

  @Override
  public void onCommunicationChannelClosed() {
    Logger.info("Communication channel closed for node " + id);
    stop();
  }

  /**
   * Set an actuator to a desired state.
   *
   * @param actuatorId ID of the actuator to set.
   * @param on         Whether it should be on (true) or off (false)
   */
  public void setActuator(int actuatorId, boolean on) {
    Actuator actuator = getActuator(actuatorId);
    if (actuator != null) {
      actuator.setOn(on);
    }
  }

  /**
   * Set all actuators to desired state.
   *
   * @param on Whether the actuators should be on (true) or off (false)
   */
  public void setAllActuators(boolean on) {
    for (Actuator actuator : actuators) {
      actuator.setOn(on);
    }
  }

  public void removeActuatorListener(ActuatorListener listener) {
    actuatorListeners.remove(listener);
  }

  public void removeSensorListener(ClientHandler subscriber) {
    sensorListeners.remove(subscriber);
  }

  public void removeActuator(int actuatorId) {
    Actuator actuator = actuators.get(actuatorId);
    actuators.remove(actuatorId);
    nodeListeners.forEach(listener -> listener.actuatorRemoved(actuator));
  }

  public void removeSensor(int sensorId) {
    Sensor sensor = sensors.get(sensorId);
    sensors.remove(sensorId);
    nodeListeners.forEach(listener -> listener.sensorRemoved(sensor));
  }

  public void removeNodeStateListener(NodeStateListener listener) {
    nodeStateListeners.remove(listener);
  }

  public void removeNodeListener(NodeListener listener) {
    nodeListeners.remove(listener);
  }

  public void removeStateListener(StateListener listener) {
    stateListeners.remove(listener);
  }
}