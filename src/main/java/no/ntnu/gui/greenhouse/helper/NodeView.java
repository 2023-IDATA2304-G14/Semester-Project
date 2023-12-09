package no.ntnu.gui.greenhouse.helper;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import no.ntnu.greenhouse.*;
import no.ntnu.gui.common.ActuatorPane;
import no.ntnu.gui.common.SensorPane;
import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.listeners.greenhouse.SensorListener;

/**
 * Window with GUI for overview and control of one specific sensor/actuator node.
 */
public class NodeView extends VBox implements SensorListener, ActuatorListener {
  private final GreenhouseNode node;
  private ActuatorPane actuatorPane;
  private SensorPane sensorPane;
  private Label nodeIdLabel;

  private Button removeNode = new Button("Remove Node");
  private Button addSensor = new Button("Add Sensor");
  private Button addActuator = new Button("Add Actuator");

  private TitledPane titledPane;

  public NodeView(GreenhouseNode node) {
    this.node = node;
    this.nodeIdLabel = new Label("Node ID: " + node.getId());

    this.nodeIdLabel.setStyle("-fx-font-weight: bold;");
    initializeListeners(node);
    initializeGui();
  }

  public Node getPane(){
    return titledPane;
  }

  private void initializeListeners(GreenhouseNode node) {
    node.addSensorListener(this);
    node.addActuatorListener(this);
  }

  private void initializeGui() {
    VBox content = new VBox();
    content.setMinWidth(300);
    content.setMinHeight(300);
    actuatorPane = new ActuatorPane(node.getActuators());

    sensorPane = new SensorPane(node.getSensors());

    // Optionally wrap in ScrollPanes
    ScrollPane sensorScrollPane = new ScrollPane(sensorPane);
    sensorScrollPane.setFitToWidth(true);
    sensorScrollPane.setMaxHeight(300); // Set a max height

    ScrollPane actuatorScrollPane = new ScrollPane(actuatorPane);
    actuatorScrollPane.setFitToWidth(true);
    actuatorScrollPane.setMaxHeight(300); // Set a max height

    HBox hBox = new HBox(removeNode, addSensor, addActuator);


    content.getChildren().addAll(hBox, sensorScrollPane, actuatorScrollPane);

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
