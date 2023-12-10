package no.ntnu.message;

public class SensorStateMessage implements Message {
  public SensorStateMessage(int nodeId, int sensorId, String type, double min, double max, double value, String unit) {
  }
  public static final String PREFIX = "sS";

  @Override
  public String getPrefix() {
    return null;
  }

  @Override
  public String serialize() {
    return null;
  }

  @Override
  public Message deserialize(String message) {
    return null;
  }
}
