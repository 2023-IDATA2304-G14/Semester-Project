package no.ntnu.message;

import java.util.regex.Pattern;

public class MessageParameterizer {
  private String prefix;
  private String nodeId;
  private String actuatorId;
  private String sensorId;
  private String type;
  private String value;
  public static final String NODE_ID_PARAMETER = "nodeId";
  public static final String ACTUATOR_ID_PARAMETER = "actuatorId";
  public static final String SENSOR_ID_PARAMETER = "sensorId";
  public static final String TYPE_PARAMETER = "type";
  public static final String VALUE_PARAMETER = "value";

  public MessageParameterizer(String prefix) {
    this.prefix = prefix;
  }

  public MessageParameterizer setNodeId(String nodeId) {
    this.nodeId = nodeId;
    return this;
  }

  public String getNodeId() {
    return nodeId;
  }

  public MessageParameterizer setActuatorId(String actuatorId) {
    this.actuatorId = actuatorId;
    return this;
  }

  public String getActuatorId() {
    return actuatorId;
  }

  public MessageParameterizer setSensorId(String sensorId) {
    this.sensorId = sensorId;
    return this;
  }

  public String getSensorId() {
    return sensorId;
  }

  public MessageParameterizer setType(String type) {
    this.type = type;
    return this;
  }

  public String getType() {
    return type;
  }

  public MessageParameterizer setValue(String value) {
    this.value = value;
    return this;
  }

  public String getValue() {
    return value;
  }

  /**
   * Parameterizes the given prefix and variables into a message. In the style of URL parameters.
   * @return The parameterized message.
   */
  public String parameterize() {
    String message = "?";
    if (nodeId != null) {
      message += NODE_ID_PARAMETER + "=" + nodeId + "&";
    }
    if (actuatorId != null) {
      message += ACTUATOR_ID_PARAMETER + "=" + actuatorId + "&";
    }
    if (sensorId != null) {
      message += SENSOR_ID_PARAMETER + "=" + sensorId + "&";
    }
    if (type != null) {
      message += TYPE_PARAMETER + "=" + type + "&";
    }
    if (value != null) {
      message += VALUE_PARAMETER + "=" + value + "&";
    }
    return prefix + message.substring(0, message.length() - 1);
  }

  /**
   * Takes a message and sets the parameters of this object to the values in the message.
   * @param message The message to deparameterize.
   * @return The MessageParameterizer object.
   */
  public MessageParameterizer deparameterize(String message) throws IllegalArgumentException {
    clearParameters();
//    String prefix = message.substring(0, message.indexOf("?"));
    String[] parameters = message.substring(message.indexOf("?") + 1).split("&");
    for (String parameter : parameters) {
      String[] keyValue = parameter.split("=");
      switch (keyValue[0]) {
        case NODE_ID_PARAMETER:
          setNodeId(keyValue[1]);
          break;
        case ACTUATOR_ID_PARAMETER:
          setActuatorId(keyValue[1]);
          break;
        case SENSOR_ID_PARAMETER:
          setSensorId(keyValue[1]);
          break;
        case TYPE_PARAMETER:
          setType(keyValue[1]);
          break;
        case VALUE_PARAMETER:
          setValue(keyValue[1]);
          break;
        default:
          throw new IllegalArgumentException("Unknown parameter: " + keyValue[0]);
      }
    }
    return this;
  }

  private void clearParameters() {
    nodeId = null;
    actuatorId = null;
    sensorId = null;
    type = null;
    value = null;
  }
}