package no.ntnu.gui.greenhouse;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import no.ntnu.greenhouse.DeviceFactory;
import no.ntnu.greenhouse.GreenhouseNode;
import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.greenhouse.Sensor;
import no.ntnu.gui.greenhouse.helper.NodeView;
import no.ntnu.listeners.greenhouse.NodeStateListener;


public class GreenHouseView implements NodeStateListener {
  // Model and Controller for MVC pattern
  private GreenHouseModel model;
  private GreenHouseController controller;
  // Layout to arrange node views
  private FlowPane flowPane = new FlowPane();

  // Constructor initializing the model, controller, and GUI
  public GreenHouseView(Stage stage, GreenhouseSimulator simulator){
    model = new GreenHouseModel(this, simulator);
    controller = new GreenHouseController(model, this);
    initialize(stage);
  }

  // Creates the top part of the GUI with buttons and labels
  private Node top(){
    HBox hBox = new HBox();
    Button button = new Button("Add New Node");
    Button newPsk = new Button("Generate New PSK Key");
    Label label = new Label("PSK key");
    Button setPort = new Button("Set Port Number");

    // Styling for the top HBox
    hBox.setStyle("-fx-border-color: black; -fx-border-width: 0 0 1 0;");

    // Left part of the top section
    HBox left = new HBox(button);
    left.setSpacing(10);
    left.setPadding(new Insets(10,10,10,10));

    // Right part of the top section
    HBox right = new HBox(newPsk, label, setPort);
    right.setSpacing(10);
    right.setPadding(new Insets(10,10,10,10));
    right.setAlignment(Pos.CENTER_RIGHT);
    hBox.getChildren().addAll(left, right);

    // Event handlers for buttons
    newPsk.setOnAction(e -> label.setText(controller.generatePSKKey()));
    setPort.setOnAction(e -> showCustomDialog());
    button.setOnAction(e -> {
      System.out.println("Add Node");
      GreenhouseNode node = DeviceFactory.createNode(0,0,0,0,0, "TestNode");
      node.addNodeStateListener(this);
      controller.addNode(node);
    });

    // Copy PSK key to clipboard on right-click
    label.setOnMouseClicked(event -> {
      if (event.getButton() == MouseButton.SECONDARY) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(label.getText());
        clipboard.setContent(content);
      }
    });

    return hBox;
  }

  // Creates the center part of the GUI with a scrollable pane
  private Node center(){
    ScrollPane scrollPane = new ScrollPane();
    scrollPane.setContent(flowPane);
    scrollPane.setFitToWidth(true);

    // Styling for the flowPane
    flowPane.setHgap(10);
    flowPane.setVgap(10);
    flowPane.setPadding(new Insets(10, 10, 10, 10));

    return scrollPane;
  }

  // Initializes the stage and sets the scene
  private void initialize(Stage stage){
    stage.setTitle("GreenHouse");
    BorderPane root = new BorderPane();
    root.setCenter(center());
    root.setTop(top());
    Scene scene = new Scene(root, 1200, 600);
    stage.setScene(scene);
    stage.show();
  }

  private void removeNode(GreenhouseNode node) {
    flowPane.getChildren().removeIf(nodeView -> ((NodeView) nodeView).getNode().getId() == node.getId());
  }

  // Getter for the model
  public GreenHouseModel getModel(){
    return model;
  }

  // Shows a custom dialog for setting the port number
  private void showCustomDialog() {
    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle("Set Port Number");

    VBox dialogLayout = new VBox(10);
    Label label = new Label("Enter Port Number:");
    TextField textField = new TextField();
    Button saveButton = new Button("Update");

    // Event handler for the save button
    saveButton.setOnAction(e -> {
      if(isValidPort(textField.getText())){
        model.setPort(textField.getText());
        dialog.setResult("");
        dialog.close();
      }
    });

    dialogLayout.getChildren().addAll(label, textField, saveButton);
    dialog.getDialogPane().setContent(dialogLayout);
    dialog.showAndWait();
  }

  // Validates the entered port number
  private boolean isValidPort(String portText) {
    try {
      int port = Integer.parseInt(portText);
      return port > 0 && port <= 65535;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  public void updateSensor(Sensor sensor) {

  }

  /**
   * This event is fired when a sensor/actuator node has finished the starting procedure and
   * has entered the "ready" state.
   *
   * @param node the node which is ready now
   */
  @Override
  public void onNodeReady(GreenhouseNode node) {
    NodeView nodeView = new NodeView(node, model.getSimulator());
    flowPane.getChildren().add(nodeView.getPane());
  }

  /**
   * This event is fired when a sensor/actuator node has stopped (shut down_.
   *
   * @param node The node which is stopped
   */
  @Override
  public void onNodeStopped(GreenhouseNode node) {
    removeNode(node);
  }
}
