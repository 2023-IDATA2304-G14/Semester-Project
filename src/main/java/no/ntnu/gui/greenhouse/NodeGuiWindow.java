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
public class NodeGuiWindow extends Parent implements SensorListener, ActuatorListener {

  private final GreenhouseNode node;

  private ActuatorPane actuatorPane;
  private SensorPane sensorPane;

  /**
   * Create a GUI window for a specific node.
   *
   * @param node The node which will be handled in this window
   */
  public NodeGuiWindow(GreenhouseNode node) {
    this.node = node;
    initializeListeners(node);
  }

  private void initializeListeners(GreenhouseNode node) {
    node.addSensorListener(this);
    node.addActuatorListener(this);
  }

  public void shutDownNode() {
    node.stop();
  }

  private Parent createContent() {
    actuatorPane = new ActuatorPane(node.getActuators());
    sensorPane = new SensorPane(node.getSensors());
    return new VBox(sensorPane, actuatorPane);
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
