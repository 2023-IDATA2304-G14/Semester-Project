package no.ntnu.gui.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.ActuatorCollection;
import no.ntnu.tools.Logger;

/**
 * A GUI component which displays a list of actuators and allows to control them.
 */
public class ActuatorPane extends TitledPane {
  private final Map<Actuator, SimpleStringProperty> actuatorValue = new HashMap<>();
  private final Map<Actuator, SimpleBooleanProperty> actuatorActive = new HashMap<>();

  /**
   * Create a new actuator pane.
   *
   * @param actuators The actuators to display
   */
  public ActuatorPane(ActuatorCollection actuators) {
    super();
    setText("Actuators");
    VBox vbox = new VBox();
    vbox.setSpacing(10);
    setContent(vbox);
    addActuatorControls(actuators, vbox);
    //GuiTools.stretchVertically(this);
  }

  private void addActuatorControls(ActuatorCollection actuators, Pane parent) {
    actuators.forEach(actuator ->
            parent.getChildren().add(createActuatorGui(actuator))
    );
  }

  private Node createActuatorGui(Actuator actuator) {
    HBox actuatorGui = new HBox(createActuatorLabel(actuator), createActuatorCheckbox(actuator));
    actuatorGui.setSpacing(5);
    return actuatorGui;
  }

  private CheckBox createActuatorCheckbox(Actuator actuator) {
    CheckBox checkbox = new CheckBox();
    SimpleBooleanProperty isSelected = new SimpleBooleanProperty(actuator.isOn());
    actuatorActive.put(actuator, isSelected);
    checkbox.selectedProperty().bindBidirectional(isSelected);

    checkbox.setId("checkBoxActuator");

    checkbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        sendActuatorCommand(actuator, newValue);
      }
    });
    return checkbox;
  }

  private Label createActuatorLabel(Actuator actuator) {
    SimpleStringProperty props = new SimpleStringProperty(generateActuatorText(actuator));
    actuatorValue.put(actuator, props);
    Label label = new Label();
    label.textProperty().bind(props);
    return label;
  }

  private String generateActuatorText(Actuator actuator) {
    String onOff = actuator.isOn() ? "ON" : "off";
    return actuator.getType() + ": " + onOff;
  }

  /**
   * Update the GUI to reflect the current state of the actuators.
   *
   * @param actuator The actuator to update
   */
  public void update(Actuator actuator) {
    SimpleStringProperty actuatorText = actuatorValue.get(actuator);
    SimpleBooleanProperty actuatorSelected = actuatorActive.get(actuator);
    if (actuatorText == null || actuatorSelected == null) {
      throw new IllegalStateException("Can't update GUI for an unknown actuator: " + actuator);
    }

    Platform.runLater(() -> {
      actuatorText.set(generateActuatorText(actuator));
      actuatorSelected.set(actuator.isOn());
    });
  }

  private void sendActuatorCommand(Actuator actuator, boolean isOn) {
    String serverAddress = "your_server_address"; // Replace with  server address
    int serverPort = 1234; // Replace with  server port

    try (Socket socket = new Socket(serverAddress, serverPort);
         PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

      String command = actuator.getId() + ":" + (isOn ? "ON" : "OFF");
      out.println(command);

    } catch (IOException e) {
      Logger.error("Error sending command to actuator: " + e.getMessage());
      // Handle the error
    }
  }

  /**
   * Remove an actuator from the GUI.

   * @param actuatorId ID of the actuator to remove
   */
  public void remove(int actuatorId) {
    actuatorValue.keySet().stream()
            .filter(actuator -> actuator.getId() == actuatorId)
            .findFirst()
            .ifPresent(actuator -> {
              actuatorValue.remove(actuator);
              actuatorActive.remove(actuator);
            });
    //    TODO: Implement a way of removing the existing labels
  }
}
