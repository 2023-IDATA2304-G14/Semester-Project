package no.ntnu.gui.controlpanel;

import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
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
import no.ntnu.controlpanel.ControlPanelChannel;
import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.controlpanel.GreenhouseNodeInfo;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.GreenhouseNode;
import no.ntnu.greenhouse.Sensor;
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
  private final Map<Integer, GreenhouseNode> nodes = new HashMap<>();
  private static ControlPanelModel controlPanelModel;

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
    controlPanelModel = new ControlPanelModel();
    ControlPanelApplication.logic = logic;
    ControlPanelApplication.channel = channel;
    Logger.info("Running control panel GUI...");
  }


  public static void newStart(){
    Logger.info("Running control panel GUI...");
    launch();
  }

  private void setup(String ip, int port){
    System.out.println("Ready");
    ControlPanelLogic logic = new ControlPanelLogic();
    initiateSocketCommunication(logic, ip, port);
    ControlPanelApplication.startApp(logic, channel);
  }


  private void initiateSocketCommunication(ControlPanelLogic logic, String host, int port) {
    channel = new ControlPanelChannel(logic, host, port);
    logic.setCommunicationChannel(channel);
  }

  @Override
  public void start(Stage stage) {
    stage.setTitle("Control Panel");

    mainLayout = new BorderPane();

    controlArea = new VBox(10);

    mainLayout.setTop(top());
    mainLayout.setCenter(center());
    mainLayout.setRight(controlArea);

    setPort();

    mainScene = new Scene(mainLayout, WIDTH, HEIGHT);
    stage.setScene(mainScene);

    stage.show();
    channel.getNodes();
    logic.addListener(this);
    logic.setCommunicationChannelListener(this);
  }

  private Node top() {
    Button openPopupButton = new Button("Set PSK key");
    openPopupButton.setOnAction(e -> showCustomDialog());
    HBox hBox = new HBox(openPopupButton);
    hBox.setPadding(new Insets(10,10,10,10));
    hBox.setStyle("-fx-border-color: black; -fx-border-width: 0 0 1 0;");
    return hBox;
  }

  private Node center(){
    FlowPane flowPane = new FlowPane();
    flowPane.setAlignment(Pos.CENTER);
    flowPane.setHgap(5);

    nodeDisplayArea = flowPane;
    return nodeDisplayArea;
  }

  private void addNodeDisplay(GreenhouseNodeInfo nodeInfo) {

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

  public void onNodeUpdated(GreenhouseNodeInfo nodeInfo) {
    Platform.runLater(() -> addNodeDisplay(nodeInfo));
  }

  /**
   * This event is fired when a node is removed from the greenhouse.
   *
   * @param nodeId ID of the node
   */
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
   * @param type     The type of the sensor
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
      Actuator actuator = getActuator(nodeId, actuatorId);
      if (actuator != null) {
        actuator.setOn(isOn);
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
      actuator = getActuator(nodeId, actuatorId);
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

  /**
   * This event is fired when a node is changed or added to the greenhouse.
   *
   * @param nodeId ID of the node
   * @param name   Name of the node
   */
  @Override
  public void onNodeStateChanged(int nodeId, String name) {
    Logger.info("Node " + nodeId + " is " + name);
    VBox nodeBox = nodeBoxes.get(nodeId);
    if (nodeBox != null) {
      Label nodeLabel = (Label) nodeBox.getChildren().get(0);
      nodeLabel.setText("Node " + nodeId + " - " + name);
    } else {
      Logger.error("No node section for node " + nodeId);
    }
  }

  /**
   * This event is fired when a node is removed from the greenhouse.
   *
   * @param nodeId   ID of the node
   * @param sensorId ID of the sensor
   */
  @Override
  public void onSensorRemoved(int nodeId, int sensorId) {
    Logger.info("sensor[" + sensorId + "] on node " + nodeId + " is removed");
    SensorPane sensorPane = sensorPanes.get(nodeId);
    if (sensorPane != null) {
      sensorPane.remove(sensorId);
    } else {
      Logger.error("No sensor section for node " + nodeId);
    }
  }

  /**
   * This event is fired when the client has successfully subscribed to a node.
   *
   * @param nodeId ID of the node to which the client has subscribed
   */
  @Override
  public void onSubscribeNode(int nodeId) {

  }

  /**
   * This event is fired when the client has successfully unsubscribed from a node.
   *
   * @param nodeId ID of the node from which the client has unsubscribed
   */
  @Override
  public void onUnsubscribeNode(int nodeId) {

  }

  /**
   * This event is fired when an error message is received from the server.
   *
   * @param message The error message
   */
  @Override
  public void onErrorReceived(String message) {

  }

  /**
   * This event is fired when an error message for an unknown message is received from the server.
   *
   * @param message The unknown message
   */
  @Override
  public void onUnknownMessageReceived(String message) {

  }

  private Actuator getActuator(int nodeId, int actuatorId) {
    Actuator actuator = null;
    GreenhouseNode node = nodes.get(nodeId);
    if (node != null) {
      actuator = node.getActuator(actuatorId);
    }
    return actuator;
  }

  private Sensor getSensor(int nodeId, int sensorId) {
    Sensor sensor = null;
    GreenhouseNode node = nodes.get(nodeId);
    if (node != null) {
      sensor = node.getSensor(sensorId);
    }
    return sensor;
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

  private void setPort() {
    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle("Connect to Greenhouse Server");

    VBox dialogLayout = new VBox(10);

    Label enterIp = new Label("Enter Server IP Number:");
    TextField enterIpField = new TextField("localhost");
    Label enterPort = new Label("Enter Server Port Number:");
    TextField enterPortField = new TextField("1238");

    Button saveButton = new Button("Update");

    saveButton.setOnAction(e -> {
      if(isValidPort(enterPortField.getText())){

        setup(enterIpField.getText(), Integer.parseInt(enterPortField.getText()));
        dialog.setResult("");
        dialog.close();
      }
    });

    dialogLayout.getChildren().addAll(enterIp, enterIpField, enterPort, enterPortField, saveButton);
    dialog.getDialogPane().setContent(dialogLayout);
    dialog.showAndWait();
  }

  private boolean isValidPort(String portText) {
    try {
      int port = Integer.parseInt(portText);
      return port > 0 && port <= 65535;
    } catch (NumberFormatException e) {
      return false;
    }
  }

}
