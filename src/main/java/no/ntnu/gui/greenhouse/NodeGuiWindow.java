package no.ntnu.gui.greenhouse;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.Sensor;
import no.ntnu.greenhouse.GreenhouseNode;
import no.ntnu.gui.common.ActuatorPane;
import no.ntnu.gui.common.SensorPane;
import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.listeners.common.SensorListener;

/**
 * Window with GUI for overview and control of one specific sensor/actuator node.
 */

public class NodeGuiWindow extends VBox implements SensorListener, ActuatorListener {
  private final GreenhouseNode node;
  private ActuatorPane actuatorPane;
  private SensorPane sensorPane;
  private Label nodeIdLabel;

  private Button button = new Button("Remove Node");
  private Button addSensor = new Button("Add Sensor");
  private Button addActuator = new Button("Remove Node");

  private TitledPane titledPane;



  public NodeGuiWindow(GreenhouseNode node) {
    this.node = node;
    this.nodeIdLabel = new Label("Node ID: " + node.getId());

    //Trying to remove node. Need to remove sensors and actuators first?
    button.setOnAction(e -> {
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

    content.getChildren().addAll(button, addSensor, sensorScrollPane, actuatorScrollPane);

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
  public void sensorDataUpdated(Sensor sensor) {
    if (sensorPane != null) {
      sensorPane.update(sensor);
    }
  }

  @Override
  public void actuatorDataUpdated(Actuator actuator) {
    if (actuatorPane != null) {
      actuatorPane.update(actuator);
    }
  }

  public void removeActuator(int actuatorId) {
    actuatorPane.remove(actuatorId);
  }

  public void removeSensor(int sensorId) {
    sensorPane.remove(sensorId);
  }

  public void updateActuator(Actuator actuator) {
    actuatorPane.update(actuator);
  }

  public void updateSensor(Sensor sensor) {
    sensorPane.update(sensor);
  }
}
