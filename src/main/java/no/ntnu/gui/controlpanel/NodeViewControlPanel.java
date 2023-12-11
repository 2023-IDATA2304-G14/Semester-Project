package no.ntnu.gui.controlpanel;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import no.ntnu.controlpanel.ControlPanelChannel;
import no.ntnu.greenhouse.*;
import no.ntnu.gui.common.ActuatorPane;
import no.ntnu.gui.common.SensorPane;
import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.listeners.common.NodeListener;
import no.ntnu.listeners.common.SensorListener;
import no.ntnu.listeners.common.StateListener;
import no.ntnu.listeners.greenhouse.NodeStateListener;

import java.util.List;

/**
 * Window with GUI for overview and control of one specific sensor/actuator node.
 */
public class NodeViewControlPanel extends VBox {
  private final GreenhouseNode node;
  private ActuatorPane actuatorPane;
  private SensorPane sensorPane;
  private Label nodeIdLabel;
  private TitledPane titledPane;
  private ControlPanelChannel channel;
  public NodeViewControlPanel(GreenhouseNode node, ControlPanelChannel channel) {
    this.node = node;
    this.channel = channel;
    this.nodeIdLabel = new Label("Node ID: " + node.getId());
    this.nodeIdLabel.setStyle("-fx-font-weight: bold;");
    initializeGui();
  }
  public TitledPane getPane(){
    return titledPane;
  }


  private void initializeGui() {
    VBox content = new VBox();
    content.setMinWidth(300);
    content.setMinHeight(300);
    actuatorPane = new ActuatorPane(node.getActuators(), node, channel);

    sensorPane = new SensorPane(node.getSensors(), node, channel);

    // Optionally wrap in ScrollPanes
    ScrollPane sensorScrollPane = new ScrollPane(sensorPane);
    sensorScrollPane.setFitToWidth(true);
    sensorScrollPane.setMaxHeight(300); // Set a max height

    ScrollPane actuatorScrollPane = new ScrollPane(actuatorPane);
    actuatorScrollPane.setFitToWidth(true);
    actuatorScrollPane.setMaxHeight(300); // Set a max height


    content.getChildren().addAll(sensorScrollPane, actuatorScrollPane);

    titledPane = new TitledPane("Node ID: " + node.getId(), content);
    titledPane.setMaxHeight(200); // Limit the height of the TitledPane
  }

  public TitledPane getTitledPane() {
    return titledPane;
  }

  public void shutDownNode() {
    node.stop();
  }

  public GreenhouseNode getNode() {
    return node;
  }

  public void updateSensorPane() {
    for (Sensor sensor : node.getSensors()) {
      sensorPane.update(sensor);
    }
  }

  public static boolean isPositiveNumber(String str) {
    try {
      int number = Integer.parseInt(str);
      return number >= 1;
    } catch (NumberFormatException e) {
      return false; // The string does not represent a valid integer
    }
  }
}
