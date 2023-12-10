package no.ntnu.gui.greenhouse.helper;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import no.ntnu.greenhouse.*;
import no.ntnu.gui.common.ActuatorPane;
import no.ntnu.gui.common.SensorPane;
import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.listeners.common.SensorListener;

/**
 * Window with GUI for overview and control of one specific sensor/actuator node.
 */
public class NodeView extends VBox implements SensorListener, ActuatorListener {
  private final GreenhouseNode node;
  private ActuatorPane actuatorPane;
  private SensorPane sensorPane;
  private Label nodeIdLabel;

  private Button removeNode = new Button("Remove Node");
  private Button addSensor = new Button("Add Sensor");
  private Button addActuator = new Button("Add Actuator");

  private TitledPane titledPane;

  public NodeView(GreenhouseNode node) {
    this.node = node;
    this.nodeIdLabel = new Label("Node ID: " + node.getId());

    this.nodeIdLabel.setStyle("-fx-font-weight: bold;");
    initializeListeners(node);
    initializeGui();
  }

  public Node getPane(){
    return titledPane;
  }

  private void initializeListeners(GreenhouseNode node) {
    node.addSensorListener(this);
    node.addActuatorListener(this);
  }

  private void initializeGui() {
    VBox content = new VBox();
    content.setMinWidth(300);
    content.setMinHeight(300);
    actuatorPane = new ActuatorPane(node.getActuators());

    sensorPane = new SensorPane(node.getSensors());

    // Optionally wrap in ScrollPanes
    ScrollPane sensorScrollPane = new ScrollPane(sensorPane);
    sensorScrollPane.setFitToWidth(true);
    sensorScrollPane.setMaxHeight(300); // Set a max height

    ScrollPane actuatorScrollPane = new ScrollPane(actuatorPane);
    actuatorScrollPane.setFitToWidth(true);
    actuatorScrollPane.setMaxHeight(300); // Set a max height

    HBox hBox = new HBox(removeNode, addSensor, addActuator);


    content.getChildren().addAll(hBox, sensorScrollPane, actuatorScrollPane);

    titledPane = new TitledPane("Node ID: " + node.getId(), content);
    titledPane.setMaxHeight(200); // Limit the height of the TitledPane

    addSensor.setOnAction(e -> {
      showSensorDialog();
    });
    addActuator.setOnAction(e -> {
      ShowActuatorDialog();
    });
  }

  public TitledPane getTitledPane() {
    return titledPane;
  }

  public void shutDownNode() {
    node.stop();
  }

  @Override
  public void sensorDataUpdated(Sensor sensor) {
    if (sensorPane != null) {
      sensorPane.update(sensor);
    }
  }

  private void showSensorDialog() {

    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle("Add sensor");
    ComboBox<String> comboBox = new ComboBox<>();
    HBox dialogLayout = new HBox(10);

    comboBox.getItems().addAll("Temperature", "Humidity");
    comboBox.setValue("Temperature");

    Label label = new Label("Amount:");
    TextField textField = new TextField();
    Button saveButton = new Button("Add Sensor");
    Button close = new Button("Exit");

    saveButton.setOnAction(e -> {
         if(isPositiveNumber(textField.getText())) {
          Sensor sensor;
          if (comboBox.getValue() == "Temperature") {
            sensor = DeviceFactory.createTemperatureSensor(node.getId());
          } else {
            sensor = DeviceFactory.createHumiditySensor(node.getId());
          }
          node.addSensors(sensor, Integer.parseInt(textField.getText()));
           dialog.setResult("");
           dialog.close();
         }
    });

    close.setOnAction(e -> {
      dialog.setResult("");
      dialog.close();
    });

    dialogLayout.getChildren().addAll(comboBox, label, textField, saveButton, close);
    dialog.getDialogPane().setContent(dialogLayout);
    dialog.showAndWait();
  }

  private void ShowActuatorDialog(){
    Dialog<String> dialog = new Dialog<>();
    HBox dialogLayout = new HBox(10);
    dialog.setTitle("Add Actuator");
    ComboBox<String> comboBox = new ComboBox<>();

    comboBox.getItems().addAll("Window", "Fan", "Heater");
    comboBox.setValue("Window");

    Button saveButton = new Button("Add");
    Button close = new Button("Exit");

    saveButton.setOnAction(e -> {
      Actuator actuator;
      if (comboBox.getValue() == "Window") {
        actuator = DeviceFactory.createWindow(node.getId());
      } else if (comboBox.getValue() == "Heater") {
        actuator = DeviceFactory.createHeater(node.getId());
      } else {
        actuator = DeviceFactory.createFan(node.getId());
      }
      node.addActuator(actuator);
      dialog.setResult("");
      dialog.close();
    });

    close.setOnAction(e -> {
      dialog.setResult("");
      dialog.close();
    });

    dialogLayout.getChildren().addAll(comboBox, saveButton, close);
    dialog.getDialogPane().setContent(dialogLayout);
    dialog.showAndWait();
  }


  public static boolean isPositiveNumber(String str) {
    try {
      int number = Integer.parseInt(str);
      return number >= 1;
    } catch (NumberFormatException e) {
      return false; // The string does not represent a valid integer
    }
  }

  /**
   * An event that is fired every time an actuator changes data.
   *
   * @param actuator The actuator that has changed its state
   */
  @Override
  public void actuatorDataUpdated(Actuator actuator) {
    if (actuatorPane != null) {
      actuatorPane.update(actuator);
    }
  }
}
