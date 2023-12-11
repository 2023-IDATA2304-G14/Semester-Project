package no.ntnu.gui.greenhouse;

import javafx.application.Application;
import javafx.stage.Stage;
import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.tools.Logger;

public class GreenhouseApplicationMVC extends Application {
  private static GreenhouseSimulator simulator;
  GreenHouseView greenHouseView;
  private GreenHouseModel model;
  @Override
  public void start(Stage primaryStage) throws Exception {
    greenHouseView = new GreenHouseView(primaryStage, simulator);
    model = greenHouseView.getModel();
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
   * The main method for launching the GreenhouseApplication application.
   *
   * @param args The command-lines arguments
   */
  public static void main(String[] args) {
    launch(args);
  }
}
