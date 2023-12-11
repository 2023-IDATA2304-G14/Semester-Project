package no.ntnu.gui.controlpanel;

import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import no.ntnu.controlpanel.ControlPanelChannel;
import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.encryption.ChangeKey;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.GreenhouseNode;
import no.ntnu.greenhouse.Sensor;
import no.ntnu.gui.common.ActuatorPane;
import no.ntnu.gui.common.SensorPane;
import no.ntnu.listeners.common.CommunicationChannelListener;
import no.ntnu.listeners.controlpanel.GreenhouseEventListener;
import no.ntnu.tools.Logger;

import static no.ntnu.tools.Error.showAlert;

/**
 * Run a control panel with a graphical user interface (GUI), with JavaFX.
 */
public class ControlPanelApplication extends Application implements GreenhouseEventListener,
    CommunicationChannelListener {
  private static ControlPanelLogic logic;
  private static final int WIDTH = 700;
  private static final int HEIGHT = 500;
  private static ControlPanelChannel channel;
  private BorderPane mainLayout;
  private FlowPane nodeDisplayArea;
  private Scene mainScene;
  private VBox controlArea;
  private final Map<Integer, NodeViewControlPanel> nodeViewPanes = new HashMap<>();

  /**
   * Application entrypoint for the GUI of a control panel.
   * Note - this is a workaround to avoid problems with JavaFX not finding the modules!
   * We need to use another wrapper-class for the debugger to work.
   *
   * @param logic   The logic of the control panel node
   * @param channel Communication channel for sending control commands and receiving events
   */
  public static void startApp(ControlPanelLogic logic, ControlPanelChannel channel) {
    if (logic == null) {
      throw new IllegalArgumentException("Control panel logic can't be null");
    }
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

//  private void addNodeDisplay(GreenhouseNode node) {
//    System.out.println(node.getId());
//    VBox nodeBox = new VBox(5);
//    Label nodeLabel = new Label("Node " + node.getId());
//    nodeBox.getChildren().add(nodeLabel);
//
//    SensorPane sensorPane = new SensorPane(node);
//    sensorPanes.put(node.getId(), sensorPane);
//    nodeBox.getChildren().add(sensorPane);
//
//    ActuatorPane actuatorPane = new ActuatorPane(node.getActuators(), node);
//    actuatorPanes.put(node.getId(), actuatorPane);
//    nodeBox.getChildren().add(actuatorPane);
//
//    nodeBoxes.put(node.getId(), nodeBox);
//    nodeDisplayArea.getChildren().add(nodeBox);
//  }

  private void removeNodeDisplay(int nodeId) {
    NodeViewControlPanel nodePane = nodeViewPanes.get(nodeId);
    if (nodePane != null) {
      nodeDisplayArea.getChildren().remove(nodePane);
      nodeViewPanes.remove(nodeId);
    }
  }

  @Override
  public void onSensorDataChanged(int nodeId, int sensorId, double value) {
    Logger.info("Sensor data from node " + nodeId);
    Platform.runLater(() -> {
      if (nodeViewPanes.containsKey(nodeId)) {
        nodeViewPanes.get(nodeId).updateSensor(sensorId, value);
      }
    });
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
    Platform.runLater(() -> {
      if (nodeViewPanes.containsKey(nodeId)) {
        nodeViewPanes.get(nodeId).updateSensor(sensorId, type, value, min, max, unit);
      }
    });
  }

  @Override
  public void onActuatorDataChanged(int nodeId, int actuatorId, boolean isOn, int strength) {
    String state = isOn ? "ON" : "off";
    Logger.info("actuator[" + actuatorId + "] on node " + nodeId + " is " + state);
    Platform.runLater(() -> {
      if (nodeViewPanes.containsKey(nodeId)) {
        nodeViewPanes.get(nodeId).updateActuator(actuatorId, isOn, strength);
      }
    });
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
    Platform.runLater(() -> {
      if (nodeViewPanes.containsKey(nodeId)) {
        nodeViewPanes.get(nodeId).updateActuator(actuatorId, type, isOn, strength, minStrength, maxStrength, unit);
      }
    });
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
    Platform.runLater(() -> {
      if (nodeViewPanes.containsKey(nodeId)) {
        nodeViewPanes.get(nodeId).removeActuator(actuatorId);
      }
    });
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
    Platform.runLater(() -> {
      if (nodeViewPanes.containsKey(nodeId)) {
        nodeViewPanes.get(nodeId).getPane().setText(name);
      } else {
        GreenhouseNode node = new GreenhouseNode(nodeId, name);
        NodeViewControlPanel nodeView = new NodeViewControlPanel(node, channel);
        nodeViewPanes.put(nodeId, nodeView);
        nodeDisplayArea.getChildren().add(nodeView.getPane());
        channel.getSensors(nodeId);
        channel.getActuators(nodeId);
//        TODO: Implement a way for the user to subscribe and unsubscribe to nodes in the GUI
        channel.subscribeToNode(nodeId);
      }
    });
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
    Platform.runLater(() -> {
      if (nodeViewPanes.containsKey(nodeId)) {
        nodeViewPanes.get(nodeId).removeSensor(sensorId);
      }
    });
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
    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error received", message, "The server sent an error message: " + message, mainScene));
  }

  /**
   * This event is fired when an error message for an unknown message is received from the server.
   *
   * @param message The unknown message
   */
  @Override
  public void onUnknownMessageReceived(String message) {
    Platform.runLater(() -> showAlert(Alert.AlertType.WARNING, "Unknown message received", message, "The message received from the server was not recognized: " + message, mainScene));
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
  public void onCommunicationChannelClosed() {
    Logger.info("Communication closed, closing the GUI");
    Platform.runLater(Platform::exit);
  }

  @Override
  public void stop() {
    Logger.info("Closing the GUI");
    channel.close();
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
      ChangeKey.getInstance().setKey(result);
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
    Label enterKey = new Label("Enter Server PSK password:");
    TextField enterKeyField = new TextField("");

    Button saveButton = new Button("Update");

    saveButton.setOnAction(e -> {
      if(isValidPort(enterPortField.getText())){
        ChangeKey.getInstance().setKey(enterKeyField.getText());
        setup(enterIpField.getText(), Integer.parseInt(enterPortField.getText()));
        dialog.setResult("");
        dialog.close();
      }
    });

    dialogLayout.getChildren().addAll(enterIp, enterIpField, enterPort, enterPortField, enterKey, enterKeyField, saveButton);
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
