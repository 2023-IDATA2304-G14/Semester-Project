package no.ntnu.gui.controlpanel;

import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import no.ntnu.controlpanel.CommunicationChannel;
import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.controlpanel.SensorActuatorNodeInfo;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.gui.common.ActuatorPane;
import no.ntnu.gui.common.SensorPane;
import no.ntnu.listeners.common.CommunicationChannelListener;
import no.ntnu.listeners.controlpanel.GreenhouseEventListener;
import no.ntnu.tools.Logger;

/**
 * Run a control panel with a graphical user interface (GUI), with JavaFX.
 */
public class ControlPanelApplication extends Application implements GreenhouseEventListener,
    CommunicationChannelListener {
  private static ControlPanelLogic logic;
  private static final int WIDTH = 500;
  private static final int HEIGHT = 400;
  private static CommunicationChannel channel;
  private BorderPane mainLayout;
  private FlowPane nodeDisplayArea;
  private Scene mainScene;
  private VBox controlArea;
  private final Map<Integer, VBox> nodeBoxes = new HashMap<>();
  private final Map<Integer, SensorPane> sensorPanes = new HashMap<>();
  private final Map<Integer, ActuatorPane> actuatorPanes = new HashMap<>();
  private final Map<Integer, SensorActuatorNodeInfo> nodeInfos = new HashMap<>();

  //Daniel Shenanigans
  private String passPhrase;
  private Label label = new Label();

  /**
   * Application entrypoint for the GUI of a control panel.
   * Note - this is a workaround to avoid problems with JavaFX not finding the modules!
   * We need to use another wrapper-class for the debugger to work.
   *
   * @param logic   The logic of the control panel node
   * @param channel Communication channel for sending control commands and receiving events
   */
  public static void startApp(ControlPanelLogic logic, CommunicationChannel channel) {
    if (logic == null) {
      throw new IllegalArgumentException("Control panel logic can't be null");
    }
    ControlPanelApplication.logic = logic;
    ControlPanelApplication.channel = channel;
    Logger.info("Running control panel GUI...");
    launch();
  }

  @Override
  public void start(Stage stage) {
    if (channel == null) {
      throw new IllegalStateException(
          "No communication channel. See the README on how to use fake event spawner!");
    }

    stage.setTitle("Control Panel");

    mainLayout = new BorderPane();

    controlArea = new VBox(10);

    mainLayout.setTop(top());
    mainLayout.setCenter(center());
    mainLayout.setRight(controlArea);

    mainScene = new Scene(mainLayout, WIDTH, HEIGHT);
    stage.setScene(mainScene);


    stage.show();

    logic.addListener(this);
    logic.setCommunicationChannelListener(this);
    if (!channel.open()) {
      logic.onCommunicationChannelClosed();
    }

    //testData();

  }

  private Node top(){
    Button openPopupButton = new Button("Set PSK key");
    openPopupButton.setOnAction(e -> showCustomDialog());
    HBox hBox = new HBox();
    hBox.getChildren().addAll(openPopupButton);
    return hBox;
  }

  private Node center(){
    FlowPane flowPane = new FlowPane();
    flowPane.setAlignment(Pos.CENTER);
    flowPane.setHgap(5);

    nodeDisplayArea = flowPane;
    return nodeDisplayArea;
  }

  private void addNodeDisplay(SensorActuatorNodeInfo nodeInfo) {

    System.out.println(nodeInfo.getId());
    VBox nodeBox = new VBox(5);
    Label nodeLabel = new Label("Node " + nodeInfo.getId());
    nodeBox.getChildren().add(nodeLabel);

    SensorPane sensorPane = new SensorPane();
    sensorPanes.put(nodeInfo.getId(), sensorPane);
    nodeBox.getChildren().add(sensorPane);

    ActuatorPane actuatorPane = new ActuatorPane(nodeInfo.getActuators());
    actuatorPanes.put(nodeInfo.getId(), actuatorPane);
    nodeBox.getChildren().add(actuatorPane);

    nodeBoxes.put(nodeInfo.getId(), nodeBox);
    nodeDisplayArea.getChildren().add(nodeBox);

  }

  private void removeNodeDisplay(int nodeId) {
    VBox nodeBox = nodeBoxes.get(nodeId);
    if (nodeBox != null) {
      nodeDisplayArea.getChildren().remove(nodeBox);
      nodeBoxes.remove(nodeId);
      sensorPanes.remove(nodeId);
      actuatorPanes.remove(nodeId);
    }
  }

  @Override
  public void onNodeUpdated(SensorActuatorNodeInfo nodeInfo) {
    Platform.runLater(() -> addNodeDisplay(nodeInfo));
  }

  @Override
  public void onNodeRemoved(int nodeId) {
    Platform.runLater(() -> removeNodeDisplay(nodeId));
  }

  @Override
  public void onSensorDataChanged(int nodeId, int sensorId, double value) {
    Logger.info("Sensor data from node " + nodeId);
    SensorPane sensorPane = sensorPanes.get(nodeId);
    if (sensorPane != null) {
      sensorPane.update(sensorId, value);
    } else {
      Logger.error("No sensor section for node " + nodeId);
    }
  }

  /**
   * This event is fired when a sensor changes state or is added to the greenhouse.
   *
   * @param nodeId   ID of the node to which the sensor is attached
   * @param sensorId ID of the sensor
   * @param type
   * @param value    The new value of the sensor
   * @param min      The minimum value of the sensor
   * @param max      The maximum value of the sensor
   * @param unit     The unit of the sensor
   */
  @Override
  public void onSensorStateChanged(int nodeId, int sensorId, String type, double value, double min, double max, String unit) {
    Logger.info("Sensor data from node " + nodeId);
    SensorPane sensorPane = sensorPanes.get(nodeId);
    if (sensorPane != null) {
      sensorPane.update(sensorId, type, value, min, max, unit);
    } else {
      Logger.error("No sensor section for node " + nodeId);
    }
  }

  @Override
  public void onActuatorDataChanged(int nodeId, int actuatorId, boolean isOn, int strength) {
    String state = isOn ? "ON" : "off";
    Logger.info("actuator[" + actuatorId + "] on node " + nodeId + " is " + state);
    ActuatorPane actuatorPane = actuatorPanes.get(nodeId);
    if (actuatorPane != null) {
      Actuator actuator = getStoredActuator(nodeId, actuatorId);
      if (actuator != null) {
        if (isOn) {
          actuator.turnOn();
        } else {
          actuator.turnOff();
        }
        actuator.setStrength(strength);
        actuatorPane.update(actuator);
      } else {
        Logger.error(" actuator not found");
      }
    } else {
      Logger.error("No actuator section for node " + nodeId);
    }
  }

  /**
   * This event is fired when an actuator changes state.
   *
   * @param nodeId      ID of the node to which the actuator is attached
   * @param actuatorId  ID of the actuator
   * @param isOn        When true, actuator is on; off when false.
   * @param strength    Strength of the actuator
   * @param minStrength Minimum strength of the actuator
   * @param maxStrength Maximum strength of the actuator
   * @param unit        Unit of the actuator
   */
  @Override
  public void onActuatorStateChanged(int nodeId, int actuatorId, String type, boolean isOn, int strength, int minStrength, int maxStrength, String unit) {
    Logger.info("actuator[" + actuatorId + "] on node " + nodeId + " is " + isOn);
    ActuatorPane actuatorPane = actuatorPanes.get(nodeId);
    Actuator actuator;
    if (actuatorPane != null) {
      actuator = getStoredActuator(nodeId, actuatorId);
      if (actuator != null) {
        if (isOn) {
          actuator.turnOn();
        } else {
          actuator.turnOff();
        }
        actuator.setStrength(strength);
        actuator.setMinStrength(minStrength);
        actuator.setMaxStrength(maxStrength);
        actuator.setUnit(unit);
        actuator.setType(type);
      } else {
        actuator = new Actuator(nodeId, actuatorId, type, strength, minStrength, maxStrength, unit);
        actuator.setOn(isOn);
      }
    actuatorPane.update(actuator);
    } else {
        Logger.error("No actuator section for node " + nodeId);
    }
  }

  /**
   * This event is fired when an actuator is removed from the greenhouse.
   *
   * @param nodeId     ID of the node to which the actuator is attached
   * @param actuatorId ID of the actuator
   */
  @Override
  public void onActuatorRemoved(int nodeId, int actuatorId) {
    Logger.info("actuator[" + actuatorId + "] on node " + nodeId + " is removed");
    ActuatorPane actuatorPane = actuatorPanes.get(nodeId);
    if (actuatorPane != null) {
      actuatorPane.remove(actuatorId);
    } else {
      Logger.error("No actuator section for node " + nodeId);
    }
  }

  private Actuator getStoredActuator(int nodeId, int actuatorId) {
    Actuator actuator = null;
    SensorActuatorNodeInfo nodeInfo = nodeInfos.get(nodeId);
    if (nodeInfo != null) {
      actuator = nodeInfo.getActuator(actuatorId);
    }
    return actuator;
  }

  @Override
  public void onCommunicationChannelClosed() {
    Logger.info("Communication closed, closing the GUI");
    Platform.runLater(Platform::exit);
  }

  private void showCustomDialog() {
    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle("PSK configurator");

    VBox dialogLayout = new VBox(10);

    Label label = new Label("Enter PSK key:");
    TextField textField = new TextField();
    Button saveButton = new Button("Save");

    saveButton.setOnAction(e -> {
      String enteredText = textField.getText();
      dialog.setResult(enteredText);
      dialog.close();
    });

    dialogLayout.getChildren().addAll(label, textField, saveButton);

    dialog.getDialogPane().setContent(dialogLayout);

    dialog.showAndWait().ifPresent(result -> {
      System.out.println("Entered Text: " + result);
    });
  }

  private void testData(){
    SensorActuatorNodeInfo sensorActuatorNodeInfo1 = new SensorActuatorNodeInfo(1);
    SensorActuatorNodeInfo sensorActuatorNodeInfo2 = new SensorActuatorNodeInfo(2);
    SensorActuatorNodeInfo sensorActuatorNodeInfo3 = new SensorActuatorNodeInfo(3);
    SensorActuatorNodeInfo sensorActuatorNodeInfo4 = new SensorActuatorNodeInfo(4);
    SensorActuatorNodeInfo sensorActuatorNodeInfo5 = new SensorActuatorNodeInfo(5);
    SensorActuatorNodeInfo sensorActuatorNodeInfo6 = new SensorActuatorNodeInfo(6);

    sensorActuatorNodeInfo1.addActuator(new Actuator(1, 1, "door", 1, 1, 0, ""));
    sensorActuatorNodeInfo1.addActuator(new Actuator(1, 2, "door", 1, 1, 0, ""));

    addNodeDisplay(sensorActuatorNodeInfo1);
    addNodeDisplay(sensorActuatorNodeInfo2);
    addNodeDisplay(sensorActuatorNodeInfo3);
    addNodeDisplay(sensorActuatorNodeInfo4);
    addNodeDisplay(sensorActuatorNodeInfo5);
    addNodeDisplay(sensorActuatorNodeInfo6);
  }

}
