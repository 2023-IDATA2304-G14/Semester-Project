package no.ntnu.gui.common;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import no.ntnu.greenhouse.Sensor;
import no.ntnu.greenhouse.SensorCollection;
import no.ntnu.greenhouse.SensorReading;
import no.ntnu.tools.Logger;

/**
 * A section of GUI displaying sensor data.
 */
public class SensorPane extends TitledPane {
  private final List<SimpleStringProperty> sensorProps = new ArrayList<>();
  private final VBox contentBox = new VBox();


//  TODO: Remove this constructor if sure not needed
//  /**
//   * Create a sensor pane.
//   *
//   * @param sensors The sensor data to be displayed on the pane.
//   */
//  public SensorPane(Iterable<SensorReading> sensors) {
//    super();
//    initialize(sensors);
//  }

  private void initialize(SensorCollection sensors) {
    setText("Sensors");
    sensors.forEach(sensor ->{
        contentBox.getChildren().add(createAndRememberSensorLabel(sensor.getReading()));
    });
    setContent(contentBox);
  }

  /**
   * Create an empty sensor pane, without any data.
   */
  public SensorPane() {
    initialize(new SensorCollection());
  }

  /**
   * Create a sensor pane.
   * Wrapper for the other constructor with SensorReading-iterable parameter
   *
   * @param sensors The sensor data to be displayed on the pane.
   */
  public SensorPane(SensorCollection sensors) {
    initialize(sensors);
  }


  /**
   * Update the GUI according to the changes in sensor data.
   *
   * @param sensorReadings The sensor data that has been updated
   */
  public void update(List<SensorReading> sensorReadings) {
    int index = 0;
    for (SensorReading sensorReading : sensorReadings) {
      updateSensorLabel(sensorReading, index++);
    }
  }

  /**
   * Update the GUI according to the changes in sensor data.
   *
   * @param sensors The sensors that has been updated
   */
  public void update(SensorCollection sensors) {
    int index = 0;
      for (Sensor sensor : sensors) {
          SensorReading sensorReading = sensor.getReading();
          updateSensorLabel(sensorReading, index++);
      }
  }
  private Label createAndRememberSensorLabel(SensorReading sensor) {
    SimpleStringProperty props = new SimpleStringProperty(generateSensorText(sensor));
    sensorProps.add(props);
    Label label = new Label();
    label.textProperty().bind(props);
    return label;
  }

  private String generateSensorText(SensorReading sensor) {
    return sensor.getType() + ": " + sensor.getFormatted();
  }

  private void updateSensorLabel(SensorReading sensor, int index) {
    if (sensorProps.size() > index) {
      SimpleStringProperty props = sensorProps.get(index);
      Platform.runLater(() -> props.set(generateSensorText(sensor)));
    } else {
      Logger.info("Adding sensor[" + index + "]");
      Platform.runLater(() -> contentBox.getChildren().add(createAndRememberSensorLabel(sensor)));
    }
  }
}
