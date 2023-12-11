package no.ntnu.gui.greenhouse;

import javafx.application.Platform;
import no.ntnu.controlpanel.GreenhouseNodeInfo;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.GreenhouseNode;
import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.greenhouse.Sensor;
import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.listeners.common.NodeListener;
import no.ntnu.listeners.common.SensorListener;
import no.ntnu.listeners.common.StateListener;
import no.ntnu.listeners.controlpanel.GreenhouseEventListener;
import no.ntnu.listeners.greenhouse.NodeStateListener;
import no.ntnu.tools.Logger;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class GreenHouseModel {
  private GreenhouseSimulator getSimulator;
  private GreenhouseSimulator simulator;
  private GreenHouseView view;
  private String portNumber;

  public GreenHouseModel(GreenHouseView view, GreenhouseSimulator simulator) {
    this.simulator = simulator;
    this.view = view;
  }

  public void setPort(String portNumber){
    this.portNumber = portNumber;
  }

  public GreenhouseSimulator getSimulator() {
    return simulator;
  }

//  /**
//   * This event is fired when a sensor/actuator node has finished the starting procedure and
//   * has entered the "ready" state.
//   *
//   * @param node the node which is ready now
//   */
//  @Override
//  public void onNodeReady(GreenhouseNode node) {
//    Logger.info("Node " + node.getId() + " is ready");
//    Platform.runLater(() -> view.addNode(node));
//  }
//
//  /**
//   * This event is fired when a sensor/actuator node has stopped (shut down_.
//   *
//   * @param node The node which is stopped
//   */
//  @Override
//  public void onNodeStopped(GreenhouseNode node) {
//    Logger.info("Node " + node.getId() + " has stopped");
//    Platform.runLater(() -> {
//      Platform.runLater(() -> view.removeNode(node));
//    });
//  }
}
