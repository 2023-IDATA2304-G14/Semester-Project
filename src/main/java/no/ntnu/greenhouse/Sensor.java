package no.ntnu.greenhouse;

import no.ntnu.listeners.common.NodeListener;
import no.ntnu.listeners.common.SensorListener;
import no.ntnu.listeners.common.StateListener;

import java.util.List;

/**
 * A sensor which can sense the environment in a specific way.
 */
public class Sensor {
  private static int nextId = 1;
  private final SensorReading reading;
  private final int id;
  private final int nodeId;
  private double min;
  private double max;
  private List<SensorListener> listeners;
  private List<StateListener> stateListeners;

  /**
   * Create a sensor.
   *
   * @param type    The type of the sensor. Examples: "temperature", "humidity"
   * @param min     Minimum allowed value
   * @param max     Maximum allowed value
   * @param current The current (starting) value of the sensor
   * @param unit    The measurement unit. Examples: "%", "C", "lux"
   */
  public Sensor(int nodeId, String type, double min, double max, double current, String unit) {
    this.reading = new SensorReading(type, current, unit);
    this.nodeId = nodeId;
    this.min = min;
    this.max = max;
    this.id = generateUniqueId();
    ensureValueBoundsAndPrecision(current, false);
  }

  /**
   * Create a sensor.
   *
   * @param id      The desired ID of the sensor.
   * @param type    The type of the sensor. Examples: "temperature", "humidity"
   * @param min     Minimum allowed value
   * @param max     Maximum allowed value
   * @param current The current (starting) value of the sensor
   * @param unit    The measurement unit. Examples: "%", "C", "lux"
   */
  public Sensor(int nodeId, int id, String type, double min, double max, double current, String unit) {
    this.reading = new SensorReading(type, current, unit);
    this.nodeId = nodeId;
    this.min = min;
    this.max = max;
    this.id = id;
    ensureValueBoundsAndPrecision(current, false);
    notifyStateChanges();
  }

  private static int generateUniqueId() {
    return nextId++;
  }

  public String getType() {
    return reading.getType();
  }

  /**
   * Get the current sensor reading.
   *
   * @return The current sensor reading (value)
   */
  public SensorReading getReading() {
    return reading;
  }

  /**
   * Get the unique ID of the sensor.
   *
   * @return The ID
   */
  public int getId() {
      return id;
  }

  /**
   * Create a clone of this sensor.
   *
   * @return A clone of this sensor, where all the fields are the same
   */
  public Sensor createClone() {
    Sensor newSensor = new Sensor(this.id, this.reading.getType(), this.min, this.max,
        this.reading.getValue(), this.reading.getUnit());
    newSensor.listeners = this.listeners;
    newSensor.stateListeners = this.stateListeners;
    return newSensor;
  }

  /**
   * Add a random noise to the sensors to simulate realistic values.
   */
  public void addRandomNoise() {
    double newValue = this.reading.getValue() + generateRealisticNoise();
    ensureValueBoundsAndPrecision(newValue, true);
  }

  private void ensureValueBoundsAndPrecision(double newValue, boolean notify) {
    newValue = roundToTwoDecimals(newValue);
    if (newValue < min) {
      newValue = min;
    } else if (newValue > max) {
      newValue = max;
    }
    reading.setValue(newValue);
    if (notify) {
      notifyDataChanges();
    }
  }

  private double roundToTwoDecimals(double value) {
    return Math.round(value * 100.0) / 100.0;
  }

  private double generateRealisticNoise() {
    final double wholeRange = max - min;
    final double onePercentOfRange = wholeRange / 100.0;
    final double zeroToTwoPercent = Math.random() * onePercentOfRange * 2;
    return zeroToTwoPercent - onePercentOfRange; // In the range [-1%..+1%]
  }

  /**
   * Apply an external impact (from an actuator) to the current value of the sensor.
   *
   * @param impact The impact to apply - the delta for the value
   */
  public void applyImpact(double impact) {
    double newValue = this.reading.getValue() + impact;
    ensureValueBoundsAndPrecision(newValue, true);
  }

  private void notifyDataChanges() {
    for (SensorListener listener : listeners) {
      listener.sensorDataUpdated(this);
    }
  }

  private void notifyStateChanges() {
    for (StateListener listener : stateListeners) {
      listener.sensorStateUpdated(this);
    }
  }

  @Override
  public String toString() {
    return reading.toString();
  }

  /**
   * Get the ID of the node to which this sensor is attached.
   *
   * @return The ID of the node
   */
  public int getNodeId() {
    return nodeId;
  }

  /**
   * Set the minimum allowed value for this sensor.
   * @param min The minimum allowed value
   */
  public void setMin(double min) {
    if (min > max) {
      min = max - 1;
    }
    this.min = min;
    ensureValueBoundsAndPrecision(reading.getValue(), false);
    notifyStateChanges();
  }

  /**
   * Get the minimum allowed value for this sensor.
   * @return The minimum allowed value
   */
  public double getMin() {
    return min;
  }

  /**
   * Set the maximum allowed value for this sensor.
   * @param max The maximum allowed value
   */
  public void setMax(double max) {
    if (max < min) {
      max = min + 1;
    }
    this.max = max;
    ensureValueBoundsAndPrecision(reading.getValue(), false);
    notifyStateChanges();
  }

  /**
   * Get the maximum allowed value for this sensor.
   * @return The maximum allowed value
   */
  public double getMax() {
    return max;
  }

  /**
   * Get the unit of the sensor.
   *
   * @return The unit
   */
  public String getUnit() {
    return reading.getUnit();
  }

  /**
   * Sets the sensor listeners.
   * @param listeners The listeners to set
   */
  public void setListeners(List<SensorListener> listeners) {
    this.listeners = listeners;
  }

  /**
   * Sets the sensor state listeners.
   * @param listeners The listeners to set
   */
  public void setStateListeners(List<StateListener> listeners) {
    this.stateListeners = listeners;
  }

  /**
   * Get the current value of the sensor.
   * @return The current value
   */
  public double getValue() {
    return reading.getValue();
  }
}
