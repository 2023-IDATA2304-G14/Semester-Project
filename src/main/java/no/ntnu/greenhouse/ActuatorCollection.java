package no.ntnu.greenhouse;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;
import no.ntnu.tools.Logger;

/**
 * A collection of actuators of different types.
 */
public class ActuatorCollection implements Iterable<Actuator> {
  private final Map<Integer, Actuator> actuators = new HashMap<>();

  /**
   * Print a short info about all the actuators. Usable for debugging. Does NOT print a newline!
   */
  public void debugPrint() {
    for (Actuator actuator : actuators.values()) {
      Logger.infoNoNewline(" " + actuator.getType() + "[" + actuator.getId() + "]"
          + (actuator.isOn() ? " ON" : " off"));
    }
  }

  /**
   * Add an actuator to the collection.
   *
   * @param actuator The actuator to add.
   */
  public void add(Actuator actuator) {
    actuators.put(actuator.getId(), actuator);
  }

  /**
   * Get an actuator by its ID.
   *
   * @param id ID of the actuator to look up.
   * @return The actuator or null if none found
   */
  public Actuator get(int id) {
    return actuators.get(id);
  }

  @Override
  public Iterator<Actuator> iterator() {
    return actuators.values().iterator();
  }

  /**
   * Get the number of actuators stored in the collection.
   *
   * @return The number of actuators in this collection
   */
  public int size() {
    return actuators.size();
  }


  /**
   * Remove an actuator from the collection.

   * @param actuatorId The ID of the actuator to remove
   */
  public void remove(int actuatorId) {
    actuators.remove(actuatorId);
  }

  /**
   * Get a stream of the actuators in this collection.

   * @return A stream of the actuators
   */
  public Stream<Actuator> stream() {
    return actuators.values().stream();
  }
}
