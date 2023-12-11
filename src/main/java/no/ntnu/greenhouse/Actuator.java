package no.ntnu.greenhouse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.listeners.common.NodeListener;
import no.ntnu.listeners.common.StateListener;

/**
 * An actuator that can change the environment in a way. The actuator will make impact on the
 * sensors attached to this same node.
 */
public class Actuator {
  private static int nextId = 1;
  private String type;
  private final int nodeId;
  private final int id;
  private Map<String, Double> impacts = new HashMap<>();
  private List<ActuatorListener> listeners;
  private List<StateListener> stateListeners;
  private boolean on;
  private int strength;
  private int maxStrength;
  private int minStrength;
  private String unit;

  /**
   * Create an actuator. An ID will be auto-generated.
   *
   * @param nodeId ID of the node to which this actuator is connected.
   * @param type   The type of the actuator.
   */
  public Actuator(
      int nodeId,
      String type,
      int strength,
      int maxStrength,
      int minStrength,
      String unit
  ) {
    this.type = type;
    this.nodeId = nodeId;
    this.on = false;
    setMinStrength(minStrength);
    setMaxStrength(maxStrength);
    setStrength(strength);
    this.unit = unit;
    this.id = generateUniqueId();
  }

  /**
   * Create an actuator.
   *
   * @param nodeId ID of the node to which this actuator is connected.
   * @param id     The desired ID of the node.
   * @param type   The type of the actuator.
   * @param strength The strength of the actuator.
   * @param maxStrength The maximum strength of the actuator.
   * @param minStrength The minimum strength of the actuator.
   * @param unit The unit of the actuator.
   */
  public Actuator(
      int nodeId,
      int id,
      String type,
      int strength,
      int maxStrength,
      int minStrength,
      String unit
  ) {
    this.type = type;
    this.nodeId = nodeId;
    this.on = false;
    setMinStrength(minStrength);
    setMaxStrength(maxStrength);
    setStrength(strength);
    this.unit = unit;
    this.id = id;
  }

  private static int generateUniqueId() {
    return nextId++;
  }

  /**
   * Add the given ActuatorListener which will be notified when actuator data changes.
   *
   * @param listener The listener of data change events
   */
  public void addActuatorListener(ActuatorListener listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  /**
   * Remove the given ActuatorListener.
   *
   * @param listener The listener to remove
   */
  public void removeActuatorListener(ActuatorListener listener) {
    listeners.remove(listener);
  }

  /**
   * Add the given NodeListener which will be notified when actuator state changes.
   *
   * @param listener The listener of state change events
   */
  public void addStateListener(StateListener listener) {
    if (!stateListeners.contains(listener)) {
      stateListeners.add(listener);
    }
  }

  /**
   * Remove the given NodeListener.
   *
   * @param listener The listener to remove
   */
  public void removeStateListener(NodeListener listener) {
    stateListeners.remove(listener);
  }

  /**
   * Register the impact of this actuator when active.
   *
   * @param sensorType     Which type of sensor readings will be impacted. Example: "temperature"
   * @param diffWhenActive What will be the introduced difference in the sensor reading when
   *                       the actuator is active. For example, if this value is 2.0 and the
   *                       sensorType is "temperature", this means that "activating this actuator
   *                       will increase the readings of temperature sensors attached to the
   *                       same node by +2 degrees".
   */
  public void setImpact(String sensorType, double diffWhenActive) {
    impacts.put(sensorType, diffWhenActive);
  }


  /**
   * Create a clone of this actuator.
   *
   * @return A clone of this actuator, where all the fields are the same
   */
  public Actuator createClone() {
    Actuator a = new Actuator(nodeId, type, strength, maxStrength, minStrength, unit);
    // Note - we pass a reference to the same map! This should not be problem, as long as we
    // don't modify the impacts AFTER creating the template
    a.impacts = impacts;
    return a;
  }

  /**
   * Toggle the actuator - if it was off, not it will be ON, and vice versa.
   */
  public void toggle() {
    this.on = !this.on;
    notifyDataChanges();
  }

  private void notifyDataChanges() {
    if (listeners != null) {
      for (ActuatorListener listener : listeners) {
        listener.actuatorDataUpdated(this);
      }
    }
  }

  private void notifyStateChanges() {
    if (stateListeners != null) {
      for (StateListener listener : stateListeners) {
        listener.actuatorStateUpdated(this);
      }
    }
  }

  /**
   * Check whether the actuator is active (ON), or inactive (OFF).
   *
   * @return True when the actuator is ON, false if it is OFF
   */
  public boolean isOn() {
    return on;
  }

  /**
   * Apply impact of this actuator to all sensors of one specific sensor node.
   *
   * @param node The sensor node to be affected by this actuator.
   */
  public void applyImpact(GreenhouseNode node) {
    for (Map.Entry<String, Double> impactEntry : impacts.entrySet()) {
      String sensorType = impactEntry.getKey();
      double impact = impactEntry.getValue();
      if (!on) {
        impact = -impact;
      } else {
        //        double normalizedStrength = (double) (strength - minStrength)
        //        / (maxStrength - minStrength); // 0.0 - 1.0
        double normalizedStrength = (double) 2 * (strength - minStrength)
            / (maxStrength - minStrength) - 1; // -1.0 - 1.0
        impact *= normalizedStrength;
      }
      node.applyActuatorImpact(sensorType, impact);
    }
  }

  @Override
  public String toString() {
    return "Actuator{"
            + "type='" + type + '\''
            + ", on=" + on + '\''
            + ", strength=" + strength + '\''
            + ", unit=" + unit + '\''
            + ", maxStrength=" + maxStrength + '\''
            + ", minStrength=" + minStrength + '\''
            + '}';
  }

  /**
   * Turn on the actuator.
   */
  public void turnOn() {
    if (!on) {
      on = true;
      notifyDataChanges();
    }
  }

  /**
   * Turn off the actuator.
   */
  public void turnOff() {
    if (on) {
      on = false;
      notifyDataChanges();
    }
  }

  /**
   * Get the ID of the actuator.
   *
   * @return An ID which is guaranteed to be unique at a node level, not necessarily unique at
   *     the whole greenhouse-network level.
   */
  public int getId() {
    return id;
  }

  /**
   * Get the ID of the node to which this actuator is connected.
   *
   * @return The ID of the node to which this actuator is connected.
   */
  public int getNodeId() {
    return nodeId;
  }

  /**
   * Set the actuator to the desired state.
   *
   * @param on Turn on when true, turn off when false
   */
  public void setOn(boolean on) {
    if (on) {
      turnOn();
    } else {
      turnOff();
    }
  }

  /**
   * Get the strength of the actuator.

   * @return The strength of the actuator.
   */
  public int getStrength() {
    return this.strength;
  }

  /**
   * Set the strength of the actuator.
   * The strength will be clamped to the range [minStrength, maxStrength].

   * @param strength The strength to set.
   */
  public void setStrength(int strength) {
    if (strength > maxStrength) {
      strength = maxStrength;
    } else if (strength < minStrength) {
      strength = minStrength;
    }
    this.strength = strength;
    notifyDataChanges();
  }

  /**
   * Get the unit of the actuator.

   * @return The unit of the actuator.
   */
  public String getUnit() {
    return this.unit;
  }

  /**
   * Set the unit of the actuator.

   * @param unit The unit to set.
   */
  public void setUnit(String unit) {
    this.unit = unit;
    notifyStateChanges();
  }

  /**
   * Get the maximum strength of the actuator.

   * @return The maximum strength of the actuator.
   */
  public int getMaxStrength() {
    return this.maxStrength;
  }

  /**
   * Set the maximum strength of the actuator.
   * The maximum strength will automatically clamp the strength
   *  to the new range [minStrength, maxStrength].
   * The maximum strength will automatically be set to minStrength + 1
   *  if the new maximum strength is less than or equal to minStrength.

   * @param maxStrength The maximum strength to set.
   */
  public void setMaxStrength(int maxStrength) {
    if (maxStrength <= minStrength) {
      maxStrength = minStrength + 1;
    }
    if (strength > maxStrength) {
      setStrength(maxStrength);
    }
    this.maxStrength = maxStrength;
    notifyStateChanges();
  }

  /**
   * Get the minimum strength of the actuator.

   * @return The minimum strength of the actuator.
   */
  public int getMinStrength() {
    return this.minStrength;
  }

  /**
   * Set the minimum strength of the actuator.
   * The minimum strength will automatically clamp the strength
   *  to the new range [minStrength, maxStrength].
   * The minimum strength will automatically be set to maxStrength - 1
   *  if the new minimum strength is greater than or equal to maxStrength.

   * @param minStrength The minimum strength to set.
   */
  public void setMinStrength(int minStrength) {
    if (minStrength >= maxStrength) {
      minStrength = maxStrength - 1;
    }
    if (strength < minStrength) {
      setStrength(minStrength);
    }
    this.minStrength = minStrength;
    notifyStateChanges();
  }

  /**
   * Get the type of the actuator.

   * @return The type of the actuator.
   */
  public String getType() {
    return this.type;
  }

  /**
   * Set the type of the actuator.

   * @param type The type to set.
   */
  public void setType(String type) {
    this.type = type;
    notifyStateChanges();
  }

  /**
   * Set the listeners for this actuator.

   * @param actuatorListeners The listeners to set.
   */
  public void setListeners(List<ActuatorListener> actuatorListeners) {
    this.listeners = actuatorListeners;
  }

  /**
   * Set the state listeners for this actuator.

   * @param stateListeners The state listeners to set.
   */
  public void setStateListeners(List<StateListener> stateListeners) {
    this.stateListeners = stateListeners;
  }
}
