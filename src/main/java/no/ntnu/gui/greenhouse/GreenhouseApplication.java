package no.ntnu.gui.greenhouse;

import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.greenhouse.GreenhouseNode;
import no.ntnu.greenhouse.Sensor;
import no.ntnu.listeners.common.NodeListener;
import no.ntnu.listeners.greenhouse.NodeStateListener;
import no.ntnu.tools.Logger;

/**
 * Run a greenhouse simulation with a graphical user interface (GUI), with JavaFX.
 */
public class GreenhouseApplication extends Application implements NodeStateListener, NodeListener {
  private static GreenhouseSimulator simulator;
  private Stage mainStage;

  //Test
  private MainGreenhouseGuiWindow mainWindow = new MainGreenhouseGuiWindow();
  @Override
  public void start(Stage mainStage) {
    this.mainStage = mainStage;
    mainStage.setScene(mainWindow);
    mainStage.setMinWidth(MainGreenhouseGuiWindow.WIDTH);
    mainStage.setMinHeight(MainGreenhouseGuiWindow.HEIGHT);
    mainStage.setTitle("Greenhouse simulator");
    mainStage.show();
    Logger.info("GUI subscribes to lifecycle events");
    simulator.initialize();
    simulator.subscribeToLifecycleUpdates(this);
    mainStage.setOnCloseRequest(event -> closeApplication());
    simulator.start();
  }

  private void closeApplication() {
    Logger.info("Closing Greenhouse application...");
    simulator.stop();
    try {
      stop();
    } catch (Exception e) {
      Logger.error("Could not stop the application: " + e.getMessage());
    }
  }

  /**
   * Start the GUI Application.
   *
   * @param fake When true, emulate fake events instead of opening real sockets
   */
  public static void startApp(boolean fake) {
    Logger.info("Running greenhouse simulator with JavaFX GUI...");
    simulator = new GreenhouseSimulator(fake);
    launch();
  }

  @Override
  public void onNodeReady(GreenhouseNode node) {
    Logger.info("Node " + node.getId() + " is ready");
    Platform.runLater(() -> {
      NodeGuiWindow nodeGui = new NodeGuiWindow(node); // Correct type
      mainWindow.addNode(node.getId(), nodeGui); // Add the node GUI to the main window
    });
  }
  public void onNodeStopped(GreenhouseNode node) {
    Logger.info("Node " + node.getId() + " has stopped");
    Platform.runLater(() -> {
      mainWindow.removeNode(node.getId()); // Remove the node GUI from the main window
    });
  }

  /**
   * An event that is fired every time an actuator is removed from a node.
   *
   * @param nodeId     ID of the node on which this actuator is placed
   * @param actuatorId ID of the actuator that has been removed
   */
  @Override
  public void actuatorRemoved(int nodeId, int actuatorId) {
    Platform.runLater(() -> {
      mainWindow.getNode(nodeId).removeActuator(actuatorId);
    });
  }

  /**
   * An event that is fired every time a sensor is removed from a node.
   *
   * @param nodeId   ID of the node on which this sensor is placed
   * @param sensorId ID of the sensor that has been removed
   */
  @Override
  public void sensorRemoved(int nodeId, int sensorId) {
    Platform.runLater(() -> {
      mainWindow.getNode(nodeId).removeSensor(sensorId);
    });
  }

  /**
   * An event that is fired every time an actuator changes state or is added.
   *
   * @param actuator The actuator that has changed its state
   */
  @Override
  public void actuatorStateUpdated(Actuator actuator) {
    Platform.runLater(() -> {
      mainWindow.getNode(actuator.getNodeId()).updateActuator(actuator);
    });
  }

  /**
   * An event that is fired every time a sensor changes state or is added.
   *
   * @param sensor The sensor that has changed its state
   */
  @Override
  public void sensorStateUpdated(Sensor sensor) {
    Platform.runLater(() -> {
      mainWindow.getNode(sensor.getNodeId()).updateSensor(sensor);
    });
  }
}

