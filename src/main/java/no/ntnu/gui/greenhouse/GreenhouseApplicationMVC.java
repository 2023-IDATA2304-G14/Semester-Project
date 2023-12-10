package no.ntnu.gui.greenhouse;

// Import statements
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import no.ntnu.greenhouse.GreenhouseNode;
import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.listeners.greenhouse.NodeStateListener;
import no.ntnu.tools.Logger;

// Main application class for the greenhouse simulator, using MVC architecture
public class GreenhouseApplicationMVC extends Application implements NodeStateListener {

  // Static simulator instance
  private static GreenhouseSimulator simulator;
  // Main GUI window for the application
  private MainGreenhouseGuiWindow mainWindow = new MainGreenhouseGuiWindow();
  // View component in MVC
  GreenHouseView greenHouseView;
  // Model component in MVC
  private GreenHouseModel model;

  // Start method overridden from Application, sets up the primary stage
  @Override
  public void start(Stage primaryStage) throws Exception {
    // Initialize the view and model
    greenHouseView = new GreenHouseView(primaryStage);
    model = greenHouseView.getModel();
    // Initialize and start the simulator
    simulator.initialize();
    simulator.subscribeToLifecycleUpdates(this);
    // Handle application close request
    primaryStage.setOnCloseRequest(event -> closeApplication());
    simulator.start();
  }

  // Method to handle application closure
  private void closeApplication() {
    Logger.info("Closing Greenhouse application...");
    simulator.stop();
    try {
      stop();
    } catch (Exception e) {
      Logger.error("Could not stop the application: " + e.getMessage());
    }
  }

  // Static method to start the application with an option for a fake simulator
  public static void startApp(boolean fake) {
    Logger.info("Running greenhouse simulator with JavaFX GUI...");
    simulator = new GreenhouseSimulator(fake);
    launch();
  }

  // Overridden method from NodeStateListener, called when a node is ready
  @Override
  public void onNodeReady(GreenhouseNode node) {
    Logger.info("Node " + node.getId() + " is ready");
    // Update the UI in the JavaFX Application thread
    Platform.runLater(() -> {
      greenHouseView.setSimulator(node);
      // mainWindow.addNode(node.getId(), nodeGui); // Add the node GUI to the main window
    });
  }

  // Method called when a node has stopped
  public void onNodeStopped(GreenhouseNode node) {
    Logger.info("Node " + node.getId() + " has stopped");
    // Update the UI in the JavaFX Application thread
    Platform.runLater(() -> {
      mainWindow.removeNode(node.getId()); // Remove the node GUI from the main window
    });
  }

  // Overridden stop method to handle application shutdown
  @Override
  public void stop() throws Exception {
    System.exit(0);
  }

  // Main method to launch the application
  public static void main(String[] args) {
    launch(args);
  }
}
