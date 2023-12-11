package no.ntnu.message;

public class MessageParameterizer {
  private String prefix;
  private String nodeId;
  private String itemId;
  private String type;
  private String value;
  private String secondaryValue;
  private String min;
  private String max;
  private String unit;

  public static final String NODE_ID_PARAMETER = "nId";
  public static final String ITEM_ID_PARAMETER = "iId";
  public static final String TYPE_PARAMETER = "t";
  public static final String VALUE_PARAMETER = "v";
  public static final String SECONDARY_VALUE_PARAMETER = "sv";
  public static final String MIN_PARAMETER = "mi";
  public static final String MAX_PARAMETER = "ma";
  public static final String UNIT_PARAMETER = "u";

  /**
   * Creates a new MessageParameterizer with the given prefix.
   * @param prefix The prefix of the message.
   */
  public MessageParameterizer(String prefix) {
    this.prefix = prefix;
  }

  /**
   * Sets the nodeId parameter.
   * @param nodeId The nodeId to set.
   * @return The MessageParameterizer object.
   */
  public MessageParameterizer setNodeId(String nodeId) {
    this.nodeId = nodeId;
    return this;
  }

  /**
   * Gets the nodeId parameter.
   * @return The nodeId parameter.
   */
  public String getNodeId() {
    return nodeId;
  }

  /**
   * Sets the itemId parameter. Either a sensorId or an actuatorId depending on the message.
   * @param itemId The itemId to set.
   * @return The MessageParameterizer object.
   */
  public MessageParameterizer setItemId(String itemId) {
    this.itemId = itemId;
    return this;
  }

  /**
   * Gets the itemId parameter. Either a sensorId or an actuatorId depending on the message.
   * @return The itemId parameter.
   */
  public String getItemId() {
    return itemId;
  }

  /**
   * Sets the type parameter. Either a sensorType, actuatorType or similar depending on the message.
   * @param type The type to set.
   * @return The MessageParameterizer object.
   */
  public MessageParameterizer setType(String type) {
    this.type = type;
    return this;
  }

  /**
   * Gets the type parameter. Either a sensorType, actuatorType or similar depending on the message.
   * @return The type parameter.
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the value parameter. Either a sensorValue, actuatorValue or similar depending on the message.
   * @param value The value to set.
   * @return The MessageParameterizer object.
   */
  public MessageParameterizer setValue(String value) {
    this.value = value;
    return this;
  }

  /**
   * Gets the value parameter. Either a sensorValue, actuatorValue or similar depending on the message.
   * @return The value parameter.
   */
  public String getValue() {
    return value;
  }

  /**
   * Sets the secondaryValue parameter. Either a sensorValue, actuatorValue or similar depending on the message.
   * @param secondaryValue The secondaryValue to set.
   * @return The MessageParameterizer object.
   */
  public MessageParameterizer setSecondaryValue(String secondaryValue) {
    this.secondaryValue = secondaryValue;
    return this;
  }

  /**
   * Gets the secondaryValue parameter. Either a sensorValue, actuatorValue or similar depending on the message.
   * @return The secondaryValue parameter.
   */
  public String getSecondaryValue() {
    return secondaryValue;
  }

  /**
   * Sets the min parameter. Either a sensorMin, actuatorMin or similar depending on the message.
   * @param min The min to set.
   */
  public MessageParameterizer setMin(String min) {
    this.min = min;
    return this;
  }

  /**
   * Gets the min parameter. Either a sensorMin, actuatorMin or similar depending on the message.
   * @return The min parameter.
   */
  public String getMin() {
    return min;
  }

  /**
   * Sets the max parameter. Either a sensorMax, actuatorMax or similar depending on the message.
   * @param max The max to set.
   */
  public MessageParameterizer setMax(String max) {
    this.max = max;
    return this;
  }

  /**
   * Gets the max parameter. Either a sensorMax, actuatorMax or similar depending on the message.
   * @return The max parameter.
   */
  public String getMax() {
    return max;
  }

  /**
   * Sets the unit parameter. Either a sensorUnit, actuatorUnit or similar depending on the message.
   * @param unit The unit to set.
   */
  public MessageParameterizer setUnit(String unit) {
    this.unit = unit;
    return this;
  }

  /**
   * Gets the unit parameter. Either a sensorUnit, actuatorUnit or similar depending on the message.
   * @return The unit parameter.
   */
  public String getUnit() {
    return unit;
  }

  /**
   * Gets the prefix of the message.
   * @param prefix The prefix to set.
   */
  private MessageParameterizer setPrefix(String prefix) {
    this.prefix = prefix;
    return this;
  }

  /**
   * Gets the prefix of the message.
   * @return The prefix of the message.
   */
  public String getPrefix() {
    return prefix;
  }

  /**
   * Parameterizes the set prefix and variables into a message string. In the style of URL parameters.
   * @return The parameterized message as a String.
   */
  public String parameterize() {
    String message = "?";
    if (nodeId != null) {
      message += NODE_ID_PARAMETER + "=" + nodeId + "&";
    }
    if (itemId != null) {
      message += ITEM_ID_PARAMETER + "=" + itemId + "&";
    }
    if (type != null) {
      message += TYPE_PARAMETER + "=" + type + "&";
    }
    if (value != null) {
      message += VALUE_PARAMETER + "=" + value + "&";
    }
    if (secondaryValue != null) {
      message += SECONDARY_VALUE_PARAMETER + "=" + secondaryValue + "&";
    }
    if (min != null) {
      message += MIN_PARAMETER + "=" + min + "&";
    }
    if (max != null) {
      message += MAX_PARAMETER + "=" + max + "&";
    }
    if (unit != null) {
      message += UNIT_PARAMETER + "=" + unit + "&";
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
    setPrefix(getPrefix(message));
    String[] parameters = message.substring(message.indexOf("?") + 1).split("&");
    for (String parameter : parameters) {
      String[] keyValue = parameter.split("=");
      switch (keyValue[0]) {
        case NODE_ID_PARAMETER:
          setNodeId(keyValue[1]);
          break;
        case ITEM_ID_PARAMETER:
          setItemId(keyValue[1]);
          break;
        case TYPE_PARAMETER:
          setType(keyValue[1]);
          break;
        case VALUE_PARAMETER:
          setValue(keyValue[1]);
          break;
        case SECONDARY_VALUE_PARAMETER:
          setSecondaryValue(keyValue[1]);
          break;
        case MIN_PARAMETER:
          setMin(keyValue[1]);
          break;
        case MAX_PARAMETER:
          setMax(keyValue[1]);
          break;
        case UNIT_PARAMETER:
          setUnit(keyValue[1]);
          break;
        default:
          throw new IllegalArgumentException("Unknown parameter: " + keyValue[0]);
      }
    }
    return this;
  }

  /**
   * Sets all parameters to null.
   */
  private void clearParameters() {
    nodeId = null;
    itemId = null;
    type = null;
    value = null;
  }

  public static String getPrefix(String message) throws IndexOutOfBoundsException {
    if (message.contains("?")) {
      return message.substring(0, message.indexOf("?"));
    } else {
      return message;
    }
  }
}