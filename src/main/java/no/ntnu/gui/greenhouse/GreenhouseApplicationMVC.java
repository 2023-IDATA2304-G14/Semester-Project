package no.ntnu.gui.greenhouse;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import no.ntnu.greenhouse.GreenhouseNode;
import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.listeners.greenhouse.NodeStateListener;
import no.ntnu.tools.Logger;

public class GreenhouseApplicationMVC extends Application implements NodeStateListener {

  private static GreenhouseSimulator simulator;
  private MainGreenhouseGuiWindow mainWindow = new MainGreenhouseGuiWindow();
  GreenHouseView greenHouseView;
  private GreenHouseModel model;

  @Override
  public void start(Stage primaryStage) throws Exception {
    greenHouseView = new GreenHouseView(primaryStage);
    model = greenHouseView.getModel();
    simulator.initialize();
    simulator.subscribeToLifecycleUpdates(this);
    primaryStage.setOnCloseRequest(event -> closeApplication());
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

  public static void startApp(boolean fake) {
    Logger.info("Running greenhouse simulator with JavaFX GUI...");
    simulator = new GreenhouseSimulator(fake);
    launch();
  }

  @Override
  public void onNodeReady(GreenhouseNode node) {
    Logger.info("Node " + node.getId() + " is ready");
    Platform.runLater(() -> {

      greenHouseView.setSimulator(node);

     // mainWindow.addNode(node.getId(), nodeGui); // Add the node GUI to the main window
    });
  }

  public void onNodeStopped(GreenhouseNode node) {
    Logger.info("Node " + node.getId() + " has stopped");
    Platform.runLater(() -> {
      mainWindow.removeNode(node.getId()); // Remove the node GUI from the main window
    });
  }



  /**
   * This method is called when the application is stopped.
   *
   * @throws Exception If there is an issue during application shutdown.
   */
  @Override
  public void stop() throws Exception {
    System.exit(0);
  }

  /**
   * The main method for launching the SmartTvApp application.
   *
   * @param args The command-lines arguments
   */
  public static void main(String[] args) {
    launch(args);
  }
}
