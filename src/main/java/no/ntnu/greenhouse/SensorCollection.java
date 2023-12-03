package no.ntnu.greenhouse;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import no.ntnu.tools.Logger;

/**
 * A collection of sensors of different types.
 */
public class SensorCollection implements Iterable<Sensor> {
    private final Map<Integer, Sensor> sensors = new HashMap<>();

    /**
     * Print a short info about all the sensors. Usable for debugging. Does NOT print a newline!
     */
    public void debugPrint() {
        for (Sensor sensor : sensors.values()) {
            Logger.infoNoNewline(" " + sensor.getType() + "[" + sensor.getId() + "]"
                    + sensor.getReading());
        }
    }

    /**
     * Add an sensor to the collection.
     *
     * @param sensor The sensor to add.
     */
    public void add(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
    }

    /**
     * Get an sensor by its ID.
     *
     * @param id ID of the sensor to look up.
     * @return The sensor or null if none found
     */
    public Sensor get(int id) {
        return sensors.get(id);
    }

    @Override
    public Iterator<Sensor> iterator() {
        return sensors.values().iterator();
    }

    /**
     * Get the number of sensors stored in the collection.
     *
     * @return The number of sensors in this collection
     */
    public int size() {
        return sensors.size();
    }
}
