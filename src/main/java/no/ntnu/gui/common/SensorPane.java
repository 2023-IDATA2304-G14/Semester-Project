package no.ntnu.gui.common;

import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import no.ntnu.controlpanel.ControlPanelChannel;
import no.ntnu.greenhouse.GreenhouseNode;
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
  private final GreenhouseNode node;
  private ControlPanelChannel channel = null;


  private void initialize(SensorCollection sensors) {
    setText("Sensors");
    sensors.forEach(sensor ->{
      HBox hBox = new HBox();
      hBox.getChildren().addAll(createAndRememberSensorLabel(sensor));
        contentBox.getChildren().add(hBox);
    });
    setContent(contentBox);
  }

  /**
   * Create an empty sensor pane, without any data.
   */
  public SensorPane(GreenhouseNode node) {
    super();
    this.node = node;
    initialize(new SensorCollection());
  }

  public SensorPane(GreenhouseNode node, ControlPanelChannel channel) {
    super();
    this.node = node;
    this.channel = channel;
    initialize(new SensorCollection());
  }

  /**
   * Create a sensor pane.
   * Wrapper for the other constructor with SensorReading-iterable parameter
   *
   * @param sensors The sensor data to be displayed on the pane.
   */
  public SensorPane(SensorCollection sensors, GreenhouseNode node) {
    super();
    this.node = node;
    initialize(sensors);
  }

  public SensorPane(SensorCollection sensors, GreenhouseNode node, ControlPanelChannel channel) {
    super();
    this.node = node;
    this.channel = channel;
    initialize(sensors);
  }

  private void addSensor(Sensor sensor) {
    Platform.runLater(() -> {
      if (!sensorValues.containsKey(sensor)) {
        HBox hBox = new HBox(5);
        hBox.setId(String.valueOf(sensor.getId()));

        Button removeButton = new Button("Remove");

        removeButton.setOnAction(e -> {
          Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION,
                  "Are you sure you want to remove this sensor?", ButtonType.YES, ButtonType.NO);
          confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
              removeSensor(sensor);
            }
          });
        });

        Label sensorLabel = createAndRememberSensorLabel(sensor);
        hBox.getChildren().addAll(sensorLabel, removeButton);
        contentBox.getChildren().add(hBox);
      }
    });
  }

  public void removeSensor (Sensor sensor) {
    node.removeSensor(sensor.getId());
  }

  /**
   * Update the GUI according to the changes in sensor data.
   *
   * @param sensor The sensor that has been updated
   */
  public void update(Sensor sensor) {
    updateSensorLabel(sensor);
  }

  /**
   * Update the GUI according to the changes in sensor data.
   * @param sensorId The id of the sensor that has been updated
   * @param value    The new value of the sensor
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
      addSensor(sensor);
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
    for (Sensor sensor : sensorValues.keySet()) {
      if (sensor.getId() == sensorId) {
        sensorValues.remove(sensor);
        contentBox.getChildren().removeIf(sensorBox -> sensorBox.getId().equals(String.valueOf(sensorId)));
        break;
      }
    }
  }
}
