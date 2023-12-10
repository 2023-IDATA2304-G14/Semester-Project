package no.ntnu.gui.controlpanel;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Startup window that gives the user an opportunity to input
 * a custom ip and port.
 */
public class ConfiguratorView extends Application {

  private static String ip = "localhost";
  private static int port = 1238;
  private static CompletableFuture<String> future = new CompletableFuture<>();

  public static String startApp() throws ExecutionException, InterruptedException {
    new Thread(() -> Application.launch(ConfiguratorView.class)).start();
    return future.get(); // This will block until future.complete is called
  }

  @Override
  public void start(Stage primaryStage) {
    TextField ipText = new TextField(ip);
    Label ipLabel = new Label("Enter IP of GreenHouse");
    TextField portText = new TextField(String.valueOf(port));
    Label portLabel = new Label("Enter Port of GreenHouse");
    Button submitButton = new Button("Submit");

    submitButton.setOnAction(e -> {
      if (isValidPort(portText.getText())) {
        ip = ipText.getText();
        port = Integer.parseInt(portText.getText());
        future.complete(ip + "/" + port); // Complete the future with the result
        primaryStage.close();
      }
    });

    VBox layout = new VBox(10, ipLabel, ipText, portLabel, portText, submitButton);
    Scene scene = new Scene(layout, 600, 400);

    primaryStage.setTitle("ControlPanel Configurator");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static boolean isValidPort(String portString) {
    try {
      int port = Integer.parseInt(portString);
      return port >= 1 && port <= 65535;
    } catch (NumberFormatException e) {
      return false; // The string does not contain a valid integer
    }
  }

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    System.out.println(startApp());
  }
}
