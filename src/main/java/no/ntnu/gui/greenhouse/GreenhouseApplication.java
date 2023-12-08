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
public class GreenhouseApplication extends Application {
  private static GreenhouseSimulator simulator;
  private Stage mainStage;
  private MainGreenhouseGuiWindow mainWindow;

  @Override
  public void start(Stage mainStage) {
    this.mainStage = mainStage;
    simulator = new GreenhouseSimulator(false); // Or true, depending on your application logic
    mainWindow = new MainGreenhouseGuiWindow(simulator); // Pass the simulator to the constructor

    mainStage.setScene(mainWindow);
    mainStage.setMinWidth(MainGreenhouseGuiWindow.WIDTH);
    mainStage.setMinHeight(MainGreenhouseGuiWindow.HEIGHT);
    mainStage.setTitle("Greenhouse Simulator");
    mainStage.show();

    // Additional setup if needed
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
}

