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
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.ActuatorCollection;
import no.ntnu.greenhouse.GreenhouseNode;
import no.ntnu.tools.Logger;

/**
 * A GUI component which displays a list of actuators and allows to control them.
 */
public class ActuatorPane extends TitledPane {
  private final Map<Actuator, SimpleStringProperty> actuatorValue = new HashMap<>();
  private final Map<Actuator, SimpleBooleanProperty> actuatorActive = new HashMap<>();
  private final VBox contentBox = new VBox();

  /**
   * Create a new actuator pane.
   *
   * @param actuators The actuators to display
   */
  public ActuatorPane(ActuatorCollection actuators) {
    super();
    setText("Actuators");
    setContent(contentBox); // Set contentBox as the content of the ActuatorPane
    addActuatorControls(actuators, contentBox);
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
//      TODO: should the checkbox do anything????
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
    return actuator.getType() + ": " + onOff + " (" + actuator.getStrength() + actuator.getUnit() + ")";
  }

  /**
   * Update the GUI to reflect the current state of the actuators.
   *
   * @param actuator The actuator to update
   */
  public void update(Actuator actuator) {
    if (actuatorValue.containsKey(actuator)) {
      actuatorValue.get(actuator).setValue(generateActuatorText(actuator));
    } else {
      Logger.info("Adding new actuator to GUI: " + actuator);
      addActuator(actuator);
    }
  }

  /**
   * Remove an actuator from the GUI.

   * @param actuatorId ID of the actuator to remove
   */
  public void remove(int actuatorId) {
    for (Actuator actuator : actuatorValue.keySet()) {
      if (actuator.getId() == actuatorId) {
        actuatorValue.remove(actuator);
        actuatorActive.remove(actuator);
        contentBox.getChildren().removeIf(actuatorBox -> actuatorBox.getId().equals(actuatorId));
        break;
      }
    }
  }

  public void addActuator(Actuator actuator) {
    Platform.runLater(() -> {
      if (!actuatorValue.containsKey(actuator)) {
        HBox hBox = new HBox(5);

        Label actuatorLabel = new Label("Actuator: " + actuator.getType() + " - " + (actuator.isOn() ? "ON" : "OFF"));

        Button removeButton = new Button("Remove");

        removeButton.setOnAction(e -> {
          Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION,
                  "Are you sure you want to remove this actuator?", ButtonType.YES, ButtonType.NO);
          confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
              removeActuator(actuator);
            }
          });
        });

        hBox.getChildren().addAll(actuatorLabel, removeButton);

        contentBox.getChildren().add(hBox);

        actuatorValue.put(actuator, new SimpleStringProperty(actuator.toString()));
      }
    });
  }

}
