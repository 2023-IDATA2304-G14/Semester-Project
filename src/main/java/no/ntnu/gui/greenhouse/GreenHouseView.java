package no.ntnu.gui.greenhouse;

// Import statements
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import no.ntnu.encryption.PSKGenerator;
import no.ntnu.greenhouse.*;
import no.ntnu.gui.greenhouse.helper.NodeView;

public class GreenHouseView {
  // Model and Controller for MVC architecture
  private GreenHouseModel model;
  private GreenHouseController controller;
  // FlowPane for dynamically arranging NodeViews
  private FlowPane flowPane = new FlowPane();

  // Constructor initializes the model, controller, and GUI setup
  public GreenHouseView(Stage stage){
    model = new GreenHouseModel();
    controller = new GreenHouseController(model, this);
    initialize(stage);
  }

  // Creates the top part of the UI with buttons and labels
  private Node top(){
    HBox hBox = new HBox();
    Button button = new Button("Add New Node");
    Button newPsk = new Button("Generate New PSK Key");
    Label label = new Label("PSK key");

    // Styling for the top HBox
    hBox.setStyle("-fx-border-color: black; -fx-border-width: 0 0 1 0;");

    // Left section with the 'Add New Node' button
    HBox left = new HBox(button);
    left.setSpacing(10);
    left.setPadding(new Insets(10,10,10,10));

    // Right section with PSK generation functionality
    HBox right = new HBox(newPsk, label);
    right.setSpacing(10);
    right.setPadding(new Insets(10,10,10,10));
    right.setAlignment(Pos.CENTER_RIGHT);
    hBox.getChildren().addAll(left, right);

    // Event handler for generating a new PSK key
    newPsk.setOnAction(e -> label.setText(controller.generatePSKKey()));

    // Event handler for adding a new node
    button.setOnAction(e -> {
      System.out.println("Add Node");
      GreenhouseNode node = DeviceFactory.createNode(0,0,0,0,0, "TestNode");
      addNode(node);
    });

    // Event handler for copying PSK key to clipboard
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

  // Creates the center part of the UI with a scrollable pane
  private Node center(){
    ScrollPane scrollPane = new ScrollPane();
    scrollPane.setContent(flowPane);
    scrollPane.setFitToWidth(true);

    // Setting gaps and padding for the flowPane
    flowPane.setHgap(10);
    flowPane.setVgap(10);
    flowPane.setPadding(new Insets(10, 10, 10, 10));

    return scrollPane;
  }

  // Initializes the stage with the UI components
  private void initialize(Stage stage){
    stage.setTitle("GreenHouse");
    BorderPane root = new BorderPane();
    root.setCenter(center());
    root.setTop(top());
    Scene scene = new Scene(root, 1200, 600);
    stage.setScene(scene);
    stage.show();
  }

  // Method to remove a node view from the UI
  public void removeNodeView(NodeView nodeView) {
    flowPane.getChildren().remove(nodeView.getPane());
  }

  // Method to add a new node view to the UI
  public void addNode(GreenhouseNode node){
    NodeView nodeView = new NodeView(node, this);
    flowPane.getChildren().add(nodeView.getPane());
  }

  // Method to set a simulator node
  public void setSimulator(GreenhouseNode node){
    System.out.println(node.getId());
    addNode(node);
  }

  // Getter for the model
  public GreenHouseModel getModel(){
    return model;
  }
}
