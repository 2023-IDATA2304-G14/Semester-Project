package no.ntnu.gui.greenhouse;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.greenhouse.GreenhouseNode;
import no.ntnu.listeners.greenhouse.NodeStateListener;
import no.ntnu.tools.Logger;

/**
 * Run a greenhouse simulation with a graphical user interface (GUI), with JavaFX.
 */
public class GreenhouseApplication extends Application implements NodeStateListener {
  private static GreenhouseSimulator simulator;
  private Stage mainStage;

  private final MainGreenhouseGuiWindow mainWindow = new MainGreenhouseGuiWindow();
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
      GreenhouseNodeGui nodeGui = new GreenhouseNodeGui(node); // Create the GUI for the node
      mainWindow.addNode(node.getId(), nodeGui); // Add the node GUI to the main window
    });
  }
  public void onNodeStopped(GreenhouseNode node) {
    Logger.info("Node " + node.getId() + " has stopped");
    Platform.runLater(() -> {
      mainWindow.removeNode(node.getId()); // Remove the node GUI from the main window
    });
  }
}

