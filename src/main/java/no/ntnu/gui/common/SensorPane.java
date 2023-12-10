package no.ntnu.gui.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import no.ntnu.greenhouse.Sensor;
import no.ntnu.greenhouse.SensorCollection;
import no.ntnu.greenhouse.SensorReading;
import no.ntnu.tools.Logger;

/**
 * A section of GUI displaying sensor data.
 */
public class SensorPane extends TitledPane {
  private final VBox contentBox = new VBox();
  private final Map<Sensor, SimpleStringProperty> sensorValues = new HashMap<>();

  private void initialize(SensorCollection sensors) {
    setText("Sensors");
    sensors.forEach(sensor ->{
      HBox hBox = new HBox();
      hBox.getChildren().addAll(new CheckBox(), createAndRememberSensorLabel(sensor));
        contentBox.getChildren().add(hBox);
    });
    setContent(contentBox);
  }

  /**
   * Create an empty sensor pane, without any data.
   */
  public SensorPane() {
    super();
    initialize(new SensorCollection());
  }

  /**
   * Create a sensor pane.
   * Wrapper for the other constructor with SensorReading-iterable parameter
   *
   * @param sensors The sensor data to be displayed on the pane.
   */
  public SensorPane(SensorCollection sensors) {
    super();
    initialize(sensors);
  }


//  /**
//   * Update the GUI according to the changes in sensor data.
//   *
//   * @param sensorReadings The sensor data that has been updated
//   */
//  public void update(List<SensorReading> sensorReadings) {
//    int index = 0;
//    for (SensorReading sensorReading : sensorReadings) {
//      updateSensorLabel(sensorReading, index++);
//    }
//  }

  /**
   * Update the GUI according to the changes in sensor data.
   *
   * @param sensors The sensors that has been updated
   */
  public void update(SensorCollection sensors) {
      for (Sensor sensor : sensors) {
          updateSensorLabel(sensor);
      }
  }

  /**
   * Update the GUI according to the changes in sensor data.
   *
   * @param sensorId The id of the sensor that has been updated
   */
  public void update(int sensorId, double value) {
    for (Sensor sensor : sensorValues.keySet()) {
      if (sensor.getId() == sensorId) {
        sensor.getReading().setValue(value);
        updateSensorLabel(sensor);
        break;
      }
    }
  }

  private Label createAndRememberSensorLabel(Sensor sensor) {
    SimpleStringProperty props = new SimpleStringProperty(generateSensorText(sensor.getReading()));
    sensorValues.put(sensor, props);
    Label label = new Label();
    label.textProperty().bind(props);
    return label;
  }

  private String generateSensorText(SensorReading sensor) {
    return sensor.getType() + ": " + sensor.getFormatted();
  }

  private void updateSensorLabel(Sensor sensor) {
    if (sensorValues.containsKey(sensor)) {
      Platform.runLater(() -> sensorValues.get(sensor).set(generateSensorText(sensor.getReading())));
    } else {
      Logger.info("Adding sensor[" + sensor.getId() + "]");
      Platform.runLater(() -> {
        HBox hBox = new HBox();
        hBox.getChildren().addAll(new CheckBox(), createAndRememberSensorLabel(sensor));
        contentBox.getChildren().add(hBox);
        //contentBox.getChildren().add(createAndRememberSensorLabel(sensor));
      });
    }
  }

  /**
   * Update the GUI according to the changes in sensor state.
   *
   * @param sensorId The id of the sensor that has been updated
   * @param type     The type of the sensor
   * @param value    The new value of the sensor
   * @param min      The minimum value of the sensor
   * @param max      The maximum value of the sensor
   * @param unit     The unit of the sensor
   */
  public void update(int sensorId, String type, double value, double min, double max, String unit) {
    for (Sensor sensor : sensorValues.keySet()) {
      if (sensor.getId() == sensorId) {
        sensor.getReading().setType(type);
        sensor.getReading().setValue(value);
        sensor.getReading().setUnit(unit);
        sensor.setMin(min);
        sensor.setMax(max);
        updateSensorLabel(sensor);
        break;
      }
    }
  }

  public void remove(int sensorId) {
//    TODO: Implement a way of removing the existing labels
    sensorValues.keySet().stream()
        .filter(actuator -> actuator.getId() == sensorId)
        .findFirst()
        .ifPresent(sensor -> {
          sensorValues.remove(sensor);
        });
  }
}
