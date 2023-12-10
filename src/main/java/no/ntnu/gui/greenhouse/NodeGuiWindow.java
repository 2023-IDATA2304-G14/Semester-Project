package no.ntnu.gui.greenhouse;

import java.util.List;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import no.ntnu.greenhouse.*;
import no.ntnu.gui.common.ActuatorPane;
import no.ntnu.gui.common.SensorPane;
import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.listeners.greenhouse.SensorListener;

/**
 * Window with GUI for overview and control of one specific sensor/actuator node.
 */

public class NodeGuiWindow extends VBox implements SensorListener, ActuatorListener {
  private final GreenhouseNode node;
  private ActuatorPane actuatorPane;
  private MainGreenhouseGuiWindow mainGuiWindow;
  private GreenhouseApplication greenhouseApplication;
  private SensorPane sensorPane;
  private Label nodeIdLabel;
  private Button removeButton = new Button("Remove Node");
  private Button addSensor = new Button("Add Sensor");
  private Button addActuator = new Button("Add Actuator");
  private TitledPane titledPane;

  public NodeGuiWindow(GreenhouseNode node) {
    this.node = node;
    this.nodeIdLabel = new Label("Nodeee ID: " + node.getId());

    //Trying to remove node. Need to remove sensors and actuators first?
    removeButton.setOnAction(e -> {
      mainGuiWindow.removeNode(node.getId());
      greenhouseApplication.onNodeStopped(node);
      System.out.println("Remove node: " + node.getId());
    });

    addSensor.setOnAction(e -> {

      //Sensor sensor = new Sensor("something", 20, 10.0, 25.0, 15.0, "Fer");
      //node.addSensors(sensor, 1);
      sensorPane = new SensorPane(node.getSensors());
      System.out.println("Add Sensor :) :) :)");
    });

  this.nodeIdLabel.setStyle("-fx-font-weight: bold;");
    initializeListeners(node);
    initializeGui();
  }

  private void initializeListeners(GreenhouseNode node) {
    node.addSensorListener(this);
    node.addActuatorListener(this);
  }

  private void initializeGui() {
    VBox content = new VBox();
    actuatorPane = new ActuatorPane(node.getActuators());

    sensorPane = new SensorPane(node.getSensors());

    // Optionally wrap in ScrollPanes
    ScrollPane sensorScrollPane = new ScrollPane(sensorPane);
    sensorScrollPane.setFitToWidth(true);
    sensorScrollPane.setMaxHeight(200); // Set a max height

    ScrollPane actuatorScrollPane = new ScrollPane(actuatorPane);
    actuatorScrollPane.setFitToWidth(true);
    actuatorScrollPane.setMaxHeight(200); // Set a max height

    content.getChildren().addAll(removeButton, addSensor, sensorScrollPane, actuatorScrollPane);

    titledPane = new TitledPane("Node ID: " + node.getId(), content);
    titledPane.setMaxHeight(200); // Limit the height of the TitledPane
  }
  public TitledPane getTitledPane() {
    return titledPane;
  }

  public void shutDownNode() {

    node.stop();
  }

  @Override
  public void sensorsUpdated(SensorCollection sensors) {
    if (sensorPane != null) {
      sensorPane.update(sensors);
    }
  }

  @Override
  public void actuatorUpdated(int nodeId, Actuator actuator) {
    if (actuatorPane != null) {
      actuatorPane.update(actuator);
    }
  }
}
