package no.ntnu.gui.greenhouse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import no.ntnu.greenhouse.SensorReading;
import no.ntnu.gui.common.SensorPane;
import no.ntnu.greenhouse.Sensor;
import java.util.List;
import java.util.stream.Collectors;


/**
 * The main GUI window for greenhouse simulator.
 */
public class MainGreenhouseGuiWindow extends Scene {
  public static final int WIDTH = 300;
  public static final int HEIGHT = 300;

  // Dashboard labels
  private static Label temperatureLabel = new Label("Temperature: -- °C");
  private static Label humidityLabel = new Label("Humidity: -- %");

  private SensorPane temperatureSensorPane;
  private SensorPane humiditySensorPane;

  public MainGreenhouseGuiWindow() {

    super(createMainContent(), WIDTH, HEIGHT);

    temperatureSensorPane = new SensorPane();
    humiditySensorPane = new SensorPane();

    ((VBox) this.getRoot()).getChildren().addAll(temperatureSensorPane, humiditySensorPane);
  }

  private static Parent createMainContent() {
    VBox container = new VBox(
            createInfoLabel(),
            createDashboard(temperatureLabel, humidityLabel),
            createMasterImage(),
            createCopyrightNotice());
    container.setPadding(new Insets(20));
    container.setAlignment(Pos.CENTER);
    container.setSpacing(5);
    return container;
  }
  public void updateSensorData(List<Sensor> updatedSensors) {
    // Filter and update temperature sensors
    List<SensorReading> temperatureReadings = updatedSensors.stream()
            .filter(sensor -> sensor.getType().equals("Temperature"))
            .map(Sensor::getReading) // Use getReading or getValue here?
            .collect(Collectors.toList());
    if (!temperatureReadings.isEmpty()) {
      temperatureSensorPane.update(temperatureReadings);
    }

    // Filter and update humidity sensors
    List<SensorReading> humidityReadings = updatedSensors.stream()
            .filter(sensor -> sensor.getType().equals("Humidity"))
            .map(Sensor::getReading) // Use getReading or getValue here?
            .collect(Collectors.toList());
    if (!humidityReadings.isEmpty()) {
      humiditySensorPane.update(humidityReadings);
    }

  }

  private static Node createDashboard(Label tempLabel, Label humidLabel) {
    VBox dashboard = new VBox();
    dashboard.setAlignment(Pos.CENTER);
    dashboard.setSpacing(10);

    dashboard.getChildren().addAll(tempLabel, humidLabel);
    return dashboard;
  }

  private static Label createInfoLabel() {
    Label l = new Label("Close this window to stop the whole simulation");
    l.setWrapText(true);
    l.setPadding(new Insets(0, 0, 10, 0));
    return l;
  }

  private static Node createCopyrightNotice() {
    Label l = new Label("Image generated with Picsart");
    l.setFont(Font.font(10));
    return l;
  }

  private static Node createMasterImage() {
    Node node;
    try {
      InputStream fileContent = new FileInputStream("images/picsart_chuck.jpeg");
      ImageView imageView = new ImageView();
      imageView.setImage(new Image(fileContent));
      imageView.setFitWidth(100);
      imageView.setPreserveRatio(true);
      node = imageView;
    } catch (FileNotFoundException e) {
      node = new Label("Could not find image file: " + e.getMessage());
    }
    return node;
  }

}
