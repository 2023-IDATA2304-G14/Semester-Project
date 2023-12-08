package no.ntnu.gui.greenhouse;

import java.util.List;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.Sensor;
import no.ntnu.greenhouse.GreenhouseNode;
import no.ntnu.greenhouse.SensorCollection;
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
  private SensorPane sensorPane;

  public NodeGuiWindow(GreenhouseNode node) {
    this.node = node;
    initializeListeners(node);
    initializeGui();
  }

  private void initializeListeners(GreenhouseNode node) {
    node.addSensorListener(this);
    node.addActuatorListener(this);
  }

  private void initializeGui() {
    actuatorPane = new ActuatorPane(node.getActuators());
    sensorPane = new SensorPane(node.getSensors());
    this.getChildren().addAll(sensorPane, actuatorPane);
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
