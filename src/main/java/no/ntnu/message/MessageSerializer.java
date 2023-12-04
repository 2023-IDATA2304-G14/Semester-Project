package no.ntnu.message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageSerializer {
  public static final String GET_SENSOR_READING_COMMAND_PREFIX = "get";
  public static final String GET_SENSOR_READING_COMMAND_REGEX = "get\\?nId=(\\d+)&sId=(\\d+)";
  public static final Pattern GET_SENSOR_READING_COMMAND_REGEX_PATTERN = Pattern.compile(GET_SENSOR_READING_COMMAND_REGEX);
  public static final String SENSOR_READING_MESSAGE_PREFIX = "sensor";
  public static final String SENSOR_READING_MESSAGE_REGEX = "sensor\\?nId=(\\d+)&sId=(\\d+)&type=(\\w+)&value=(\\d+)";
  public static final Pattern SENSOR_READING_MESSAGE_REGEX_PATTERN = Pattern.compile(SENSOR_READING_MESSAGE_REGEX);
  public static final String ERROR_MESSAGE_PREFIX = "e: ";
  public static final String ERROR_MESSAGE_REGEX = "e:\\s+(.+)";
  public static final Pattern ERROR_MESSAGE_REGEX_PATTERN = Pattern.compile(ERROR_MESSAGE_REGEX);
  private static final String PARAMETERIZED_COMMAND_REGEX = "^\\w+\\?\\w+=\\w+(&\\w+=\\w+)*$";
  private static final String UNKNOWN_MESSAGE_PREFIX = "uM: ";

  private MessageSerializer() {
  }
  public static String serialize(Message message) throws IllegalArgumentException {
    if (message == null) {
      throw new IllegalArgumentException("Message cannot be null");
    }
    if (message instanceof GetSensorReadingCommand getSensorReadingCommand) {
      return new MessageParameterizer(GET_SENSOR_READING_COMMAND_PREFIX)
          .setNodeId(String.valueOf(getSensorReadingCommand.getNodeId()))
          .setSensorId(String.valueOf(getSensorReadingCommand.getSensorId()))
          .parameterize();
    } else if (message instanceof SensorReadingMessage sensorReadingMessage) {
      return new MessageParameterizer(SENSOR_READING_MESSAGE_PREFIX)
          .setNodeId(String.valueOf(sensorReadingMessage.getNodeId()))
          .setSensorId(String.valueOf(sensorReadingMessage.getSensorId()))
          .parameterize();

//    } else if (...) {
//    TODO: implement other messages
    } else {
      throw new IllegalArgumentException(UNKNOWN_MESSAGE_PREFIX + message);
    }
  }

  public static Message deserialize(String message) throws IllegalArgumentException {
    if (message == null) {
      throw new IllegalArgumentException("Message cannot be null");
    }
    if (message.matches(PARAMETERIZED_COMMAND_REGEX)) {
      return deserializeParameterizedMessage(message);
    } else {
      switch (message) {
//        TODO: Implement other messages
        default:
          return new UnknownMessage(message);
      }
    }

  }

  private static Message deserializeParameterizedMessage(String message) throws IllegalArgumentException {
    Matcher getSensorReaderCommandMatcher = GET_SENSOR_READING_COMMAND_REGEX_PATTERN.matcher(message);

    if (getSensorReaderCommandMatcher.matches()) {
      MessageParameterizer messageParameterizer = new MessageParameterizer(GET_SENSOR_READING_COMMAND_PREFIX)
              .deparameterize(message);

      int nodeID = Integer.parseInt(messageParameterizer.getNodeId());
      int sensorID = Integer.parseInt(messageParameterizer.getSensorId());
      return new GetSensorReadingCommand(nodeID, sensorID);
//    } else if (...) {
//      TODO: Implement other parameterized messages
//    }
    } else {
      return new UnknownMessage(message);
    }
  }
}
