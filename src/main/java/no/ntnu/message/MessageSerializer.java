package no.ntnu.message;

import java.lang.reflect.Modifier;
import java.util.Set;

public class MessageSerializer {
  private MessageSerializer() {
  }
  public static String serialize(Message message) throws IllegalArgumentException {
    if (message == null) {
      throw new IllegalArgumentException("Message cannot be null");
    }
    return message.serialize();
  }

  public static Message deserialize(String message) throws IllegalArgumentException {
    if (message == null) {
      throw new IllegalArgumentException("Message cannot be null");
    }
    String prefix = MessageParameterizer.getPrefix(message);

    Set<Class<? extends Message>> messageClasses = Set.of(
        ActuatorReadingMessage.class,
        ActuatorRemoveMessage.class,
        ActuatorUpdatedMessage.class,
        ErrorMessage.class,
        GetSensorReadingCommand.class,
        NodeRemovedMessage.class,
        NodeUpdatedMessage.class,
        SensorReadingMessage.class,
        SensorRemoveMessage.class,
        SensorUpdatedMessage.class,
        SubscribeNodeCommand.class,
        SubscribeNodeMessage.class,
        UnknownMessage.class,
        UnsubscribeNodeCommand.class,
        UnsubscribeNodeMessage.class
    );

    for (Class<? extends Message> messageClass : messageClasses) {
      if (Modifier.isAbstract(messageClass.getModifiers())) {
        continue;
      }
      try {
        Message instance = messageClass.getDeclaredConstructor().newInstance();
        if (instance.getPrefix().equals(prefix)) {
          return instance.deserialize(message);
        }
      } catch (Exception e) {
        return new UnknownMessage(message);
      }
    }
    return null;
  }
}
