package no.ntnu.gui.common;

import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import no.ntnu.controlpanel.ControlPanelChannel;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.GreenhouseNode;
import no.ntnu.tools.Logger;

/**
 * A GUI component which displays a list of actuators and allows to control them.
 */
public class ActuatorPane extends TitledPane {
  private final Map<Actuator, SimpleStringProperty> actuatorValue = new HashMap<>();
  private final VBox contentBox = new VBox();
  private final GreenhouseNode node;
  private ControlPanelChannel channel = null; // If this is null, the actuator pane is used in the greenhouse simulator

  /**
   * Create a new actuator pane.
   */
  public ActuatorPane(GreenhouseNode node) {
    super();
    setText("Actuators");
    setContent(contentBox); // Set contentBox as the content of the ActuatorPane
    this.node = node;
  }

  /**
   * Create a new actuator pane.
   */
  public ActuatorPane(GreenhouseNode node, ControlPanelChannel channel) {
    super();
    setText("Actuators");
    setContent(contentBox); // Set contentBox as the content of the ActuatorPane
    this.channel = channel;
    this.node = node;
  }

  private String generateActuatorText(Actuator actuator) {
    String onOff = actuator.isOn() ? "ON" : "off";
    return actuator.getType() + ": " + onOff + " (" + actuator.getStrength() + (actuator.getUnit() == null || actuator.getUnit().equals("null") ? "" : actuator.getUnit())  + ")";
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
   *
   * @param actuator
   */
  public void removeActuator(Actuator actuator) {
    node.removeActuator(actuator.getId());
  }

  /**
   *
   *
   * @param actuatorId
   */
  public void removeFromView(int actuatorId) {
    for (Actuator actuator : actuatorValue.keySet()) {
      if (actuator.getId() == actuatorId) {
        actuatorValue.remove(actuator);
        contentBox.getChildren().removeIf(actuatorBox -> actuatorBox.getId().equals(String.valueOf(actuatorId)));
        break;
      }
    }
  }

  public void addActuator(Actuator actuator) {
    Platform.runLater(() -> {
      if (!actuatorValue.containsKey(actuator)) {
        HBox hBox = new HBox(5);
        hBox.setId(String.valueOf(actuator.getId()));

        Label actuatorLabel = new Label(generateActuatorText(actuator));
        SimpleStringProperty prop = new SimpleStringProperty(generateActuatorText(actuator));
        actuatorValue.put(actuator, prop);
        actuatorLabel.textProperty().bind(prop);

        hBox.getChildren().addAll(actuatorLabel);
        if (channel == null) {
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
          hBox.getChildren().addAll(removeButton);
        } else {
          CheckBox checkBox = new CheckBox();
          checkBox.setSelected(actuator.isOn());
          checkBox.setOnAction(e ->
            channel.sendActuatorChange(
              node.getId(),
              actuator.getId(),
              checkBox.isSelected(),
              actuator.getStrength()
            )
          );
          hBox.getChildren().addAll(checkBox);
        }


        contentBox.getChildren().add(hBox);

      }
    });
  }

  public void update(int actuatorId, String type, boolean isOn, int strength, int minStrength, int maxStrength, String unit) {
    for (Actuator actuator : actuatorValue.keySet()) {
      if (actuator.getId() == actuatorId) {
        actuator.setType(type);
        actuator.setOn(isOn);
        actuator.setStrength(strength);
        actuator.setMinStrength(minStrength);
        actuator.setMaxStrength(maxStrength);
        actuator.setUnit(unit);
        update(actuator);
        break;
      }
    }
    Actuator actuator = new Actuator(node.getId(), actuatorId, type, strength, maxStrength, minStrength, unit);
    actuator.setOn(isOn);
    update(actuator);
  }

  public void update(int actuatorId, boolean isOn, int strength) {
    for (Actuator actuator : actuatorValue.keySet()) {
      if (actuator.getId() == actuatorId) {
        actuator.setOn(isOn);
        actuator.setStrength(strength);
        update(actuator);
        break;
      }
    }
  }

  public void remove(int actuatorId) {
    for (Actuator actuator : actuatorValue.keySet()) {
      if (actuator.getId() == actuatorId) {
        removeActuator(actuator);
        break;
      }
    }
  }
}
